package controller.runtime.modify;

/**
 * Java representation of a runtime manipulation message, which is sent by
 * clients to manipulate existing data instances.
 * 
 * @author Christian Danowski
 *
 */
public class RuntimeModificationMessage extends AbstractRuntimeManipulationMessage {

	/*
	 * XPath selector against the global schema to identify, which
	 * attribute/property shall be updated
	 */
	private String propertySelector_globalSchema;

	/*
	 * stores the name and value of the modified property;
	 */
	private Object newPropertyValue;

	public String getPropertySelector_globalSchema() {
		return propertySelector_globalSchema;
	}

	public void setPropertySelector_globalSchema(String propertySelector_globalSchema) {
		this.propertySelector_globalSchema = propertySelector_globalSchema;
	}

	public Object getNewPropertyValue() {
		return newPropertyValue;
	}

	public void setNewPropertyValue(Object newPropertyValue) {
		this.newPropertyValue = newPropertyValue;
	}

	@Override
	public String toString() {
		return "RuntimeModificationMessage [propertySelector_globalSchema=" + propertySelector_globalSchema
				+ ", newPropertyValue=" + newPropertyValue + ", getWrapperReference()=" + getWrapperReference()
				+ ", getApplicationTemplateIdentifier()=" + getApplicationTemplateIdentifier() + ", getQuery()="
				+ getQuery() + ", getResponseInfoMessage()=" + getResponseInfoMessage() + "]";
	}

}
