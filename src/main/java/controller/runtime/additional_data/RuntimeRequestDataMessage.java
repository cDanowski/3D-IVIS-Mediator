package controller.runtime.additional_data;

import java.util.List;

import application_template.impl.VisualizationObject;
import controller.AbstractMessage;
import ivisQuery.IvisQuery;

/**
 * Java representation of a runtime request data message, which is sent by
 * clients to request additional data from within a running scene.
 * 
 * @author Christian Danowski
 *
 */
public class RuntimeRequestDataMessage extends AbstractMessage{

	/*
	 * the resulting scene content as X3DOM subtree
	 */
	public List<VisualizationObject> additionalObjects;

	public List<VisualizationObject> getAdditionalObjects() {
		return additionalObjects;
	}

	public void setAdditionalObjects(List<VisualizationObject> additionalObjects) {
		this.additionalObjects = additionalObjects;
	}

	@Override
	public String toString() {
		return "RuntimeRequestDataMessage [applicationTemplateIdentifier=" + getApplicationTemplateIdentifier() + ", query="
				+ getQuery() + ", additionalObjects=" + additionalObjects + "]";
	}

}
