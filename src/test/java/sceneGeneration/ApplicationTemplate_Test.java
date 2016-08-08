package sceneGeneration;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import application_template.impl.bookstoreTemplate.BookstoreApplicationTemplate;
import config.WebSocketConfig;
import controller.visualize.ServerSideVisualizationMessage;
import ivisObject.IvisObject;
import ivisQuery.FilterStrategy;
import ivisQuery.IvisFilterForQuery;
import ivisQuery.IvisQuery;
import mediator_wrapper.mediation.impl.IvisMediator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebSocketConfig.class, loader = AnnotationConfigContextLoader.class)
public class ApplicationTemplate_Test {

	@Autowired
	IvisMediator mediator;
	
	private ServerSideVisualizationMessage message;

	private List<IvisObject> queriedData;
	
	@Before
	public void before() throws Exception{
		String selectorGlobalSchema = "bookstore/book";
		
		List<IvisFilterForQuery> filters = new ArrayList<IvisFilterForQuery>();
		
		IvisQuery ivisQuery = new IvisQuery();

		ivisQuery.setFilters(filters);
		ivisQuery.setSelector(selectorGlobalSchema);
		ivisQuery.setFilterStrategy(FilterStrategy.AND);
		
		this.message = new ServerSideVisualizationMessage();
		this.message.setApplicationTemplateIdentifier("bookstoreApplicationTemplate");
		this.message.setQuery(ivisQuery);
		
		this.queriedData = this.mediator.queryData(ivisQuery);
	}
	
	@Test
	public void test() {
		BookstoreApplicationTemplate applTemplate = new BookstoreApplicationTemplate();
		
		String x3domScene = applTemplate.createInitialScene(queriedData);
		
		assertTrue(x3domScene.contains("<x3d"));
		
		assertTrue(x3domScene.contains("<scene>"));
		assertTrue(x3domScene.contains("</scene>"));
		
		assertTrue(x3domScene.contains("<box"));
		assertTrue(x3domScene.contains("<text"));
		
		assertTrue(x3domScene.contains("</x3d>"));
	}

}
