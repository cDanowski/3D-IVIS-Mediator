package controller.runtime.modify;

import ivisObject.AttributeValuePair;

/**
 * Java representation of a runtime manipulation message, which is sent by
 * clients to manipulate data (create new data, or modify existing data).
 * 
 * @author Christian Danowski
 *
 */
public class RuntimeModificationMessage {

	/*
	 * identifier for the application template
	 */
	public String applicationTemplateIdentifier;

	/*
	 * type of modification method
	 */
	public ModificationType modificationType;

	/*
	 * wrapper reference comprises the name of the wrapper, that manages the
	 * source file of this object.
	 */
	public String wrapperReference;

	/*
	 * to identify the object
	 */
	public String objectId;

	/*
	 * XPath selector against the global schema to identify, which
	 * attribute/property shall be updated
	 */
	public String propertySelector_globalSchema;

	/*
	 * stores the name and value of the modified property;
	 */
	public Object newPropertyValue;

	/*
	 * in case of a new object: the complete object with all attributes must be
	 * transmitted to be stored at the datasource! How could that be made
	 * generic?
	 */
	public Object newObject;

	/*
	 * this object represents the server response object. It is the
	 * visualization object that has been generated as consequence of the
	 * modification request.
	 */
	public Object responseVisualizationObject;

}
