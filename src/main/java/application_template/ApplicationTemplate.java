package application_template;

import java.util.List;

import application_template.impl.VisualizationObject;
import ivisObject.IvisObject;

/**
 * The application template interface defines central methods of an application
 * template, which are called by the Spring controllers to visualize and process
 * the data returned by a query.
 * 
 * Via this template mechanism, it is possible to easily switch between concrete
 * implementations.
 * 
 * Besides the important methods, each implementing template class shall provide
 * a unique identifier to distinguish between different interfaces.
 * 
 * @author Christian Danowski
 *
 */
public interface ApplicationTemplate {

	/**
	 * Returns the unique identifier of the template. It may be used to
	 * determine, which concrete template implementation shall process the data.
	 * 
	 * @return the template's unique identifier
	 */
	public String getUniqueIdentifier();

	/**
	 * Takes the given data and creates an interactive scene using template
	 * specific visualization techniques and mapping definitions. Realizes the
	 * Server SIde Information Visualization task, where data is initially
	 * transformed into a new scene for client-side visualization. .
	 * 
	 * @param dataToVisualize
	 *            the data that shall be transformed to visual shapes
	 * @return a completely new interactive scene
	 */
	public Object createInitialScene(List<IvisObject> dataToVisualize);

	/**
	 * Takes the given data and creates visual objects, which can be integrated
	 * into an already running scene. Realizes the Runtime Information
	 * Visualization, where clients may wish to visualize additional data. ALso
	 * this method may be utilized to be called on a client-triggered update of
	 * any data property, since this method could be called with the modified
	 * data object, which will then be re-transformed into a modified visual
	 * shape.
	 * 
	 * @param dataToVisualize
	 *            the data that shall be transformed to visual shapes
	 * @return the visual objects as result of the IVIS transformation used on
	 *         'dataToVisualize'
	 */
	public List<VisualizationObject> visualizeData_runtime(List<IvisObject> dataToVisualize);

	/**
	 * Takes the given data and creates a visual object, which can be integrated
	 * into an already running scene. Realizes the Runtime Information
	 * Visualization, where clients may wish to visualize additional data. ALso
	 * this method may be utilized to be called on a client-triggered update of
	 * any data property, since this method could be called with the modified
	 * data object, which will then be re-transformed into a modified visual
	 * shape.
	 * 
	 * @param dataToVisualize
	 *            the data that shall be transformed to visual shapes
	 * @return the visual object as result of the IVIS transformation used on
	 *         'dataToVisualize'
	 */
	public VisualizationObject visualizeData_runtime(IvisObject dataToVisualize);

}
