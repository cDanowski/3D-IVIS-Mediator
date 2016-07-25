package wrappers;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.DocumentException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import config.WebSocketConfig;
import ivisObject.AttributeValuePair;
import ivisObject.IvisObject;
import ivisQuery.FilterStrategy;
import ivisQuery.FilterType;
import ivisQuery.IvisFilterForQuery;
import ivisQuery.IvisQuery;
import mediator_wrapper.mediation.impl.SubqueryGenerator;
import mediator_wrapper.wrapper.impl.XmlWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebSocketConfig.class, loader = AnnotationConfigContextLoader.class)
public class XmlWrapper_test {

	@Autowired
	private XmlWrapper xmlWrapper;

	@Autowired
	SubqueryGenerator subqueryGenerator;

	private IvisQuery ivisQuery;

	@Before
	public void before() {

		String selectorGlobalSchema = "bookstore/book";

		FilterStrategy filterStrategy = FilterStrategy.AND;

		List<IvisFilterForQuery> filters = new ArrayList<IvisFilterForQuery>();

		IvisFilterForQuery filter = new IvisFilterForQuery();
		filter.setFilterType(FilterType.EQUAL);
		filter.setSelector("bookstore/book/author");
		filter.setFilterValue("Bernhard Hennen");

		IvisFilterForQuery filter2 = new IvisFilterForQuery();
		filter2.setFilterType(FilterType.GREATER_THAN);
		filter2.setSelector("bookstore/book/price");
		filter2.setFilterValue(5);

		filters.add(filter);
		filters.add(filter2);

		this.ivisQuery = new IvisQuery();

		this.ivisQuery.setFilters(filters);
		this.ivisQuery.setSelector(selectorGlobalSchema);
		this.ivisQuery.setFilterStrategy(filterStrategy);
	}

	@Test
	public void test() throws DocumentException {
		List<IvisObject> retrievedData = xmlWrapper.queryData(this.ivisQuery, subqueryGenerator.getSubqueryMapping().get("bookstore/book"));
	
		assertNotNull(retrievedData);
		
		Iterator<IvisObject> iterator = retrievedData.iterator();
		while(iterator.hasNext()){
			IvisObject nextObject = iterator.next();
			
			assertEquals(nextObject.getElementName(), "book");
			
			List<AttributeValuePair> attributeValuePairs = nextObject.getAttributeValuePairs();
			
			assertTrue(attributeValuePairs.size() > 0);
			
			for (AttributeValuePair attributeValuePair : attributeValuePairs) {
				if (attributeValuePair.getName().equalsIgnoreCase("author"))
					assertEquals(attributeValuePair.getValue(), "Bernhard Hennen");
			}	
		}
	}

}
