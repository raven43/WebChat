package hello.services;

import chat.common.Role;
import chat.common.message.ChatMessage;
import hello.model.ChatRoom;
import hello.model.ChatUser;
import hello.model.MessageRepo;
import hello.repo.ChatRepo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service
public class MessageService {

    private static Logger log = Logger.getLogger(MessageService.class);
    private static final String USER_DEST = "/reply";

    private final ChatRepo repo;
    private final SimpMessagingTemplate template;
    private final MessageRepo messageRepo;

    @Autowired
    public MessageService(
            ChatRepo repo,
            SimpMessagingTemplate template,
            MessageRepo messageRepo
    ) {
        this.repo = repo;
        this.template = template;
        this.messageRepo = messageRepo;
    }

    public void handleMessage(Long senderId, String message) {
        ChatUser user = repo.getUser(senderId);
        ChatMessage chatMessage = new ChatMessage(user.getName(), user.getRole(), message);
        handleMessage(senderId, chatMessage);
    }


    public void handleMessage(Long senderId, ChatMessage message) {
        ChatUser user = repo.getUser(senderId);
        user.active();
        if (!user.isFree()) {

            user.getChat().getMessageHistory().add(message);
            user.getChat().update();
            send(user.getChat().getAgent(), message);
            send(user.getChat().getClient(), message);
            //template.convertAndSendToUser(user.getId(), USER_DEST, message);
            //template.convertAndSendToUser(user.getChat().getId(), USER_DEST, message);
        } else if (user.getRole().equals(Role.CLIENT)) {
            if (user.getChat() == null) {
                ChatRoom room = new ChatRoom(user, message);
                user.setChat(room);
                findCompanion(user);
            } else {
                user.getChat().getMessageHistory().add(message);
                user.getChat().update();
            }
            send(user, message);
        }
    }

    private void send(ChatUser user, ChatMessage message) {
        switch (user.getConnectionType()) {
            case WebSocket:
                template.convertAndSendToUser(String.valueOf(user.getId()), USER_DEST, message);
                break;
            case HTTP:
                messageRepo.addMessage(user.getId(), message);
                break;
            default:
                log.warn("Unrecognized user " + user);
                break;
        }
    }

    public void findCompanion(ChatUser user) {
        send(user, new ChatMessage(user.getRole().equals(Role.AGENT) ? "Wait for the client..." : "Wait for the agent..."));
        synchronized (repo) {
            if (user.isFree()) {
                if (user.getRole().equals(Role.CLIENT)) {
                    if (repo.getFreeAgentQ().isEmpty())
                        repo.getFreeClientQ().add(user);
                    else
                        setCouple(user, repo.getFreeAgentQ().poll());
                } else {
                    if (repo.getFreeClientQ().isEmpty())
                        repo.getFreeAgentQ().add(user);
                    else
                        setCouple(user, repo.getFreeClientQ().poll());
                }
            }
        }
    }

    private void setCouple(ChatUser user1, ChatUser user2) {
        ChatUser client = user1.getRole().equals(Role.CLIENT) ? user1 : user2;
        ChatUser agent = user1.getRole().equals(Role.AGENT) ? user1 : user2;

        client.getChat().startChat(agent);
        repo.getChats().put(client.getChat().getId(), client.getChat());

        log.info("Start chat btw agent [" + agent.getName() + "] and client [" + client.getName() + "]");

        send(agent, new ChatMessage(client.getName(), "You get client " + client.getName()));
        send(client, new ChatMessage(agent.getName(), "You get agent " + agent.getName()));

        for (ChatMessage message : client.getChat().getMessageHistory()) {
            send(agent, message);
        }
    }

    public void handleRegister(ChatUser user) {
        if (user.getConnectionType().equals(ChatUser.ConnectionType.HTTP))
            messageRepo.addStorage(user.getId());
        log.info("Register user " + user);
        repo.putUser(user);
    }

    public void activateUser(Long id) {
        ChatUser user = repo.getUser(id);
        log.info("Activate " + user);
        send(user, new ChatMessage("Welcome " + user.getRole().str + " " + user.getName()));
        if (user.getRole().equals(Role.AGENT))
            findCompanion(user);
    }

    public void handleExit(Long id) {
        ChatUser user = repo.getUser(id);
        if (!user.isFree()) {
            log.info("Stop chat btw agent [" + user.getChat().getAgent().getName() + "] and client [" +
                    user.getChat().getClient().getName() + "]: " + user.getRole().str + " exit");

            repo.getChats().remove(user.getChat().getId());
            ChatUser orphan = user.getChat().breakChat(user);
            send(orphan, new ChatMessage("Your " + user.getRole().str + " exit"));
            findCompanion(orphan);
        } else if (user.getRole().equals(Role.CLIENT))
            repo.getFreeClientQ().remove(user);
        else if (user.getRole().equals(Role.AGENT))
            repo.getFreeAgentQ().remove(user);

        log.info("Exit user " + user);
        repo.removeUser(id);
        messageRepo.removeStorage(id);
    }

    public void handleLeave(Long id) {
        ChatUser client = repo.getUser(id);
        client.active();
        if (client.getChat() != null) {
            send(client, new ChatMessage("You leave the chat"));
            //template.convertAndSendToUser(client.getId(), USER_DEST, new ChatMessage("You leave the chat"));
            if (!client.isFree() && client.getRole().equals(Role.CLIENT)) {
                log.info("Stop chat btw agent [" + client.getChat().getAgent().getName() +
                        "] and client [" + client.getName() + "]: Client leave");
                ChatUser orphan = client.getChat().breakChat(client);
                repo.getChats().remove(client.getChat().getId());
                send(orphan, new ChatMessage("You client leave the chat"));
                findCompanion(orphan);
            }
            client.setChat(null);
            repo.getFreeClientQ().remove(client);
        }
    }

    public ChatRepo getRepo() {
        return repo;
    }

    public Collection<ChatUser> getHttpUser() {
        List<ChatUser> response = new ArrayList<>();
        for (ChatUser user : repo.getUserMap().values()) {
            if (user.getConnectionType().equals(ChatUser.ConnectionType.HTTP))
                response.add(user);
        }
        return response;
    }

    public String stats() {
        return repo.toString();
    }
}