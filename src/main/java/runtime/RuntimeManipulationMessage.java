package runtime;

/**
 * Java representation of a runtime manipulation message, which is sent by
 * clients to manipulate data (create new data, or modify existing data).
 * 
 * @author Christian Danowski
 *
 */
public class RuntimeManipulationMessage {

	/*
	 * e.g. NEW or UPDATE to indicate the type of manipulation method
	 */
	public String manipulationType;

	/*
	 * to identify the object, could be an XPath expression against the global
	 * data schema!
	 */
	public String objectId;

	/*
	 * TODO in case of a new object: the complete object with all attributes
	 * must be transmitted to be stored at the datasource! How could that be
	 * made generic?
	 */
	public Object object;

	/*
	 * where should the new content be embedded within the running scene
	 */
	public String appendLocation;

	/*
	 * scene content needed to inform all other clients and keep them
	 * synchronized.
	 */
	public String responseSceneContent;

	/*
	 * TODO what else is needed?
	 */

}
