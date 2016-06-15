package trigger;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import util.UrlConstants;

/**
 * Controller that handles trigger events, which occur when a data source was
 * updated (data instances have changed).
 * 
 * TODO really implement as spring Controller, reacting on a URL?
 * 
 * @author Christian Danowski
 *
 */
@Controller
public class DataSourceTriggerController {

	/**
	 * 
	 * 
	 * TODO really implement as spring Controller, reacting on a URL? SInce
	 * client will not trigger trigger events themselves. This will be initiated
	 * by the serve side that either scans data sources periodically or receives
	 * trigger notifications from data sources.
	 * 
	 * @param triggerMessage
	 * @return
	 */
	@MessageMapping(UrlConstants.TRIGGER_ENDPOINT)
	@SendTo("") // TODO inform all clients!
	public TriggerMessage handleTriggerEvent(Object triggerMessage) {

		
		return null;
	}

}
