let id = window.localStorage.getItem('chat-id');
let listener;

wrapper(window.chatConfig);

function testConnection() {

}

wrapper.register(function (name) {

    let storageId = window.localStorage.getItem('chat-id');
    if (storageId) {
        let xhr = new XMLHttpRequest();
        xhr.open('GET', 'http://localhost:8080/chat/' + storageId + '/test', true);
        xhr.onreadystatechange = function (ev) {
            if (xhr.readyState === 4 && xhr.status === 200) {
                console.log('no register ' + storageId);
                startListener(250);
            } else {
                console.log('register req');
                registerRequest();
            }
        };
        xhr.send();
    } else registerRequest();

    function registerRequest() {
        let xhr = new XMLHttpRequest();
        xhr.open('POST', 'http://localhost:8080/chat/register', true);
        xhr.onreadystatechange = function (ev) {
            if (xhr.status === 201) {
                id = xhr.responseText;
                window.localStorage.setItem('chat-id', id);
                startListener(250);
            }
        };
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.send('role=CLIENT&name=' + encodeURIComponent(name));
    }

});

function startListener(ms) {
    listener = setInterval(function () {
        let xhr = new XMLHttpRequest();
        xhr.open('GET', 'http://localhost:8080/chat/' + id, true);
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
}

wrapper.exit(function () {
    clearInterval(listener);
    let xhr = new XMLHttpRequest();
    let idcopy = window.localStorage.getItem('chat-id');
    xhr.open('DELETE', 'http://localhost:8080/chat/' + idcopy, true);
    xhr.send();
    console.log("Disconnected");
    id = undefined;
    window.localStorage.removeItem('chat-id');
});
wrapper.send(function (message) {
    let xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8080/chat/' + id, true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.send('message=' + encodeURIComponent(message.content));
});
wrapper.leave(function () {
    let xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8080/chat/' + id + '/leave', true);
    xhr.send();
    console.log("Leave");
});

if (window.localStorage.getItem('chat-id'))
    wrapper.register()(window.localStorage.getItem('chat-name'));