;"use strict";
(function () {
    //chat body
    const chat = $('<div>', {class: 'chat-container right'});

    //header
    const dragCtrl = $('<div>', {class: 'drag-ctrl'}).html('<b>✛</b>');
    const chatLabel = $('<div>', {class: 'chat-label'});
    const btnSettings = $('<div>', {class: 'btn-settings'}).html('<b>⛭</b>');
    const btnHide = $('<div>', {class: 'btn-hide'}).html('<b>–</b>');
    const btnExit = $('<div>', {class: 'btn-exit'}).html('<b>×</b>');
    const compLabel = $('<div>', {class: 'comp-label'});
    const header = $("<header>", {class: 'chat-header'})
        .append(dragCtrl)
        .append(chatLabel)
        .append(btnExit)
        .append(btnHide)
        .append(btnSettings)
        .append(compLabel)
        .appendTo(chat);

    //settings
    const labelInp = $('<div class="settings-el">' +
        '<label class="input-label" for="label-input">Chat label</label> ' +
        '<input id="label-input" class="input-control" type="text">' +
        '</div>');
    const positionSelect = $('<div class="settings-el">' +
        '<label class="input-label" for="pos-select">Position</label>' +
        '<select id="pos-select" class="input-control">' +
        '<option class="input-control" value="right">right</option>' +
        '<option class="input-control" value="left">left</option>' +
        '</select>' +
        '</div>');
    const minCheck = $('<div class="settings-el">' +
        '<input class="input-control" id="min-check" type="checkbox">' +
        '<label class="input-label" for="min-check">Allow to minimize</label>' +
        '</div>');
    const dragCheck = $('<div class="settings-el">' +
        '<input class="input-control" id="drag-check" type="checkbox">' +
        '<label class="input-label" for="drag-check">Allow to drag</label>' +
        '</div>');

    const defaultBtn = $('<button class="default-btn">Default</button>');
    const applyBtn = $('<button class="apply-btn">Apply</button>');

    const chatSettings = $('<div></div>', {class: 'chat-settings'})
        .append(labelInp)
        .append(positionSelect)
        .append(minCheck)
        .append(dragCheck)
        .append(
            $('<div>', {class: 'settings-el'})
                .append(applyBtn)
                .append(defaultBtn)
        )
        .hide()
        .appendTo(chat);

    //message history
    const messageArea = $("<div>", {class: "message-history"})
        .appendTo(chat);

    //footer
    const input = $("<input>", {class: "message-input", type: "text", placeholder: "Your name pls..."});
    const footer = $("<footer>", {class: 'message-form'})
        .append(input)
        .appendTo(chat);


    //local var
    const dragController = new DragController(dragCtrl[0], chat[0]);
    let isHide = false;
    let isRegistred = false;
    let name;

    //css
    let servCss = '';
    let incCss = '';
    let outCss = '';

    //config variables
    let currentConfig;
    let chatPosition = 'right';
    let minimizeable = true;

    //configuration
    function validateConfig(config) {
        if (!config || typeof config !== 'object') config = {};
        if (!config.label) config.label = "WebChat";
        if (!config.position || (config.position !== 'left' && config.position !== 'right')) config.position = "right";
        if (!config.hasOwnProperty('drag')) config.drag = false;
        if (!config.hasOwnProperty('minimize')) config.minimize = true;
        if (!config.hasOwnProperty('customize')) config.customize = defaultSettings ? defaultSettings.customize : true;
        return config;
    }

    wrapper.getConfig = function () {
        return currentConfig;
    };

    function getConfigFromSettings() {
        return {
            label: labelInp[0].children[1].value || defaultSettings.label,
            position: positionSelect[0].children[1].value || defaultSettings.position,
            drag: dragCheck[0].children[0].checked || defaultSettings.drag,
            minimize: minCheck[0].children[0].checked || defaultSettings.minimize
        };
    }

    wrapper.setConfig = function (config) {
        currentConfig = config;
        //to settings window
        labelInp[0].children[1].value = config.label || defaultSettings.label;
        positionSelect[0].children[1].value = config.position || defaultSettings.position;
        dragCheck[0].children[0].checked = config.hasOwnProperty('drag') ? config.drag : defaultSettings.drag;
        minCheck[0].children[0].checked = config.hasOwnProperty('minimize') ? config.minimize : defaultSettings.minimize;
    };

    wrapper.configurate = function () {
        currentConfig = validateConfig(currentConfig);
        console.log('configurate chat with: ' + JSON.stringify(currentConfig));
        //customize
        if (!currentConfig.customize) btnSettings.remove();
        //label
        chatLabel.text(currentConfig.label);
        //position
        if (chatPosition !== currentConfig.position) {
            chatPosition = currentConfig.position;
            chat.toggleClass("left");
            chat.toggleClass("right");
        }
        //minimize
        minimizeable = currentConfig.minimize;
        //drag
        dragController.direct = currentConfig.position;
        if (currentConfig.drag) {
            dragCtrl.show();
            dragController.allow();
        }
        else {
            dragCtrl.hide();
            dragController.forbid();
            chat[0].style.right = "";
            chat[0].style.left = "";
            chat[0].style.bottom = "";
        }
    };

    wrapper.toggle = function () {
        if (minimizeable) {
            footer.toggle('fast');
            if (isHide) {
                messageArea.show();
            }
            else {
                messageArea.hide('fast');
                chatSettings.hide('fast');
            }
            btnSettings.toggle('fast');
            btnHide.toggle('fast');
            btnExit.toggle('fast');
            isHide = !isHide;
        }
    };

    wrapper.hide = function () {
        isHide = true;
        messageArea.hide('fast');
        footer.hide('fast');
        btnSettings.hide('fast');
        btnHide.hide('fast');
        btnExit.hide('fast');
    };

    wrapper.show = function () {
        isHide = false;
        messageArea.show('fast');
        footer.show('fast');
        btnSettings.show('fast');
        btnHide.show('fast');
        btnExit.show('fast');
    };

    wrapper.setRegistred = function (bool) {
        isRegistred = bool;
        messageArea.html("");
        compLabel.html("");
        input.attr('placeholder', bool ? 'Your message..' : 'Your name..');
    };

    //init
    let defaultSettings;

    function wrapper(settings, css) {

        defaultSettings = validateConfig(settings);
        let savedConf = window.localStorage.getItem('chat-config');
        wrapper.setConfig(savedConf ? savedConf : defaultSettings);
        wrapper.configurate();
        setCss(css);
        wrapper.toggle();

        chat.appendTo('body');
    }

    function setCss(css) {
        if (css) {
            if (css.body) chat.addClass(css.body);
            if (css.header) header.addClass(css.header);
            if (css.settings) chatSettings.addClass(css.settings);
            if (css.history) history.addClass(css.history);
            if (css.footer) footer.addClass(css.footer);
            if (css.label) chatLabel.addClass(css.label);
            if (css.complabel) compLabel.addClass(css.complabel);
            if (css.settingslabel) $('.input-label').addClass(css.settingslabel);
            if (css.settingsinput) $('.input-control').addClass(css.settingsinput);
            if (css.messageinput) input.addClass(css.messageinput)

            if (css.servmessage) servCss = css.servmessage;
            if (css.incmessage) incCss = css.incmessage;
            if (css.outmessage) outCss = css.outmessage;
        }

    }

    wrapper.showMessage = function (message) {
        let senderRole = message.role;
        let senderName = message.name;
        let content = message.content;

        if (!senderRole && senderName)
            compLabel.text(senderName);

        let show = $('<label>');
        show.addClass("message");
        switch (senderRole) {
            case "AGENT":
                show.addClass('incoming');
                show.addClass(incCss);
                break;
            case "CLIENT":
                show.addClass('outgoing');
                show.addClass(outCss);
                break;
            default:
                show.addClass('server');
                show.addClass(servCss);
        }
        show.text(content);
        messageArea.append(show);
        messageArea.scrollTop(messageArea[0].scrollHeight);
    };

    wrapper.clearChat = function () {
        messageArea.html('');
    };

    //cb functions
    let register;
    let send;
    let leave;
    let exit;

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

    //inner wrappers for cb functions
    function _register(name) {
        window.localStorage.setItem('chat-login', name);
        if (isHide) {
            wrapper.toggle();
        }
        if (!isRegistred) {
            register(name);
            wrapper.setRegistred(true);
            console.log("Register");
        }
    }

    function _send() {
        let message = {
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
            wrapper.setRegistred(false);
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
        let cnf = getConfigFromSettings();
        window.localStorage.setItem('chat-config', JSON.stringify(cnf));
        wrapper.setConfig(cnf);
        wrapper.configurate();
    });
    defaultBtn.click(function () {
        window.localStorage.setItem('chat-config', JSON.stringify(defaultSettings));
        wrapper.setConfig(defaultSettings);
        wrapper.configurate();
    });

    wrapper.label = function (str) {
        return chatLabel.text(str);
    };

    wrapper.companion = function (str) {
        return compLabel.text(str);
    };

    window.wrapper = wrapper;

    function DragController(control, draggable, dir) {
        function validateDir(val) {
            return !val || (val !== "right" && val !== "left") ? "right" : val;
        }

        let isAllow = false;
        let direct = validateDir(dir);

        Object.defineProperty(this, "direct", {
            configurable: false,
            get: function () {
                return direct;
            },
            set: function (val) {
                direct = validateDir(val);
            }
        });
        this.allow = function () {
            prepareEl(direct);
            control.onmousedown = direct === "left" ? onMouseDownLeftBind : onMouseDownRightBind;
            isAllow = true;
        };

        this.forbid = function () {
            control.onmousedown = null;
            isAllow = false;
        };

        this.toggle = function () {
            if (isAllow) this.forbid();
            else this.allow();
        };

        function prepareEl(dir) {
            if (dir === 'right') {
                let docWidth = document.documentElement.clientWidth;
                let coords = draggable.getBoundingClientRect();
                draggable.style.right = docWidth - coords.right;
                draggable.style.left = "";
            }
            if (dir === 'left') {
                let coords = draggable.getBoundingClientRect();
                draggable.style.left = coords.left;
                draggable.style.right = "";
            }
        }

        function onMouseDownLeftBind(e) {
            let docHeight = document.documentElement.clientHeight;

            let coordinates = draggable.getBoundingClientRect();
            let shiftX = e.pageX - coordinates.left + pageXOffset;
            let shiftY = e.pageY - coordinates.bottom + pageYOffset;

            draggable.style.position = 'fixed';
            document.body.appendChild(draggable);

            draggable.style.zIndex = 1000;

            function move(e) {
                draggable.style.left = e.pageX - shiftX + 'px';
                draggable.style.bottom = docHeight - (e.pageY - shiftY) + 'px';
            }

            let onmm = document.onmousemove;
            let onmu = document.onmouseup;

            document.onmousemove = function (e) {
                move(e);
            };
            document.onmouseup = function () {
                document.onmousemove = onmm;
                document.onmouseup = onmu;
            }
        }

        function onMouseDownRightBind(e) {
            let docWidth = document.documentElement.clientWidth;
            let docHeight = document.documentElement.clientHeight;

            let coordinates = draggable.getBoundingClientRect();
            let shiftX = e.pageX - coordinates.right;
            let shiftY = e.pageY - coordinates.bottom;

            draggable.style.position = 'fixed';
            document.body.appendChild(draggable);

            draggable.style.zIndex = 1000;

            function move(e) {
                draggable.style.right = docWidth - (e.pageX - shiftX) + 'px';
                draggable.style.bottom = docHeight - (e.pageY - shiftY) + 'px';
            }

            let onmm = document.onmousemove;
            let onmu = document.onmouseup;

            document.onmousemove = function (e) {
                move(e);
            };
            document.onmouseup = function () {
                document.onmousemove = onmm;
                document.onmouseup = onmu;
            }
        }
    }

})();