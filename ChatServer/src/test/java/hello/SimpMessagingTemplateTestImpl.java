package hello;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class SimpMessagingTemplateTestImpl extends SimpMessagingTemplate {

    public SimpMessagingTemplateTestImpl() {
        super(new MessageChannel() {
            @Override
            public boolean send(Message<?> message) {
                return true;
            }

            @Override
            public boolean send(Message<?> message, long timeout) {
                return true;
            }
        });
    }

    @Override
    public void convertAndSendToUser(String user, String destination, Object payload) throws MessagingException {
        super.convertAndSendToUser(user, destination, payload);
    }
}
