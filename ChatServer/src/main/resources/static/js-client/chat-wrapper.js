;(function () {

    var chat = $('<div>', {class: 'chat-container'});
    var messageArea = $("<div>", {class: "message-history"});
    var input = $("<input>", {class: "message-input", type: "text", placeholder: "Your name pls..."});
    var header = $("<header>", {class: 'chat-header'});
    var footer = $("<footer>", {class: 'message-form'});
    var compLabel = $('<div>', {class: 'comp-label'});
    var btnHide = $('<div>', {class: 'btn-hide'});
    btnHide.html('<b>–</b>');
    var btnExit = $('<div>', {class: 'btn-exit'});
    btnExit.html('<b>×</b>');
    var chatLabel = $('<div>', {class: 'chat-label'});
    header.append(chatLabel);
    header.append(btnExit);
    header.append(btnHide);
    header.append(compLabel);
    footer.append(input);
    chat.append(header);
    chat.append(messageArea);
    chat.append(footer);

    var register;
    var send;
    var leave;
    var exit;

    var isHide = true;
    var isRegistred = false;
    var name;

    function wrapper(str) {
        chatLabel.text(str ? str : 'WebChat');
        messageArea.hide();
        footer.hide();
        btnHide.hide();
        btnExit.hide();
        chat.appendTo('body');
    }

    wrapper.showMessage = function (message) {
        var senderRole = message.role;
        var senderName = message.name;
        var content = message.content;

        if (!senderRole && senderName)
            compLabel.text(senderName);

        var show = $('<label>');
        show.addClass("message");
        switch (senderRole) {
            case "AGENT":
                show.addClass('incoming');
                break;
            case "CLIENT":
                show.addClass('outgoing');
                break;
            default:
                show.addClass('server');
        }
        show.text(content);
        messageArea.append(show);
        messageArea.scrollTop(messageArea[0].scrollHeight);
    };

    wrapper.clearChat = function () {
        messageArea.html('');
    };
    //getter & setters to cb function
    wrapper.register = function (cb) {
        if (cb) register = cb;
        else return register;
    };
    wrapper.send = function (cb) {
        if (cb) send = cb;
        else return send;
    };
    wrapper.leave = function (cb) {
        if (cb) leave = cb;
        else return leave;
    };
    wrapper.exit = function (cb) {
        if (cb) exit = cb;
        else return exit;
    };

    //inner wrappers
    function _register(name) {
        if (!isRegistred) {
            register(name);
            isRegistred = true;

            messageArea.html("");
            compLabel.html("");
            input.attr('placeholder', 'Your message..');

            console.log("Register");
        }
    }

    function _send() {
        var message = {
            'name': name,
            'role': 'CLIENT',
            'content': input.val()
        };
        send(message)
    }

    function _leave() {
        compLabel.text('');
        leave();
    }

    function _exit() {
        messageArea.html("");
        compLabel.html("");

        if (isRegistred) {
            exit();
            wrapper.clearChat();
            isRegistred = false;

            input.attr('placeholder', 'Your name..');
            messageArea.html("");
            compLabel.html("");

            console.log("Exit");
        }
    }


    input.on('keypress', function (e) {
        if (e.keyCode === 13 && input.val()) {
            if (!isRegistred) {
                name = input.val();
                _register(name);
            } else if (~input.val().indexOf("/leave"))
                _leave();
            else if (~input.val().indexOf("/exit"))
                _exit();
            else
                _send();

            input.val('');
        }
    });


    wrapper.toggle = function () {
        if (!isHide) {
            isHide = true;
            messageArea.hide('fast');
            footer.hide('fast');
            btnHide.hide('fast');
            btnExit.hide('fast');
        } else {
            isHide = false;
            messageArea.show('fast');
            footer.show('fast');
            btnHide.show('fast');
            btnExit.show('fast');
            input.focus();
        }
    };

    chatLabel.click(function () {
        wrapper.toggle();
    });
    btnHide.click(function () {
        _leave();
        wrapper.toggle();
    });

    btnExit.click(function () {
        wrapper.toggle();
        _exit();
    });

    wrapper.label = function (str) {
        return chatLabel.text(str);
    };

    window.wrapper = wrapper;

})();