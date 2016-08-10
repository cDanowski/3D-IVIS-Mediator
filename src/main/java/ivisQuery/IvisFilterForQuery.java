package ivisQuery;

/**
 * Java representation of a filter object, which is used to filter returned
 * objects by specifying the selector (which subelement or attribute is used to
 * filter), the filterType (how it should be filtered) and the filterValue (for
 * comparison)
 * 
 * @author Christian Danowski
 *
 */
public class IvisFilterForQuery {

	private String selector;

	private Object filterValue;

	private FilterType filterType;

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public Object getFilterValue() {
		return filterValue;
	}

	public void setFilterValue(Object filterValue) {
		this.filterValue = filterValue;
	}

	public FilterType getFilterType() {
		return filterType;
	}

	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}

	@Override
	public String toString() {
		return "IvisFilterForQuery [selector=" + selector + ", filterValue=" + filterValue + ", filterType="
				+ filterType + "]";
	}

}
