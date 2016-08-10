package mediator_wrapper.wrapper.impl.xml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import controller.runtime.modify.RuntimeModificationMessage;
import ivisObject.AttributeValuePair;
import ivisObject.IvisObject;
import ivisQuery.IvisQuery;
import mediator_wrapper.wrapper.IvisWrapperInterface;
import mediator_wrapper.wrapper.abstract_types.AbstractIvisFileWrapper;

/**
 * Wrapper to manage access to XML files.
 * 
 * @author Christian Danowski
 *
 */
public class XmlWrapper extends AbstractIvisFileWrapper implements IvisWrapperInterface {

	public XmlWrapper(String pathToSourcefile, String pathToSchemaMappingFile) throws DocumentException {
		super(pathToSourcefile, pathToSchemaMappingFile);

	}

	@Override
	public List<IvisObject> queryData(IvisQuery queryAgainstGlobalSchema, List<String> subquerySelectors_globalSchema)
			throws DocumentException {
		/*
		 * queryAgainstGlobalScheme has selector of elements that shall be
		 * retrieved.
		 * 
		 * subquerySelectors contains all subqueries that point to child
		 * elements and attributes of element selected by
		 * queryAgainstGlobalScheme!
		 * 
		 * --> hence, we must retrieve all information to create an IvisObject
		 * that comprises all subquery-properties
		 */

		Map<String, String> subquerySelectors_global_and_localSchema = transformIntoGlobalAndLocalSubqueries(
				queryAgainstGlobalSchema, subquerySelectors_globalSchema);

		String localQuery = (String) this.transformToLocalQuery(queryAgainstGlobalSchema);

		return this.executeLocalQuery(localQuery, subquerySelectors_global_and_localSchema, queryAgainstGlobalSchema);
	}

	@Override
	protected Map<String, String> transformIntoGlobalAndLocalSubqueries(IvisQuery globalQuery,
			List<String> subquerySelectors_globalSchema) {
		String selector_globalSchema = globalQuery.getSelector();
		String selector_localSchema = this.getSchemaMapping().get(selector_globalSchema);

		Map<String, String> subqueries_global_and_local_schema = new HashMap<String, String>();

		for (String subquery_globalSchema : subquerySelectors_globalSchema) {
			String subquery_localSchema = this.getSchemaMapping().get(subquery_globalSchema);

			/*
			 * now cut off the part that is equal with the selector_localSchema
			 */
			subquery_localSchema = removeSelector_localSchema(selector_localSchema, subquery_localSchema);

			subqueries_global_and_local_schema.put(subquery_globalSchema, subquery_localSchema);
		}

		return subqueries_global_and_local_schema;
	}

	private String removeSelector_localSchema(String selector_localSchema, String propertySelector) {
		if (propertySelector.contains(selector_localSchema)) {
			String[] subqueryElements = propertySelector.split(selector_localSchema);

			propertySelector = subqueryElements[subqueryElements.length - 1];

			/*
			 * eliminate a leading '/'
			 */
			if (propertySelector.startsWith("/"))
				propertySelector = propertySelector.substring(1);
		}
		return propertySelector;
	}

	private List<IvisObject> retrieveDataFromXml(String localQuery,
			Map<String, String> subquerySelectors_global_and_local_schema, IvisQuery globalQuery)
			throws DocumentException {

		List<IvisObject> ivisObjects = new ArrayList<IvisObject>();

		/*
		 * parse document
		 */
		SAXReader reader = new SAXReader();
		Document document = reader.read(this.getSourceFile());

		/*
		 * find all matching nodes
		 */
		List<Node> selectedNodes = document.selectNodes(localQuery);

		/*
		 * now execute all subqueries for each element and create IvisObject
		 */

		String elementName = this.getNameFromXPathExpression(globalQuery.getSelector());

		for (Node node : selectedNodes) {
			ivisObjects.add(this.createIvisObject(node, subquerySelectors_global_and_local_schema, elementName));
		}

		return ivisObjects;

	}

	/**
	 * create IvisObject
	 * 
	 * @param node
	 *            the node containing all information
	 * @param subquerySelectors_global_and_local_schema
	 *            the subqueries that are used to retrieve the information from
	 *            the node
	 * @param elementName
	 *            the name of the object
	 * @return new instance of {@link IvisObject}
	 */
	private IvisObject createIvisObject(Node node, Map<String, String> subquerySelectors_global_and_local_schema,
			String elementName) {

		List<AttributeValuePair> attributeValuePairs = new ArrayList<AttributeValuePair>(
				subquerySelectors_global_and_local_schema.size());

		Iterator<Entry<String, String>> subqueryIterator = subquerySelectors_global_and_local_schema.entrySet()
				.iterator();

		while (subqueryIterator.hasNext()) {
			Entry<String, String> subqueryEntry = subqueryIterator.next();

			String name = getNameFromXPathExpression(subqueryEntry.getKey());

			Object value = node.selectSingleNode(subqueryEntry.getValue()).getText();

			attributeValuePairs.add(new AttributeValuePair(name, value));
		}

		addWrapperReference(attributeValuePairs);

		IvisObject newIvisObject = new IvisObject(elementName, attributeValuePairs);

		return newIvisObject;

	}

	@Override
	protected Object transformToLocalQuery(IvisQuery globalQuery) {
		String selector_globalSchema = globalQuery.getSelector();

		String selector_localSchema = this.getSchemaMapping().get(selector_globalSchema);

		String xPathQuery = XPathQueryHelper.addFiltersToExpression(selector_localSchema, globalQuery.getFilters(),
				globalQuery.getFilterStrategy(), this.getSchemaMapping());

		return xPathQuery;
	}

	@Override
	protected List<IvisObject> executeLocalQuery(Object localQuery,
			Map<String, String> subquerySelectors_global_and_localSchema, IvisQuery globalQuery)
			throws DocumentException {
		// we know that in this class the localQuery is of type String!
		return this.retrieveDataFromXml((String) localQuery, subquerySelectors_global_and_localSchema, globalQuery);
	}

	@Override
	public IvisObject modifyDataInstance(RuntimeModificationMessage modificationMessage,
			List<String> subquerySelectors_globalSchema) throws DocumentException, IOException {

		/*
		 * local query is an xPath expression! Hence, String
		 */
		String localQuery = (String) transformToLocalQuery(modificationMessage.getQuery());

		Map<String, String> subquerySelectors_global_and_localSchema = transformIntoGlobalAndLocalSubqueries(
				modificationMessage.getQuery(), subquerySelectors_globalSchema);

		IvisObject modifiedIvisObject = executeDataInstanceModification(modificationMessage, localQuery,
				subquerySelectors_global_and_localSchema);

		return modifiedIvisObject;
	}

	private IvisObject executeDataInstanceModification(RuntimeModificationMessage modificationMessage,
			String localQuery, Map<String, String> subquerySelectors_global_and_localSchema)
			throws DocumentException, UnsupportedEncodingException, FileNotFoundException, IOException {
		IvisObject modifiedIvisObject = null;

		String selector_localSchema = this.getSchemaMapping().get(modificationMessage.getQuery().getSelector());

		String propertySelector_globalSchema = modificationMessage.getPropertySelector_globalSchema();
		String propertySelector_localSchema = this.getSchemaMapping().get(propertySelector_globalSchema);
		propertySelector_localSchema = removeSelector_localSchema(selector_localSchema, propertySelector_localSchema);

		String newPropertyValue = String.valueOf(modificationMessage.getNewPropertyValue());

		SAXReader reader = new SAXReader();
		Document document = reader.read(this.getSourceFile());

		// target object instance
		Node node = document.selectSingleNode(localQuery);

		// target property
		node.selectSingleNode(propertySelector_localSchema).setText(newPropertyValue);

		String elementName = this.getNameFromXPathExpression(modificationMessage.getQuery().getSelector());
		modifiedIvisObject = this.createIvisObject(node, subquerySelectors_global_and_localSchema, elementName);

		// Pretty print the document to System.out
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer;
		writer = new XMLWriter(new FileOutputStream(this.getSourceFile()), format);
		writer.write(document);
		return modifiedIvisObject;
	}

}
