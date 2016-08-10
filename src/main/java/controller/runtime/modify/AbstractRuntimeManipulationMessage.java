package controller.runtime.modify;

import controller.AbstractMessage;
import ivisQuery.IvisQuery;

public class AbstractRuntimeManipulationMessage extends AbstractMessage {

	/*
	 * wrapper reference comprises the name of the wrapper, that manages the
	 * source file of this object.
	 */
	private String wrapperReference;

	/*
	 * this object represents the server response object. It is the
	 * visualization object that has been generated as consequence of the
	 * modification request.
	 */
	private Object responseVisualizationObject;

	public String getWrapperReference() {
		return wrapperReference;
	}

	public void setWrapperReference(String wrapperReference) {
		this.wrapperReference = wrapperReference;
	}

	public Object getResponseVisualizationObject() {
		return responseVisualizationObject;
	}

	public void setResponseVisualizationObject(Object responseVisualizationObject) {
		this.responseVisualizationObject = responseVisualizationObject;
	}

}
