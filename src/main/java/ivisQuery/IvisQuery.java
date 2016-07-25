package ivisQuery;

import java.util.List;

/**
 * Generic query object, which comprises of a selector (what elements shall be
 * returned) and optional filter (which reduce the returned objects according to
 * the specified filter definition)
 * 
 * @author Christian Danowski
 *
 */
public class IvisQuery {

	private String selector;

	private List<IvisFilterForQuery> filters;
	
	private FilterStrategy filterStrategy;

	public FilterStrategy getFilterStrategy() {
		return filterStrategy;
	}

	public void setFilterStrategy(FilterStrategy filterStrategy) {
		this.filterStrategy = filterStrategy;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public List<IvisFilterForQuery> getFilters() {
		return filters;
	}

	public void setFilters(List<IvisFilterForQuery> filters) {
		this.filters = filters;
	}

}
