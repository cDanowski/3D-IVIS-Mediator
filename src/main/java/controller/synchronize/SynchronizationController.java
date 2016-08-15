package controller.synchronize;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import application_template.ApplicationTemplateInterface;
import application_template.impl.VisualizationObject;
import ivisObject.IvisObject;
import mediator_wrapper.mediation.impl.IvisMediator;
import util.UrlConstants;

/**
 * Controller that manages synchronization events triggered when instances at
 * data source level have changed.
 * 
 * @author Christian Danowski
 *
 */
@Controller
public class SynchronizationController {

	@Autowired
	private IvisMediator mediator;

	@Autowired
	private List<ApplicationTemplateInterface> availableApplicationTemplates;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

//	/**
//	 * Manages an incoming data query request from clients against the global
//	 * schema.
//	 * 
//	 * Realizes Server Side Information Visualization feature (request data and
//	 * create an X3DOM scene)
//	 * 
//	 * @return
//	 */
//	@MessageMapping(UrlConstants.DATA_SOURCE_CHANGE_ENDPOINT)
//	@SendTo(UrlConstants.STOMP_CLIENT_DATA_SOURCE_CHANGE_ENDPOINT)
//	public SynchronizationMessage onDataSourceChange(@RequestParam("dataSourceIdentifier") String id) {
//
//		/*
//		 * TODO send a message to the client telling that a data source change
//		 * happened
//		 * 
//		 * request the selector_globalSchema from the client in order to know
//		 * what to extract from the data sources!
//		 * 
//		 * --> then extract modified entries and send back to client
//		 */
//		
//		System.out.println(id);
//
//		SynchronizationMessage syncMessage = new SynchronizationMessage();
//		
//		syncMessage.setDataSourceIdentifier(id);
//		
//		return syncMessage;
//
//	}

	/**
	 * Manages an incoming data query request from clients against the global
	 * schema.
	 * 
	 * Realizes Server Side Information Visualization feature (request data and
	 * create an X3DOM scene)
	 * 
	 * @return
	 */
	@MessageMapping(UrlConstants.SYNCHRONIZATION_ENDPOINT)
	@SendTo(UrlConstants.STOMP_CLIENT_SYNCHRONIZATION_ENDPOINT)
	public void synchronize(SynchronizationMessage synchronizationMessage) {

		/*
		 * TODO send a message to the client telling that a data source change
		 * happened
		 * 
		 * request the selector_globalSchema from the client in order to know
		 * what to extract from the data sources!
		 * 
		 * --> then extract modified entries and send back to client
		 */

		/**
		 * queryObject should have an attribute with the query against the
		 * global schema.
		 */
		try {

			/*
			 * forward query to mediator
			 */
			List<IvisObject> retrievedData = this.mediator.onSynchronizationEvent(synchronizationMessage);

			/*
			 * forward retrieved data to applicationTemplate to generate scene
			 * 
			 * problem: we do not know which templates are in use!?! we just
			 * know that a data source was modified
			 * 
			 * --> so we have to iterate over all application templates, check
			 * if they produce a valid result and send it to the clients (for
			 * each template! ,multiple messages might occur; on the client
			 * side, check message for template identifier!)
			 */

			for (ApplicationTemplateInterface applTemplate : this.availableApplicationTemplates) {

				List<VisualizationObject> modifiedObjects = applTemplate.visualizeData_runtime(retrievedData);

				synchronizationMessage.setResponseVisualizationObjects(modifiedObjects);

				messagingTemplate.convertAndSend(UrlConstants.STOMP_CLIENT_SYNCHRONIZATION_ENDPOINT,
						synchronizationMessage);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		//
		// return synchronizationMessage;
	}

}
