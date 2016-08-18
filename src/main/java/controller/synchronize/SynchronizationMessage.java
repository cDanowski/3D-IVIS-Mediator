package controller.synchronize;

import java.util.List;

import application_template.impl.VisualizationObject;
import controller.AbstractMessage;

/**
 * Java representation of a synchronize message sent to clients! The message
 * must contain all necessary information to update/embed scene content into a
 * running scene.
 * 
 * @author Christian Danowski
 *
 */
public class SynchronizationMessage extends AbstractMessage {

	/*
	 * identifier of the data source
	 */
	private String dataSourceIdentifier;

	private List<String> recordIds;

	/*
	 * computed by system; all modified/changed objects
	 */
	private List<VisualizationObject> responseVisualizationObjects;

	public String getDataSourceIdentifier() {
		return dataSourceIdentifier;
	}

	public void setDataSourceIdentifier(String dataSourceIdentifier) {
		this.dataSourceIdentifier = dataSourceIdentifier;
	}

	public List<String> getRecordIds() {
		return recordIds;
	}

	public void setRecordIds(List<String> recordIds) {
		this.recordIds = recordIds;
	}

	public List<VisualizationObject> getResponseVisualizationObjects() {
		return responseVisualizationObjects;
	}

	public void setResponseVisualizationObjects(List<VisualizationObject> responseVisualizationObjects) {
		this.responseVisualizationObjects = responseVisualizationObjects;
	}

	@Override
	public String toString() {
		return "SynchronizationMessage [dataSourceIdentifier=" + dataSourceIdentifier + ", recordIds=" + recordIds
				+ ", responseVisualizationObjects=" + responseVisualizationObjects + "]";
	}

}
