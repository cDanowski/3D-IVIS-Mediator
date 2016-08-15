package mediator_wrapper.wrapper.abstract_types;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import ivisObject.IvisObject;
import ivisQuery.IvisQuery;

/**
 * Abstract wrapper class that manages file-based data sources.
 * 
 * @author Christian Danowski
 *
 */
public abstract class AbstractIvisFileWrapper extends AbstractIvisWrapper {

	private static final String XPATH_EXPRESSION_MAPPING_ELEMENT = "//mapping";
	private static final String XPATH_EXPRESSION_SELECTOR_GLOBAL_SCHEMA_ELEMENT = "selector_globalSchema";
	private static final String XPATH_EXPRESSION_SELECTOR_LOCAL_SCHEMA_ELEMENT = "selector_localSchema";

	private File sourceFile;
	private File shadowCopyFile;

	private Map<String, String> schemaMapping;

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

	/**
	 * instantiates the schemaMapping map by parsing the mapping file and adding
	 * a pair of String (selector of global schema, selector of local schema)
	 * for each mapping
	 * 
	 * @param pathToSchemaMappingFile
	 * @throws DocumentException
	 */
	private void instantiateSchemaMapping(String pathToSchemaMappingFile) throws DocumentException {
		/*
		 * parse document
		 */
		File inputFile = new File(pathToSchemaMappingFile);
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputFile);

		/*
		 * new instance of schemaMapping
		 */
		this.schemaMapping = new HashMap<String, String>();

		/*
		 * find all mappings
		 */
		List<Node> mappingNodes = document.selectNodes(XPATH_EXPRESSION_MAPPING_ELEMENT);

		for (Node node : mappingNodes) {
			/*
			 * extract selector as key
			 */
			String selector_globalSchema = node.selectSingleNode(XPATH_EXPRESSION_SELECTOR_GLOBAL_SCHEMA_ELEMENT)
					.getText();

			/*
			 * create a list of all wrapper instances that offer data for
			 * selector
			 */
			String selector_localSchema = node.selectSingleNode(XPATH_EXPRESSION_SELECTOR_LOCAL_SCHEMA_ELEMENT)
					.getText();

			/*
			 * add mapping to wrapperMapping map
			 */
			this.schemaMapping.put(selector_globalSchema, selector_localSchema);
		}

	}

	public File getSourceFile() {
		return sourceFile;
	}

	public File getShadowCopyFile() {
		return shadowCopyFile;
	}

	public Map<String, String> getSchemaMapping() {
		return schemaMapping;
	}

	/**
	 * extracts the name of the given XPath selector.
	 * 
	 * @param xPathSelector
	 *            an XPath selector
	 * @return the name. this will usually be the last element of the XPath
	 *         expression (e.g. from 'item1/item2' the name will be 'item2')
	 */
	protected String getNameFromXPathExpression(String xPathSelector) {
		if (xPathSelector.contains("/")) {
			String[] elements = xPathSelector.split("/");

			String name = elements[elements.length - 1];

			/*
			 * in case of an attribute there is a leading '@', which should be
			 * removed
			 */
			if (name.startsWith("@"))
				name = name.substring(1);

			return name;
		} else
			return xPathSelector;
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
	
	protected void replaceShadowCopy() throws IOException {
		Path FROM = Paths.get(this.getSourceFile().getAbsolutePath());
		Path TO = Paths.get(this.getShadowCopyFile().getAbsolutePath());
		CopyOption[] options = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING,
				StandardCopyOption.COPY_ATTRIBUTES };
		java.nio.file.Files.copy(FROM, TO, options);

	}

}
