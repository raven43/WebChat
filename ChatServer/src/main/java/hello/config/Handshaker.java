package hello.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

public class Handshaker extends DefaultHandshakeHandler {

    private static final String ATTR_PRINCIPAL = "__principal__";

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        final String name;
        //generate id for the current session
        if (!attributes.containsKey(ATTR_PRINCIPAL)) {
            name = generateId();
            attributes.put(ATTR_PRINCIPAL, name);
        } else {
            name = (String) attributes.get(ATTR_PRINCIPAL);
        }
        return () -> name;
    }

    private String generateId() {
        return String.valueOf(UUID.randomUUID().getMostSignificantBits());
    }

}
