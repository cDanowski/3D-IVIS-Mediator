package config;

import java.io.IOException;
import java.sql.SQLException;
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
import mediator_wrapper.mediation.impl.dataSourceMonitor.DatabaseListener;
import mediator_wrapper.mediation.impl.dataSourceMonitor.SourceFilesMonitor;
import mediator_wrapper.wrapper.IvisWrapperInterface;
import mediator_wrapper.wrapper.impl.csv.CsvWrapper;
import mediator_wrapper.wrapper.impl.database.DatabaseWrapper;
import mediator_wrapper.wrapper.impl.xml.XmlWrapper;
import util.UrlConstants;

@Configuration
@ComponentScan("controller")
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	private static final String serviceURL = "http://localhost:8080";

	private String pathToWrapperMappingFile = "data/config/GlobalSchemaToWrapperMapping.xml";
	private String pathToSubqueryMappingFile = "data/config/SubqueryMapping.xml";
	private String sourceFilesDirectory = "data/data_sources";

	public static String getServiceURL() {
		return serviceURL;
	}

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
		// config.setUserDestinationPrefix(UrlConstants.APPLICATION_USER_PREFIX);
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
		 * 3. endpoint: runtime request (from within a running visualization) to
		 * send data modifications to the server and apply them to the data
		 * sources
		 */
		registry.addEndpoint(UrlConstants.RUNTIME_MODIFY_EXISTING_OBJECT_ENDPOINT).withSockJS();

		/*
		 * 4. endpoint: synchronization endpoint. Distributes data modifications
		 * to all connected clients
		 */
		registry.addEndpoint(UrlConstants.SYNCHRONIZATION_ENDPOINT).withSockJS();

		/*
		 * 5. endpoint: data source change event endpoint.
		 */
		// registry.addEndpoint(UrlConstants.DATA_SOURCE_CHANGE_ENDPOINT).withSockJS();
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
		String shadowCopyFileLocation = "data/data_sources_shadowCopies/products_shadowCopy.xml";
		String localSchemaMappingLocation = "data/config/XmlBookstoreMapping.xml";

		return new XmlWrapper(fileLocation, shadowCopyFileLocation, localSchemaMappingLocation);
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
		String shadowCopyFileLocation = "data/data_sources_shadowCopies/products_shadowCopy.csv";
		String localSchemaMappingLocation = "data/config/CsvBookstoreMapping.xml";

		return new CsvWrapper(fileLocation, shadowCopyFileLocation, localSchemaMappingLocation);
	}

	/**
	 * configure database wrapper instance
	 * 
	 * @return
	 * @throws DocumentException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IOException 
	 */
	@Bean
	public DatabaseWrapper initializeDatabaseWrapper() throws DocumentException, ClassNotFoundException, SQLException, IOException {
		// JDBC driver name and database URL
		String jdbc_driver = "org.postgresql.Driver";
		String db_url = "jdbc:postgresql://127.0.0.1:5432/bookstore";

		// Database credentials
		String user = "bookUser";
		String password = "bookPassword";

		String localSchemaMappingLocation = "data/config/DatabaseBookstoreMapping.xml";

		return new DatabaseWrapper(jdbc_driver, db_url, user, password, localSchemaMappingLocation,
				getDatabaseListener());
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
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	@Bean
	public IvisMediator initializeMediator()
			throws DocumentException, IOException, InterruptedException, ClassNotFoundException, SQLException {
		List<IvisWrapperInterface> wrappers = new ArrayList<IvisWrapperInterface>();
		wrappers.add(initializeXmlWrapper());
		wrappers.add(initializeCsvWrapper());
		wrappers.add(initializeDatabaseWrapper());

		SubqueryGenerator subqueryGenerator = subqueryGenerator();

		IvisMediator mediator = new IvisMediator(wrappers, this.pathToWrapperMappingFile, subqueryGenerator,
				getSourceFilesMonitor());

		return mediator;
	}

	@Bean
	public SourceFilesMonitor getSourceFilesMonitor() throws IOException {
		// TODO Auto-generated method stub
		return new SourceFilesMonitor(sourceFilesDirectory);
	}

	@Bean
	public DatabaseListener getDatabaseListener() throws IOException {
		// TODO Auto-generated method stub
		return new DatabaseListener();
	}

	/**
	 * create instance of {@link BookstoreApplicationTemplate}
	 * 
	 * @return
	 * @throws DocumentException
	 */
	@Bean
	public ApplicationTemplateInterface bookstoreApplicationTemplate() throws DocumentException {

		return new BookstoreApplicationTemplate();
	}

}