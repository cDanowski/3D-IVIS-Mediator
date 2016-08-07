package mediator_wrapper.wrapper.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ivisQuery.FilterStrategy;
import ivisQuery.FilterType;
import ivisQuery.IvisFilterForQuery;

/**
 * Helper class for creating/modifying XPath expressions.
 * 
 * @author Christian Danowski
 *
 */
public class XPathQueryHelper {

	/**
	 * here we query an XML document using XPath. Thus, property
	 * selector_localSchema is the generic XPath expression identifying the
	 * nodes that are of interest.
	 * 
	 * This method applies filters to the XPath expression to allow filtering of
	 * the queried nodes. Hence it modifies the generic XPath expression and add
	 * filters using XPath syntax.
	 * 
	 * @param selector_localSchema
	 *            the generic XPath expression that points to certain nodes of
	 *            an XML document
	 * @param filters
	 *            list of filters that shall be applied to the above XPath
	 *            expression
	 * @param filterStrategy how to combine multiple filters
	 * @param schemaMapping
	 *            contains mapping between global and local schema
	 * @return a new XPath expression containing the specified filters
	 */
	public static String addFiltersToExpression(String selector_localSchema, List<IvisFilterForQuery> filters,
			FilterStrategy filterStrategy, Map<String, String> schemaMapping) {
		/*
		 * TODO modify generic XPath expression and add filters where applicable
		 * 
		 */

		String newXPathExpression = selector_localSchema;

		if (filters != null && filters.size() > 0){
			String filterStatement = createCompleteFilterExpression(selector_localSchema, filters, filterStrategy, schemaMapping);
		
			/*
			 * identify where to add the filter
			 */
			newXPathExpression = integrateFilterIntoExpression(newXPathExpression, filterStatement);
		}

		return newXPathExpression;
	}

	private static String integrateFilterIntoExpression(String newXPathExpression, String filterStatement) {

		/**
		 * append filter statement to expression
		 */
		return newXPathExpression + filterStatement;
		
	}

	private static String createCompleteFilterExpression(String selector_localSchema, List<IvisFilterForQuery> filters,
			FilterStrategy filterStrategy, Map<String, String> schemaMapping) {
		/*
		 * for each filter
		 * 
		 * create corresponding XPath expression
		 * 
		 * then combine all filter expressions using defined strategy
		 */
		List<String> filterExpressions = new ArrayList<String>();
		
		for (IvisFilterForQuery filter : filters) {
			filterExpressions.add(createFilterExpression(selector_localSchema, filter, schemaMapping));
		}
		
		return buildCompleteFilterExpression(filterExpressions, filterStrategy);
	}

	private static String buildCompleteFilterExpression(List<String> filterExpressions, FilterStrategy filterStrategy) {
		StringBuilder stringBuilder = new StringBuilder();
		
		/*
		 * start filter
		 */
		stringBuilder.append("[");
		
		switch (filterStrategy) {
		case AND:
			for (int i=0; i<filterExpressions.size(); i++) {
				String singleFilterExpression = filterExpressions.get(i);
				stringBuilder.append(singleFilterExpression);
				
				if(i < filterExpressions.size() - 1)
					stringBuilder.append(" and ");
			}
			break;
		case OR:
			for (int i=0; i<filterExpressions.size(); i++) {
				String singleFilterExpression = filterExpressions.get(i);
				stringBuilder.append(singleFilterExpression);
				
				if(i < filterExpressions.size() - 1)
					stringBuilder.append(" or ");
			}
			break;

		default:
			break;
		}
		
		/*
		 * end filter
		 */
		stringBuilder.append("]");
		
		return stringBuilder.toString();
	}

//	private static String addFilterExpressionToXPathExpression(String selector_localSchema,
//			IvisFilterForQuery ivisFilterForQuery, Map<String, String> schemaMapping) {
//
//		String filterSelector_globalSchema = ivisFilterForQuery.getSelector();
//		String filterSelector_localSchema = schemaMapping.get(filterSelector_globalSchema);
//
//		/*
//		 * create the filter expression
//		 */
//		String filterExpression = createFilterExpression(selector_localSchema, ivisFilterForQuery,
//				filterSelector_localSchema);
//
//		/*
//		 * identify where to add the filter
//		 */
//		this.integrateFilterIntoExpression();
//
//		return newXPathExpression;
//
//	}

	private static String createFilterExpression(String selector_localSchema, IvisFilterForQuery ivisFilterForQuery, Map<String, String> schemaMapping) {
		Object filterValue = ivisFilterForQuery.getFilterValue();
		FilterType filterType = ivisFilterForQuery.getFilterType();
		String filterSelector_localSchema = schemaMapping.get(ivisFilterForQuery.getSelector());

		/*
		 * selector_localSchema points to a parent element (e.g.
		 * 'bookstore/book')
		 * 
		 * whereas
		 * 
		 * filterSelector_localSchema points to a child node or attribute! (e.g.
		 * 'bookstore/book/price/@currency')
		 * 
		 * hence, we can used this knowledge to cut off the equal part and
		 * achieve the filter expression
		 */
		if (filterSelector_localSchema.contains(selector_localSchema)) {
			String[] elements = filterSelector_localSchema.split(selector_localSchema);

			/*
			 * the last item should be the right filter selector for XPath
			 * expression
			 */
			String filterPropertyName = elements[elements.length - 1];

			if (filterPropertyName.startsWith("/"))
				filterPropertyName = filterPropertyName.substring(1);

			/*
			 * build expression depending on filterType
			 */
			return buildFilterExpression(filterPropertyName, filterValue, filterType);
		}

		else {
			/*
			 * error?!? return empty expression
			 */
			return "";
		}
	}

	private static String buildFilterExpression(String filterPropertyName, Object filterValue, FilterType filterType) {
		// expression could look like "node/@attribute='String'" or
		// "node/@attribute>1"

		String filterExpression = "";

		switch (filterType) {
		case EQUAL:
			if (filterValue instanceof Number)
				filterExpression = filterPropertyName + "=" + filterValue;
			else
				filterExpression = filterPropertyName + "=" + "'" + filterValue + "'";
			break;

		case GREATER_THAN:
			if (filterValue instanceof Number)
				filterExpression = filterPropertyName + ">" + filterValue;
			else
				filterExpression = filterPropertyName + ">" + "'" + filterValue + "'";
			break;

		case GREATER_THAN_OR_EQUAL_TO:
			if (filterValue instanceof Number)
				filterExpression = filterPropertyName + ">=" + filterValue;
			else
				filterExpression = filterPropertyName + ">=" + "'" + filterValue + "'";
			break;

		case LESS_THAN:
			if (filterValue instanceof Number)
				filterExpression = filterPropertyName + "<" + filterValue;
			else
				filterExpression = filterPropertyName + "<" + "'" + filterValue + "'";
			break;

		case LESS_THAN_OR_EQUAL_TO:
			if (filterValue instanceof Number)
				filterExpression = filterPropertyName + "<=" + filterValue;
			else
				filterExpression = filterPropertyName + "<=" + "'" + filterValue + "'";
			break;

		case NOT_EQUAL:
			if (filterValue instanceof Number)
				filterExpression = filterPropertyName + "!=" + filterValue;
			else
				filterExpression = filterPropertyName + "!=" + "'" + filterValue + "'";
			break;

		default:
			break;
		}
		return filterExpression;
	}

}
