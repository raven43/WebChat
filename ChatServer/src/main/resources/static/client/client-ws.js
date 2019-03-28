;window.appendChat = function (config) {

    config = new ChatConfig(config);

    console.log(JSON.stringify(config, null, 3));

    let url = config.network.url;
    let stompClient = null;

    let wrapper = new ChatWrap(config.settings, config.css);

    wrapper.register(register);

    wrapper.exit(exit);

    wrapper.leave(leave);

    wrapper.send(send);

    function register(name) {
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
    }

    function exit() {
        if (stompClient !== null) {
            stompClient.disconnect();
        }
        console.log("Disconnected");
    }

    function send(message) {
        stompClient.send("/message", {}, JSON.stringify(message));
    }

    function leave() {
        stompClient.send("/command", {}, JSON.stringify({
            'type': 'LEAVE'
        }));
    }

    document.body.appendChild(wrapper.getHtml());
};
