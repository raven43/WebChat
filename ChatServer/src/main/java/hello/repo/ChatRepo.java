package hello.repo;

import hello.model.ChatRoom;
import hello.model.user.ChatUser;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Repository
public class ChatRepo {

    private Map<Long, ChatUser> userMap = new HashMap<Long, ChatUser>();
    private Map<Long, ChatRoom> chats = new HashMap<>();
    private Queue<ChatUser> freeClientQ = new ConcurrentLinkedQueue<ChatUser>();
    private Queue<ChatUser> freeAgentQ = new ConcurrentLinkedQueue<ChatUser>();

    public void putUser(ChatUser user) {
        userMap.put(user.getId(), user);
    }

    public void removeUser(Long id) {
        Object o = userMap.remove(id);
    }

    public ChatUser getUser(Long id) {
        return userMap.get(id);
    }

    public Map<Long, ChatRoom> getChats() {
        return chats;
    }

    public Queue<ChatUser> getFreeClientQ() {
        return freeClientQ;
    }

    public Queue<ChatUser> getFreeAgentQ() {
        return freeAgentQ;
    }

    @Override
    public String toString() {
        String result = "ChatRepo{" +
                "users: " +
                userMap.size() +
                ", free Clients: " +
                freeClientQ.size() +
                ", free Agents: " +
                freeAgentQ.size() +
                '}';
        return result;
    }

    public Map<Long, ChatUser> getUserMap() {
        return userMap;
    }
}
