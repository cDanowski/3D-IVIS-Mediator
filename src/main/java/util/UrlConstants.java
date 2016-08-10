package util;

public class UrlConstants {
	
	/*
	 * server side spring controller endpoints
	 */
	public static final String SERVER_SIDE_IVIS_ENDPOINT = "/initial/visualize";
	public static final String RUNTIME_ADDITIONAL_DATA_ENDPOINT = "/runtime/visualizeAdditionalData";
	
	public static final String RUNTIME_MODIFY_EXISTING_OBJECT_ENDPOINT = "/runtime/modify";
	public static final String RUNTIME_NEW_OBJECT_ENDPOINT = "/runtime/newObject";
	
	/*
	 * stomp endpoint, used to broadcast data changes to affected clients
	 */
	public static final String STOMP_CLIENT_SYNCHRONIZATION_ENDPOINT = "/topic/synchronize";
	public static final String STOMP_CLIENT_SERVER_SIDE_IVIS_ENDPOINT = "/queue" + SERVER_SIDE_IVIS_ENDPOINT;
	public static final String STOMP_CLIENT_RUNTIME_ADDITIONAL_DATA_ENDPOINT = "/queue" + RUNTIME_ADDITIONAL_DATA_ENDPOINT;
	public static final String APPLICATION_PREFIX = "/ivisApp";
	public static final String APPLICATION_USER_PREFIX = "/user";

}
