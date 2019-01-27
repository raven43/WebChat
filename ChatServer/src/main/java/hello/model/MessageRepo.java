package hello.model;

import chat.common.message.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageRepo {
    private Map<Long, List<ChatMessage>> messageMap = new HashMap<>();

    public void addStorage(long id) {
        messageMap.put(id, new ArrayList<>());
    }

    public List<ChatMessage> removeStorage(long id) {
        return messageMap.remove(id);
    }

    public void addMessage(long id, ChatMessage message) {
        messageMap.get(id).add(message);
    }

    public void addMessage(long id, List<ChatMessage> messages) {
        messageMap.get(id).addAll(messages);
    }

    public List<ChatMessage> getMessages(long id) {
        List<ChatMessage> list = new ArrayList<>(messageMap.get(id));
        messageMap.get(id).clear();
        return list;
    }


}
