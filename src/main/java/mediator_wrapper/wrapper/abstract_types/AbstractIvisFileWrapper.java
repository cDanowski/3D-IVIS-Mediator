package mediator_wrapper.wrapper.abstract_types;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.dom4j.DocumentException;

import ivisObject.IvisObject;
import ivisQuery.IvisQuery;

/**
 * Abstract wrapper class that manages file-based data sources.
 * 
 * @author Christian Danowski
 *
 */
public abstract class AbstractIvisFileWrapper extends AbstractIvisWrapper {

	private File sourceFile;
	private File shadowCopyFile;

	public AbstractIvisFileWrapper(String pathToSourcefile, String pathToShadowCopyFile, String pathToSchemaMappingFile)
			throws DocumentException {

		this.sourceFile = new File(pathToSourcefile);

		this.shadowCopyFile = new File(pathToShadowCopyFile);
		/*
		 * TODO instantiate wrapper properly!
		 * 
		 * parse mapping file; as map that maps selectors to local elements?!?!
		 */
		this.instantiateSchemaMapping(pathToSchemaMappingFile);
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public File getShadowCopyFile() {
		return shadowCopyFile;
	}

	/**
	 * This method is called, when the source file of the wrapper is modified.
	 * 
	 * It then identifies which entries are modified for subsequent
	 * transformation into visualization objects, which are sent to the client.
	 * 
	 * @param query_globalSchema
	 *            query against the global schema that contains the selector
	 *            indicating which objects to retrieve
	 * @param subquerySelectors_globalSchema
	 *            subqueires to indicate which properties shall be extracted
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws DocumentException
	 */
	public abstract List<IvisObject> onSourceFileChanged(IvisQuery query_globalSchema,
			List<String> subquerySelectors_globalSchema)
			throws UnsupportedEncodingException, FileNotFoundException, IOException, DocumentException;

	/**
	 * TODO generic methods to access, parse, modify a file?
	 * 
	 * does that make sense here? or do file types differ that much, that this
	 * should be done in implementing classes?
	 */

	protected void replaceShadowCopy() {
		Path FROM = Paths.get(this.getSourceFile().getAbsolutePath());
		Path TO = Paths.get(this.getShadowCopyFile().getAbsolutePath());
		CopyOption[] options = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING,
				StandardCopyOption.COPY_ATTRIBUTES };

		try {
			java.nio.file.Files.delete(TO);
			java.nio.file.Files.copy(FROM, TO, options);
			
			FROM = null;
			TO = null;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
