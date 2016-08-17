package mediator_wrapper.wrapper.impl.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.DocumentException;

import controller.runtime.modify.RuntimeModificationMessage;
import ivisObject.AttributeValuePair;
import ivisObject.IvisObject;
import ivisQuery.IvisFilterForQuery;
import ivisQuery.IvisQuery;
import mediator_wrapper.wrapper.IvisWrapperInterface;
import mediator_wrapper.wrapper.abstract_types.AbstractIvisDataBaseWrapper;

/**
 * Wrapper that manages database access
 * 
 * @author Christian Danowski
 *
 */
public class DatabaseWrapper extends AbstractIvisDataBaseWrapper implements IvisWrapperInterface {

	/*
	 * TODO wenn ich von der Datenbank aus meine Anwendung erreichen will, so
	 * muss ich eine WebSocket Verbindung aufbauen! Dann Requests schicken an
	 * ".../iviaApp/Endpoint".
	 * 
	 * 
	 * 
	 * 
	 * @see mediator_wrapper.wrapper.IvisWrapperInterface#queryData(ivisQuery.
	 * IvisQuery, java.util.List)
	 */

	public DatabaseWrapper(String jdbc_driver, String db_url, String user, String password,
			String localSchemaMappingLocation) throws DocumentException {
		super(jdbc_driver, db_url, user, password, localSchemaMappingLocation);
	}

	@Override
	public List<IvisObject> queryData(IvisQuery queryAgainstGlobalSchema, List<String> subquerySelectors_globalSchema)
			throws Exception {
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

		IvisQuery localQuery = this.transformToLocalQuery(queryAgainstGlobalSchema);

		return this.executeLocalQuery(localQuery, subquerySelectors_global_and_localSchema, queryAgainstGlobalSchema);
	}

	@Override
	public boolean modifyDataInstance(RuntimeModificationMessage modificationMessage,
			List<String> subquerySelectors_globalSchema) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected IvisQuery transformToLocalQuery(IvisQuery globalQuery) {

		String selector_globalSchema = globalQuery.getSelector();

		String selector_localSchema = this.getSchemaMapping().get(selector_globalSchema);

		/*
		 * transform the selectors of filters to local schema
		 */
		List<IvisFilterForQuery> globalFilters = globalQuery.getFilters();

		List<IvisFilterForQuery> localFilters = new ArrayList<IvisFilterForQuery>();

		if (globalFilters != null && globalFilters.size() > 0) {

			for (IvisFilterForQuery globalFilter : globalFilters) {
				String filterSelector_globalSchema = globalFilter.getSelector();
				String filterSelector_localSchema = this.getSchemaMapping().get(filterSelector_globalSchema);

				IvisFilterForQuery localFilter = new IvisFilterForQuery();
				localFilter.setSelector(filterSelector_localSchema);
				localFilter.setFilterType(globalFilter.getFilterType());
				localFilter.setFilterValue(globalFilter.getFilterValue());

				localFilters.add(localFilter);
			}
		}

		IvisQuery localQuery = new IvisQuery();
		localQuery.setFilters(localFilters);
		localQuery.setFilterStrategy(globalQuery.getFilterStrategy());
		localQuery.setSelector(selector_localSchema);

		return localQuery;

	}

	@Override
	protected Map<String, String> transformIntoGlobalAndLocalSubqueries(IvisQuery globalQuery,
			List<String> subquerySelectors_globalSchema) {
		Map<String, String> subqueries_global_and_local_schema = new HashMap<String, String>();

		for (String subquerySelector_globalSchema : subquerySelectors_globalSchema) {
			/*
			 * each subquerySelector_localSchema stores tableName and
			 * propertyName, separated by ":", e.g. "tableName:PropertyName"
			 * 
			 * we only need the propertyName here
			 */
			String subquerySelector_localSchema = this.getSchemaMapping().get(subquerySelector_globalSchema);

			String[] split = subquerySelector_localSchema.split(":");

			String columnHeader = split[1];

			subqueries_global_and_local_schema.put(subquerySelector_globalSchema, columnHeader);
		}

		return subqueries_global_and_local_schema;

	}

	@Override
	protected List<IvisObject> executeLocalQuery(Object localQuery,
			Map<String, String> subquerySelectors_global_and_local_schema, IvisQuery globalQuery) throws Exception {
		// TODO Auto-generated method stub
		return retrieveDataFromDatabase((IvisQuery) localQuery, subquerySelectors_global_and_local_schema, globalQuery);
	}

	private List<IvisObject> retrieveDataFromDatabase(IvisQuery localQuery,
			Map<String, String> subquerySelectors_global_and_local_schema, IvisQuery globalQuery) {

		List<IvisObject> ivisObjects = null;

		/*
		 * selectorLoalSchema stores tableName and propertyName separated by
		 * ":", e.g. "tableName:propertyName"
		 */
		String selector_localSchema = localQuery.getSelector();
		String tableName = selector_localSchema.split(":")[0];

		/*
		 * columns to select are stored in subqueries!
		 */
		List<String> columnsToSelect = extractColumnsToSelect(subquerySelectors_global_and_local_schema);

		/*
		 * whereClauses are extracted from filters!
		 */
		List<WhereClause> whereClauses = extractWhereClauses(localQuery.getFilters());

		try {
			this.establishConnection();

			ResultSet resultSet = this.executeSelectStatement(tableName, columnsToSelect, whereClauses,
					localQuery.getFilterStrategy());

			ivisObjects = createIvisObjects(resultSet, subquerySelectors_global_and_local_schema, globalQuery);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.cleanupConnection();
		}

		return ivisObjects;
	}

	private List<IvisObject> createIvisObjects(ResultSet resultSet,
			Map<String, String> subquerySelectors_global_and_local_schema, IvisQuery globalQuery) throws SQLException {
		List<IvisObject> ivisObjects = new ArrayList<IvisObject>();

		String elementName = this.getNameFromXPathExpression(globalQuery.getSelector());

		while (resultSet.next()) {
			try {
				ivisObjects
						.add(this.createIvisObject(resultSet, subquerySelectors_global_and_local_schema, elementName));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ivisObjects;
	}

	private IvisObject createIvisObject(ResultSet resultSet,
			Map<String, String> subquerySelectors_global_and_local_schema, String elementName) throws SQLException {

		List<AttributeValuePair> attributeValuePairs = new ArrayList<AttributeValuePair>(
				subquerySelectors_global_and_local_schema.size());

		Iterator<Entry<String, String>> subqueryIterator = subquerySelectors_global_and_local_schema.entrySet()
				.iterator();

		while (subqueryIterator.hasNext()) {
			Entry<String, String> subqueryEntry = subqueryIterator.next();

			String name = getNameFromXPathExpression(subqueryEntry.getKey());

			Object value = resultSet.getObject(subqueryEntry.getValue());

			attributeValuePairs.add(new AttributeValuePair(name, value));
		}

		addWrapperReference(attributeValuePairs);

		IvisObject newIvisObject = new IvisObject(elementName, attributeValuePairs);

		return newIvisObject;
	}

	private List<WhereClause> extractWhereClauses(List<IvisFilterForQuery> filters) {
		List<WhereClause> whereClauses = new ArrayList<>(filters.size());

		for (IvisFilterForQuery filter : filters) {
			/*
			 * filterSelector_localSchema stores tableName and propertyName
			 * separated by ":", e.g. "tableName:propertyName"
			 * 
			 * we just need property name, since subqueries point to the same
			 * table as selector_localSchema
			 */
			String filterSelector_localSchema = filter.getSelector();
			String columnIdentifier = filterSelector_localSchema.split(":")[1];

			WhereClause clause = new WhereClause(columnIdentifier, filter.getFilterValue(), filter.getFilterType());
			whereClauses.add(clause);
		}
		return whereClauses;
	}

	private List<String> extractColumnsToSelect(Map<String, String> subquerySelectors_global_and_local_schema) {
		List<String> columnsToSelect = new ArrayList<>(subquerySelectors_global_and_local_schema.size());

		for (String columnName : subquerySelectors_global_and_local_schema.values()) {

			columnsToSelect.add(columnName);
		}

		return columnsToSelect;
	}

}
