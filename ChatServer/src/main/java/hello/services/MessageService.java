package hello.services;

import chat.common.Role;
import chat.common.message.ChatMessage;
import hello.model.ChatRoom;
import hello.model.ChatUser;
import hello.model.HttpUser;
import hello.model.WebSocketUser;
import hello.repo.ChatRepo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
public class MessageService {

    private static Logger log = Logger.getLogger(MessageService.class);

    private static final String USER_DEST = "/reply";
    private ChatRepo repo;
    private SimpMessagingTemplate template;

    @Autowired
    public MessageService(ChatRepo repo, SimpMessagingTemplate template) {
        this.repo = repo;
        this.template = template;
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
            //template.convertAndSendToUser(user.getId(), USER_DEST, message);
        }
    }

    private void send(ChatUser user, ChatMessage message) {
        if (user instanceof WebSocketUser)
            template.convertAndSendToUser(String.valueOf(user.getId()), USER_DEST, message);
        else if (user instanceof HttpUser) ((HttpUser) user).addMessage(message);
        else log.warn("Unrecognased user " + user);

    }

    public void findCompanion(ChatUser user) {
        /*template.convertAndSendToUser(
                user.getId(),
                USER_DEST,
                new ChatMessage(user.getRole().equals(Role.AGENT) ? "Wait for the client..." : "Wait for the agent...")
        );*/
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

        send(agent, new ChatMessage("You get client " + client.getName()));
        send(client, new ChatMessage("You get agent " + agent.getName()));

        //template.convertAndSendToUser(agent.getId(), USER_DEST, new ChatMessage("You get client " + client.getName()));
        //template.convertAndSendToUser(client.getId(), USER_DEST, new ChatMessage("You get agent " + agent.getName()));

        for (ChatMessage message : client.getChat().getMessageHistory()) {
            send(agent, message);
            //template.convertAndSendToUser(agent.getId(), USER_DEST, message);
        }
    }

    public void handleRegister(ChatUser user) {
        log.info("Register user " + user);
        repo.putUser(user);
    }

    public void activateUser(Long id) {
        ChatUser user = repo.getUser(id);
        log.info("Activate " + user);
        send(user, new ChatMessage("Welcome " + user.getRole().str + " " + user.getName()));
        //template.convertAndSendToUser(user.getId(), USER_DEST, new ChatMessage("Welcome " + user.getRole().str + " " + user.getName()));
        if (user.getRole().equals(Role.AGENT))
            findCompanion(user);
    }

    public void handleExit(Long id) {
        ChatUser user = repo.getUser(id);
        if (!user.isFree()) {
            log.info("Stop chat btw agent [" + user.getChat().getAgent().getName() + "] and client [" +
                    user.getChat().getClient().getName() + "]: " + user.getRole().str + " exit");

            ChatUser orphan = user.getChat().breakChat(user);
            repo.getChats().remove(user.getChat().getId());

            send(orphan, new ChatMessage("Your " + user.getRole().str + " exit"));
            //template.convertAndSendToUser(orphan.getId(), USER_DEST, new ChatMessage("Your " + user.getRole().str + " exit"));
            findCompanion(orphan);
        } else if (user.getRole().equals(Role.CLIENT))
            repo.getFreeClientQ().remove(user);
        else if (user.getRole().equals(Role.AGENT))
            repo.getFreeAgentQ().remove(user);

        log.info("Exit user " + user);
        repo.removeUser(id);

    }

    public void handleLeave(Long id) {
        ChatUser client = repo.getUser(id);
        client.active();
        if (client.getChat() != null) {
            send(client, new ChatMessage("You leave the chat"));
            //template.convertAndSendToUser(client.getId(), USER_DEST, new ChatMessage("You leave the chat"));
            if (!client.isFree() && client.getRole().equals(Role.CLIENT)) {
                log.info("Stop chat btw agent [" + client.getChat().getAgent().getName() + "] and client [" +
                        client.getName() + "]: Client leave");

                ChatUser orphan = client.getChat().breakChat(client);
                repo.getChats().remove(client.getChat().getId());

                send(orphan, new ChatMessage("You client leave the chat"));
                //template.convertAndSendToUser(orphan.getId(), USER_DEST, new ChatMessage("You client leave the chat"));
                findCompanion(orphan);
            }
            client.setChat(null);
            //client.getMessageHistory().clear();

            repo.getFreeClientQ().remove(client);
        }
    }

    public ChatRepo getRepo() {
        return repo;
    }
}