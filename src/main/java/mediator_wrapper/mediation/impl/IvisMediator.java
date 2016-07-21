package mediator_wrapper.mediation.impl;

import controller.runtime.modify.RuntimeModificationMessage;
import mediator_wrapper.mediation.IvisMediatorInterface;

/**
 * Central mediator component of a Mediator-Wrapper architecture to offer a
 * homogeneous interface to query heterogeneous data sources.
 * 
 * @author Christian Danowski
 *
 */
public class IvisMediator implements IvisMediatorInterface {

	private String pathToGlobalSchema;

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

	/*
	 * initiate instance
	 */
	private IvisMediator instance = new IvisMediator();

	private IvisMediator() {
		/*
		 * TODO load all necessary files and wrapper classes etc.
		 */
	}

	public IvisMediator getInstance() {

		return this.instance;

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
