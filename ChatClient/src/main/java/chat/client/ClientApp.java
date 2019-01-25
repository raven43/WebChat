package chat.client;

import chat.common.Role;
import chat.common.message.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
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

public class ClientApp {

    private static Logger logger = Logger.getLogger(ClientApp.class);


    private static WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    private static ObjectMapper mapper = new ObjectMapper();
    private static String name = null;
    private static Role role = null;

    public ListenableFuture<StompSession> connect(String name, Role role) {

        Transport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
        List<Transport> transports = Collections.singletonList(webSocketTransport);
        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.put("name", Collections.singletonList(name));
        stompHeaders.put("role", Collections.singletonList(role.toString()));

        String url = "ws://{host}:{port}/ws";
        return stompClient.connect(url, headers, stompHeaders, new MyHandler(), "localhost", 8080);
    }

    public void subscribe(StompSession stompSession, String subTopic) {

        stompSession.subscribe(subTopic, new StompFrameHandler() {

            public Type getPayloadType(StompHeaders stompHeaders) {
                return byte[].class;
            }

            public void handleFrame(StompHeaders stompHeaders, Object o) {
                try {
                    ChatMessage message = mapper.readValue(new String((byte[]) o), ChatMessage.class);
                    if (!name.equals(message.getName()) && !role.equals(message.getRole()))
                        logger.info(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private class MyHandler extends StompSessionHandlerAdapter {
        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
            logger.info("Successful connect");
        }

    }

    private static void register() {
        boolean registered = false;
        name = null;
        role = null;
        logger.info("Please register:");
        do {
            String regline = in.nextLine();
            if (regline.contains("/register agent ") && regline.length() > 16) {
                role = Role.AGENT;
                name = regline.trim().substring(16);
                registered = true;
            } else if (regline.contains("/register client ") && regline.length() > 17) {
                role = Role.CLIENT;
                name = regline.trim().substring(17);
                registered = true;
            } else
                logger.info("Try again...");
        } while (!registered);
    }

    public static boolean handleMessage(String line, StompSession stompSession) {
        switch (role) {
            case AGENT:
                if (line.contains("/exit")) {
                    stompSession.disconnect();
                    return false;
                } else {
                    stompSession.send("/message", String.format(MESSAGE_FORMAT, name, role, line).getBytes());
                    return true;
                }
            case CLIENT:
                if (line.contains("/exit")) {
                    stompSession.disconnect();
                    return false;
                } else if (line.contains("/leave")) {
                    stompSession.send("/command", LEAVE_COMAND.getBytes());
                    return true;
                } else {
                    stompSession.send("/message", String.format(MESSAGE_FORMAT, name, role.toString(), line).getBytes());
                    return true;
                }
            default:
                return false;
        }
    }

    static Scanner in = new Scanner(System.in);
    public static final String MESSAGE_FORMAT = "{ " +
            "\"name\" : \"%s\", " +
            "\"role\" : \"%s\", " +
            "\"content\" : \"%s\" " +
            "}";

    public static final String LEAVE_COMAND = "{ " +
            "\"type\" : \"LEAVE\"}";

    public static void main(String[] args) throws Exception {
        ClientApp clientApp = new ClientApp();

        while (true) {
            register();

            ListenableFuture<StompSession> f = clientApp.connect(name, role);
            StompSession stompSession = f.get();
            clientApp.subscribe(stompSession, "/private/reply");

            boolean isChated;
            do {
                String line = in.nextLine();
                isChated = handleMessage(line, stompSession);
            } while (isChated);
        }
    }
}
