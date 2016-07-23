package mediator_wrapper.wrapper.abstract_types;

import java.io.File;

/**
 * Abstract wrapper class that manages file-based data sources.
 * 
 * @author Christian Danowski
 *
 */
public abstract class AbstractIvisFileWrapper extends AbstractIvisWrapper {

	private File sourceFile;

	private Object schemaMapping;

	public AbstractIvisFileWrapper(String pathToSourcefile, String pathToSchemaMappingFile) {

		this.sourceFile = new File(pathToSourcefile);
		/* 
		 * TODO instantiate wrapper properly!
		 * 
		 * parse mapping file; as map that maps selectors to local elements?!?!
		 */
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public Object getSchemaMapping() {
		return schemaMapping;
	}

	public void setSchemaMapping(Object schemaMapping) {
		this.schemaMapping = schemaMapping;
	}

	/**
	 * TODO generic methods to access, parse, modify a file?
	 * 
	 * does that make sense here? or do file types differ that much, that this
	 * should be done in implementing classes?
	 */

}
