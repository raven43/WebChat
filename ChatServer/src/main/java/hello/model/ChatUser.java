package hello.model;

import chat.common.Role;
import chat.common.message.ChatMessage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ChatUser {

    @JsonView(Summary.class)
    private String id;
    @JsonView(Summary.class)
    private String name;
    @JsonView(Summary.class)
    private Role role;
    @JsonIgnore
    private ChatUser companion;
    @JsonView(Detail.class)
    private Queue<ChatMessage> messageHistory;
    @JsonView(Detail.class)
    private Date registerTime;
    @JsonView(Detail.class)
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

    @JsonView(Detail.class)
    public String getCompanionId() {
        return isFree() ? null : companion.id;
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

    @JsonView(Summary.class)
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

    public static class Detail extends Summary {
    }

    public static class Summary {
    }
}
