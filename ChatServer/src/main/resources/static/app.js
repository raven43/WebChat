var stompClient = null;

function setConnected(connected) {

    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function setRegistred(registred) {
    if (registred) {
        $("#regarea").hide();
        $("#sendarea").show();
        $("#conversation").show();
        $("#header").text($("#role").val() + ' ' + $("#name").val());
        if ($("#role").val() == 'CLIENT') {
            $("#leave").show();
        }
        else {
            $("#leave").hide();
        }
    } else {
        $("#regarea").show();
        $("#sendarea").hide();
        $("#conversation").hide();
        $("#header").text('');
    }
    $("#greetings").html("");
}

function register() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    var headers = {
        'name': $("#name").val(),
        'role': $("#role").val()
    };
    stompClient.connect(headers,
        function (frame) {
            //setConnected(true);
            console.log('Connected: ' + frame);
            stompClient.subscribe('/private/reply', function (message) {


                showMessage(message);
            });
        }
    );
    setRegistred(true);
}

function exit() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
    setRegistred(false);
}


function sendMessage() {
    stompClient.send("/message", {}, JSON.stringify({
        'name': $("#name").val(),
        'role': $("#role").val(),
        'content': $("#message").val()
    }));
    $("#message").val("");
}

function leave() {
    stompClient.send("/command", {}, JSON.stringify({
        'type': 'LEAVE'
    }));

}


function showMessage(message) {

    var role = JSON.parse(message.body).role;
    var name = JSON.parse(message.body).name;
    var content = JSON.parse(message.body).content;

    var show = "<tr><td style='width: 20%'><b>" + (role == null ? '' : role) + " " + (name == null ? '' : name + ': ') + "</b></td><td>" + content + "</td></tr>";
    $("#greetings").append(show);
    window.scrollTo(0, document.body.scrollHeight);
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });


    $("#send").click(function () {
        sendMessage();
    });
    $("#register").click(function () {
        if ($("#name").val() !== "")
            register();
    });
    $("#leave").click(function () {
        leave();
    });
    $("#exit").click(function () {
        exit();
    });
});

