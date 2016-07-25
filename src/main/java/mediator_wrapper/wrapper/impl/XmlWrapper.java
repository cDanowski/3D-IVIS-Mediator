package mediator_wrapper.wrapper.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import controller.runtime.modify.RuntimeModificationMessage;
import ivisObject.AttributeValuePair;
import ivisObject.IvisObject;
import ivisQuery.FilterStrategy;
import ivisQuery.IvisFilterForQuery;
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

		String selector_globalSchema = queryAgainstGlobalSchema.getSelector();

		String selector_localSchema = this.getSchemaMapping().get(selector_globalSchema);
		
		List<String> subquerySelectors_localSchema = transformIntoLocalQueries(selector_localSchema, subquerySelectors_globalSchema);

		/*
		 * now for each element that matches the selector of the local schema,
		 * execute all subqueries and instantiate a new IvisObject!!!
		 */

		return this.retrieveDataFromXml(selector_localSchema, selector_globalSchema,
				queryAgainstGlobalSchema.getFilters(), queryAgainstGlobalSchema.getFilterStrategy(), 
				subquerySelectors_localSchema);
	}

	private List<String> transformIntoLocalQueries(String selector_localSchema, List<String> subquerySelectors_globalSchema) {
		List<String> subqueries_localSchema = new ArrayList<String>(subquerySelectors_globalSchema.size());
		
		for (String subquery_globalSchema : subquerySelectors_globalSchema) {
			String subquery_localSchema = this.getSchemaMapping().get(subquery_globalSchema);
			
			/*
			 * now cut off the part that is equal with the selector_localSchema
			 */
			if(subquery_localSchema.contains(selector_localSchema)){
				String[] subqueryElements = subquery_localSchema.split(selector_localSchema);
				
				subquery_localSchema = subqueryElements[subqueryElements.length - 1];
				
				/*
				 * eliminate a leading '/'
				 */
				if(subquery_localSchema.startsWith("/"))
					subquery_localSchema = subquery_localSchema.substring(1);
			}
				
			
			subqueries_localSchema.add(subquery_localSchema);
		}
		
		return subqueries_localSchema;
	}

	private List<IvisObject> retrieveDataFromXml(String selector_localSchema, String selector_globalSchema,
			List<IvisFilterForQuery> filters, FilterStrategy filterStrategy, List<String> subquerySelectors)
			throws DocumentException {

		List<IvisObject> ivisObjects = new ArrayList<IvisObject>();

		String xPathQuery = XPathQueryHelper.addFiltersToExpression(selector_localSchema, filters, filterStrategy,
				this.getSchemaMapping());

		/*
		 * parse document
		 */
		SAXReader reader = new SAXReader();
		Document document = reader.read(this.getSourceFile());

		/*
		 * find all matching nodes
		 */
		List<Node> selectedNodes = document.selectNodes(xPathQuery);

		/*
		 * now execute all subqueries for each element and create IvisObject
		 */

		String elementName = this.getName(selector_globalSchema);

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
	public Object applyModification(RuntimeModificationMessage modificationMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object transformToLocalQuery(Object globalQuery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object executeLocalQuery(Object localQuery) {
		// TODO Auto-generated method stub
		return null;
	}

}
