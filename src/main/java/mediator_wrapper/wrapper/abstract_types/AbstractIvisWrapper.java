package mediator_wrapper.wrapper.abstract_types;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import ivisObject.AttributeValuePair;
import ivisObject.IvisObject;
import ivisQuery.IvisQuery;

public abstract class AbstractIvisWrapper {

	protected static final String WRAPPER_REFERENCE = "wrapperReference";

	private static final String XPATH_EXPRESSION_MAPPING_ELEMENT = "//mapping";
	private static final String XPATH_EXPRESSION_SELECTOR_GLOBAL_SCHEMA_ELEMENT = "selector_globalSchema";
	private static final String XPATH_EXPRESSION_SELECTOR_LOCAL_SCHEMA_ELEMENT = "selector_localSchema";
	private static final String XPATH_EXPRESSION_ID_MAPPING_ELEMENT = "//id-mapping";

	private Map<String, String> schemaMapping;

	private IdProperty idProperty;

	public Map<String, String> getSchemaMapping() {
		return schemaMapping;
	}

	public IdProperty getIdProperty() {
		return idProperty;
	}

	/**
	 * extracts the name of the given XPath selector.
	 * 
	 * @param xPathSelector
	 *            an XPath selector
	 * @return the name. this will usually be the last element of the XPath
	 *         expression (e.g. from 'item1/item2' the name will be 'item2')
	 */
	protected String getNameFromXPathExpression(String xPathSelector) {
		if (xPathSelector.contains("/")) {
			String[] elements = xPathSelector.split("/");

			String name = elements[elements.length - 1];

			/*
			 * in case of an attribute there is a leading '@', which should be
			 * removed
			 */
			if (name.startsWith("@"))
				name = name.substring(1);

			return name;
		} else
			return xPathSelector;
	}

	/**
	 * instantiates the schemaMapping map by parsing the mapping file and adding
	 * a pair of String (selector of global schema, selector of local schema)
	 * for each mapping
	 * 
	 * @param pathToSchemaMappingFile
	 * @throws DocumentException
	 */
	protected void instantiateSchemaMapping(String pathToSchemaMappingFile) throws DocumentException {
		/*
		 * parse document
		 */
		File inputFile = new File(pathToSchemaMappingFile);
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputFile);

		/*
		 * new instance of schemaMapping
		 */
		this.schemaMapping = new HashMap<String, String>();

		/*
		 * find all mappings
		 */
		List<Node> mappingNodes = document.selectNodes(XPATH_EXPRESSION_MAPPING_ELEMENT);

		for (Node node : mappingNodes) {
			/*
			 * extract selector as key
			 */
			String selector_globalSchema = node.selectSingleNode(XPATH_EXPRESSION_SELECTOR_GLOBAL_SCHEMA_ELEMENT)
					.getText();

			/*
			 * create a list of all wrapper instances that offer data for
			 * selector
			 */
			String selector_localSchema = node.selectSingleNode(XPATH_EXPRESSION_SELECTOR_LOCAL_SCHEMA_ELEMENT)
					.getText();

			/*
			 * add mapping to wrapperMapping map
			 */
			this.schemaMapping.put(selector_globalSchema, selector_localSchema);
		}

		/*
		 * if property
		 */
		Node idMappingNode = document.selectSingleNode(XPATH_EXPRESSION_ID_MAPPING_ELEMENT);

		String id_selector_globalSchema = idMappingNode
				.selectSingleNode(XPATH_EXPRESSION_SELECTOR_GLOBAL_SCHEMA_ELEMENT).getText();

		String id_selector_localSchema = idMappingNode.selectSingleNode(XPATH_EXPRESSION_SELECTOR_LOCAL_SCHEMA_ELEMENT)
				.getText();


		/*
		 * new instance of idMapping
		 */
		this.idProperty = new IdProperty(id_selector_globalSchema, id_selector_localSchema);

	}

	/**
	 * Transforms the global query to an equivalent query against the wrapper's
	 * local schema.
	 * 
	 * @param globalQuery
	 *            a query against the global schema
	 * @return query against the wrapper's local schema
	 */
	protected abstract Object transformToLocalQuery(IvisQuery globalQuery);

	/**
	 * Transform global subqueries into local subqueries (against the local
	 * schema).
	 * 
	 * @param globalQuery
	 *            the global query object
	 * @param subquerySelectors_globalSchema
	 *            subqueries against the global schema, which shall be
	 *            transformed into equivalent subqueries against the local
	 *            schema
	 * @return equivalent subqueries against the local schema in a map with the
	 *         global equivalents as key
	 */
	protected abstract Map<String, String> transformIntoGlobalAndLocalSubqueries(IvisQuery globalQuery,
			List<String> subquerySelectors_globalSchema);

	/**
	 * Performs the query against the wrapper's data source.
	 * 
	 * @param localQuery
	 *            the localQuery that was transformed from the globalSchema
	 * @param subquerySelectors_global_and_local_schema
	 *            subqueries that are necessary to retrieve data from a
	 *            requested parent element (keys of map are the gloabl
	 *            equivalents)
	 * @param globalQuery
	 *            the initial global query
	 * @return queried result objects from the wrapper's data source
	 * @throws Exception
	 */
	protected abstract List<IvisObject> executeLocalQuery(Object localQuery,
			Map<String, String> subquerySelectors_global_and_local_schema, IvisQuery globalQuery) throws Exception;

	/**
	 * add a reference to the name of the wrapper to the list of attributeValue
	 * pairs
	 * 
	 * @param attributeValuePairs
	 */
	protected void addWrapperReference(List<AttributeValuePair> attributeValuePairs) {
		/*
		 * add an attribute to identify the wrapper!
		 */
		attributeValuePairs.add(new AttributeValuePair(WRAPPER_REFERENCE, this.getClass().getSimpleName().toString()));
	}

}
