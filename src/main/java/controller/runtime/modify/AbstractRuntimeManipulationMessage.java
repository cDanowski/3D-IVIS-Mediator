package controller.runtime.modify;

import controller.AbstractMessage;
import ivisQuery.IvisQuery;

public class AbstractRuntimeManipulationMessage extends AbstractMessage {

	/*
	 * wrapper reference comprises the name of the wrapper, that manages the
	 * source file of this object.
	 */
	private String wrapperReference;

	public String getWrapperReference() {
		return wrapperReference;
	}

	public void setWrapperReference(String wrapperReference) {
		this.wrapperReference = wrapperReference;
	}

}
