package hello.model;

import hello.model.message.ChatMessage;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatUser {



    private String id;
    private String name;
    private Role role;
    private ChatUser companion;
    private Queue<ChatMessage> messageHistory;

    public ChatUser() {
        messageHistory = new ConcurrentLinkedQueue<>();
    }

    public ChatUser(String id) {
        this.id = id;
        messageHistory = new ConcurrentLinkedQueue<>();
    }

    public ChatUser(String id, String name, Role role) {
        this.id = id;
        this.name = name;
        this.role = role;
        messageHistory = new ConcurrentLinkedQueue<>();
    }

    public String getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public ChatUser getCompanion() {
        return companion;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Queue<ChatMessage> getMessageHistory() {
        return messageHistory;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setCompanion(ChatUser companion) {
        this.companion = companion;
    }

    public boolean isFree() {
        return companion == null;
    }

    public ChatUser breakChat() {
        ChatUser comp = companion;
        companion.companion = null;
        companion = null;
        return comp;
    }

    @Override
    public String toString() {
        return "ChatUser{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                '}';
    }
}
