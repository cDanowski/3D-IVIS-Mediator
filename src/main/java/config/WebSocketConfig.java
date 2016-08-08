package config;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import application_template.ApplicationTemplateInterface;
import application_template.impl.bookstoreTemplate.BookstoreApplicationTemplate;
import mediator_wrapper.mediation.impl.IvisMediator;
import mediator_wrapper.mediation.impl.SubqueryGenerator;
import mediator_wrapper.wrapper.IvisWrapperInterface;
import mediator_wrapper.wrapper.impl.CsvWrapper;
import mediator_wrapper.wrapper.impl.XmlWrapper;
import util.UrlConstants;

@Configuration
@ComponentScan("controller")
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	private String pathToWrapperMappingFile = "data/config/GlobalSchemaToWrapperMapping.xml";
	private String pathToSubqueryMappingFile = "data/config/SubqueryMapping.xml";

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		/*
		 * enable STOMP broker, to which client may subscribe in order to
		 * receive synchronization broadcasts
		 */
		config.enableSimpleBroker("/topic", "/queue");

		/*
		 * a global application prefix
		 */
		config.setApplicationDestinationPrefixes(UrlConstants.APPLICATION_PREFIX);
		//config.setUserDestinationPrefix(UrlConstants.APPLICATION_USER_PREFIX);
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {

		/*
		 * three endpoints for each user-initiated action
		 */

		/*
		 * 1. endpoint: initial request to visualize data
		 */
		registry.addEndpoint(UrlConstants.SERVER_SIDE_IVIS_ENDPOINT).withSockJS();

		/*
		 * 2. endpoint: runtime request (from within a running visualization) to
		 * visualize additional data
		 */
		registry.addEndpoint(UrlConstants.RUNTIME_ADDITIONAL_DATA_ENDPOINT).withSockJS();

		/*
		 * 2. endpoint: runtime request (from within a running visualization) to
		 * send data modifications to the server and apply them to the data
		 * sources
		 */
		registry.addEndpoint(UrlConstants.RUNTIME_MODIFICATION_ENDPOINT).withSockJS();
	}

	/**
	 * add spring beans for dependency injection
	 */

	/**
	 * configure xml wrapper instance
	 * 
	 * @return
	 * @throws DocumentException
	 */
	@Bean
	public XmlWrapper initializeXmlWrapper() throws DocumentException {
		String fileLocation = "data/data_sources/products.xml";
		String localSchemaMappingLocation = "data/config/XmlBookstoreMapping.xml";

		return new XmlWrapper(fileLocation, localSchemaMappingLocation);
	}

	/**
	 * configure csv wrapper instance
	 * 
	 * @return
	 * @throws DocumentException
	 */
	@Bean
	public CsvWrapper initializeCsvWrapper() throws DocumentException {
		String fileLocation = "data/data_sources/products.csv";
		String localSchemaMappingLocation = "data/config/CsvBookstoreMapping.xml";

		return new CsvWrapper(fileLocation, localSchemaMappingLocation);
	}

	/**
	 * create instance of SubqueryGenerator
	 * 
	 * @return
	 * @throws DocumentException
	 */
	@Bean
	public SubqueryGenerator subqueryGenerator() throws DocumentException {

		return new SubqueryGenerator(this.pathToSubqueryMappingFile);
	}

	/**
	 * configure mediator instance
	 * 
	 * @return
	 * @throws DocumentException
	 */
	@Bean
	public IvisMediator initializeMediator() throws DocumentException {
		List<IvisWrapperInterface> wrappers = new ArrayList<IvisWrapperInterface>();
		wrappers.add(initializeXmlWrapper());
		wrappers.add(initializeCsvWrapper());

		SubqueryGenerator subqueryGenerator = subqueryGenerator();

		IvisMediator mediator = new IvisMediator(wrappers, this.pathToWrapperMappingFile, subqueryGenerator);

		return mediator;
	}
	
	/**
	 * create instance of {@link BookstoreApplicationTemplate}
	 * 
	 * @return
	 * @throws DocumentException
	 */
	@Bean
	public ApplicationTemplateInterface bookstoreApplicationTemplate() throws DocumentException {

		return new BookstoreApplicationTemplate() ;
	}

}