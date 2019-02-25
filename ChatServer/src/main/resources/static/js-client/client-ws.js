;"use strict";
let stompClient = null;

//TODO full ES6 and "use strict" support

wrapper(window.chatConfig);

wrapper.register(function (name) {
    let socket = new SockJS("http://localhost:8080/ws");
    stompClient = Stomp.over(socket);
    let headers = {
        'name': name,
        'role': 'CLIENT'
    };
    stompClient.connect(headers,
        function (frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/private/reply', function (message) {
                let parsedMessage = {
                    name: JSON.parse(message.body).name,
                    role: JSON.parse(message.body).role,
                    content: JSON.parse(message.body).content
                };
                wrapper.showMessage(parsedMessage);
            });
        }
    );
});
wrapper.exit(function () {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
});
wrapper.send(function (message) {
    stompClient.send("/message", {}, JSON.stringify(message));
});
wrapper.leave(function () {
    stompClient.send("/command", {}, JSON.stringify({
        'type': 'LEAVE'
    }));
});