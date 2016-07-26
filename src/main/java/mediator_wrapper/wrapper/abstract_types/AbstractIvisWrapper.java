package mediator_wrapper.wrapper.abstract_types;

import java.util.List;

import ivisObject.IvisObject;
import ivisQuery.IvisQuery;

public abstract class AbstractIvisWrapper {

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
	 * @return equivalent subqueries against the local schema
	 */
	protected abstract List<String> transformIntoLocalSubqueries(IvisQuery globalQuery,
			List<String> subquerySelectors_globalSchema);

	/**
	 * Performs the query against the wrapper's data source.
	 * 
	 * @param localQuery
	 *            the localQuery that was transformed from the globalSchema
	 * @param subquerySelectors_localSchema
	 *            subqueries that are necessary to retrieve data from a
	 *            requested arent element
	 * @param globalQuery
	 *            the initial global query
	 * @return queried result objects from the wrapper's data source
	 * @throws Exception
	 */
	protected abstract List<IvisObject> executeLocalQuery(Object localQuery, List<String> subquerySelectors_localSchema,
			IvisQuery globalQuery) throws Exception;

}
