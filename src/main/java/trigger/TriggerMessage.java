package trigger;

/**
 * Java representation of a trigger message sent to clients! The message must
 * contain all necessary information to update/embed scene content into a
 * running scene.
 * 
 * TODO maybe use RuntimeModificationMessage????
 * 
 * @author Christian Danowski
 *
 */
public class TriggerMessage {

	/*
	 * could be NEW for completely new data or UPDATE for modification of
	 * existing objects
	 */
	public String modificationType;

	public String sceneContent;

	/*
	 * identifies the object within the running scene that is updated
	 */
	public String objectId;

	/*
	 * identifies the location here new data should be added/appended
	 */
	public String appendLocation;
}
