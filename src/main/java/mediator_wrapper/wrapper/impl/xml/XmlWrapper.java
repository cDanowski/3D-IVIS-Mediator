package mediator_wrapper.wrapper.impl.xml;

import java.io.File;
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

	public XmlWrapper(String pathToSourcefile, String pathToShadowCopyFile, String pathToSchemaMappingFile)
			throws DocumentException {
		super(pathToSourcefile, pathToShadowCopyFile, pathToSchemaMappingFile);

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
	public boolean modifyDataInstance(RuntimeModificationMessage modificationMessage,
			List<String> subquerySelectors_globalSchema) throws DocumentException, IOException {

		boolean hasModified = false;

		/*
		 * local query is an xPath expression! Hence, String
		 */
		String localQuery = (String) transformToLocalQuery(modificationMessage.getQuery());

		Map<String, String> subquerySelectors_global_and_localSchema = transformIntoGlobalAndLocalSubqueries(
				modificationMessage.getQuery(), subquerySelectors_globalSchema);

		hasModified = executeDataInstanceModification(modificationMessage, localQuery,
				subquerySelectors_global_and_localSchema);

		return hasModified;
	}

	private boolean executeDataInstanceModification(RuntimeModificationMessage modificationMessage, String localQuery,
			Map<String, String> subquerySelectors_global_and_localSchema)
			throws DocumentException, UnsupportedEncodingException, FileNotFoundException, IOException {

		boolean hasModified = false;

		try {
			String selector_localSchema = this.getSchemaMapping().get(modificationMessage.getQuery().getSelector());

			String propertySelector_globalSchema = modificationMessage.getPropertySelector_globalSchema();
			String propertySelector_localSchema = this.getSchemaMapping().get(propertySelector_globalSchema);
			propertySelector_localSchema = removeSelector_localSchema(selector_localSchema,
					propertySelector_localSchema);

			String newPropertyValue = String.valueOf(modificationMessage.getNewPropertyValue());

			SAXReader reader = new SAXReader();
			Document document = reader.read(this.getSourceFile());

			// target object instance
			Node node = document.selectSingleNode(localQuery);

			// target property
			node.selectSingleNode(propertySelector_localSchema).setText(newPropertyValue);

			// Pretty print the document to System.out
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer;
			writer = new XMLWriter(new FileOutputStream(this.getSourceFile()), format);
			writer.write(document);

			hasModified = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hasModified;
	}

	@Override
	public List<IvisObject> onSourceFileChanged(IvisQuery query_globalSchema,
			List<String> subquerySelectors_globalSchema)
			throws UnsupportedEncodingException, FileNotFoundException, IOException, DocumentException {

		// we know it is an XPath String for XML files!
		String query_localSchema = (String) this.transformToLocalQuery(query_globalSchema);

		Map<String, String> subqueries_global_and_local_schema = this
				.transformIntoGlobalAndLocalSubqueries(query_globalSchema, subquerySelectors_globalSchema);

		String elementName = this.getNameFromXPathExpression(query_globalSchema.getSelector());

		/*
		 * parse the whole file and compare its contents to shadow copy file
		 * contents
		 */

		File sourceFile = this.getSourceFile();
		File shadowCopyFile = this.getShadowCopyFile();

		List<Node> selectedNodes_sourceFile = retrieveAllNodesForQuery(query_localSchema, sourceFile);

		/*
		 * shadow copy contains all elements BEFORE the modification happened!
		 * 
		 * hence, we can compare allRecords against the shadowCopyRecords to
		 * identify modifications
		 */
		List<Node> selectedNodes_shadowCopy = retrieveAllNodesForQuery(query_localSchema, shadowCopyFile);

		List<IvisObject> modifiedInstances = identifyModifiedOrNewInstances(selectedNodes_sourceFile,
				selectedNodes_shadowCopy, subqueries_global_and_local_schema, elementName);

		return modifiedInstances;
	}

	private List<IvisObject> identifyModifiedOrNewInstances(List<Node> selectedNodes_sourceFile,
			List<Node> selectedNodes_shadowCopy, Map<String, String> subqueries_global_and_local_schema,
			String elementName) throws IOException {

		List<IvisObject> modifiedObjects = new ArrayList<IvisObject>();

		String idProperty = "@id";

		Map<String, Node> idForXmlRecordMap = createIdForXmlRecordMap(selectedNodes_shadowCopy, idProperty);

		for (Node node_sourceFile : selectedNodes_sourceFile) {
			String recordId = node_sourceFile.selectSingleNode(idProperty).getText();

			if (!idForXmlRecordMap.containsKey(recordId)) {
				/*
				 * new object
				 */
				modifiedObjects.add(createIvisObject(node_sourceFile, subqueries_global_and_local_schema, elementName));
			}

			else {
				// compare to shadow copy

				Node shadowCopy = idForXmlRecordMap.get(recordId);

				if (hasModifiedProperties(node_sourceFile, shadowCopy, subqueries_global_and_local_schema)) {
					/*
					 * modified object
					 */
					modifiedObjects
							.add(createIvisObject(node_sourceFile, subqueries_global_and_local_schema, elementName));
				}
			}
		}

		// replace shadow copy file
		this.replaceShadowCopy();

		return modifiedObjects;

	}

	private boolean hasModifiedProperties(Node node_sourceFile, Node shadowCopy,
			Map<String, String> subqueries_global_and_local_schema) {

		Iterator<Entry<String, String>> subqueryIterator = subqueries_global_and_local_schema.entrySet().iterator();

		while (subqueryIterator.hasNext()) {
			Entry<String, String> subqueryEntry = subqueryIterator.next();
			String subquery_localSchema = subqueryEntry.getValue();

			String value_sourceFile = node_sourceFile.selectSingleNode(subquery_localSchema).getText();
			String value_shadowCopy = shadowCopy.selectSingleNode(subquery_localSchema).getText();

			if (!value_shadowCopy.equals(value_sourceFile))
				return true;
		}

		return false;
	}

	private Map<String, Node> createIdForXmlRecordMap(List<Node> selectedNodes_shadowCopy, String idProperty) {
		Map<String, Node> idForXmlRecordMap = new HashMap<>();

		for (Node node : selectedNodes_shadowCopy) {
			String idKey = node.selectSingleNode(idProperty).getText();

			idForXmlRecordMap.put(idKey, node);
		}

		return idForXmlRecordMap;
	}

	private List<Node> retrieveAllNodesForQuery(String query_localSchema, File sourceFile) throws DocumentException {
		/*
		 * parse document
		 */
		SAXReader reader = new SAXReader();

		Document document = reader.read(sourceFile);

		/*
		 * find all matching nodes
		 */
		List<Node> selectedNodes = document.selectNodes(query_localSchema);
		return selectedNodes;
	}

}
