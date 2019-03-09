package hello.controllers;

import chat.common.message.ChatMessage;
import chat.common.message.ComandMessage;
import hello.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class MainController {

    private static final String ENDPOINT_COMMAND = "/command";
    private static final String ENDPOINT_MESSAGE = "/message";

    private MessageService service;

    @Autowired
    public MainController(
            MessageService service
    ) {
        this.service = service;
    }


    @MessageMapping(ENDPOINT_MESSAGE)
    public void message(
            @Payload ChatMessage message,
            Principal principal
    ) {
        service.handleMessage(Long.valueOf(principal.getName()), message);
    }

    @MessageMapping(ENDPOINT_COMMAND)
    public void command(
            @Payload ComandMessage message,
            Principal principal
    ) {
        switch (message.getType()) {
            case LEAVE:
                service.handleLeave(Long.valueOf(principal.getName()));
                break;
        }
    }

}
