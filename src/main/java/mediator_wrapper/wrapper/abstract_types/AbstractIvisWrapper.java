package mediator_wrapper.wrapper.abstract_types;

public abstract class AbstractIvisWrapper {

	/*
	 * the local schema of the wrapper
	 */
	protected Object localSchema;

	/*
	 * object that holds the maping between entries of the global schema and
	 * entries of the wrapper's local schema
	 */
	protected Object schemaMapping;

	public Object getLocalSchema() {
		return localSchema;
	}

	public void setLocalSchema(Object localSchema) {
		this.localSchema = localSchema;
	}

	public Object getSchemaMapping() {
		return schemaMapping;
	}

	public void setSchemaMapping(Object schemaMapping) {
		this.schemaMapping = schemaMapping;
	}

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
