package wrappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import mediator_wrapper.wrapper.impl.CsvWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebSocketConfig.class, loader = AnnotationConfigContextLoader.class)
public class CsvWrapper_test {

	@Autowired
	private CsvWrapper csvWrapper;

	@Autowired
	SubqueryGenerator subqueryGenerator;

	private IvisQuery ivisQuery;
	
	double priceFilterValue = 4.99;

	@Before
	public void before() {

		String selectorGlobalSchema = "bookstore/book";

		FilterStrategy filterStrategy = FilterStrategy.AND;

		List<IvisFilterForQuery> filters = new ArrayList<IvisFilterForQuery>();

		IvisFilterForQuery filter = new IvisFilterForQuery();
		filter.setFilterType(FilterType.GREATER_THAN);
		filter.setSelector("bookstore/book/price");
		
		filter.setFilterValue(priceFilterValue);

		IvisFilterForQuery filter2 = new IvisFilterForQuery();
		filter2.setFilterType(FilterType.GREATER_THAN);
		filter2.setSelector("bookstore/book/@stock");
		filter2.setFilterValue(15);

		filters.add(filter);
		filters.add(filter2);

		this.ivisQuery = new IvisQuery();

		this.ivisQuery.setFilters(filters);
		this.ivisQuery.setSelector(selectorGlobalSchema);
		this.ivisQuery.setFilterStrategy(filterStrategy);
	}

	@Test
	public void test() throws Exception {
		List<IvisObject> retrievedData = csvWrapper.queryData(this.ivisQuery,
				subqueryGenerator.getSubqueryMapping().get("bookstore/book"));

		assertNotNull(retrievedData);
		assertTrue(retrievedData.size() > 0);

		Iterator<IvisObject> iterator = retrievedData.iterator();
		while (iterator.hasNext()) {
			IvisObject nextObject = iterator.next();

			assertEquals(nextObject.getElementName(), "book");

			List<AttributeValuePair> attributeValuePairs = nextObject.getAttributeValuePairs();

			assertTrue(attributeValuePairs.size() > 0);

			for (AttributeValuePair attributeValuePair : attributeValuePairs) {
				if (attributeValuePair.getName().equalsIgnoreCase("price"))
					assertTrue((Double.parseDouble( String.valueOf(attributeValuePair.getValue()))) > priceFilterValue);
			}
		}
	}

}
