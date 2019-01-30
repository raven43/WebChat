package hello.model;

import chat.common.message.ChatMessage;
import chat.common.message.View;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Date;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ChatRoom {

    private long id;
    private ChatUser agent;
    private ChatUser client;
    private final Date clientRequestTime;
    private Date startTime;
    private Date lastMessageTime;
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

    @JsonView(View.Summary.class)
    public long getId() {
        return id;
    }

    @JsonView(View.Summary.class)
    public ChatUser getAgent() {
        return agent;
    }

    @JsonView(View.Summary.class)
    public ChatUser getClient() {
        return client;
    }

    @JsonView(View.Detail.class)
    public Date getClientRequestTime() {
        return clientRequestTime;
    }

    @JsonView(View.Detail.class)
    public Date getStartTime() {
        return startTime;
    }

    @JsonView(View.Detail.class)
    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void update() {
        lastMessageTime = new Date();
    }

    @JsonView(View.Detail.class)
    public Queue<ChatMessage> getMessageHistory() {
        return messageHistory;
    }
}
