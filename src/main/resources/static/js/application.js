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

function integrateSceneIntoDOM_runtime(additionalObjects){
	/*
	 * for each object, 
	 * 
	 * check if it already exists (using ID), then replace the existing object
	 * 
	 * if it is a new object insert it into the scene in front of the existing ones
	 */
	
	var numberOfObjects = additionalObjects.length;

	var maxNumberOfColumns = numberOfObjects / 2;

	var currentColumn = 0;

	// column translation
	var translation_x = 0;
	// row translation
	var translation_z = 5;

	var translationIncrement = 5;
	
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
			// it is a new object that has to be inserted at a new position
			var newX3domString = "<transform translation='" + translation_x + " 0 " + translation_z + "'>";
			newX3domString = newX3domString + x3domString;
			newX3domString = newX3domString + "</transform>";
			
			/*
			 * append new object to the "scene" element of the x3dom scene
			 */
			$("scene").append(newX3domString);
			
			currentColumn++;
			translation_x = translation_x + translationIncrement;

			if (currentColumn > maxNumberOfColumns) {
				// next row
				translation_z = translation_z + translationIncrement;
				translation_x = 0;

				currentColumn = 0;
			}
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
}
