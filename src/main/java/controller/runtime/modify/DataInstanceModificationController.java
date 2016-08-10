package controller.runtime.modify;

import java.io.IOException;
import java.util.List;

import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import application_template.ApplicationTemplateInterface;
import application_template.impl.VisualizationObject;
import ivisObject.IvisObject;
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

	@Autowired
	private List<ApplicationTemplateInterface> availableApplicationTemplates;

	@MessageMapping(UrlConstants.RUNTIME_MODIFY_EXISTING_OBJECT_ENDPOINT)
	@SendTo(UrlConstants.STOMP_CLIENT_RUNTIME_MODIFY_EXISTING_OBJECT_ENDPOINT)
	public RuntimeModificationMessage updateDataSource(RuntimeModificationMessage runtimeModificationMessage) {

		/*
		 * TODO message contains information about update TASK (NEW object or
		 * UPDATE existing object), which object is meant (ID)
		 * 
		 * contact mediator to perform update of data sources
		 * 
		 * trigger synchronization broadcast on successful update.
		 */

		IvisObject modifiedVisualizationObject;
		try {
			modifiedVisualizationObject = this.mediator.modifyDataInstance(runtimeModificationMessage);

			String applicationTemplateIdentifier = runtimeModificationMessage.getApplicationTemplateIdentifier();

			for (ApplicationTemplateInterface applTemplate : this.availableApplicationTemplates) {
				/*
				 * identify the requested template!
				 */
				if (applTemplate.getUniqueIdentifier().equalsIgnoreCase(applicationTemplateIdentifier)) {

					VisualizationObject visualizationObject = applTemplate
							.visualizeData_runtime(modifiedVisualizationObject);

					runtimeModificationMessage.setResponseVisualizationObject(visualizationObject);

					break;
				}
			}

		} catch (DocumentException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return runtimeModificationMessage;
	}

	@MessageMapping(UrlConstants.RUNTIME_NEW_OBJECT_ENDPOINT)
	@SendTo("") // TODO inform all other clients!
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
