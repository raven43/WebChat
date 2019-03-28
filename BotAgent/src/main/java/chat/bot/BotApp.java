package chat.bot;

import chat.common.Role;
import chat.common.message.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class BotApp {

    private static WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    private static ObjectMapper mapper = new ObjectMapper();

    private static String[] phrases = {
            "Hello!",
            "I am just Chat Bot",
            "I don't understand u, try again",
            "You are special!)",
            "Please leave me alone",
            "Don't worry"
    };

    private static String botName = "#bot";

    private static Scanner in = new Scanner(System.in);

    private static final String MESSAGE_FORMAT = "{ " +
            "\"name\" : \"%s\", " +
            "\"role\" : \"%s\", " +
            "\"content\" : \"%s\" " +
            "}";

    public static void main(String[] args) throws Exception {

        if (args.length > 0)
            botName = args[0];

        BotApp clientApp = new BotApp();
        StompSession stompSession = clientApp.connect().get();
        clientApp.subscribe(stompSession);

        boolean isRunning = true;
        while (isRunning) {
            String line = in.nextLine();
            isRunning = handleMessage(line, stompSession);
        }
    }


    private ListenableFuture<StompSession> connect() {

        Transport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
        List<Transport> transports = Collections.singletonList(webSocketTransport);
        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.put("name", Collections.singletonList(botName));
        stompHeaders.put("role", Collections.singletonList(Role.AGENT.toString()));

        String url = "ws://{host}:{port}/ws";
        return stompClient.connect(url, headers, stompHeaders, new MyHandler(), "localhost", 8080);
    }


    private void subscribe(final StompSession stompSession) {

        stompSession.subscribe("/private/reply", new StompFrameHandler() {

            public Type getPayloadType(StompHeaders stompHeaders) {
                return byte[].class;
            }

            public void handleFrame(StompHeaders stompHeaders, Object o) {
                try {
                    ChatMessage message = mapper.readValue(new String((byte[]) o), ChatMessage.class);
                    System.out.println(message);

                    boolean send = false;
                    try {
                        send = message.getRole() != null && message.getName() != null && !message.getName().equals(botName);
                    } catch (Throwable ignored) {
                    }

                    if (send) {
                        stompSession.send("/message", String.format(MESSAGE_FORMAT, botName, Role.AGENT, phrases[Math.round((int) (Math.random() * phrases.length))]).getBytes());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private class MyHandler extends StompSessionHandlerAdapter {
        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
            System.out.println("Successful connect");
        }
    }

    private static boolean handleMessage(String line, StompSession stompSession) {
        if (line.contains("/stop")) {
            stompSession.disconnect();
            return false;
        } else {
            stompSession.send("/message", String.format(MESSAGE_FORMAT, botName, Role.AGENT, line).getBytes());
            return true;
        }
    }

}
