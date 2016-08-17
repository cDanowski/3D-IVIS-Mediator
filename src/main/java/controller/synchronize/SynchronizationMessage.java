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

	private String recordId;

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

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public List<VisualizationObject> getResponseVisualizationObjects() {
		return responseVisualizationObjects;
	}

	public void setResponseVisualizationObjects(List<VisualizationObject> responseVisualizationObjects) {
		this.responseVisualizationObjects = responseVisualizationObjects;
	}

	@Override
	public String toString() {
		return "SynchronizationMessage [dataSourceIdentifier=" + dataSourceIdentifier + ", recordId=" + recordId
				+ ", responseVisualizationObjects=" + responseVisualizationObjects + "]";
	}

}
