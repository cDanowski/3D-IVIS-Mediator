package mediator_wrapper.wrapper.abstract_types;

public abstract class AbstractIvisWrapper {

	/**
	 * Transforms the global query to an equivalent query against the wrapper's
	 * local schema.
	 * 
	 * @param globalQuery
	 *            a query against the global schema
	 * @return query against the wrapper's local schema
	 */
	public abstract Object transformToLocalQuery(Object globalQuery);

	/**
	 * Performs the query against the wrapper's data source.
	 * 
	 * @param localQuery
	 * @return queried result objects from the wrapper's data source
	 */
	public abstract Object executeLocalQuery(Object localQuery);

}
