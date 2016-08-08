package application_template.impl;

/**
 * Maps a concrete visualization object (of any form) to it's unique id.
 * 
 * @author Christian Danowski
 *
 */
public class VisualizationObject {

	private String id;

	private Object visualizationObject;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            unique id of visualization object
	 * @param visualizationObject
	 *            the concrete visualization object
	 */
	public VisualizationObject(String id, Object visualizationObject) {
		super();
		this.id = id;
		this.visualizationObject = visualizationObject;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getVisualizationObject() {
		return visualizationObject;
	}

	public void setVisualizationObject(Object visualizationObject) {
		this.visualizationObject = visualizationObject;
	}

}
