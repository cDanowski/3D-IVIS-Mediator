package autowireWrappers;

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
import mediator_wrapper.wrapper.IvisWrapperInterface;
import mediator_wrapper.wrapper.abstract_types.AbstractIvisFileWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebSocketConfig.class, loader = AnnotationConfigContextLoader.class)
public class AutoWireWrappers_test {

	@Autowired
	List<IvisWrapperInterface> wrappers;

	@Test
	public void testWrappers() {
		assertNotNull(wrappers);
		assertTrue(wrappers.size() > 0);

		for (IvisWrapperInterface wrapper : wrappers) {
			assertTrue(wrapper instanceof IvisWrapperInterface);

			if (wrapper instanceof AbstractIvisFileWrapper) {
				assertNotNull(((AbstractIvisFileWrapper) wrapper).getSourceFile());

				Map<String, String> schemaMapping = ((AbstractIvisFileWrapper) wrapper).getSchemaMapping();

				assertNotNull(schemaMapping);
				assertTrue(schemaMapping.size() > 0);

				Iterator<Entry<String, String>> schemaIterator = schemaMapping.entrySet().iterator();

				while (schemaIterator.hasNext()) {
					Entry<String, String> mappingEntry = schemaIterator.next();

					assertNotNull(mappingEntry);
					assertNotNull(mappingEntry.getKey());
					assertNotNull(mappingEntry.getValue());
				}
			}
		}
	}

}
