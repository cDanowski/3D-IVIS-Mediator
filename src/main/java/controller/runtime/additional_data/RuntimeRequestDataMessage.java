package controller.runtime.additional_data;

import java.util.List;

import application_template.impl.VisualizationObject;
import ivisQuery.IvisQuery;

/**
 * Java representation of a runtime request data message, which is sent by
 * clients to request additional data from within a running scene.
 * 
 * @author Christian Danowski
 *
 */
public class RuntimeRequestDataMessage {

	/*
	 * identifier for the application template
	 */
	public String applicationTemplateIdentifier;

	/*
	 * query against the global schema
	 */
	public IvisQuery query;

	/*
	 * the resulting scene content as X3DOM subtree
	 */
	public List<VisualizationObject> additionalObjects;

	public String getApplicationTemplateIdentifier() {
		return applicationTemplateIdentifier;
	}

	public void setApplicationTemplateIdentifier(String applicationTemplateIdentifier) {
		this.applicationTemplateIdentifier = applicationTemplateIdentifier;
	}

	public IvisQuery getQuery() {
		return query;
	}

	public void setQuery(IvisQuery query) {
		this.query = query;
	}

	public List<VisualizationObject> getAdditionalObjects() {
		return additionalObjects;
	}

	public void setAdditionalObjects(List<VisualizationObject> additionalObjects) {
		this.additionalObjects = additionalObjects;
	}

	@Override
	public String toString() {
		return "RuntimeRequestDataMessage [applicationTemplateIdentifier=" + applicationTemplateIdentifier + ", query="
				+ query + ", additionalObjects=" + additionalObjects + "]";
	}

}
