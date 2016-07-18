package util;

public class UrlConstants {
	
	/*
	 * server side spring controller endpoints
	 */
	public static final String SERVER_SIDE_IVIS_ENDPOINT = "/initial/visualize";
	public static final String RUNTIME_MODIFICATION_ENDPOINT = "/runtime/modify";
	public static final String RUNTIME_ADDITIONAL_DATA_ENDPOINT = "/runtime/visualizeAdditionalData";
	
	/*
	 * stomp endpoint, used to broadcast data changes to affected clients
	 */
	public static final String STOMP_CLIENT_SYNCHRONIZATION_ENDPOINT = "/topic/synchronize";
	public static final String APPLICATION_PREFIX = "/ivisApp/";

}
