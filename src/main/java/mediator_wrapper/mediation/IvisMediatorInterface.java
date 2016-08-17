package mediator_wrapper.mediation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.dom4j.DocumentException;
import org.jaxen.JaxenException;

import controller.runtime.modify.RuntimeModificationMessage;
import controller.runtime.modify.RuntimeNewObjectMessage;
import controller.synchronize.SynchronizationMessage;
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
	 * @return true, if modification was successful
	 * @throws IOException
	 * @throws DocumentException
	 * @throws UnsupportedEncodingException
	 */
	public boolean modifyDataInstance(RuntimeModificationMessage modificationMessage)
			throws UnsupportedEncodingException, DocumentException, IOException;

	/**
	 * Analyzes the message and creates a new data instance (if possible). To do
	 * this, it delegates the insert-task to the appropriate wrapper component.
	 * 
	 * @param runtimeNewObjectMessage
	 *            a message-object that contains all necessary information to
	 *            insert a new object.
	 * @return new visualization object or error message
	 */
	public Object insertNewObject(RuntimeNewObjectMessage runtimeNewObjectMessage);

	/**
	 * Is triggered when instances at data source level are modified. Then
	 * initiates retrieval of the modified objects.
	 * 
	 * @param syncMessage
	 *            instance of {@link SynchronizationMessage}
	 * @return a list of {@link IvisObject} representing the modified instances
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws DocumentException
	 * @throws Exception
	 */
	List<IvisObject> onSynchronizationEvent(SynchronizationMessage syncMessage)
			throws UnsupportedEncodingException, FileNotFoundException, IOException, DocumentException, Exception;

}
