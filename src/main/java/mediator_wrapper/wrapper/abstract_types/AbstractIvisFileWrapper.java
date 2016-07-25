package mediator_wrapper.wrapper.abstract_types;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

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

	private Map<String, String> schemaMapping;

	public AbstractIvisFileWrapper(String pathToSourcefile, String pathToSchemaMappingFile) throws DocumentException {

		this.sourceFile = new File(pathToSourcefile);
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

	public Map<String, String> getSchemaMapping() {
		return schemaMapping;
	}

	/**
	 * TODO generic methods to access, parse, modify a file?
	 * 
	 * does that make sense here? or do file types differ that much, that this
	 * should be done in implementing classes?
	 */

}
