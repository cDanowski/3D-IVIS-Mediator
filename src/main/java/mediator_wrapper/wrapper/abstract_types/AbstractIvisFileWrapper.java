package mediator_wrapper.wrapper.abstract_types;

import java.net.URI;

/**
 * Abstract wrapper class that manages file-based data sources.
 * 
 * @author Christian Danowski
 *
 */
public abstract class AbstractIvisFileWrapper extends AbstractIvisWrapper {

	private URI fileLocation;

	/**
	 * TODO generic methods to access, parse, modify a file?
	 * 
	 * does that make sense here? or do file types differ that much, that this
	 * should be done in implementing classes?
	 */

}
