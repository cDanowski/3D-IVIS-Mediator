package controller.runtime.modify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import mediator_wrapper.mediation.impl.IvisMediator;
import util.UrlConstants;

/**
 * Controller that handles following runtime behavior:
 * 
 * - user triggered updates of content --> causing data source updates
 * 
 * @author Christian Danowski
 *
 */
@Controller
public class DataInstanceModificationController {
	
	@Autowired
	IvisMediator mediator;

	@MessageMapping(UrlConstants.RUNTIME_MODIFY_EXISTING_OBJECT_ENDPOINT)
	@SendTo("") //TODO inform all other clients!
	public RuntimeModificationMessage updateDataSource(RuntimeModificationMessage runtimeModificationMessage) {

		/*
		 * TODO message contains information about update TASK (NEW object or
		 * UPDATE existing object), which object is meant (ID)
		 * 
		 * contact mediator to perform update of data sources
		 * 
		 * trigger synchronization broadcast on successful update.
		 */
		
		this.mediator.modifyDataInstance(runtimeModificationMessage);
		
		return null;
	}
	
	@MessageMapping(UrlConstants.RUNTIME_NEW_OBJECT_ENDPOINT)
	@SendTo("") //TODO inform all other clients!
	public RuntimeModificationMessage insertNewObject(RuntimeNewObjectMessage runtimeNewObjectMessage) {

		/*
		 * TODO message contains information about update TASK (NEW object or
		 * UPDATE existing object), which object is meant (ID)
		 * 
		 * contact mediator to perform update of data sources
		 * 
		 * trigger synchronization broadcast on successful update.
		 */
		
		this.mediator.insertNewObject(runtimeNewObjectMessage);
		
		return null;
	}

}
