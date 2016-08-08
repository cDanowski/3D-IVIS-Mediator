package controller.visualize;

import ivisQuery.IvisQuery;

/**
 * Java representation of a server side visualization message, which is sent by
 * clients that request an initial visualization of data.
 * 
 * @author Christian Danowski
 *
 */
public class ServerSideVisualizationMessage {
	
	public IvisQuery query;
	
	public String applicationTemplateIdentifier;
	
	public Object responseScene;

	public IvisQuery getQuery() {
		return query;
	}

	public void setQuery(IvisQuery query) {
		this.query = query;
	}

	public String getApplicationTemplateIdentifier() {
		return applicationTemplateIdentifier;
	}

	public void setApplicationTemplateIdentifier(String applicationTemplateIdentifier) {
		this.applicationTemplateIdentifier = applicationTemplateIdentifier;
	}

	public Object getResponseScene() {
		return responseScene;
	}

	public void setResponseScene(Object responseScene) {
		this.responseScene = responseScene;
	}

}
