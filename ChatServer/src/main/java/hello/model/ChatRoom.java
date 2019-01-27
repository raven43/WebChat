package hello.model;

import chat.common.message.ChatMessage;
import chat.common.message.View;
import com.fasterxml.jackson.annotation.JsonView;
import hello.model.user.ChatUser;

import java.util.Date;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ChatRoom {

    @JsonView(View.Summary.class)
    private long id;
    @JsonView(View.Summary.class)
    private ChatUser agent;
    @JsonView(View.Summary.class)
    private ChatUser client;
    @JsonView(View.Detail.class)
    private final Date clientRequestTime;
    @JsonView(View.Detail.class)
    private Date startTime;
    @JsonView(View.Detail.class)
    private Date lastMessageTime;
    @JsonView(View.Detail.class)
    private Queue<ChatMessage> messageHistory;

    public ChatRoom(ChatUser client, ChatMessage startMessage) {
        this.id = UUID.randomUUID().getMostSignificantBits();
        this.client = client;
        this.clientRequestTime = new Date();
        this.messageHistory = new ConcurrentLinkedQueue<>();
        this.messageHistory.add(startMessage);
    }

    public void startChat(ChatUser agent) {
        this.agent = agent;
        agent.setChat(this);
        this.startTime = new Date();
    }

    public ChatUser breakChat(ChatUser breaker) {
        agent.setChat(null);
        client.setChat(null);
        if (agent.equals(breaker)) return client;
        if (client.equals(breaker)) return agent;
        return null;
    }

    public long getId() {
        return id;
    }

    public ChatUser getAgent() {
        return agent;
    }

    public ChatUser getClient() {
        return client;
    }

    public Date getClientRequestTime() {
        return clientRequestTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void update() {
        lastMessageTime = new Date();
    }

    public Queue<ChatMessage> getMessageHistory() {
        return messageHistory;
    }
}
