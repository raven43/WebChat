package hello.scheduled;

import hello.model.ChatUser;
import hello.services.MessageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger log = Logger.getLogger(ScheduledTasks.class);

    private static final long MAX_TIMEOUT = 300000;
    private final MessageService service;

    @Autowired
    public ScheduledTasks(MessageService service) {
        this.service = service;
    }

    @Scheduled(fixedRate = 600000)
    public void repoStats() {
        log.info(service.stats());
    }

    @Scheduled(fixedRate = 15000)
    public void clean() {
        for (ChatUser user : service.getHttpUser())
            if (user.getPassiveTime() > MAX_TIMEOUT)
                service.handleExit(user.getId());
    }
}
