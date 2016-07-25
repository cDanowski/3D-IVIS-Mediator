package autowireMediator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import config.WebSocketConfig;
import mediator_wrapper.mediation.impl.IvisMediator;
import mediator_wrapper.wrapper.IvisWrapperInterface;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebSocketConfig.class, loader = AnnotationConfigContextLoader.class)
public class AutowireMediator_test {

	@Autowired
	IvisMediator mediator;

	@Test
	public void testMediator() {
		assertNotNull(mediator);
	}

	@Test
	public void testAvailableWrappers() {

		List<IvisWrapperInterface> availableWrappers = mediator.getAvailableWrappers();

		assertNotNull(availableWrappers);
		assertTrue(availableWrappers.size() > 0);
	}

	@Test
	public void testSchemaMapping() {

		Map<String, List<IvisWrapperInterface>> wrapperMapping = mediator.getWrapperMapping();

		assertNotNull(wrapperMapping);
		assertTrue(wrapperMapping.size() > 0);

		Iterator<Entry<String, List<IvisWrapperInterface>>> mappingIterator = wrapperMapping.entrySet().iterator();

		while (mappingIterator.hasNext()) {
			Entry<String, List<IvisWrapperInterface>> mappingEntry = mappingIterator.next();

			assertNotNull(mappingEntry);
			assertNotNull(mappingEntry.getKey());
			assertNotNull(mappingEntry.getValue());
		}

	}

}
