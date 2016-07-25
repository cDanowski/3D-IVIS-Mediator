package mediator_wrapper.mediation;

import java.util.List;

import org.jaxen.JaxenException;

import controller.runtime.modify.RuntimeModificationMessage;
import ivisObject.IvisObject;
import ivisQuery.IvisQuery;

/**
 * Interface definition of the central mediator component of a Mediator-Wrapper
 * architecture, which offers a homogeneous interface to query heterogeneous
 * data sources.
 * 
 * @author Christian Danowski
 *
 */
public interface IvisMediatorInterface {

	/**
	 * Analyzes the query against the global schema to delegate adequate
	 * sub-queries to wrapper-components to retrieve the queried data.
	 * 
	 * @param queryAgainstGlobalSchema
	 *            a ready-to-use query against the global data schema. The query
	 *            is represented through an XPath expression selecting a subset
	 *            of elements according to the global schema and filter objects
	 * @return all queried data objects
	 * @throws JaxenException
	 * @throws Exception 
	 */
	public List<IvisObject> queryData(IvisQuery queryAgainstGlobalSchema) throws JaxenException, Exception;

	/**
	 * Analyzes the modification message to identify, which object of the global
	 * schema shall be modified. Then delegates the update-task to the
	 * appropriate wrapper component.
	 * 
	 * @param modificationMessage
	 *            a message-object that contains all necessary information to
	 *            identify the data object and update it with new information.
	 * @return object containing information whether modification was successful
	 */
	public Object modifyDataInstances(RuntimeModificationMessage modificationMessage);

}
