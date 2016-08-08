package controller.visualize;

import java.util.List;

import org.jaxen.JaxenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import application_template.ApplicationTemplateInterface;
import ivisObject.IvisObject;
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
	private IvisMediator mediator;

	@Autowired
	private List<ApplicationTemplateInterface> availableApplicationTemplates;

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
	@SendToUser(destinations = UrlConstants.STOMP_CLIENT_SERVER_SIDE_IVIS_ENDPOINT, broadcast = false)
	public ServerSideVisualizationMessage query(ServerSideVisualizationMessage queryObject) {
		/**
		 * queryObject should have an attribute with the query against the
		 * global schema.
		 */
		try {

			/*
			 * forward query to mediator
			 */
			List<IvisObject> retrievedData = this.mediator.queryData(queryObject.query);

			/*
			 * forward retrieved data to applicationTemplate to generate scene
			 */
			String applicationTemplateIdentifier = queryObject.getApplicationTemplateIdentifier();

			for (ApplicationTemplateInterface applTemplate : this.availableApplicationTemplates) {
				/*
				 * identify the requested template!
				 */
				if (applTemplate.getUniqueIdentifier().equalsIgnoreCase(applicationTemplateIdentifier)) {
					Object x3domScene = applTemplate.createInitialScene(retrievedData);

					queryObject.setResponseScene(x3domScene);
					break;
				}
			}

		} catch (JaxenException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return queryObject;
	}

}
