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

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

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
		return "RuntimeModificationMessage [objectId=" + objectId + ", propertySelector_globalSchema="
				+ propertySelector_globalSchema + ", newPropertyValue=" + newPropertyValue + ", modificationType="
				+ modificationType + ", wrapperReference=" + wrapperReference + ", responseVisualizationObject="
				+ responseVisualizationObject + ", applicationTemplateIdentifier=" + applicationTemplateIdentifier
				+ "]";
	}

}
