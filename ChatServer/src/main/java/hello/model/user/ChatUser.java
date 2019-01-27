package hello.model.user;

import chat.common.Role;
import chat.common.message.View;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import hello.model.ChatRoom;

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

    protected ChatUser() {
    }

    public ChatUser(Long id, String name, Role role) {
        this.id = id;
        this.name = name;
        this.role = role;
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

    @JsonView(View.Detail.class)
    public String getConnectionType() {
        if (this instanceof HttpUser) return "HTTP";
        if (this instanceof WebSocketUser) return "WebSocket";
        return "Unrecognised";
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

    public ChatRoom breakChat() {
        ChatRoom comp = chat;
        chat = null;
        chat = null;
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
