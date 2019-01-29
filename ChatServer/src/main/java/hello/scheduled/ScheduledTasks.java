package hello.scheduled;

import hello.model.ChatUser;
import hello.repo.ChatRepo;
import hello.services.MessageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger log = Logger.getLogger(ScheduledTasks.class);

    private static final long MAX_TIMEOUT = 120000;
    @Autowired
    private ChatRepo repo;
    @Autowired
    private MessageService service;

    @Scheduled(fixedRate = 600000)
    public void repoStats() {
        log.info(repo);
    }

    @Scheduled(fixedRate = 30000)
    public void clean() {
        for (ChatUser user : repo.getUserMap().values())
            if (user.getConnectionType().equals(ChatUser.ConnectionType.HTTP) && user.getPassiveTime() > MAX_TIMEOUT)
                service.handleExit(user.getId());
    }
}
