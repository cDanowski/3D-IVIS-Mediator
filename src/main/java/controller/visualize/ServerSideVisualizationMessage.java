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
	
	public String responseScene;

	public IvisQuery getQuery() {
		return query;
	}

	public void setQuery(IvisQuery query) {
		this.query = query;
	}

	public String getResponseScene() {
		return responseScene;
	}

	public void setResponseScene(String responseScene) {
		this.responseScene = responseScene;
	}

}
