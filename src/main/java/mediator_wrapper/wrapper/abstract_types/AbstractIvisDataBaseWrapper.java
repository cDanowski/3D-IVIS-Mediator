package mediator_wrapper.wrapper.abstract_types;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.dom4j.DocumentException;

import ivisObject.AttributeValuePair;
import ivisObject.IvisObject;
import ivisQuery.FilterStrategy;
import ivisQuery.FilterType;
import ivisQuery.IvisQuery;
import mediator_wrapper.wrapper.impl.database.WhereClause;

/**
 * Abstract wrapper class that manages data bases.
 * 
 * @author Christian Danowski
 *
 */
public abstract class AbstractIvisDataBaseWrapper extends AbstractIvisWrapper {

	private String jdbc_driver;
	private String db_url;
	private String user;
	private String password;

	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet;

	public AbstractIvisDataBaseWrapper(String jdbc_driver, String db_url, String user, String password,
			String localSchemaMappingLocation) throws DocumentException {

		this.jdbc_driver = jdbc_driver;
		this.db_url = db_url;
		this.user = user;
		this.password = password;

		this.instantiateSchemaMapping(localSchemaMappingLocation);

	}

	public String getJdbc_driver() {
		return jdbc_driver;
	}

	public String getDb_url() {
		return db_url;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public void establishConnection() throws ClassNotFoundException, SQLException {
		// Register JDBC driver
		Class.forName(this.jdbc_driver);

		// Open a connection
		this.connection = DriverManager.getConnection(this.db_url, this.user, this.password);
	}

	public ResultSet executeSelectStatement(String tableName, List<String> columnsToSelect,
			List<WhereClause> whereClauses, FilterStrategy filterStrategy) throws SQLException {

		String sqlStatement = buildSelectStatement(tableName, columnsToSelect, whereClauses, filterStrategy);

		this.statement = this.connection.createStatement();

		this.resultSet = this.statement.executeQuery(sqlStatement);

		return resultSet;

	}

	private String buildSelectStatement(String tableName, List<String> columnsToSelect, List<WhereClause> whereClauses,
			FilterStrategy filterStrategy) {
		String selectItems = buildSelectItems(columnsToSelect);

		String whereClause = buildWhereClause(whereClauses, filterStrategy);

		StringBuilder builder = new StringBuilder();

		builder.append("SELECT");
		builder.append(" ");
		builder.append(selectItems);
		builder.append(" ");
		builder.append("from");
		builder.append(" ");
		builder.append("\"" + tableName + "\"");

		if (whereClause != null && whereClause.length() > 0) {
			builder.append(" ");
			builder.append("WHERE");
			builder.append(" ");
			builder.append(whereClause);
		}

		builder.append(";");

		String sqlStatement = builder.toString();

		return sqlStatement;
	}

	public void executeUpdateStatement(String tableName, AttributeValuePair columnToUpdate,
			List<WhereClause> whereClauses, FilterStrategy filterStrategy) throws SQLException {

		String sqlStatement = buildUpdateStatement(tableName, columnToUpdate, whereClauses, filterStrategy);

		this.statement = this.connection.createStatement();

		this.statement.executeUpdate(sqlStatement);
	}

	private String buildUpdateStatement(String tableName, AttributeValuePair columnToUpdate,
			List<WhereClause> whereClauses, FilterStrategy filterStrategy) {

		String whereClause = buildWhereClause(whereClauses, filterStrategy);

		String setColumnString = buildSetColumnString(columnToUpdate);

		StringBuilder builder = new StringBuilder();

		builder.append("UPDATE");
		builder.append(" ");
		builder.append("\"" + tableName + "\"");
		builder.append(" ");
		builder.append("SET");
		builder.append(" ");
		builder.append(setColumnString);

		if (whereClause != null && whereClause.length() > 0) {
			builder.append(" ");
			builder.append("WHERE");
			builder.append(" ");
			builder.append(whereClause);
		}

		builder.append(";");

		String sqlStatement = builder.toString();

		return sqlStatement;
	}

	private String buildSetColumnString(AttributeValuePair columnToUpdate) {
		StringBuilder builder = new StringBuilder();
		builder.append("\"");
		builder.append(columnToUpdate.getName());
		builder.append("\"");
		builder.append(" = ");

		Object newValue = columnToUpdate.getValue();

		/*
		 * if newValue is a textual value, wrap it in single quotes
		 */
		if (newValue instanceof String)
			builder.append("'" + newValue + "'");
		else
			builder.append(newValue);

		return builder.toString();
	}

	private String buildWhereClause(List<WhereClause> whereClauses, FilterStrategy filterStrategy) {
		/*
		 * if null return null
		 */
		if (whereClauses == null || whereClauses.size() == 0)
			return null;

		if (filterStrategy == null)
			filterStrategy = FilterStrategy.AND;

		StringBuilder builder = new StringBuilder();

		// append first entry
		appendWhereClause(builder, whereClauses.get(0));

		// append all other entries and filter strategy
		for (int i = 1; i < whereClauses.size(); i++) {

			WhereClause whereClause = whereClauses.get(i);

			builder.append(" ");
			builder.append(filterStrategy.toString());
			builder.append(" ");

			appendWhereClause(builder, whereClause);
		}

		String whereClauseString = builder.toString();

		return whereClauseString;

	}

	private void appendWhereClause(StringBuilder builder, WhereClause whereClause) {
		builder.append("\"" + whereClause.getColumnIdentifier() + "\"");
		builder.append(getComparator(whereClause.getFilterType()));

		/*
		 * if value is a textual value, wrap it in single quotes
		 */
		Object value = whereClause.getValue();
		if (value instanceof String) {
			builder.append("'");
			builder.append(value);
			builder.append("'");
		} else {
			builder.append(value);
		}
	}

	private Object getComparator(FilterType filterType) {
		String filter;

		switch (filterType) {
		case EQUAL:
			filter = "=";
			break;

		case NOT_EQUAL:
			filter = "<>";
			break;

		case GREATER_THAN:
			filter = ">";
			break;

		case GREATER_THAN_OR_EQUAL_TO:
			filter = ">=";
			break;

		case LESS_THAN:
			filter = "<";
			break;

		case LESS_THAN_OR_EQUAL_TO:
			filter = "<=";
			break;

		default:
			filter = "=";
			break;
		}

		return filter;
	}

	private String buildSelectItems(List<String> columnsToSelect) {
		/*
		 * if size is 0 or object is null
		 * 
		 * then simply return "*" to select all columns
		 * 
		 * else concatenate all items separated by comma
		 */
		if (columnsToSelect == null || columnsToSelect.size() == 0)
			return "*";

		StringBuilder builder = new StringBuilder();

		// append first entry
		builder.append("\"" + columnsToSelect.get(0) + "\"");

		// append all other entries and separating comma
		for (int i = 1; i < columnsToSelect.size(); i++) {
			String columnIdentifier = columnsToSelect.get(i);

			builder.append(", ");
			builder.append("\"" + columnIdentifier + "\"");
		}

		String selectItemsString = builder.toString();

		return selectItemsString;
	}

	public void cleanupConnection() {
		try {

			if (this.resultSet != null)
				this.resultSet.close();

			if (this.statement != null)
				this.statement.close();

			if (this.connection != null)
				this.connection.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Called to find modified instances within the database.
	 * 
	 * (Via a database trigger, the ID of a modified object should be available
	 * in the property 'recordId')
	 * 
	 * @param query_globalSchema
	 * @param subquerySelectors_globalSchema
	 * @param recordId
	 *            should be set to the ID of the modified instance
	 * @return the modified instance as 
	 * @throws Exception 
	 */
	public abstract List<IvisObject> onSourceFileChanged(IvisQuery query_globalSchema,
			List<String> subquerySelectors_globalSchema, String recordId) throws Exception;

	/*
	 * TODO common database parameters and access methods can be implemented
	 * here
	 */
}
