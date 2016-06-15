package mediaton;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import util.UrlConstants;

/**
 * Controller that manages incoming data queries against the global data schema.
 * 
 * @author Christian Danowski
 *
 */
@Controller
public class MediationController {

	/**
	 * Manages an incoming data query request from clients against the global
	 * schema.
	 * 
	 * Realizes Server Side Information Visualization feature (request data and
	 * create an X3DOM scene)
	 * 
	 * @return
	 */
	@MessageMapping(UrlConstants.MEDIATION_ENDPOINT)
	@SendTo("") // TODO only send back to requesting client!
	public ServerSideVisualizationMessage query(ServerSideVisualizationMessage queryObject) {
		/**
		 * queryObject should have an attribute with the query against the global schema.
		 */

		// TODO return object should have an attribute that holds the x3d scene!
		return null;
	}

}
