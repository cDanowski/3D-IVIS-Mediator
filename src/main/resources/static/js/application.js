// constant definition
var SERVER_SIDE_VISUALIZATION_ENDPOINT = '/initial/visualize';
var RUNTIME_ADDITIONAL_DATA_ENDPOINT = '/runtime/visualizeAdditionalData';
var RUNTIME_MODIFY_ENDPOINT = '/runtime/modify';

// STOMP ENDPOINTS
var STOMP_SERVER_SIDE_IVIS_ENDPOINT = '/user/queue'
		+ SERVER_SIDE_VISUALIZATION_ENDPOINT;
var STOMP_RUNTIME_ADDITIONAL_DATA_ENDPOINT = '/user/queue'
		+ RUNTIME_ADDITIONAL_DATA_ENDPOINT;
var STOMP_SYNCHRONIZE_ENDPOINT = '/topic/synchronize';

// send message endpoints with prefix
var SEND_SERVER_SIDE_VISUALIZATION_ENDPOINT = '/ivisApp'
		+ SERVER_SIDE_VISUALIZATION_ENDPOINT;
var SEND_RUNTIME_ADDITIONAL_DATA_ENDPOINT = '/ivisApp'
		+ RUNTIME_ADDITIONAL_DATA_ENDPOINT;
var SEND_RUNTIME_MODIFY_ENDPOINT = '/ivisApp' + RUNTIME_MODIFY_ENDPOINT;

var APPLICATION_TEMPLATE_IDENTIFIER = "bookstoreApplicationTemplate";

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
		stompClient.subscribe(STOMP_SERVER_SIDE_IVIS_ENDPOINT,
				function(object) {

					var serverSideVisualizationMessage = JSON.parse(object.body);

					var x3domSceneString = serverSideVisualizationMessage.responseScene;

					integrateSceneIntoDOM(x3domSceneString);
				});
		
		// runtime additional data
		stompClient.subscribe(STOMP_RUNTIME_ADDITIONAL_DATA_ENDPOINT,
				function(object) {

					var runtimeAdditionalDataMessage = JSON.parse(object.body);

					var additionalObjects = runtimeAdditionalDataMessage.additionalObjects;

					integrateSceneIntoDOM_runtime(additionalObjects);
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

	var serverSideVisualizationMessage = createServerSideVisualizationRequest();

	stompClient.send(SEND_SERVER_SIDE_VISUALIZATION_ENDPOINT, {}, JSON
			.stringify(serverSideVisualizationMessage));
}

function createServerSideVisualizationRequest() {

	var serverSideVisualizationMessage = {};

	serverSideVisualizationMessage.applicationTemplateIdentifier = APPLICATION_TEMPLATE_IDENTIFIER;

	serverSideVisualizationMessage.query = {};
	serverSideVisualizationMessage.query.selector = "bookstore/book";
	serverSideVisualizationMessage = configureFilters(serverSideVisualizationMessage);
	
	return serverSideVisualizationMessage;
}

function configureFilters(serverSideVisualizationMessage) {
	if ($("#filter_enableCheckbox").is(':checked')) {
		// checked

		var authorFilterValue = $("#filter_author_value").val() || undefined;
		var authorFilterComparisonMethod = $(
		"#filter_author_comparison option:selected").text();
		var priceFilterValue = $("#filter_price_value").val() || undefined;
		var priceFilterComparisonMethod = $(
				"#filter_price_comparison option:selected").text();

		serverSideVisualizationMessage.query.filters = [];

		if (authorFilterValue != undefined && authorFilterValue != "") {
			/*
			 * a filter has the following properties:
			 * 
			 * private String selector;
			 * 
			 * private Object filterValue;
			 * 
			 * private FilterType filterType;
			 */
			var authorFilter = {};
			authorFilter.selector = "bookstore/book/author";
			authorFilter.filterValue = authorFilterValue;
			authorFilter.filterType = authorFilterComparisonMethod;
			
			serverSideVisualizationMessage.query.filters.push(authorFilter);
		}
		
		if (priceFilterValue != undefined && priceFilterValue != "") {
			/*
			 * a filter has the following properties:
			 * 
			 * private String selector;
			 * 
			 * private Object filterValue;
			 * 
			 * private FilterType filterType;
			 */
			var priceFilter = {};
			priceFilter.selector = "bookstore/book/price";
			priceFilter.filterValue = priceFilterValue;
			priceFilter.filterType = priceFilterComparisonMethod;
			
			serverSideVisualizationMessage.query.filters.push(priceFilter);
		}

		serverSideVisualizationMessage.query.filterStrategy = $(
				"#filter_strategy option:selected").text();
	} else {
		// unchecked
		serverSideVisualizationMessage.query.filters = undefined;
		serverSideVisualizationMessage.query.filterStrategy = "AND";
	}

	return serverSideVisualizationMessage;
}

function integrateSceneIntoDOM(x3domSceneString) {

	/*
	 * identify "scene" div (with id "scene") and clear its content
	 * 
	 * then add new scene description as child nodes!
	 */
	$("#scene").empty();
	$("#scene").append(x3domSceneString);

	reloadAndZoomScene();

	$('.runtime').prop("disabled", false);
}

function reloadAndZoomScene(){
	// trigger reload to parse new x3d scene
	x3dom.reload();

	// zoom/fit to all elements of the scene
	var sceneElement = $('x3d').get(0);
	sceneElement.runtime.showAll();
}

//global y-translation value needed to properly insert additional objects
var translation_y = -5;

function integrateSceneIntoDOM_runtime(additionalObjects){
	/*
	 * for each object, 
	 * 
	 * check if it already exists (using ID), then replace the existing object
	 * 
	 * if it is a new object insert it into the scene in front of the existing ones
	 */
	
	var numberOfObjects = additionalObjects.length;

	// column translation
	var translation_x_positive = 7;
	var translation_x_negative = -7;
	
	// rotation about 90°
	var rotation_z_left = "0 0 1 1.57";
	var rotation_z_right = "0 0 1 -1.57";

	var translationIncrement = 5;
	
	var isEvenIndex = false;
	
	for(var index=0; index < numberOfObjects; index++){
		var currentAdditionalObject = additionalObjects[index];
		
		var currentId = currentAdditionalObject.id;
		var x3domString = currentAdditionalObject.visualizationObject;
		
		// "_object" must be appended, since scene elements have this suffix!
		var jqueryExpression = "#"+currentId + "_object";
		
		if($(jqueryExpression).length > 0){
			// replace the existing object
			
			/*
			 * get parent element
			 * 
			 * delete all child elements (including the target object)
			 * 
			 * append new child elements (with our new object)
			 */
			var parentElement = $(jqueryExpression).parent();
			
			parentElement.empty();
			
			parentElement.append(x3domString);
		}
		else{
			
			if(isEvenIndex){
				// translation to right of the scene
				var newX3domString = "<transform translation='" + translation_x_positive + " " + translation_y + " 0 '>";
				newX3domString = newX3domString + "	<transform rotation='" + rotation_z_right + "' >";
				newX3domString = newX3domString + x3domString;
				newX3domString = newX3domString + "	</transform>";
				newX3domString = newX3domString + "</transform>";
				
				// after each second object decrease y-translation
				translation_y = translation_y - translationIncrement;
				isEvenIndex = false;
			}
			else{
				// translation to left of the scene
				var newX3domString = "<transform translation='" + translation_x_negative + " " + translation_y + " 0 '>";
				newX3domString = newX3domString + "	<transform rotation='" + rotation_z_left + "' >";
				newX3domString = newX3domString + x3domString;
				newX3domString = newX3domString + "	</transform>";
				newX3domString = newX3domString + "</transform>";
				
				isEvenIndex = true;
			}
			
			/*
			 * append new object to the "scene" element of the x3dom scene
			 */
			$("scene").append(newX3domString);
		}
	}
	
	reloadAndZoomScene();
}

function onEnableFiltersChange() {
	if ($("#filter_enableCheckbox").is(':checked')) {
		// checked
		$('.form-control').prop("disabled", false);
	} else {
		// unchecked
		$('.form-control').prop("disabled", true);
	}
}

function visualizeBookStocks_runtime(){
	// create and send request to fetch additional scene objects!

	var runtimeAdditionalDataMessage = createRuntimeAdditionalDataRequest();

	stompClient.send(SEND_RUNTIME_ADDITIONAL_DATA_ENDPOINT, {}, JSON
			.stringify(runtimeAdditionalDataMessage));
}

function createRuntimeAdditionalDataRequest(){
	var runtimeAdditionalDataMessage = {};

	runtimeAdditionalDataMessage.applicationTemplateIdentifier = APPLICATION_TEMPLATE_IDENTIFIER;

	runtimeAdditionalDataMessage.query = {};
	runtimeAdditionalDataMessage.query.selector = "bookstore/book";
	runtimeAdditionalDataMessage = configureFilters(runtimeAdditionalDataMessage);
	
	return runtimeAdditionalDataMessage;
}

function resetScene(){
	/*
	 * remove the complete x3d scene subtree from DOM
	 */
	$("#scene").empty();
	
	$('.runtime').prop("disabled", true);
	
	// also reset translation value to initial value
	translation_y = -5;
}
