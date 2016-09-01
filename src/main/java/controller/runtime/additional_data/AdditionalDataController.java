package controller.runtime.additional_data;

import java.util.List;

import org.jaxen.JaxenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import application_template.ApplicationTemplate;
import application_template.impl.VisualizationObject;
import ivisObject.IvisObject;
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

	@Autowired
	private List<ApplicationTemplate> availableApplicationTemplates;

	@MessageMapping(UrlConstants.RUNTIME_ADDITIONAL_DATA_ENDPOINT)
	@SendToUser(destinations = UrlConstants.STOMP_CLIENT_RUNTIME_ADDITIONAL_DATA_ENDPOINT, broadcast = false)
	public RuntimeRequestDataMessage requestAdditionalData(RuntimeRequestDataMessage runtimeMessage) {

		try {
			List<IvisObject> retrievedData = this.mediator.queryData(runtimeMessage.getQuery());

			/*
			 * forward retrieved data to applicationTemplate to generate scene
			 */
			String applicationTemplateIdentifier = runtimeMessage.getApplicationTemplateIdentifier();

			for (ApplicationTemplate applTemplate : this.availableApplicationTemplates) {
				/*
				 * identify the requested template!
				 */
				if (applTemplate.getUniqueIdentifier().equalsIgnoreCase(applicationTemplateIdentifier)) {
					List<VisualizationObject> visualizationObjects = applTemplate.visualizeData_runtime(retrievedData);

					runtimeMessage.setAdditionalObjects(visualizationObjects);
					break;
				}
			}

		} catch (JaxenException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return runtimeMessage;
	}

}
