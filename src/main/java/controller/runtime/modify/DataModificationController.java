package controller.runtime.modify;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

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
public class DataModificationController {

	@MessageMapping(UrlConstants.RUNTIME_MODIFICATION_ENDPOINT)
	@SendTo("") //TODO inform all other clients!
	public RuntimeModificationMessage updateDataSource(RuntimeModificationMessage runtimeMessage) {

		/*
		 * TODO message contains information about update TASK (NEW object or
		 * UPDATE existing object), which object is meant (ID)
		 * 
		 * contact mediator to perform update of data sources
		 * 
		 * trigger synchronization broadcast on successful update.
		 */
		return null;
	}

}
