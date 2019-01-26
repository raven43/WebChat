package hello.scheduled;

import hello.repo.ChatRepo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger log = Logger.getLogger(ScheduledTasks.class);

    @Autowired
    private ChatRepo repo;

    @Scheduled(fixedRate = 600000)
    public void repoStats() {
        log.info(repo);
    }
}
