package mediator_wrapper.mediation.impl;

import org.springframework.beans.factory.annotation.Autowired;

import controller.runtime.modify.RuntimeModificationMessage;
import mediator_wrapper.mediation.IvisMediatorInterface;
import mediator_wrapper.wrapper.impl.XmlWrapper;

/**
 * Central mediator component of a Mediator-Wrapper architecture to offer a
 * homogeneous interface to query heterogeneous data sources.
 * 
 * @author Christian Danowski
 *
 */
public class IvisMediator implements IvisMediatorInterface {
	
	@Autowired
	XmlWrapper xmlWrapper;
	
	@Autowired
	XmlWrapper csvWrapper;

	private Object wrapperMapping;

	/**
	 * attribute shall indicate whether a user already performs a modification
	 * request. Different Modifications should not happen at the same time.
	 * 
	 * Could return an info message, if another user already updates something
	 */
	private boolean isCurrentlyModifying = false;

	/*
	 * TODO attribute that holds the global schema!
	 */

	/*
	 * list of available wrapper components
	 * 
	 * TODO, maybe through scanning for all classes of a specific type? (to
	 * allow dynamic addition and deletion of wrappers)
	 */

	public IvisMediator(String pathToWrapperMappingFile) {
		/*
		 * TODO load mapping file from argument
		 * 
		 * as a map that maps selectors to wrapper names?!?!
		 */
	}

	@Override
	public Object queryData(String queryAgainstGlobalSchema) {

		/*
		 * analyze the query against the global schema
		 * 
		 * identify affected wrappers (data sources, that have to be accessed)
		 * 
		 * create and delegate sub-queries to wrappers (best as new Thread to
		 * allow concurrent execution!)
		 * 
		 * collect and return results (all queried objects!)
		 */

		return null;

	}

	@Override
	public Object modifyDataInstances(RuntimeModificationMessage modificationMessage) {

		this.isCurrentlyModifying = true;
		
		/*
		 * analyze the query against the global schema
		 * 
		 * identify affected wrappers (data sources, that have to be accessed)
		 * 
		 * create and delegate sub-queries to wrappers (best as new Thread to
		 * allow concurrent execution!)
		 * 
		 * collect and return results (all queried objects!)
		 */
		
		this.isCurrentlyModifying = false;

		return null;

	}

}
