package mediator_wrapper.wrapper.impl.database;

import ivisQuery.FilterType;

/**
 * Java representation of a where clause for SQL statements. It maps a column
 * identifier to a value and a comparison method.
 * 
 * @author Christian Danowski
 *
 */
public class WhereClause {

	private String columnIdentifier;

	private Object value;

	private FilterType filterType;

	public WhereClause(String columnIdentifier, Object value, FilterType filterType) {
		super();
		this.columnIdentifier = columnIdentifier;
		this.value = value;
		this.filterType = filterType;
	}

	public String getColumnIdentifier() {
		return columnIdentifier;
	}

	public void setColumnIdentifier(String columnIdentifier) {
		this.columnIdentifier = columnIdentifier;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public FilterType getFilterType() {
		return filterType;
	}

	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}

	@Override
	public String toString() {
		return "WhereClause [columnIdentifier=" + columnIdentifier + ", value=" + value + ", filterType=" + filterType
				+ "]";
	}
}
