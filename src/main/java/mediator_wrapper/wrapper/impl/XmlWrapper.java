package mediator_wrapper.wrapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

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
		 * TODO
		 * 
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

		List<String> subquerySelectors_localSchema = transformIntoLocalSubqueries(queryAgainstGlobalSchema,
				subquerySelectors_globalSchema);

		String localQuery = (String) this.transformToLocalQuery(queryAgainstGlobalSchema);

		return this.executeLocalQuery(localQuery, subquerySelectors_localSchema, queryAgainstGlobalSchema);
	}

	@Override
	protected List<String> transformIntoLocalSubqueries(IvisQuery globalQuery,
			List<String> subquerySelectors_globalSchema) {
		String selector_globalSchema = globalQuery.getSelector();
		String selector_localSchema = this.getSchemaMapping().get(selector_globalSchema);

		List<String> subqueries_localSchema = new ArrayList<String>(subquerySelectors_globalSchema.size());

		for (String subquery_globalSchema : subquerySelectors_globalSchema) {
			String subquery_localSchema = this.getSchemaMapping().get(subquery_globalSchema);

			/*
			 * now cut off the part that is equal with the selector_localSchema
			 */
			if (subquery_localSchema.contains(selector_localSchema)) {
				String[] subqueryElements = subquery_localSchema.split(selector_localSchema);

				subquery_localSchema = subqueryElements[subqueryElements.length - 1];

				/*
				 * eliminate a leading '/'
				 */
				if (subquery_localSchema.startsWith("/"))
					subquery_localSchema = subquery_localSchema.substring(1);
			}

			subqueries_localSchema.add(subquery_localSchema);
		}

		return subqueries_localSchema;
	}

	private List<IvisObject> retrieveDataFromXml(String localQuery, List<String> subquerySelectors,
			IvisQuery globalQuery) throws DocumentException {

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

		String elementName = this.getName(globalQuery.getSelector());

		for (Node node : selectedNodes) {
			ivisObjects.add(this.createIvisObject(node, subquerySelectors, elementName));
		}

		return ivisObjects;

	}

	private String getName(String selector) {
		if (selector.contains("/")) {
			String[] elements = selector.split("/");

			String name = elements[elements.length - 1];

			/*
			 * in case of an attribute there is a leading '@', which should be
			 * removed
			 */
			if (name.startsWith("@"))
				name = name.substring(1);

			return name;
		} else
			return selector;
	}

	private IvisObject createIvisObject(Node node, List<String> subquerySelectors, String elementName) {

		List<AttributeValuePair> attributeValuePairs = new ArrayList<AttributeValuePair>(subquerySelectors.size());

		for (String subquerySelector : subquerySelectors) {
			String name = getName(subquerySelector);

			Object value = node.selectSingleNode(subquerySelector).getText();

			attributeValuePairs.add(new AttributeValuePair(name, value));
		}

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
	protected List<IvisObject> executeLocalQuery(Object localQuery, List<String> subquerySelectors_localSchema,
			IvisQuery globalQuery) throws DocumentException {
		// we know that in this class the localQuery is of type String!
		return this.retrieveDataFromXml((String) localQuery, subquerySelectors_localSchema, globalQuery);
	}

	@Override
	public Object applyModification(RuntimeModificationMessage modificationMessage) {
		// TODO Auto-generated method stub
		return null;
	}

}
