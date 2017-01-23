//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
webSocket.onmessage = function (msg) {
    updateChat(msg);
};
webSocket.onclose = function () {
    alert("WebSocket connection closed")
};
webSocket.onopen = login();

function login() {
    var username = getCookie("username");// getCookie("username");//= getCookie("username");
    if (username != "") {
        alert("HELLO " + username);
        webSocket.send("#username#*" + username);
    }
    else
        setUsername();
}
function setUsername() {
    var username = prompt("Type your username: ");
    if (username == null || username == "") {
        alert("You can't be without username");
        setUsername();
        return;
    }
    setCookie("username", username);
    webSocket.send("#username#*" + username);
}
//Send message if "Send" is clicked
id("send").addEventListener("click", function () {
    sendMessage(id("message").value);
});
id("addChannel").addEventListener("click", function () {
    addChannel();
});
function addChannel() {
    var channelName = prompt("How do you want to name your Channel?");
    if (channelName == "" || channelName == null) {
        alert("You have to name your channel!");
        addChannel();
        return;
    }
    webSocket.send("#addChannel#*" + channelName);
}

//Send message if enter is pressed in the input field
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) {
        sendMessage(e.target.value);
    }
});

//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
        webSocket.send(message);
        id("message").value = "";
    }
}
function joinChannel(channel){
    webSocket.send("#joinChannel#*"+channel)
}

//Update the chat-panel, and the list of connected users
function updateChat(msg) {
    var data = JSON.parse(msg.data);

    if (data.reason == "duplicate_username") {
        alert("this username is taken!");
        setUsername();
        return;
    }
    if (data.reason == "duplicate_channelname") {
        alert("this channelname is taken!");
        addChannel();
        //return;
    }


    if (data.reason == "message") {
        insert("chat", data.userMessage);
    }
        id("channellist").innerHTML = "";
        data.channellist.forEach(function (channel) {

            var znacznik = document.createElement('button');
            znacznik.onclick = function () {
                joinChannel(channel);
            };
            var t = document.createTextNode(channel);
            znacznik.appendChild(t);

            var kontener = id("channellist");
            kontener.appendChild(znacznik);
        });


}


//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function setCookie(name, value) {
    document.cookie = name + "=" + value + ";";
}
