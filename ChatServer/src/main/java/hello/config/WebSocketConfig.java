package hello.config;

import hello.model.ChatUser;
import hello.model.Role;
import hello.services.MessageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.LinkedList;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static Logger log = Logger.getLogger(WebSocketConfig.class);

    public static final String SUBSCRIBE_USER_PREFIX = "/private";
    public static final String SUBSCRIBE_USER_REPLY = "/reply";
    public static final String SUBSCRIBE_QUEUE = "/queue";

    @Autowired
    private MessageService service;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(SUBSCRIBE_QUEUE, SUBSCRIBE_USER_REPLY);
        config.setUserDestinationPrefix(SUBSCRIBE_USER_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setHandshakeHandler(new Handshaker())
                .withSockJS();
    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        if (((LinkedList) ((Map) event.getMessage().getHeaders().get("nativeHeaders")).get("destination")).get(0)
                .equals("/private/reply")
        )
            service.activateUser(event.getUser().getName());
    }

    @EventListener
    public void handleConnectEvent(SessionConnectEvent event) {

        log.info("Connect user "+event.getUser().getName());
        if (((Map<String, Object>) event.getMessage().getHeaders().get("nativeHeaders")).containsKey("role")){

            Map<String, Object> headers = (Map<String, Object>) event.getMessage().getHeaders().get("nativeHeaders");

            Role role = Role.valueOf((String) ((LinkedList) headers.get("role")).get(0));
            String name = (String) ((LinkedList) headers.get("name")).get(0);

            String id = event.getUser().getName();
            ChatUser user = new ChatUser(id, name, role);
            service.handleRegister(user);
        }

    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        log.info("Disconnect user " + event.getUser().getName());
        service.handleExit(event.getUser().getName());
    }
}