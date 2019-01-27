package hello.model;

import chat.common.Role;
import chat.common.message.ChatMessage;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HttpUser extends ChatUser {

    private Queue<ChatMessage> messageCash;

    public HttpUser(Long id, String name, Role role) {
        super(id, name, role);
        messageCash = new ConcurrentLinkedQueue<>();
    }

    public void addMessage(ChatMessage message) {
    }

    public Queue<ChatMessage> clearMessageCash() {
        Queue<ChatMessage> copy = new ConcurrentLinkedQueue<>(messageCash);
        messageCash.clear();
        return copy;
    }
}
