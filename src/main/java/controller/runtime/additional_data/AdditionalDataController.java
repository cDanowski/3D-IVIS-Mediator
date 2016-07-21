package controller.runtime.additional_data;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

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
		return null;
	}

}
