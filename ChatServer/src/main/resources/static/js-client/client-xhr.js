var id = null;
var listener;

wrapper('Some chat');
wrapper.register(function (name) {


    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8080/chat/register', true);
    xhr.onreadystatechange = function (ev) {
        if (xhr.status == 201) {
            id = xhr.responseText;
            startListener(250);
        }
    };
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.send('role=CLIENT&name=' + encodeURIComponent(name));
});

function startListener(ms) {
    listener = setInterval(function () {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', 'http://localhost:8080/chat/' + id, true);
        xhr.onreadystatechange = function (ev) {
            if (xhr.readyState != 4) return;
            if (xhr.status == 200) {
                var list = JSON.parse(xhr.response);
                for (var i = 0; i < list.length; i++)
                    wrapper.showMessage(list[i]);
            }
        };
        xhr.send();
    }, ms);
}

wrapper.exit(function () {
    clearInterval(listener);
    xhr.open('DELETE', 'http://localhost:8080/chat/' + id, true);
    xhr.send();
    console.log("Disconnected");
});
wrapper.send(function (message) {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8080/chat/' + id, true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.send('message=' + encodeURIComponent(message.content));
});
wrapper.leave(function () {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8080/chat/' + id + '/leave', true);
    xhr.send();
    console.log("Leave");
});