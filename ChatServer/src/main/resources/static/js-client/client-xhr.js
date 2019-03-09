window.startChat = function (config) {

    let url = config.network.url;
    let id;
    let listener;


    wrapper.register(function (name) {

        let xhr = new XMLHttpRequest();
        xhr.open('POST', url + '/chat/register', true);
        xhr.onreadystatechange = function (ev) {
            if (xhr.readyState !== 4) return;
            if (xhr.status === 201) {
                id = xhr.responseText;
                startListener(250);
            }
        };
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.send('role=CLIENT&name=' + encodeURIComponent(name));

    });

    function startListener(ms) {

        listener = setInterval(function () {
            let xhr = new XMLHttpRequest();
            xhr.open('GET', url + '/chat/' + id, true);
            xhr.onreadystatechange = function (ev) {
                if (xhr.readyState !== 4) return;
                if (xhr.status === 200) {
                    let list = JSON.parse(xhr.response);
                    for (var i = 0; i < list.length; i++)
                        wrapper.showMessage(list[i]);
                } else
                    clearInterval(listener);
            };
            xhr.send();
        }, ms);

        console.log('Short polling started: ' + listener);
    }

    wrapper.exit(function () {
        console.log('Short polling stopped: ' + listener);
        clearInterval(listener);
        let xhr = new XMLHttpRequest();
        xhr.open('DELETE', url + '/chat/' + idcopy, true);
        xhr.send();
        console.log("Disconnected");
        id = undefined;
    });

    wrapper.send(function (message) {
        let xhr = new XMLHttpRequest();
        xhr.open('POST', url + '/chat/' + id, true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.send('message=' + encodeURIComponent(message.content));
    });

    wrapper.leave(function () {
        let xhr = new XMLHttpRequest();
        xhr.open('POST', url + '/chat/' + id + '/leave', true);
        xhr.send();
        console.log("Leave");
    });

    wrapper(config.settings, config.css);

};
