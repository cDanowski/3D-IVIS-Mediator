var stompClient = null;
        
        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
            document.getElementById('response').innerHTML = '';
        }
        
        function connect() {
            var socket = new SockJS('/changeColor');
            stompClient = Stomp.over(socket);            
            stompClient.connect({}, function(frame) {
                setConnected(true);
                console.log('Connected: ' + frame);
     
                stompClient.subscribe('/topic/changeColor', function(object){
                	//TODO
                    changeColor(JSON.parse(object.body));
                });
            });
        }
        
        function disconnect() {
            if (stompClient != null) {
                stompClient.disconnect();
            }
            setConnected(false);
            console.log("Disconnected");
        }
        
        function changeColor(object) {
            var shapeId = object.shapeId;
            
            var newColor = object.diffuseColor;
            
            $("#" + shapeId).find("material").attr("diffuseColor", newColor);
            
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode("Replaced color of shape with id=\"" + shapeId + "\""));
            response.appendChild(p);
        }
        
        function sendObject(x3dNode){
        	//x3dNode is a shape node
        	
        	var shapeId = $(x3dNode).attr("id");
        	var diffuseColor = $(x3dNode).find("material").attr("diffuseColor");
        	
        	var object = new Object();
        	
        	object.shapeId = shapeId;
        	object.diffuseColor = diffuseColor;
        	
        	stompClient.send("/app/changeColor", {}, JSON.stringify(object));
        }/**
 * 
 */