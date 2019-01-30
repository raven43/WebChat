package hello.model;

import chat.common.Role;
import chat.common.message.View;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Date;


public class ChatUser {

    private Long id;
    private String name;
    private Role role;
    @JsonIgnore
    private ChatRoom chat;
    private Date registerTime;
    private Date lastActive;
    @JsonIgnore
    private ConnectionType connectionType;

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

    public void active() {
        lastActive = new Date();
    }

    public void setChat(ChatRoom chat) {
        this.chat = chat;
    }

    @JsonView(View.Summary.class)
    public Long getId() {
        return id;
    }

    @JsonView(View.Summary.class)
    public String getName() {
        return name;
    }

    @JsonView(View.Summary.class)
    public Role getRole() {
        return role;
    }

    @JsonIgnore
    public ChatRoom getChat() {
        return chat;
    }

    @JsonView(View.Summary.class)
    public boolean isFree() {
        if (chat == null) return true;
        return chat.getAgent() == null;
    }

    @JsonView(View.Detail.class)
    public Long getChatId() {
        return isFree() ? null : chat.getId();
    }

    @JsonIgnore
    public ConnectionType getConnectionType() {
        return connectionType;
    }

    @JsonView(View.Detail.class)
    public String getType() {
        return connectionType.toString();
    }

    @JsonView(View.Detail.class)
    public Date getRegisterTime() {
        return registerTime;
    }

    @JsonView(View.Detail.class)
    public Date getLastActive() {
        return lastActive;
    }

    @JsonIgnore
    public long getPassiveTime() {
        return new Date().getTime() - lastActive.getTime();
    }

    @Override
    public String toString() {
        return "ChatUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", registerTime=" + registerTime +
                ", lastActive=" + lastActive +
                ", connectionType=" + connectionType +
                '}';
    }

    public enum ConnectionType {
        WebSocket, HTTP
    }

}
