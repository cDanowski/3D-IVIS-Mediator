package visualize;

/**
 * Java representation of a server side visualization message, which is sent by
 * clients that request an initial visualization of data.
 * 
 * @author Christian Danowski
 *
 */
public class ServerSideVisualizationMessage {
	
	public String query;
	
	public String responseScene;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getResponseScene() {
		return responseScene;
	}

	public void setResponseScene(String responseScene) {
		this.responseScene = responseScene;
	}

}
