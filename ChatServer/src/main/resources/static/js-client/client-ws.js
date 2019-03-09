;"use strict";
window.startChat = function (config) {

    let url = config.network.url;
    let stompClient = null;

    wrapper.register(function (name) {
        let socket = new SockJS(url + "/ws");
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

    wrapper(config.settings, config.css);
};


// fetch("/config.json").then(function (response) {
//     return response.json();
// })
//     .then(function (cnf) {
//         console.log(cnf);
//         window.chatConf = cnf;
//     })
//     .catch(function (reason) {
//         console.log(reason);
//     })
//     .finally(function () {
//         wrapper(window.chatConf);
//     });
