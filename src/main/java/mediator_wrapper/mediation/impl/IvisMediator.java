package mediator_wrapper.mediation.impl;

import mediator_wrapper.mediation.IvisMediatorInterface;
import runtime.modify.RuntimeModificationMessage;

/**
 * Central mediator component of a Mediator-Wrapper architecture to offer a
 * homogeneous interface to query heterogeneous data sources.
 * 
 * TODO as instance object?
 * 
 * @author Christian Danowski
 *
 */
public class IvisMediator implements IvisMediatorInterface {

	private String pathToGlobalSchema;

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

}
