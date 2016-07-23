package controller.visualize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import mediator_wrapper.mediation.impl.IvisMediator;
import util.UrlConstants;

/**
 * Controller that manages incoming server side information visualization data
 * queries against the global data schema (for initial visualizations).
 * 
 * @author Christian Danowski
 *
 */
@Controller
public class ServerSideVisualizationController {
	
	@Autowired
	IvisMediator mediator;

	/**
	 * Manages an incoming data query request from clients against the global
	 * schema.
	 * 
	 * Realizes Server Side Information Visualization feature (request data and
	 * create an X3DOM scene)
	 * 
	 * @return
	 */
	@MessageMapping(UrlConstants.SERVER_SIDE_IVIS_ENDPOINT)
	@SendTo("") // TODO only send back to requesting client!
	public ServerSideVisualizationMessage query(ServerSideVisualizationMessage queryObject) {
		/**
		 * queryObject should have an attribute with the query against the
		 * global schema.
		 */
		
		/*
		 * forward query to mediator
		 */
		Object retrievedData = this.mediator.queryData(queryObject.query);

		// TODO return object should have an attribute that holds the x3d scene!
		return null;
	}

}
