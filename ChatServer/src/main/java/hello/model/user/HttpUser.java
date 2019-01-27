package hello.model.user;

import chat.common.Role;

public class HttpUser extends ChatUser {

    public HttpUser(Long id, String name, Role role) {
        super(id, name, role);
    }
}
