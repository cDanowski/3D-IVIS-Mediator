package controller.runtime.modify;

import java.io.IOException;

import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
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
	@SendToUser(destinations = UrlConstants.STOMP_CLIENT_RUNTIME_MODIFY_EXISTING_OBJECT_ENDPOINT, broadcast = false)
	public RuntimeModificationMessage updateDataSource(RuntimeModificationMessage runtimeModificationMessage) {

		/*
		 * TODO message contains information about update TASK (NEW object or
		 * UPDATE existing object), which object is meant (ID)
		 * 
		 * contact mediator to perform update of data sources
		 * 
		 * trigger synchronization broadcast on successful update.
		 */

		try {
			boolean hasModified = this.mediator.modifyDataInstance(runtimeModificationMessage);

			if (hasModified)
				runtimeModificationMessage.setResponseInfoMessage("Modification succeeded!");
			else
				runtimeModificationMessage.setResponseInfoMessage("Modification failed!");
		} catch (DocumentException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return runtimeModificationMessage;
	}

	@MessageMapping(UrlConstants.RUNTIME_NEW_OBJECT_ENDPOINT)
	@SendToUser(destinations = UrlConstants.STOMP_CLIENT_RUNTIME_MODIFY_EXISTING_OBJECT_ENDPOINT, broadcast = false)
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
