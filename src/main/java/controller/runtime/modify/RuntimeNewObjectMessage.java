package controller.runtime.modify;

/**
 * Java representation of a runtime new object message, which is sent by clients
 * to create new data instances
 * 
 * @author Christian Danowski
 *
 */
public class RuntimeNewObjectMessage extends AbstractRuntimeManipulationMessage {

	/*
	 * in case of a new object: the complete object with all attributes must be
	 * transmitted to be stored at the datasource! How could that be made
	 * generic?
	 */
	public Object newObject;

	/*
	 * XPath selector against the global schema to identify, which
	 * attribute/property shall be updated
	 */
	public String propertySelector_globalSchema;

	public Object getNewObject() {
		return newObject;
	}

	public void setNewObject(Object newObject) {
		this.newObject = newObject;
	}

	public String getPropertySelector_globalSchema() {
		return propertySelector_globalSchema;
	}

	public void setPropertySelector_globalSchema(String propertySelector_globalSchema) {
		this.propertySelector_globalSchema = propertySelector_globalSchema;
	}

	@Override
	public String toString() {
		return "RuntimeNewObjectMessage [newObject=" + newObject + ", propertySelector_globalSchema="
				+ propertySelector_globalSchema + ", modificationType=" + modificationType + ", wrapperReference="
				+ wrapperReference + ", responseVisualizationObject=" + responseVisualizationObject + ", query=" + query
				+ ", applicationTemplateIdentifier=" + applicationTemplateIdentifier + "]";
	}

}
