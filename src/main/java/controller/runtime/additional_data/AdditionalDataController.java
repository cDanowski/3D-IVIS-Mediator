package controller.runtime.additional_data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import mediator_wrapper.mediation.impl.IvisMediator;
import util.UrlConstants;

/**
 * Controller that handles following runtime behavior:
 * 
 * - query of additional data the user requests from within a running scene!
 * 
 * @author Christian Danowski
 *
 */
@Controller
public class AdditionalDataController {
	
	@Autowired
	IvisMediator mediator;

	@MessageMapping(UrlConstants.RUNTIME_ADDITIONAL_DATA_ENDPOINT)
	@SendTo("") // TODO only send back to requesting client!
	public RuntimeRequestDataMessage requestAdditionalData(RuntimeRequestDataMessage runtimeMessage) {

		/*
		 * TODO message contains a query to request additional data
		 * 
		 * Will return a message with the resulting X3DOM subtree
		 * 
		 * TODO should info where the resulting subtree should be integrated be
		 * sent to server as well, or be left on the client side JavScript
		 * code???
		 */
		
		Object retrievedData = this.mediator.queryData(runtimeMessage.query);
		
		return null;
	}

}
