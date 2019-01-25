package hello.model;

import chat.common.Role;
import chat.common.message.ChatMessage;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatUser {

    private String id;
    private String name;
    private Role role;
    private ChatUser companion;
    private Queue<ChatMessage> messageHistory;
    private Date registerTime;
    private Date lastActive;

    public ChatUser(String id, String name, Role role) {
        this.id = id;
        this.name = name;
        this.role = role;
        messageHistory = new ConcurrentLinkedQueue<>();
        registerTime = new Date();
        lastActive = registerTime;
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

    public String getName() {
        return name;
    }

    public Queue<ChatMessage> getMessageHistory() {
        return messageHistory;
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

    public Date getRegisterTime() {
        return registerTime;
    }

    public Date getLastActive() {
        return lastActive;
    }

    public void active() {
        lastActive = new Date();
    }

    @Override
    public String toString() {
        return "ChatUser{" +
                "id='" + id + '\'' +
                ", " + role +
                " " + name +
                ", register=" + registerTime +
                ", active=" + lastActive +
                '}';
    }
}
