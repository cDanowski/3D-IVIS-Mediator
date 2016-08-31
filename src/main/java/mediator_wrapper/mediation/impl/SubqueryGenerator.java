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

import ivisQuery.IvisQuery;
import mediator_wrapper.wrapper.IvisWrapper;

/**
 * Helper class to find all subqueries for a certain selector that points to
 * parent elements of the global schema.
 * 
 * A parent element has child elements and/or attributes, for which a subquery
 * has to be created.
 * 
 * Hence, a subquery refers to a single "final" element/attribute of the global
 * schema
 * 
 * @author Christian Danowski
 *
 */
public class SubqueryGenerator {
	
	private static final String XPATH_EXPRESSION_SUBQUERY_SELECTOR = "subqueries/subquerySelector";

	private static final String XPATH_EXPRESSION_SELECTOR = "selector";

	private static final String XPATH_EXPRESSION_MAPPING_ELEMENT = "//mapping";
	
	private Map<String, List<String>> subqueryMapping;
	
	@Autowired
	public SubqueryGenerator(String pathToSubqueryMappingFile) throws DocumentException {
		this.instantiate(pathToSubqueryMappingFile);
	}

	private void instantiate(String pathToSubqueryMappingFile) throws DocumentException {

		/*
		 * parse document
		 */
		File inputFile = new File(pathToSubqueryMappingFile);
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputFile);

		/*
		 * new instance of subqueryMapping
		 */
		this.subqueryMapping = new HashMap<String, List<String>>();

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
			 * create a list of all subquerySelectors
			 */
			List<Node> subquerySelectorNodes = node.selectNodes(XPATH_EXPRESSION_SUBQUERY_SELECTOR);

			List<String> subquerySelectors = makeListFromSubquerySelectorNodes(subquerySelectorNodes);

			/*
			 * add mapping to wrapperMapping map
			 */
			this.subqueryMapping.put(selector_globalSchema, subquerySelectors);
		}
		
	}

	public Map<String, List<String>> getSubqueryMapping() {
		return subqueryMapping;
	}

	/**
	 * parse the values of each subquerySelectorNode and add it to a new list of
	 * subquerySelectors
	 * 
	 * @param subquerySelectorNodes
	 * @return a list of XPath expressions
	 */
	private List<String> makeListFromSubquerySelectorNodes(List<Node> subquerySelectorNodes) {
		List<String> subquerySelectors = new ArrayList<>(subquerySelectorNodes.size());

		for (Node subquerySelectorNode : subquerySelectorNodes) {
			subquerySelectors.add(subquerySelectorNode.getText());
		}

		return subquerySelectors;
	}

	/**
	 * Creates 
	 * @param query
	 * @return
	 */
	public List<String> findSubquerySelectors(IvisQuery query) {
		/*
		 * get selector from query object
		 * 
		 * use subQueryMapping object to find all subqueries for that selector!
		 * 
		 *  e.g.: in the xml document
		 * 
		 * <book stock="" language="">
			<title></title>
			<author></author>
			<category></category>
			<price currency=""></price>
		   </book>
		 * 
		 * when the selector is 'book', then the mapping file will contain a 
		 * subquery definition for each child element and attribute of book
		 * 
		 * Hence, the subqueries point to a "final" element of the global schema, 
		 * which does not have any child elements
		 */
		
		String selector_globalSchema = query.getSelector();

		/*
		 * if the above selector is present within the mapping object
		 * then return the corresponding list of subquerySelectors
		 */
		if (this.subqueryMapping.containsKey(selector_globalSchema))
			return this.subqueryMapping.get(selector_globalSchema);

		/*
		 * else: no mapping for the specified selector was found.
		 * 
		 * Thus, just return the initial query selector.
		 * 
		 * THis might happen, if the initial selector already 
		 * points to a final node without child nodes
		 */
		else {
			List<String> subquerySelectors = new ArrayList<String>();
			subquerySelectors.add(query.getSelector());
			return subquerySelectors;
		}
			
	}

}
