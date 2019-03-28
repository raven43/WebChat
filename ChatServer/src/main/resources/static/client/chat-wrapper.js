/**
 * Wrapper for chat
 * do not reserve messages,
 * just show it
 *
 * Callbacks for user actions
 * {@link ChatWrap#send}
 * {@link ChatWrap#leave}
 * {@link ChatWrap#exit}
 * {@link ChatWrap#register}
 *
 * @param {ChatSettings} [initSettings]
 * @param {ChatCss} [css]
 * @constructor
 */
function ChatWrap(initSettings, css) {
    if (!this instanceof ChatWrap) return new ChatWrap();

    const self = this;

    initSettings = initSettings ? new ChatSettings(initSettings) : new ChatSettings();
    css = css ? new ChatCss(css) : new ChatCss();
    //if (!initSettings instanceof ChatSettings) initSettings = new ChatSettings(initSettings);
    //if (!css instanceof ChatCss) css = new ChatCss(css);

    //CREATE WRAP

    const chat = $('<div>', {class: 'chat-container right'});

    const header = $("<header>", {class: 'chat-header'}).appendTo(chat);
    const dragCtrl = $('<div>', {class: 'drag-ctrl'}).html('<b>✛</b>').appendTo(header);
    const chatLabel = $('<div>', {class: 'chat-label'}).appendTo(header);
    const btnSettings = $('<div>', {class: 'btn-settings'}).html('<b>⛭</b>').appendTo(header);
    const btnHide = $('<div>', {class: 'btn-hide'}).html('<b>–</b>').appendTo(header);
    const btnExit = $('<div>', {class: 'btn-exit'}).html('<b>×</b>').appendTo(header);
    const compLabel = $('<div>', {class: 'comp-label'}).appendTo(header);

    const labelInp = $('<div class="settings-el">' +
        '<label class="input-label" for="label-input">Chat label</label> ' +
        '<input id="label-input" class="input-control" type="text"></div>');
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
        .append(labelInp).append(positionSelect)
        .append(minCheck).append(dragCheck)
        .append(
            $('<div>', {class: 'settings-el'})
                .append(applyBtn).append(defaultBtn)
        )
        .hide()
        .appendTo(chat);

    const messageArea = $("<div>", {class: "message-history"})
        .appendTo(chat);

    const input = $("<input>", {class: "message-input", type: "text", placeholder: "Your name pls..."});
    const footer = $("<footer>", {class: 'message-form'}).append(input).appendTo(chat);


    //local var
    const dragController = new DragController(dragCtrl[0], chat[0]);
    let isHide = false;
    let isRegistered = false;
    let name;

    //css
    let servCss = '';
    let incCss = '';
    let outCss = '';

    //config variables
    let currentSettings;
    let chatPosition = 'right';
    let minimizeable = true;

    //minimize
    function toggle() {
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
    }

    this.toggle = toggle;

    function hide() {
        isHide = true;
        messageArea.hide('fast');
        footer.hide('fast');
        btnSettings.hide('fast');
        btnHide.hide('fast');
        btnExit.hide('fast');
    }

    this.hide = hide;

    function show() {
        isHide = false;
        messageArea.show('fast');
        footer.show('fast');
        btnSettings.show('fast');
        btnHide.show('fast');
        btnExit.show('fast');
    }

    this.show = show;


    /**
     *
     * @returns {ChatSettings}
     */
    function getSettingsFromUserForm() {
        return new ChatSettings({
            label: labelInp[0].children[1].value || initSettings.label,
            position: positionSelect[0].children[1].value || initSettings.position,
            drag: dragCheck[0].children[0].checked || initSettings.drag,
            minimize: minCheck[0].children[0].checked || initSettings.minimize
        });
    }

    /**
     *
     * @param {Object|ChatSettings} settings
     */
    function setConfigToUserForm(settings) {
        if (!settings instanceof ChatSettings) settings = new ChatSettings(settings);
        labelInp[0].children[1].value = settings.label;
        positionSelect[0].children[1].value = settings.position;
        dragCheck[0].children[0].checked = settings.drag;
        minCheck[0].children[0].checked = settings.minimize;
    }

    /**
     *
     * @param {Object|ChatSettings} settings
     */
    function configurate(settings) {
        if (!settings instanceof ChatSettings) settings = new ChatSettings(settings);

        currentSettings = settings;
        //setConfigToUserForm(settings);

        console.log('configurate chat with settings: ' + JSON.stringify(settings));
        //customize
        if (!settings.customize) btnSettings.remove();
        else btnSettings.show();
        //label
        chatLabel.text(settings.label);
        //position
        if (settings.position === 'left') {
            chat.addClass("left");
            chat.removeClass("right");
        } else {
            chat.addClass("right");
            chat.removeClass("left");
        }
        //minimize
        minimizeable = settings.minimize;
        //drag
        dragController.direct = settings.position;
        if (settings.drag) {
            dragCtrl.show();
            dragController.allow();
        }
        else {
            dragCtrl.hide();
            chat[0].style.right = "";
            chat[0].style.left = "";
            chat[0].style.bottom = "";
            dragController.forbid();
        }
    }

    this.configurate = configurate;

    /**
     * Add custom style to chat wrap
     *
     * @param {ChatCss} css
     */
    function addCss(css) {
        if (css instanceof ChatCss) css = new ChatCss(css);
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
            if (css.messageinput) input.addClass(css.messageinput);

            if (css.servmessage) servCss = css.servmessage;
            if (css.incmessage) incCss = css.incmessage;
            if (css.outmessage) outCss = css.outmessage;
        }
    }

    this.addCss = addCss;


    //INIT

    let savedConf = window.localStorage.getItem('chat-config');
    this.configurate(savedConf ? JSON.parse(savedConf) : initSettings);
    currentSettings = initSettings;
    this.addCss(css);
    this.toggle();


    this.setRegistered = function (bool) {
        isRegistered = bool;
        messageArea.html("");
        compLabel.html("");
        input.attr('placeholder', bool ? 'Your message..' : 'Your name..');
    };

    this.showMessage = function (message) {
        let senderRole = message.role;
        let senderName = message.name;
        let content = message.content;

        if (!senderRole && senderName)
            compLabel.text(senderName);

        let show = $('<div>');
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

    this.clearChat = function () {
        messageArea.html('');
    };

    let register;
    let send;
    let leave;
    let exit;

    //getter & setters to cb function
    this.register = function (cb) {
        if (cb) register = cb;
        else return register;
    };
    this.send = function (cb) {
        if (cb) send = cb;
        else return send;
    };
    this.leave = function (cb) {
        if (cb) leave = cb;
        else return leave;
    };
    this.exit = function (cb) {
        if (cb) exit = cb;
        else return exit;
    };

    //inner wrappers for cb functions
    function _register(name) {
        window.localStorage.setItem('chat-login', name);
        if (isHide) {
            self.toggle();
        }
        if (!isRegistered) {
            register(name);
            self.setRegistered(true);
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
        messageArea.html("");
        compLabel.html("");

        if (isRegistered) {
            exit();
            self.setRegistered(false);
            console.log("Exit");
        }
    }


    input.on('keypress', function (e) {
        if (e.keyCode === 13 && input.val()) {
            if (!isRegistered) {
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
        self.toggle();
    });
    btnHide.click(function () {
        if (isRegistered) _leave();
        self.toggle();
    });
    btnExit.click(function () {
        self.toggle();
        _exit();
    });
    btnSettings.click(function () {
        messageArea.toggle('fast');
        chatSettings.toggle('fast');
    });

    applyBtn.click(function () {
        let settings = getSettingsFromUserForm();
        currentSettings = settings;
        window.localStorage.setItem('chat-config', JSON.stringify(settings));
        self.configurate(settings);
    });
    defaultBtn.click(function () {
        window.localStorage.setItem('chat-config', JSON.stringify(initSettings));
        currentSettings = initSettings;
        self.configurate(initSettings);
    });

    this.getHtml = function () {
        return chat[0];
    }

}

/**
 * Wrap for chat settings
 *
 * @param {Object} [config] - source of setting
 * @returns {*|ChatSettings}
 * @constructor
 */
function ChatSettings(config) {
    if (!config) config = {};
    if (!this instanceof ChatSettings) return new ChatSettings(config);
    this.label = config.label ? config.label : "WebChat";
    this.position = config.position && (config.position === 'left' || config.position === 'right') ? config.position : "right";
    this.drag = config.hasOwnProperty('drag') ? config.drag : true;
    this.minimize = config.hasOwnProperty('minimize') ? config.minimize : true;
    this.customize = config.hasOwnProperty('customize') ? config.customize : true;
}

/**
 * Wrap for chat styles
 *
 * @param {Object} [css]
 * @constructor
 */
function ChatCss(css) {
    if (!css) css = {};
    this.body = css.body;
    this.header = css.header;
    this.settings = css.settings;
    this.history = css.history;
    this.footer = css.footer;
    this.label = css.label;
    this.complabel = css.complabel;
    this.settingslabel = css.settingslabel;
    this.settingsinput = css.settingsinput;
    this.messageinput = css.messageinput;
    this.servmessage = css.servmessage;
    this.incmessage = css.incmessage;
    this.outmessage = css.outmessage;
}

/**
 * Wrap for network params
 *
 * @param {Object} [network]
 * @constructor
 */
function ChatNetwork(network) {
    if (!network) network = {};
    //if (!this instanceof ChatNetwork) return new ChatNetwork(network);
    this.url = network.url ? network.url : 'http://localhost:8080';
    this.type = network.type ? network.type : 'ws';
}

/**
 * Wrap for whole chat configuration
 * including
 * {@link ChatNetwork}
 * {@link ChatSettings}
 * {@link ChatCss}
 *
 * @param config
 * @returns {*|ChatConfig}
 * @constructor
 */
function ChatConfig(config) {
    if (!config) config = {};
    //if (!this instanceof ChatConfig) return new ChatConfig(config);

    this.network = new ChatNetwork(config.network);
    this.settings = new ChatSettings(config.settings);
    this.css = new ChatCss(config.css);
}

/**
 * Simple class to make HTMLElement draggable
 *
 * @param {HTMLElement} control
 * @param {HTMLElement} draggable
 * @param {string} [dir] side of screen
 * @constructor
 */
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




