package hello.services;

import hello.model.ChatUser;
import hello.model.Role;
import hello.model.message.ChatMessage;
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

    public void handleMessage(String senderId, ChatMessage message) {
        ChatUser user = repo.getUser(senderId);
        if (!user.isFree()) {
            if (user.getRole().equals(Role.CLIENT)) user.getMessageHistory().add(message);
            else user.getCompanion().getMessageHistory().add(message);
            template.convertAndSendToUser(user.getId(), USER_DEST, message);
            template.convertAndSendToUser(user.getCompanion().getId(), USER_DEST, message);
        } else if (user.getRole().equals(Role.CLIENT)) {
            user.getMessageHistory().add(message);
            template.convertAndSendToUser(user.getId(), USER_DEST, message);
            if (!repo.getFreeClientQ().contains(user))
                findCompanion(user);
        }
    }

    public void findCompanion(ChatUser user) {
        template.convertAndSendToUser(
                user.getId(),
                USER_DEST,
                new ChatMessage(user.getRole().equals(Role.AGENT) ? "Wait for the client..." : "Wait for the agent...")
        );
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
        user1.setCompanion(user2);
        user2.setCompanion(user1);
        ChatUser client = user1.getRole().equals(Role.CLIENT) ? user1 : user2;
        ChatUser agent = user1.getRole().equals(Role.AGENT) ? user1 : user2;
        log.info("Start chat btw agent [" + agent.getName() + "] and client [" + client.getName() + "]");

        template.convertAndSendToUser(agent.getId(), USER_DEST, new ChatMessage("You get client " + client.getName()));
        template.convertAndSendToUser(client.getId(), USER_DEST, new ChatMessage("You get agent " + agent.getName()));

        for (ChatMessage message : client.getMessageHistory()) {
            template.convertAndSendToUser(agent.getId(), USER_DEST, message);
        }

    }

    public void handleRegister(ChatUser user) {
        log.info("Register user " + user);
        repo.putUser(user);
    }

    public void activateUser(String id) {
        ChatUser user = repo.getUser(id);
        log.info("Activate " + user);
        template.convertAndSendToUser(user.getId(), USER_DEST, new ChatMessage("Welcome " + user.getRole().str + " " + user.getName()));
        if (user.getRole().equals(Role.AGENT))
            findCompanion(user);
    }

    public void handleExit(String id) {
        ChatUser user = repo.getUser(id);
        if (!user.isFree()) {
            log.info("Stop chat btw " + user.getRole().str + " [" + user.getName() + "] and " +
                    user.getCompanion().getRole().str + " [" + user.getCompanion().getName() + "]: "+user.getRole().str+" exit");
            ChatUser orphan = user.breakChat();
            template.convertAndSendToUser(orphan.getId(), USER_DEST, new ChatMessage("Your " + user.getRole().str + " exit"));
            findCompanion(orphan);
        } else if (user.getRole().equals(Role.CLIENT))
            repo.getFreeClientQ().remove(user);
        else if (user.getRole().equals(Role.AGENT))
            repo.getFreeAgentQ().remove(user);

        log.info("Exit user "+ user);
        repo.removeUser(id);

    }

    public void handleLeave(String id) {
        ChatUser user = repo.getUser(id);
        if (!user.getMessageHistory().isEmpty()) {
            template.convertAndSendToUser(user.getId(), USER_DEST, new ChatMessage("You leave the chat"));
            if (!user.isFree() && user.getRole().equals(Role.CLIENT)) {
                log.info("Stop chat btw agent [" + user.getCompanion().getName() + "] and client [" + user.getName()+"]: Client leave");

                ChatUser orphan = user.breakChat();
                template.convertAndSendToUser(orphan.getId(), USER_DEST, new ChatMessage("You client leave the chat"));
                findCompanion(orphan);
            }
            user.getMessageHistory().clear();

            repo.getFreeClientQ().remove(user);
        }
    }

    public ChatRepo getRepo() {
        return repo;
    }
}
