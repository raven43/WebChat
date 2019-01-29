package hello.model;

import chat.common.Role;
import chat.common.message.View;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Date;


public class ChatUser {

    @JsonView(View.Summary.class)
    private Long id;
    @JsonView(View.Summary.class)
    private String name;
    @JsonView(View.Summary.class)
    private Role role;
    @JsonIgnore
    private ChatRoom chat;
    @JsonView(View.Detail.class)
    private Date registerTime;
    @JsonView(View.Detail.class)
    private Date lastActive;
    @JsonView(View.Detail.class)
    private ConnectionType connectionType;

    protected ChatUser() {
    }

    public ChatUser(Long id, String name, Role role) {
        this(id, name, role, ConnectionType.WebSocket);
    }

    public ChatUser(Long id, String name, Role role, ConnectionType type) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.connectionType = type;
        registerTime = new Date();
        lastActive = registerTime;
    }


    public Long getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public ChatRoom getChat() {
        return chat;
    }

    @JsonView(View.Detail.class)
    public long getChatId() {
        return isFree() ? null : chat.getId();
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public String getName() {
        return name;
    }

    public void setChat(ChatRoom chat) {
        this.chat = chat;
    }

    @JsonView(View.Summary.class)
    public boolean isFree() {
        if (chat == null) return true;
        return chat.getAgent() == null;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public Date getLastActive() {
        return lastActive;
    }

    @JsonIgnore
    public long getPassiveTime() {
        return new Date().getTime() - lastActive.getTime();
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

    public enum ConnectionType {
        WebSocket, HTTP
    }

}
