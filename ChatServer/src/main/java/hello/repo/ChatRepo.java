package hello.repo;

import hello.model.ChatUser;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Repository
public class ChatRepo {

    private Map<String, ChatUser> userMap = new HashMap<>();
    private Queue<ChatUser> freeClientQ = new ConcurrentLinkedQueue<>();
    private Queue<ChatUser> freeAgentQ = new ConcurrentLinkedQueue<>();


    public void putUser(ChatUser user) {
        userMap.put(user.getId(), user);
    }

    public void removeUser(String id) {
        Object o = userMap.remove(id);
    }

    public ChatUser getUser(String id) {
        return userMap.get(id);
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

    public Map<String, ChatUser> getUserMap() {
        return userMap;
    }
}
