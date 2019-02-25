;"use strict";
(function () {

    const chat = $('<div>', {class: 'chat-container right'});
    const messageArea = $("<div>", {class: "message-history"});
    const input = $("<input>", {class: "message-input", type: "text", placeholder: "Your name pls..."});
    const header = $("<header>", {class: 'chat-header'});
    const footer = $("<footer>", {class: 'message-form'});
    const compLabel = $('<div>', {class: 'comp-label'});
    const btnSettings = $('<div>', {class: 'btn-settings'});
    btnSettings.html('<b>⚙</b>');
    const btnHide = $('<div>', {class: 'btn-hide'});
    btnHide.html('<b>–</b>');
    const btnExit = $('<div>', {class: 'btn-exit'});
    btnExit.html('<b>×</b>');

    const chatLabel = $('<div>', {class: 'chat-label'});

    const chatSettings = $('<div></div>', {class: 'chat-settings'});
    //settings
    chatSettings.hide();
    const labelInp = $('<div>' +
        '<label class="input-label" for="label-input">Chat label</label> ' +
        '<input id="label-input" class="input-control" type="text">' +
        '</div>');
    const positionSelect = $('<div>' +
        '<label class="input-label" for="pos-select">Position</label>' +
        '<select id="pos-select" class="input-control">' +
        '<option class="input-control" value="right">right</option>' +
        '<option class="input-control" value="left">left</option>' +
        '</select>' +
        '</div>');
    const urlInput = $('<div>' +
        '<label class="input-label" for="url-input">URL</label>' +
        '<input class="input-control" id="url-input" class="input-control" type="url">' +
        '</div>');
    const minCheck = $('<div>' +
        '<input class="input-control" id="min-check" type="checkbox">' +
        '<label class="input-label" for="min-check">Allow to minimize</label>' +
        '</div>');
    const dragCheck = $('<div>' +
        '<input class="input-control" id="drag-check" type="checkbox">' +
        '<label class="input-label" for="drag-check">Allow to drag</label>' +
        '</div>');

    const defaultBtn = $('<button class="default-btn">Default</button>');
    const applyBtn = $('<button class="apply-btn">Apply</button>');


    chatSettings.append(labelInp);
    chatSettings.append(positionSelect);
    chatSettings.append(urlInput);
    chatSettings.append(minCheck);
    chatSettings.append(dragCheck);
    chatSettings.append(applyBtn);
    chatSettings.append(defaultBtn);
    //header
    header.append(chatLabel);
    header.append(btnExit);
    header.append(btnHide);
    header.append(btnSettings);
    header.append(compLabel);
    //footer
    footer.append(input);
    //body
    chat.append(header);
    chat.append(chatSettings);
    chat.append(messageArea);
    chat.append(footer);

    let register;
    let send;
    let leave;
    let exit;

    let isHide = false;
    let isRegistred = false;
    let name;
    //config variables
    let chatPosition = 'right';
    let dragable = false;
    let minimizeable = true;

    wrapper.toggle = function () {
        if (minimizeable) {

            isHide = !isHide;
            messageArea.toggle('fast');
            footer.toggle('fast');
            btnSettings.toggle('fast');
            btnHide.toggle('fast');
            btnExit.toggle('fast');
        }
    };

    wrapper.getConfig = function () {
        return {
            label: labelInp[0].children[1].value || 'WebChat',
            position: positionSelect[0].children[1].value || 'right',
            drag: dragCheck[0].children[0].checked || false,
            minimize: minCheck[0].children[0].checked || true,
        };
    };
    wrapper.setConfig = function (config) {
        labelInp[0].children[1].value = config.label || chatLabel.text();
        positionSelect[0].children[1].value = config.position || chatPosition;
        dragCheck[0].children[0].checked = config.drag || dragable;
        minCheck[0].children[0].checked = config.minimize || minimizeable;
    };

    function validateSettings(settings) {
        if (!settings || typeof settings !== 'object') settings = {};
        if (!settings.label) settings.label = "WebChat";
        if (!settings.position || (settings.position !== 'left' && settings.position !== 'right')) settings.position = "right";
        if (!settings.drag) settings.drag = false;
        if (!settings.minimize) settings.minimize = true;
        return settings;
    }


    wrapper.configurate = function (settings) {

        settings = validateSettings(settings);
        //save config
        window.localStorage.setItem('chat-config', JSON.stringify(settings));
        //label
        chatLabel.text(settings.label);
        //position
        if (chatPosition !== settings.position) {
            chatPosition = settings.position;
            chat.toggleClass("left");
            chat.toggleClass("right");
        }
        //minimize
        minimizeable = settings.minimize;
        //drag
        dragable = settings.drag;
        //...
    };

    //init
    function wrapper(settings) {
        settings = validateSettings(settings);
        window.localStorage.setItem('chat-config', JSON.stringify(settings));
        wrapper.setConfig(settings);
        wrapper.configurate(settings);
        wrapper.toggle();
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
        window.localStorage.setItem('chat-login', name);
        if (isHide) {
            wrapper.toggle();
        }
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
        window.localStorage.removeItem('chat-login');
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


    chatLabel.click(function () {
        wrapper.toggle();
    });
    btnHide.click(function () {
        if (isRegistred) _leave();
        wrapper.toggle();
    });
    btnExit.click(function () {
        wrapper.toggle();
        _exit();
    });
    btnSettings.click(function () {
        messageArea.toggle('fast');
        chatSettings.toggle('fast');
    });

    applyBtn.click(function () {
        wrapper.configurate(wrapper.getConfig());
    });
    defaultBtn.click(function () {
        wrapper.setConfig(JSON.parse(window.localStorage.getItem('chat-config')))
    });

    wrapper.label = function (str) {
        return chatLabel.text(str);
    };

    window.wrapper = wrapper;

})();