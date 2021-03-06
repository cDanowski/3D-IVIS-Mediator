// constant definition
var SERVER_SIDE_VISUALIZATION_ENDPOINT = '/initial/visualize';
var RUNTIME_ADDITIONAL_DATA_ENDPOINT = '/runtime/visualizeAdditionalData';
var RUNTIME_MODIFY_ENDPOINT = '/runtime/modify';
var RUNTIME_NEW_OBJECT_ENDPOINT = '/runtime/new';
var DATA_SOURCE_CHANGE_ENDPOINT = '/dataSourceChange';
var SYNCHRONIZE_ENDPOINT = '/synchronize';

// STOMP ENDPOINTS
var STOMP_SERVER_SIDE_IVIS_ENDPOINT = '/user/queue'
		+ SERVER_SIDE_VISUALIZATION_ENDPOINT;
var STOMP_RUNTIME_ADDITIONAL_DATA_ENDPOINT = '/user/queue'
		+ RUNTIME_ADDITIONAL_DATA_ENDPOINT;
var STOMP_RUNTIME_MODIFY_ENDPOINT = '/user/queue' + RUNTIME_MODIFY_ENDPOINT;
var STOMP_RUNTIME_NEW_OBJECT_ENDPOINT = '/user/queue' + RUNTIME_NEW_OBJECT_ENDPOINT;
var STOMP_DATA_SOURCE_CHANGE_ENDPOINT = '/topic' + DATA_SOURCE_CHANGE_ENDPOINT;
var STOMP_SYNCHRONIZE_ENDPOINT = '/user/queue' + SYNCHRONIZE_ENDPOINT;

// send message endpoints with prefix
var SEND_SERVER_SIDE_VISUALIZATION_ENDPOINT = '/ivisApp'
		+ SERVER_SIDE_VISUALIZATION_ENDPOINT;
var SEND_RUNTIME_ADDITIONAL_DATA_ENDPOINT = '/ivisApp'
		+ RUNTIME_ADDITIONAL_DATA_ENDPOINT;
var SEND_RUNTIME_MODIFY_ENDPOINT = '/ivisApp'
	+ RUNTIME_MODIFY_ENDPOINT;
var SEND_RUNTIME_NEW_OBJECT_ENDPOINT = '/ivisApp'
	+ RUNTIME_NEW_OBJECT_ENDPOINT;
var SEND_SYNCHRONIZE_ENDPOINT = '/ivisApp'
	+ SYNCHRONIZE_ENDPOINT;

var APPLICATION_TEMPLATE_IDENTIFIER = "bookstoreApplicationTemplate";

// stomp client variable that holds the connection to server
var stompClient = null;

//global y-translation value needed to properly insert additional objects
var translation_y = -5;
var translationIncrement = 5;

var objectModalReference = "#objectModal";
var lastClickedObject;

var newStockValueReference = "#newStockValue";
var newStockValue;

var bookMetadataReference = "#bookMetadata";

$(document).ready(function() {
	// establish WebSocket connection
	connect();
	
	$(objectModalReference).modal({ show: false})
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
		
		// runtime user triggered modification
		stompClient.subscribe(STOMP_RUNTIME_MODIFY_ENDPOINT,
				function(object) {

					var runtimeModificationMessage = JSON.parse(object.body);

					var infoMessage = runtimeModificationMessage.responseInfoMessage;
					
					alert(infoMessage);
					
				});
		
		// data source change event
		stompClient.subscribe(STOMP_DATA_SOURCE_CHANGE_ENDPOINT, function(object) {
			if($("x3d").length > 0){
				var dataSourceChangeMessage = JSON.parse(object.body);
				
				sendSynchronizationRequest(dataSourceChangeMessage);
			}
			
		});

		// synchronization updates
		stompClient.subscribe(STOMP_SYNCHRONIZE_ENDPOINT, function(object) {

			var syncMessage = JSON.parse(object.body);
			
			var modifiedObjects = syncMessage.responseVisualizationObjects;

			synchronizeSceneContents(modifiedObjects);
			
			reloadAndZoomScene();
			
		});
	});
}

function disconnect() {
	if (stompClient != null) {
		stompClient.disconnect();
	}
	console.log("Disconnected");
}

function sendSynchronizationRequest(dataSourceChangeMessage){
	
	var dataSourceIdentifier = dataSourceChangeMessage.dataSourceIdentifier;
	
	// might be unset!
	var idsOfModifiedRecord = dataSourceChangeMessage.recordIds;
	
	var syncMessage = {};
	
	syncMessage.dataSourceIdentifier = dataSourceIdentifier;
	syncMessage.recordIds = idsOfModifiedRecord;
	syncMessage.applicationTemplateIdentifier = APPLICATION_TEMPLATE_IDENTIFIER;
	
	syncMessage.query = {};
	syncMessage.query.selector = 'bookstore/book';
	
	syncMessage = configureFilters(syncMessage);
	
	stompClient.send(SEND_SYNCHRONIZE_ENDPOINT, {}, JSON
			.stringify(syncMessage));
}

function synchronizeSceneContents(modifiedObjects){
	
	/*
	 * if an object already exists, then replace it!
	 * 
	 * if it is a new object, integrate it into scene!
	 */
	
	for (var i=0; i<modifiedObjects.length; i++){
		
		var object = modifiedObjects[i];
		
		// "_object" must be appended, since scene elements have this suffix!
		var jqueryExpression = "#" + object.id + "_object";
		
		if($(jqueryExpression).length > 0)
			replaceModifiedObject_runtime(object);
		else{
			alert("New content is available and added to the scene!");
			
			integrateIntoScene_runtime(object);
		}
			
	}
}

function visualizeBookStocks() {
	// create and send request to fetch initial scene!

	var serverSideVisualizationMessage = createServerSideVisualizationMessage();

	stompClient.send(SEND_SERVER_SIDE_VISUALIZATION_ENDPOINT, {}, JSON
			.stringify(serverSideVisualizationMessage));
}

function createServerSideVisualizationMessage() {

	var serverSideVisualizationMessage = {};

	serverSideVisualizationMessage.applicationTemplateIdentifier = APPLICATION_TEMPLATE_IDENTIFIER;

	serverSideVisualizationMessage.query = {};
	serverSideVisualizationMessage.query.selector = "bookstore/book";
	serverSideVisualizationMessage = configureFilters(serverSideVisualizationMessage);
	
	return serverSideVisualizationMessage;
}

function configureFilters(message) {
	if ($("#filter_enableCheckbox").is(':checked')) {
		// checked

		var authorFilterValue = $("#filter_author_value").val() || undefined;
		var authorFilterComparisonMethod = $(
		"#filter_author_comparison option:selected").text();
		var priceFilterValue = $("#filter_price_value").val() || undefined;
		var priceFilterComparisonMethod = $(
				"#filter_price_comparison option:selected").text();

		message.query.filters = [];

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
			
			message.query.filters.push(authorFilter);
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
			
			message.query.filters.push(priceFilter);
		}

		message.query.filterStrategy = $(
				"#filter_strategy option:selected").text();
	} else {
		// unchecked
		message.query.filters = undefined;
		message.query.filterStrategy = "AND";
	}

	return message;
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
	
	//Add a onclick callback to every node with class='ivis_visualizationObject'.
	$(".ivis_visualizationObject").each(function() {
		$(this).attr("onclick", "handleSingleClick(this)");

	});
}

//column translation
var translation_x_positive = 7;
var translation_x_negative = -7;

// rotation about 90°
var rotation_z_left = "0 0 1 1.57";
var rotation_z_right = "0 0 1 -1.57";

var isEvenIndex = false;

function integrateSceneIntoDOM_runtime(additionalObjects){
	/*
	 * for each object, 
	 * 
	 * check if it already exists (using ID), then replace the existing object
	 * 
	 * if it is a new object insert it into the scene in front of the existing ones
	 */

	var numberOfObjects = additionalObjects.length;
	
	for(var index=0; index < numberOfObjects; index++){
		var currentAdditionalObject = additionalObjects[index];
		
		var currentId = currentAdditionalObject.id;
		
		// "_object" must be appended, since scene elements have this suffix!
		var jqueryExpression = "#"+currentId + "_object";
		
		if($(jqueryExpression).length > 0){
			// replace the existing object
			replaceModifiedObject_runtime(currentAdditionalObject);
		}
		else{
			integrateIntoScene_runtime(currentAdditionalObject);
		}
	}
	
	reloadAndZoomScene();
}

function integrateIntoScene_runtime(object){
	var x3domString = object.visualizationObject;
	
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

function replaceModifiedObjects_runtime(modifiedObjects){
	for (var i=0; i<modifiedObjects.length; i++){
		replaceModifiedObject_runtime(modifiedObjects[i]);
	}
}

function replaceModifiedObject_runtime(modifiedObject){
	// replace the existing object
	
	var id = modifiedObject.id;
	var x3domString = modifiedObject.visualizationObject;
	
	// "_object" must be appended, since scene elements have this suffix!
	var jqueryExpression = "#" + id + "_object";
	
	if ($(jqueryExpression).length > 0){
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
		
		alert("The stock value of an object has been updated! \n\nThe object-id is: " + id);
	}
	
}

function onEnableFiltersChange() {
	if ($("#filter_enableCheckbox").is(':checked')) {
		// checked
		$('.filter').prop("disabled", false);
	} else {
		// unchecked
		$('.filter').prop("disabled", true);
	}
}

function onNewStockValueChange() {
	var newStockValue = $(newStockValueReference).val();
	
	if (!isNaN(newStockValue) && parseInt(Number(newStockValue)) == newStockValue && !isNaN(parseInt(newStockValue, 10))) {
		// valid integer input
		$('#changeStockValueButton').prop("disabled", false);
	} else {
		// no integer input
		$('#changeStockValueButton').prop("disabled", true);
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

// RUNTIME

/**
 * 
 * @param clickedObject is a transform node that groups all subelements of the visual object; 
 * 			also should have a child node 'MetadataSet' 
 * 			that comprises multiple 'MetadataString' elements holding object metadata
 * @returns
 */
function handleSingleClick(clickedObject){
	
	lastClickedObject = clickedObject;
	
	/**
	 * create a table with entries for each MetadataString element in DOM subtree
	 */
	var metadataElements = $(clickedObject).find("MetadataString");
	
	var newTableDomString = createTableFromMetadataElements(metadataElements);
	
	/**
	 * now delete previous table at target div and append new table
	 */
	resetObjectModal();
	
	$(bookMetadataReference).append(newTableDomString);
	
	$(objectModalReference).modal('show');
	
}

function resetObjectModal(){
	/**
	 * delete table and stock value
	 */
	$(bookMetadataReference).empty();
	$(newStockValueReference).val('');
}

function createTableFromMetadataElements(metadataElements){
	
	var tableString = "<table class='table table-striped'>";
	
	tableString += '	<thead>'
		
	tableString += '		<tr>'
		
	tableString += '			<th>Attribute Name</th>'
	tableString += '			<th>Attribute Value</th>'
		
	tableString += '		</tr>'
	
	tableString += '	</thead>'
		
	tableString += '	<tbody>'	
		
	// now append all entries	
	for (var i=0; i< metadataElements.length; i++){
		/**
		 * the metadata element has two attributes: 'name' and 'value'
		 */
		var metadata = metadataElements[i];
		
		var name = metadata.name;
		var value = metadata.value;
		
		tableString += '		<tr>'
			
		tableString += '			<td>' + name + '</th>'
		tableString += '			<td>' + value +'</th>'
				
		tableString += '		</tr>'
	}
		
	tableString += '	</tbody>'
	
	tableString += '</table>';
	
	return tableString;
}

function changeStockValue(){
	newStockValue = $(newStockValueReference).val();
	
	// create and send request to fetch initial scene!
	
	/*
	 * selector must point to the property of the global schema, which is modified
	 */
	var propertySelector_globalSchema = 'bookstore/book/@stock';

	var runtimeModificationMessage = createRuntimeModificationMessage(propertySelector_globalSchema, newStockValue);

	stompClient.send(SEND_RUNTIME_MODIFY_ENDPOINT, {}, JSON
			.stringify(runtimeModificationMessage));
	
	// hide modal
	$(objectModalReference).modal('hide');
	resetObjectModal();
}

function createRuntimeModificationMessage(propertySelector_globalSchema, newStockValue) {

	// need the last clicked object
	lastClickedObject;
	
	/*
	 * message has following properties:
	 * 
	 * public String applicationTemplateIdentifier;
	 * 
	 * public ModificationType modificationType;

	   public String wrapperReference;
	   
	   public String objectId;

	   public String propertySelector_globalSchema;

	   public Object newPropertyValue;

	   public Object responseVisualizationObject;
	 */
	
	var runtimeModificationMessage = {};

	runtimeModificationMessage.applicationTemplateIdentifier = APPLICATION_TEMPLATE_IDENTIFIER;

	/*
	 * the following properties are all defined by "MetadataString" elements 
	 * have an attribute with a certain name (according to the global schema)
	 */
	
	/*
	 * wrapperReference is an exception: it is not included in the global schema,
	 * but set when the object is extracted by the wrapper
	 */
	runtimeModificationMessage.wrapperReference = $(lastClickedObject).find('MetadataString[name="wrapperReference"]').attr('value');
	
	runtimeModificationMessage.objectId = 
	
	runtimeModificationMessage.propertySelector_globalSchema = propertySelector_globalSchema;
	
	runtimeModificationMessage.newPropertyValue = newStockValue;
	
	runtimeModificationMessage.query = {};
	runtimeModificationMessage.query.selector = "bookstore/book";
	runtimeModificationMessage.query.filters = [];
	runtimeModificationMessage.query.filterStrategy = "AND";

	/*
	 * filter for property id --> to filter the target object!
	 */
	var idFilter = {};
	idFilter.selector = "bookstore/book/@id";
	idFilter.filterValue = $(lastClickedObject).find('MetadataString[name="id"]').attr('value');;
	idFilter.filterType = "EQUAL";
	
	runtimeModificationMessage.query.filters.push(idFilter);
	
	/*
	 * responseVisualizationObject is not created by client, but by server!
	 */
	
	return runtimeModificationMessage;
}

