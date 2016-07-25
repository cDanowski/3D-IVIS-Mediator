package autowireSubqueryGenerator;

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
import mediator_wrapper.mediation.impl.SubqueryGenerator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebSocketConfig.class, loader = AnnotationConfigContextLoader.class)
public class AutowireSubqueryGenerator_test {

	@Autowired
	SubqueryGenerator subqueryGenerator;

	@Test
	public void testSubqueryGenerator() {
		assertNotNull(subqueryGenerator);
	}
	
	@Test
	public void testSubqueryMapping() {
		Map<String, List<String>> subqueryMapping = subqueryGenerator.getSubqueryMapping();
		
		assertNotNull(subqueryMapping);
		assertTrue(subqueryMapping.size() > 0);
		
		Iterator<Entry<String, List<String>>> schemaIterator = subqueryMapping.entrySet().iterator();

		while (schemaIterator.hasNext()) {
			Entry<String, List<String>> mappingEntry = schemaIterator.next();

			assertNotNull(mappingEntry);
			assertNotNull(mappingEntry.getKey());
			assertNotNull(mappingEntry.getValue());
			assertTrue(mappingEntry.getValue().size() > 0);
		}
	}

}
