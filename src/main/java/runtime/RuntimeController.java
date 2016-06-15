package runtime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import util.UrlConstants;

/**
 * Controller that handles runtime behavior like:
 * 
 * - user triggered updates of content --> causing data source updates
 * 
 * - query of additional data the user requests from within a running scene!
 * 
 * @author Christian Danowski
 *
 */
@Controller
public class RuntimeController {

	@MessageMapping(UrlConstants.RUNTIME_MODIFICATION_ENDPOINT)
	@SendTo("") //TODO inform all other clients!
	public RuntimeManipulationMessage updateDataSource(RuntimeManipulationMessage runtimeMessage) {

		/*
		 * TODO message contains information about update TASK (NEW object or
		 * UPDATE existing object), which object is meant (ID)
		 */
		return null;
	}

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
