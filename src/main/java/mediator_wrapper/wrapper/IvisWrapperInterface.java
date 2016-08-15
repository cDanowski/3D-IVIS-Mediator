package mediator_wrapper.wrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.dom4j.DocumentException;

import controller.runtime.modify.RuntimeModificationMessage;
import ivisObject.IvisObject;
import ivisQuery.IvisQuery;

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
	 * @param queryAgainstGlobalSchema
	 *            the query against the global schema of the mediator-wrapper
	 *            that contains the selector against the global schema and
	 *            filter definitions.
	 * @param subquerySelectors
	 *            necessary subquerySelectors that point to all child nodes and
	 *            attributes of the element selected by
	 *            queryAgainstGlobalSchema. If it represents the same selector
	 *            as in queryAgainstGlobalSchema, then there are no such sub
	 *            elements
	 * @return the data objects which were queried
	 * @throws Exception
	 */
	public List<IvisObject> queryData(IvisQuery queryAgainstGlobalSchema, List<String> subquerySelectors)
			throws Exception;

	/**
	 * Modifies a certain data instance.
	 * 
	 * @param modificationMessage
	 *            contains all necessary information to identify the target
	 *            object and modify the target property
	 * @param subquerySelectors_globalSchema
	 *            references to all object properties
	 * @return the modified object
	 * @throws DocumentException 
	 * @throws UnsupportedEncodingException 
	 * @throws IOException 
	 */
	public boolean modifyDataInstance(RuntimeModificationMessage modificationMessage,
			List<String> subquerySelectors_globalSchema) throws DocumentException, UnsupportedEncodingException, IOException;

}
