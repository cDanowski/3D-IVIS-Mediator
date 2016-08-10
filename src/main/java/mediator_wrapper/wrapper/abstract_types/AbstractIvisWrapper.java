package mediator_wrapper.wrapper.abstract_types;

import java.util.List;
import java.util.Map;

import ivisObject.AttributeValuePair;
import ivisObject.IvisObject;
import ivisQuery.IvisQuery;

public abstract class AbstractIvisWrapper {

	protected static final String WRAPPER_REFERENCE = "wrapperReference";

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
