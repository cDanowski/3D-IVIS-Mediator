package mediator_wrapper.wrapper.abstract_types;

import java.net.URI;

/**
 * Abstract wrapper class that manages Web services as data sources.
 * 
 * @author Christian Danowski
 *
 */
public abstract class AbstractIvisWebServiceWrapper extends AbstractIvisWrapper {

	/**
	 * generic endpoint of the webservice
	 */
	private URI webServiceEndpoint;

	/**
	 * TODO generic methods to send requests to the Web service?
	 * 
	 * does that make sense here? or do different services require an individual
	 * implementation?
	 */

	/**
	 * Sends a HTTP GET request to the specified service endpoint.
	 * 
	 * @param requestParameters
	 *            the requestParameters which represent key-value-pairs that are
	 *            used to construct the query string, which is appended to the
	 *            service endpoint.
	 * @return queried objects
	 */
	public Object sendGetRequest(Object requestParameters) {

		/*
		 * build the request and send it to retrieve the response
		 */

		/*
		 * now call the process method, which is implemented by each final child
		 * class, that analyzes the serviceResponse
		 */
		// return this.processServiceResponse(serviceResponse);
		return null;
	}

	/**
	 * Sends a HTTP POST request to the specified service endpoint.
	 * 
	 * @param requestPayload
	 *            the POST payload which is sent with the request
	 * @return queried objects
	 */
	public Object sendPostRequest(Object requestPayload) {
		/*
		 * build the request and send it to retrieve the response
		 */

		/*
		 * now call the process method, which is implemented by each final child
		 * class, that analyzes the serviceResponse
		 */
		// return this.processServiceResponse(serviceResponse);
		return null;
	}

	/**
	 * Analyzes and processes the response returned from the Web service (due to
	 * a previous query request)
	 * 
	 * @param serviceResponse
	 *            the service-specific response
	 * @return queried objects
	 */
	public abstract Object processServiceResponse(Object serviceResponse);

}
