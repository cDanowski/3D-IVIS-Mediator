package mediator_wrapper.mediation.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;

import controller.runtime.modify.RuntimeModificationMessage;
import controller.runtime.modify.RuntimeNewObjectMessage;
import controller.synchronize.SynchronizationMessage;
import ivisObject.IvisObject;
import ivisQuery.IvisQuery;
import mediator_wrapper.mediation.IvisMediatorInterface;
import mediator_wrapper.mediation.impl.dataSourceMonitor.SourceFilesMonitor;
import mediator_wrapper.wrapper.IvisWrapper;
import mediator_wrapper.wrapper.abstract_types.AbstractIvisDataBaseWrapper;
import mediator_wrapper.wrapper.abstract_types.AbstractIvisFileWrapper;
import mediator_wrapper.wrapper.abstract_types.AbstractIvisWebServiceWrapper;

/**
 * Central mediator component of a Mediator-Wrapper architecture to offer a
 * homogeneous interface to query heterogeneous data sources.
 * 
 * @author Christian Danowski
 *
 */
public class IvisMediator implements IvisMediatorInterface {

	private static final String XPATH_EXPRESSION_WRAPPER = "wrappers/wrapper";

	private static final String XPATH_EXPRESSION_SELECTOR = "selector";

	private static final String XPATH_EXPRESSION_MAPPING_ELEMENT = "//mapping";

	private List<IvisWrapper> availableWrappers;

	private Map<String, List<IvisWrapper>> wrapperMapping;

	private SubqueryGenerator subqueryGenerator;

	/**
	 * attribute shall indicate whether a user already performs a modification
	 * request. Different Modifications should not happen at the same time.
	 * 
	 * Could return an info message, if another user already updates something
	 */
	private boolean isCurrentlyModifying = false;

	private SourceFilesMonitor sourceFilesMonitor;

	/*
	 * TODO attribute that holds the global schema!
	 */

	/*
	 * list of available wrapper components
	 * 
	 * TODO, maybe through scanning for all classes of a specific type? (to
	 * allow dynamic addition and deletion of wrappers)
	 */

	@Autowired
	public IvisMediator(List<IvisWrapper> wrappers, String pathToWrapperMappingFile,
			SubqueryGenerator subqueryGenerator, SourceFilesMonitor sourceFilesMonitor)
			throws DocumentException, IOException, InterruptedException {

		this.availableWrappers = wrappers;

		this.subqueryGenerator = subqueryGenerator;

		this.sourceFilesMonitor = sourceFilesMonitor;

		this.instantiate(pathToWrapperMappingFile);
	}

	/**
	 * Instantiates the mediator.
	 * 
	 * Creates a map that maps a selector of the global schema (pointing to a
	 * specific element of the global schema) to instances of wrappers, that
	 * offer data for it
	 * 
	 * @param pathToWrapperMappingFile
	 * @throws DocumentException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void instantiate(String pathToWrapperMappingFile)
			throws DocumentException, IOException, InterruptedException {

		/*
		 * parse document
		 */
		File inputFile = new File(pathToWrapperMappingFile);
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputFile);

		/*
		 * new instance of wrapperMapping
		 */
		this.wrapperMapping = new HashMap<String, List<IvisWrapper>>();

		/*
		 * find all mappings
		 */
		List<Node> mappingNodes = document.selectNodes(XPATH_EXPRESSION_MAPPING_ELEMENT);

		for (Node node : mappingNodes) {
			/*
			 * extract selector as key
			 */
			String selector_globalSchema = node.selectSingleNode(XPATH_EXPRESSION_SELECTOR).getText();

			/*
			 * create a list of all wrapper instances that offer data for
			 * selector
			 */
			List<Node> wrapperNodes = node.selectNodes(XPATH_EXPRESSION_WRAPPER);

			List<IvisWrapper> wrappersForSelector = makeListFromWrapperNodes(wrapperNodes);

			/*
			 * add mapping to wrapperMapping map
			 */
			this.wrapperMapping.put(selector_globalSchema, wrappersForSelector);

			this.initiateWatchService();
		}

	}

	private void initiateWatchService() throws IOException, InterruptedException {

		// this.sourceFilesMonitor = new
		// SourceFilesMonitor(this.sourceFilesDirectory);

		this.sourceFilesMonitor.startListening(this);

	}

	public List<IvisWrapper> getAvailableWrappers() {
		return availableWrappers;
	}

	public Map<String, List<IvisWrapper>> getWrapperMapping() {
		return wrapperMapping;
	}

	public SubqueryGenerator getSubqueryGenerator() {
		return subqueryGenerator;
	}

	/**
	 * Traverses list of available wrapper instances to find all wrappers that
	 * offer data for a selector against the global schema.
	 * 
	 * It compares the class name to the value of the wrapper node of the
	 * mapping file.
	 * 
	 * @param wrapperNodes
	 * @return
	 */
	private List<IvisWrapper> makeListFromWrapperNodes(List<Node> wrapperNodes) {
		List<IvisWrapper> wrappersForSelector = new ArrayList<IvisWrapper>();

		/*
		 * now for each wrapperNode
		 * 
		 * compare its value (class name of target wrapper) to the class names
		 * of availableWrapper-instances
		 */
		for (Node wrapperNode : wrapperNodes) {
			String classNameOfTargetWrapper = wrapperNode.getText();

			for (IvisWrapper wrapperInstance : this.availableWrappers) {
				/*
				 * if class name is identical, then target wrapper has been
				 * found!
				 */
				String classNameofCurrentWrapper = wrapperInstance.getClass().getSimpleName().toString();
				if (classNameOfTargetWrapper.equalsIgnoreCase(classNameofCurrentWrapper)) {
					wrappersForSelector.add(wrapperInstance);
					break;
				}
			}
		}

		return wrappersForSelector;
	}

	@Override
	public List<IvisObject> queryData(IvisQuery query) throws Exception {

		/*
		 * analyze the query (XPath expression) against the global schema
		 * 
		 * identify affected wrappers (data sources, that have to be accessed)
		 * 
		 * create and delegate sub-queries to wrappers (best as new Thread to
		 * allow concurrent execution!)
		 * 
		 * collect and return results (all queried objects!)
		 */
		List<IvisObject> retrievedItems = new ArrayList<IvisObject>();

		String selector_globalSchema = query.getSelector();

		/*
		 * create all subqueries identifying possible child nodes and attributes
		 * of the selected element of the global schema
		 */
		List<String> subquerySelectors = this.subqueryGenerator.findSubquerySelectors(query);

		/*
		 * find all wrapper instances, that offer data for the selected element
		 * of the global schema
		 */
		List<IvisWrapper> wrappersForSelector = this.wrapperMapping.get(selector_globalSchema);

		/*
		 * now forward the initial query object and the identified
		 * subquerySelectors to each found wrapper instance and collect its data
		 */
		for (IvisWrapper wrapper : wrappersForSelector) {

			try {
				List<IvisObject> retrievedObjectsForWrapper = wrapper.queryData(query, subquerySelectors);

				retrievedItems.addAll(retrievedObjectsForWrapper);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return retrievedItems;

	}

	@Override
	public boolean modifyDataInstance(RuntimeModificationMessage modificationMessage)
			throws UnsupportedEncodingException, DocumentException, IOException {

		boolean hasModified = false;

		if (!isCurrentlyModifying) {
			this.isCurrentlyModifying = true;

			/*
			 * identify affected wrapper (data source, that has to be accessed)
			 * 
			 * forward information to wrapper to create new data instance
			 * 
			 * collect and return results (new visualization object!)
			 */

			String wrapperReference = modificationMessage.getWrapperReference();

			for (IvisWrapper wrapper : availableWrappers) {
				if (wrapper.getClass().getSimpleName().equalsIgnoreCase(wrapperReference))
					hasModified = wrapper.modifyDataInstance(modificationMessage);
			}

			this.isCurrentlyModifying = false;
		}

		return hasModified;

	}

	@Override
	public IvisObject insertNewObject(RuntimeNewObjectMessage runtimeNewObjectMessage) {

		this.isCurrentlyModifying = true;

		/*
		 * identify affected wrapper (data source, that has to be accessed)
		 * 
		 * forward information to wrapper to create new data instance
		 * 
		 * collect and return results (new visualization object!)
		 */

		this.isCurrentlyModifying = false;

		return null;

	}

	@Override
	public List<IvisObject> onSynchronizationEvent(SynchronizationMessage syncMessage) throws Exception {

		IvisQuery query_globalSchema = syncMessage.getQuery();

		List<String> subquerySelectors_globalSchema = this.subqueryGenerator.findSubquerySelectors(query_globalSchema);

		String dataSourceIdentifier = syncMessage.getDataSourceIdentifier();

		/*
		 * recordId could be unset! It is not guaranteed to be set
		 */
		List<String> recordIds = syncMessage.getRecordIds();

		List<IvisObject> modifiedInstances = null;

		for (IvisWrapper wrapper : availableWrappers) {
			if (wrapper instanceof AbstractIvisFileWrapper) {
				AbstractIvisFileWrapper fileWrapper = (AbstractIvisFileWrapper) wrapper;
				/*
				 * for file based data sources will be a java.nio.Path object
				 */
				String file = dataSourceIdentifier;
				/*
				 * wrapper for 'text' files
				 * 
				 * compare dataSourceIdentifier to file name of wrapper
				 */
				if (fileWrapper.getSourceFile().getName().equalsIgnoreCase(file.toString())) {
					modifiedInstances = fileWrapper.onSourceFileChanged(query_globalSchema,
							subquerySelectors_globalSchema, recordIds);
					break;
				}
			} else if (wrapper instanceof AbstractIvisDataBaseWrapper) {
				AbstractIvisDataBaseWrapper databaseWrapper = (AbstractIvisDataBaseWrapper) wrapper;

				/*
				 * find the appropriate wrapper by comparing
				 * 'dataSourceIdentifier' to the class name of the wrapper!
				 */

				String className = dataSourceIdentifier;
				/*
				 * wrapper for databases
				 * 
				 * compare dataSourceIdentifier to class name of wrapper
				 */
				if (databaseWrapper.getClass().toString().equalsIgnoreCase(className)) {
					modifiedInstances = databaseWrapper.onSourceFileChanged(query_globalSchema,
							subquerySelectors_globalSchema, recordIds);
					break;
				}

				// TODO
			} else if (wrapper instanceof AbstractIvisWebServiceWrapper) {
				AbstractIvisWebServiceWrapper webserviceWrapper = (AbstractIvisWebServiceWrapper) wrapper;

				// TODO
			}
		}

		return modifiedInstances;

	}

	/**
	 * Identifies the instance of {@link AbstractIvisFileWrapper} to retrieve
	 * the IDs of modified instances
	 * 
	 * @param fileName
	 *            source file name of wrapper
	 * @return
	 * @throws IOException 
	 * @throws DocumentException 
	 */
	public List<String> fetchModifiedRecordIds(String fileName) throws DocumentException, IOException {
		for (IvisWrapper ivisWrapperInterface : availableWrappers) {
			if (ivisWrapperInterface instanceof AbstractIvisFileWrapper){
				AbstractIvisFileWrapper fileWrapper = (AbstractIvisFileWrapper) ivisWrapperInterface;
				
				if(fileName.equalsIgnoreCase(fileWrapper.getSourceFile().getName())){
					
					return fileWrapper.extractIdsOfModifiedRecords(this.subqueryGenerator); 
					
				}
			}
		}
		
		return null;
	}

}
