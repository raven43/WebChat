package hello.model.user;

import chat.common.Role;

public class WebSocketUser extends ChatUser {

    public WebSocketUser(Long id, String name, Role role) {
        super(id, name, role);
    }

}
