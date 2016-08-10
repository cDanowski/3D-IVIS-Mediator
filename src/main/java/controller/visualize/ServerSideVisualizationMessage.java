package controller.visualize;

import controller.AbstractMessage;
import ivisQuery.IvisQuery;

/**
 * Java representation of a server side visualization message, which is sent by
 * clients that request an initial visualization of data.
 * 
 * @author Christian Danowski
 *
 */
public class ServerSideVisualizationMessage extends AbstractMessage{
	
	public IvisQuery query;
	
	public Object responseScene;

	public IvisQuery getQuery() {
		return query;
	}

	public void setQuery(IvisQuery query) {
		this.query = query;
	}

	public Object getResponseScene() {
		return responseScene;
	}

	public void setResponseScene(Object responseScene) {
		this.responseScene = responseScene;
	}

	@Override
	public String toString() {
		return "ServerSideVisualizationMessage [query=" + query + ", applicationTemplateIdentifier="
				+ applicationTemplateIdentifier + ", responseScene=" + responseScene + "]";
	}

}
