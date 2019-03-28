;"use strict";
(() => {

//  get config if exists
//  get wrapper script
//  get ws/xhr/fetch script
//  launch chat

    function loadScript(url) {
        return new Promise(function (resolve) {
            let script = document.createElement('script');
            script.type = 'text/javascript';
            script.src = url;
            script.onreadystatechange = resolve;
            script.onload = resolve;
            document.head.appendChild(script);
        });
    }

    function loadCss(url) {
        return new Promise(function (resolve) {
            let css = document.createElement('link');
            css.rel = 'stylesheet';
            css.href = url;
            css.onreadystatechange = resolve;
            css.onload = resolve;
            document.head.appendChild(css);
        });
    }

    function loadConfig(url) {
        return new Promise(function (resolve, reject) {
            let cnf;
            fetch(url)
                .then((response) =>
                    response.json()
                )
                .then((config) => {
                    cnf = config;
                })
                //.catch((reason) => reject(reason))
                .finally(() => {
                    resolve(cnf);
                })
        });
    }

    function mock() {
        return new Promise(function (resolve) {
            resolve();
        });
    }


    let cnf;
    let url;
    loadConfig('/config.json')
        .then(config => {
            console.log('we got config ' + config);
            config = validateConfig(config);
            cnf = config;
            url = cnf.network.url;
            return !window.$ ? loadScript(url + '/webjars/jquery/jquery.min.js') : mock();
        })
        .then(() => {
            return loadScript(url + '/client/chat-wrapper.js');
        })
        .then(() => {
            console.log('we got wrap');
            return loadCss(url + '/client/wrap.css')
        })
        .then(() => {
            console.log('we got css');
            switch (cnf.network.type) {
                case 'xhr':
                    return loadScript(url + '/client/client-xhr.js');
                case 'fetch':
                    return loadScript(url + '/client/client-fetch.js');
                default:
                    return loadScript(url + '/client/client-ws.js');
            }
        })
        .then(() => cnf.network.type === 'ws' ? (!window.SockJS ? loadScript(url + '/webjars/sockjs-client/sockjs.min.js') : mock()) : mock())
        .then(() => cnf.network.type === 'ws' ? (!window.Stomp ? loadScript(url + '/webjars/stomp-websocket/stomp.min.js') : mock()) : mock())
        .then(() => {
            console.log('we got client');
            console.log('here chat must starts');
            appendChat(cnf);
        })
        .catch(smth => console.error(smth));

    function validateConfig(config) {
        if (!config) config = {};
        if (!config.network) config.network = {};
        if (!config.network.url) config.network.url = 'http://localhost:8080';
        if (!config.network.type) config.network.type = 'ws';
        return config;
    }
})();
