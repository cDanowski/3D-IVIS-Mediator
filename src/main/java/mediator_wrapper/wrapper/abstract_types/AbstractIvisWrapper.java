package mediator_wrapper.wrapper.abstract_types;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import ivisObject.AttributeValuePair;
import ivisObject.IvisObject;
import ivisQuery.FilterStrategy;
import ivisQuery.FilterType;
import ivisQuery.IvisFilterForQuery;
import ivisQuery.IvisQuery;

public abstract class AbstractIvisWrapper {

	protected static final String WRAPPER_REFERENCE = "wrapperReference";

	private static final String XPATH_EXPRESSION_MAPPING_ELEMENT = "//mapping";
	private static final String XPATH_EXPRESSION_SELECTOR_GLOBAL_SCHEMA_ELEMENT = "selector_globalSchema";
	private static final String XPATH_EXPRESSION_SELECTOR_LOCAL_SCHEMA_ELEMENT = "selector_localSchema";
	private static final String XPATH_EXPRESSION_ID_MAPPING_ELEMENT = "//id-mapping";
	private static final String XPATH_EXPRESSION_DEFAULT_QUERY_ELEMENT = "//default-query";
	

	private Map<String, String> schemaMapping;

	private IdProperty idProperty;

	private DefaultQuery defaultQuery;

	public Map<String, String> getSchemaMapping() {
		return schemaMapping;
	}

	public IdProperty getIdProperty() {
		return idProperty;
	}

	public DefaultQuery getDefaultQuery() {
		return defaultQuery;
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
	protected void instantiateProperties(String pathToSchemaMappingFile) throws DocumentException {
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
		 * id property
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
		
		/*
		 * default query
		 */
		Node defaultQueryNode = document.selectSingleNode(XPATH_EXPRESSION_DEFAULT_QUERY_ELEMENT);

		String defaultQuery_selector_globalSchema = defaultQueryNode
				.selectSingleNode(XPATH_EXPRESSION_SELECTOR_GLOBAL_SCHEMA_ELEMENT).getText();

		String defaultQuery_selector_localSchema = defaultQueryNode.selectSingleNode(XPATH_EXPRESSION_SELECTOR_LOCAL_SCHEMA_ELEMENT)
				.getText();


		/*
		 * new instance of idMapping
		 */
		this.defaultQuery = new DefaultQuery(defaultQuery_selector_globalSchema, defaultQuery_selector_localSchema);
	}
	
	protected boolean passesClientFilters(IvisObject modifiedRecord, IvisQuery query_localSchema, IvisQuery query_globalSchema) {
		/*
		 * 2. now check if that modified object is actually visualized by the
		 * requesting client
		 * 
		 * that means check the original filter definitions of the original
		 * query_localSchema
		 * 
		 * if NO FILTERS are defined, then he visualizes everything, including
		 * modifiedRecord
		 * 
		 * else if defined filters include modifiedRecord, then he also
		 * visualizes it
		 */

		List<IvisFilterForQuery> filters_globalSchema = query_globalSchema.getFilters();

		List<IvisFilterForQuery> filters_localSchema = query_localSchema.getFilters();
		if (filters_localSchema == null || filters_localSchema.size() == 0)
			return true;

		else {
			boolean passesFilters = false;

			FilterStrategy filterStrategy = query_localSchema.getFilterStrategy();
			if(filterStrategy == null)
				filterStrategy = FilterStrategy.AND;

			for (int i = 0; i < filters_localSchema.size(); i++) {

				IvisFilterForQuery ivisFilter_localSchema = filters_localSchema.get(i);
				IvisFilterForQuery ivisFilter_globalSchema = filters_globalSchema.get(i);
				String propertySelector_globalSchema = ivisFilter_globalSchema.getSelector();

				if (this.passesFilter(modifiedRecord, ivisFilter_localSchema, propertySelector_globalSchema)) {
					passesFilters = true;
					/*
					 * if filter strategy is set to OR, then just one filter
					 * must be passed.
					 * 
					 * Hence, we can skip other filters and return true!
					 */
					if (filterStrategy.equals(FilterStrategy.OR))
						break;

				} else {
					passesFilters = false;

					/*
					 * if filter strategy is set to AND, then if one filter
					 * fails, we can return false, since every filter must be
					 * passed, which is not the case
					 * 
					 * Hence, we can skip other filters and return false!
					 */
					if (filterStrategy.equals(FilterStrategy.AND))
						break;
				}
			}

			return passesFilters;
		}
	}
	
	protected boolean passesFilter(IvisObject modifiedRecord, IvisFilterForQuery ivisFilter,
			String propertySelector_globalSchema) {

		FilterType filterType = ivisFilter.getFilterType();
		
		Object filterValue = ivisFilter.getFilterValue();

		String objectValue = getObjectValueForSelector(modifiedRecord, propertySelector_globalSchema);

		if (this.passesFilter(objectValue, filterValue, filterType))
			return true;

		return false;
	}

	protected String getObjectValueForSelector(IvisObject modifiedRecord, String propertySelector_globalSchema) {

		/*
		 * propertySelector_globalSchema is a complete XPath expression
		 * 
		 * here, we just need the final expression (after the last "/") without
		 * any "@" prefix.
		 */
		String propertyName = this.getNameFromXPathExpression(propertySelector_globalSchema);
		String valueForAttribute = String.valueOf(modifiedRecord.getValueForAttribute(propertyName));

		return valueForAttribute;
	}
	
	protected String createIdString(String idValue) {
		String idProperty = this.getNameFromXPathExpression(this.getIdProperty().getSelector_localSchema());
		return "id=" + idValue;
	}
	
	protected boolean passesFilter(String objectValue, Object filterValue, FilterType filterType) {
		boolean passesFilter = false;

		switch (filterType) {
		case EQUAL:
			if (objectValue.equalsIgnoreCase(String.valueOf(filterValue)))
				passesFilter = true;
			break;
		case GREATER_THAN:
			try {
				double numericObjectValue = Double.parseDouble(objectValue);
				double numericFilterValue = Double.parseDouble(String.valueOf(filterValue));
				if (numericObjectValue > numericFilterValue)
					passesFilter = true;
			} catch (Exception e) {
				e.printStackTrace();
				/*
				 * do nothing, passesFilter remains false
				 */
			}
			break;
		case GREATER_THAN_OR_EQUAL_TO:
			try {
				double numericObjectValue = Double.parseDouble(objectValue);
				double numericFilterValue = Double.parseDouble(String.valueOf(filterValue));
				if (numericObjectValue >= numericFilterValue)
					passesFilter = true;
			} catch (Exception e) {
				e.printStackTrace();
				/*
				 * do nothing, passesFilter remains false
				 */
			}
			break;
		case LESS_THAN:
			try {
				double numericObjectValue = Double.parseDouble(objectValue);
				double numericFilterValue = Double.parseDouble(String.valueOf(filterValue));
				if (numericObjectValue < numericFilterValue)
					passesFilter = true;
			} catch (Exception e) {
				e.printStackTrace();
				/*
				 * do nothing, passesFilter remains false
				 */
			}
			break;
		case LESS_THAN_OR_EQUAL_TO:
			try {
				double numericObjectValue = Double.parseDouble(objectValue);
				double numericFilterValue = Double.parseDouble(String.valueOf(filterValue));
				if (numericObjectValue <= numericFilterValue)
					passesFilter = true;
			} catch (Exception e) {
				e.printStackTrace();
				/*
				 * do nothing, passesFilter remains false
				 */
			}
			break;
		case NOT_EQUAL:
			if (!objectValue.equalsIgnoreCase(String.valueOf(filterValue)))
				passesFilter = true;
			break;

		default:
			break;
		}

		return passesFilter;
	}
	
	protected Object createQueryForModifiedRecords(List<String> recordIds, IvisQuery query_localSchema) {
		IvisQuery query_modifiedRecords = new IvisQuery();
		query_modifiedRecords.setSelector(query_localSchema.getSelector());
		
		List<IvisFilterForQuery> filters = new ArrayList<IvisFilterForQuery>();
		String idProperty = this.getIdProperty().getSelector_localSchema();
		
		for (String recordId : recordIds) {
			/*
			 * add one filter: where id=recordId
			 */
			IvisFilterForQuery idFilter = new IvisFilterForQuery();
			
			idFilter.setSelector(idProperty);
			/*
			 * recordId looks like "id=5"
			 * 
			 * hence, we have to split string by "=" and use second value
			 */
			idFilter.setFilterValue(recordId.split("=")[1]);
			idFilter.setFilterType(FilterType.EQUAL);
			filters.add(idFilter);
		}

		query_modifiedRecords.setFilters(filters);
		
		/*
		 * set strategy to OR, since we have to extract all new items
		 */
		query_modifiedRecords.setFilterStrategy(FilterStrategy.OR);
		return query_modifiedRecords;
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
