package chat.common.message;

import chat.common.Role;
import com.fasterxml.jackson.annotation.JsonView;

public class ChatMessage {

    @JsonView(View.Summary.class)
    private String name;
    @JsonView(View.Summary.class)
    private Role role;
    @JsonView(View.Summary.class)
    private String content;

    public ChatMessage() {
    }

    public ChatMessage(String content) {
        this.content = content;
    }

    public ChatMessage(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public ChatMessage(String name, Role role, String content) {
        this.name = name;
        this.role = role;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return (role != null ? role + " " : "") + (name != null ? name + ": " : "") + content;
    }
}
