package hello.repo;

import chat.common.message.ChatMessage;
import hello.model.ChatUser;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Chat {

    private static long counter = 0;

    private long id;
    private ChatUser agent;
    private ChatUser client;
    private final Date clientRequestTime;
    private Date startTime;
    private Date lastMessageTime;
    private Queue<ChatMessage> messageHistory;

    public Chat(ChatUser client, ChatMessage startMessage) {
        this.id = counter++;
        this.client = client;
        this.clientRequestTime = new Date();
        this.messageHistory = new ConcurrentLinkedQueue<>();
        this.messageHistory.add(startMessage);
    }

    public void startChat(ChatUser agent) {
        this.agent = agent;
        //TODO send messages to agent
        this.startTime = new Date();
    }

    public void sendMessageToAgent(ChatMessage message) {
        messageHistory.add(message);
        agent.send(message);
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

    public Date getStartTime() {
        return startTime;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public Queue<ChatMessage> getMessageHistory() {
        return messageHistory;
    }
}
