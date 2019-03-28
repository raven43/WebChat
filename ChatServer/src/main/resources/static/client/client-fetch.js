;window.appendChat = function (config) {

    config = new ChatConfig(config);

    let url = config.network.url;
    let id;
    let listener;

    let wrapper = new ChatWrap(config.settings, config.css);

    wrapper.register(register);

    wrapper.exit(exit);

    wrapper.leave(leave);

    wrapper.send(send);

    function register(name) {

        fetch(url + '/chat/register', {
            method: 'POST',
            body: 'role=CLIENT&name=' + encodeURIComponent(name),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
            .then(function (response) {
                if (response.status === 201)
                    return response.text();
                else
                    throw new Error('Register Failed ' + response.status);
            })
            .then(function (val) {
                id = val;
                startListener(250);
            })
            .catch(console.log);
    }

    function startListener(ms) {
        listener = setInterval(function () {
            fetch(url + '/chat/' + id)
                .then(function (response) {
                    if (response.status === 200)
                        return response.json();
                    else
                        throw new Error('Short polling error' + response.status);
                })
                .then(function (list) {
                    for (var i = 0; i < list.length; i++)
                        wrapper.showMessage(list[i]);
                })
                .catch(function (reason) {
                    console.log(reason);
                    clearInterval(listener);
                });
        }, ms);
    }

    function exit() {
        clearInterval(listener);
        fetch(url + '/chat/' + id, {method: 'DELETE'}).catch(console.log);
        console.log("Disconnected");
    }

    function send(message) {
        fetch(url + '/chat/' + id, {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'message=' + encodeURIComponent(message.content)
        })
            .catch(console.error);
        console.log("Send message " + message);

    }

    function leave() {
        fetch(url + '/chat/' + id + '/leave', {method: 'POST'})
            .catch(console.log);
        console.log("Leave");
    }


    document.body.appendChild(wrapper.getHtml());
};