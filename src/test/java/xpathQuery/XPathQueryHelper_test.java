package xpathQuery;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import config.WebSocketConfig;
import ivisQuery.FilterStrategy;
import ivisQuery.FilterType;
import ivisQuery.IvisFilterForQuery;
import mediator_wrapper.wrapper.impl.xml.XPathQueryHelper;
import mediator_wrapper.wrapper.impl.xml.XmlWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebSocketConfig.class, loader = AnnotationConfigContextLoader.class)
public class XPathQueryHelper_test {

	@Autowired
	private XmlWrapper xmlWrapper;

	private File sourceFile;

	private Map<String, String> schemaMapping;

	private String selectorGlobalSchema;

	private String selectorLocalSchema;

	private List<IvisFilterForQuery> filters;

	private FilterStrategy filterStrategy;

	@Before
	public void before() {
		this.sourceFile = xmlWrapper.getSourceFile();
		this.schemaMapping = xmlWrapper.getSchemaMapping();

		this.selectorGlobalSchema = "bookstore/book";
		this.selectorLocalSchema = "books/book";

		this.filterStrategy = FilterStrategy.AND;

		this.filters = new ArrayList<IvisFilterForQuery>();

		IvisFilterForQuery filter = new IvisFilterForQuery();
		filter.setFilterType(FilterType.EQUAL);
		filter.setSelector("bookstore/book/author");
		filter.setFilterValue("Bernhard Hennen");

		IvisFilterForQuery filter2 = new IvisFilterForQuery();
		filter2.setFilterType(FilterType.GREATER_THAN);
		filter2.setSelector("bookstore/book/price");
		filter2.setFilterValue(5);

		this.filters.add(filter);
		this.filters.add(filter2);
	}

	@Test
	public void test() {

		String expressionWithFilters = XPathQueryHelper.addFiltersToExpression(this.selectorLocalSchema, this.filters, this.filterStrategy,
				this.schemaMapping);
		
		assertNotNull(expressionWithFilters);
		assertTrue(expressionWithFilters.contains("[author='Bernhard Hennen' and cost/price>5]"));
	}

}
