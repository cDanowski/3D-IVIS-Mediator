package mediator_wrapper.mediation.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;

import controller.runtime.modify.RuntimeModificationMessage;
import mediator_wrapper.mediation.IvisMediatorInterface;
import mediator_wrapper.wrapper.IvisWrapperInterface;

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

	List<IvisWrapperInterface> availableWrappers;

	private Map<String, List<IvisWrapperInterface>> wrapperMapping;

	/**
	 * attribute shall indicate whether a user already performs a modification
	 * request. Different Modifications should not happen at the same time.
	 * 
	 * Could return an info message, if another user already updates something
	 */
	private boolean isCurrentlyModifying = false;

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
	public IvisMediator(List<IvisWrapperInterface> wrappers, 
			String pathToWrapperMappingFile) throws DocumentException{
		
		this.availableWrappers = wrappers;
		
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
	 */
	private void instantiate(String pathToWrapperMappingFile) throws DocumentException {

		/*
		 * parse document
		 */
		File inputFile = new File(pathToWrapperMappingFile);
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputFile);

		/*
		 * new instnce of wrapperMapping
		 */
		this.wrapperMapping = new HashMap<String, List<IvisWrapperInterface>>();

		Element rootElement = document.getRootElement();

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
			 * create a list of all wrapper instances that offer data for selector
			 */
			List<Node> wrapperNodes = node.selectNodes(XPATH_EXPRESSION_WRAPPER);
			
			List<IvisWrapperInterface> wrappersForSelector = makeListFromWrapperNodes(wrapperNodes);
			
			/*
			 * add mapping to wrapperMapping map
			 */
			this.wrapperMapping.put(selector_globalSchema, wrappersForSelector);
		}

	}

	public List<IvisWrapperInterface> getAvailableWrappers() {
		return availableWrappers;
	}

	public Map<String, List<IvisWrapperInterface>> getWrapperMapping() {
		return wrapperMapping;
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
	private List<IvisWrapperInterface> makeListFromWrapperNodes(List<Node> wrapperNodes) {
		List<IvisWrapperInterface> wrappersForSelector = new ArrayList<IvisWrapperInterface>();
		
		/*
		 * now for each wrapperNode 
		 * 
		 * compare its value (class name of target wrapper) to the class names of 
		 * availableWrapper-instances  
		 */
		for (Node wrapperNode : wrapperNodes) {
			String classNameOfTargetWrapper = wrapperNode.getText();
			
			for (IvisWrapperInterface wrapperInstance : this.availableWrappers) {
				/*
				 * if class name is identical, then target wrapper has been found!
				 */
				String classNameofCurrentWrapper = wrapperInstance.getClass().getSimpleName().toString();
				if(classNameOfTargetWrapper.equalsIgnoreCase(classNameofCurrentWrapper)){
					wrappersForSelector.add(wrapperInstance);
					break;
				}
			}
		}
		
		return wrappersForSelector;
	}

	@Override
	public Object queryData(String queryAgainstGlobalSchema) {

		/*
		 * analyze the query against the global schema
		 * 
		 * identify affected wrappers (data sources, that have to be accessed)
		 * 
		 * create and delegate sub-queries to wrappers (best as new Thread to
		 * allow concurrent execution!)
		 * 
		 * collect and return results (all queried objects!)
		 */

		return null;

	}

	@Override
	public Object modifyDataInstances(RuntimeModificationMessage modificationMessage) {

		this.isCurrentlyModifying = true;

		/*
		 * analyze the query against the global schema
		 * 
		 * identify affected wrappers (data sources, that have to be accessed)
		 * 
		 * create and delegate sub-queries to wrappers (best as new Thread to
		 * allow concurrent execution!)
		 * 
		 * collect and return results (all queried objects!)
		 */

		this.isCurrentlyModifying = false;

		return null;

	}

}
