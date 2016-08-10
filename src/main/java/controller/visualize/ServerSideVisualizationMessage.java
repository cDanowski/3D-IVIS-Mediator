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
	
	/*
	 * the complete response scene with all visualization objects
	 */
	public Object responseScene;

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
