package mediator_wrapper.wrapper;

import runtime.modify.RuntimeModificationMessage;

/**
 * Interface definition for wrapper components of a Mediator-Wrapper
 * architecture, which offers a homogeneous interface to query heterogeneous
 * data sources.
 * 
 * A wrapper hereby manages and executes then communication to a specific data
 * source.
 * 
 * @author Christian Danowski
 *
 */
public interface IvisWrapperInterface {

	/**
	 * Transforms the query against the global schema to a query against the
	 * wrapper's local schema and then executes that query to retrieve data from
	 * the wrapper's specific data source.
	 * 
	 * @param subQueryAgainstGlobalSchema
	 *            the sub-query against the global schema of the
	 *            mediator-wrapper. "Sub-query" indicates, that the query only
	 *            contains selectors for elements which are guaranteed to be
	 *            found in the local schema of the wrapper.
	 * @return the data objects which were queried
	 */
	public Object queryData(String subQueryAgainstGlobalSchema);

	/**
	 * 
	 * @param modificationMessage
	 * @return
	 */
	public Object applyModification(RuntimeModificationMessage modificationMessage);

}
