// constant definition
var SERVER_SIDE_VISUALIZATION_ENDPOINT = '/initial/visualize';
var RUNTIME_ADDITIONAL_DATA_ENDPOINT = '/runtime/visualizeAdditionalData';
var RUNTIME_MODIFY_ENDPOINT = '/runtime/modify';

//STOMP ENDPOINTS
var STOMP_SERVER_SIDE_IVIS_ENDPOINT = '/user/queue' + SERVER_SIDE_VISUALIZATION_ENDPOINT;
var STOMP_RUNTIME_ADDITIONAL_DATA_ENDPOINT = '/user/queue' + RUNTIME_ADDITIONAL_DATA_ENDPOINT;
var STOMP_SYNCHRONIZE_ENDPOINT = '/topic/synchronize';

//send message endpoints with prefix
var SEND_SERVER_SIDE_VISUALIZATION_ENDPOINT = '/ivisApp' + SERVER_SIDE_VISUALIZATION_ENDPOINT;
var SEND_RUNTIME_ADDITIONAL_DATA_ENDPOINT = '/ivisApp' + RUNTIME_ADDITIONAL_DATA_ENDPOINT;
var SEND_RUNTIME_MODIFY_ENDPOINT = '/ivisApp' + RUNTIME_MODIFY_ENDPOINT;

// stomp client variable that holds the connection to server
var stompClient = null;

$(document).ready(function() {
	// establish WebSocket connection
	connect();
});

function connect() {
	var socket = new SockJS(SERVER_SIDE_VISUALIZATION_ENDPOINT);
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		console.log('Connected: ' + frame);

		// subscriptions to STOMP endpoints
		
		// initial scene
		stompClient.subscribe(STOMP_SERVER_SIDE_IVIS_ENDPOINT, function(object) {

			var returnedQueryResult = JSON.parse(object.body);
			
			var x3domSceneString = returnedQueryResult.responseScene;
			
			integrateSceneIntoDOM(x3domSceneString);
		});
		
		// synchronization updates
		stompClient.subscribe(STOMP_SYNCHRONIZE_ENDPOINT, function(object) {
			// TODO
			applyUpdate(JSON.parse(object.body));
		});
	});
}

function disconnect() {
	if (stompClient != null) {
		stompClient.disconnect();
	}
	console.log("Disconnected");
}

function applyUpdate(object) {
	// TODO
}

function visualizeBookStocks() {
	// create and send request to fetch initial scene!

	var serverSideVisualizationMessage = {};
	
	serverSideVisualizationMessage.applicationTemplateIdentifier = "bookstoreApplicationTemplate";
	
	serverSideVisualizationMessage.query = {};
	serverSideVisualizationMessage.query.selector = "bookstore/book";
	serverSideVisualizationMessage.query.filters = undefined;
	serverSideVisualizationMessage.query.filterStrategy = "AND";

	stompClient.send(SEND_SERVER_SIDE_VISUALIZATION_ENDPOINT, {}, JSON.stringify(serverSideVisualizationMessage));
}

function integrateSceneIntoDOM(x3domSceneString){
	
	/*
	 * identify "scene" div (with id "scene") and clear its content
	 * 
	 * then add new scene description as child nodes!
	 */
	$("#scene").empty();
	$("#scene").append(x3domSceneString);
	
	// trigger reload to parse new x3d scene
	x3dom.reload();
	
	// zoom/fit to all elements of the scene
	var sceneElement = $('x3d').get(0);
	sceneElement.runtime.showAll();
	
	console.log("should see new scene");
}
