package hello.controllers;

import chat.common.message.ChatMessage;
import chat.common.message.ComandMessage;
import hello.repo.ChatRepo;
import hello.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class MainController {

    public static final String ENDPOINT_COMAND = "/command";
    public static final String ENDPOINT_MESSAGE = "/message";

    private SimpMessagingTemplate template;
    private ChatRepo repo;
    private MessageService service;

    @Autowired
    public MainController(
            SimpMessagingTemplate template,
            ChatRepo repo,
            MessageService service
    ) {
        this.template = template;
        this.repo = repo;
        this.service = service;
    }


    @MessageMapping(ENDPOINT_MESSAGE)
    public void message(
            @Payload ChatMessage message,
            Principal principal
    ) {
        service.handleMessage(principal.getName(), message);
    }

    @MessageMapping(ENDPOINT_COMAND)
    public void command(
            @Payload ComandMessage message,
            Principal principal
    ) {

        switch (message.getType()){
            case LEAVE:
                service.handleLeave(principal.getName());
                break;
        }
    }

}
