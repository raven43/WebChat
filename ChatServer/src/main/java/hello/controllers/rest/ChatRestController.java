package hello.controllers.rest;

import chat.common.Role;
import chat.common.message.ChatMessage;
import hello.model.MessageRepo;
import hello.model.user.ChatUser;
import hello.model.user.HttpUser;
import hello.repo.ChatRepo;
import hello.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
public class ChatRestController {

    @Autowired
    MessageService service;
    @Autowired
    ChatRepo repo;
    @Autowired
    MessageRepo messageRepo;

    //CHAT INTERFACE
    //register
    @PostMapping("/register")
    public ResponseEntity<Object> register(
            @RequestParam String name,
            @RequestParam String role
    ) {
        long id = UUID.randomUUID().getMostSignificantBits();
        ChatUser user = new HttpUser(id, name, Role.valueOf(role));
        messageRepo.addStorage(id);
        service.handleRegister(user);
        service.activateUser(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    //send message
    @PostMapping("/{id}/send")
    public ResponseEntity sendMessage(
            @PathVariable Long id,
            @RequestParam String message
    ) {
        service.handleMessage(id, message);
        return new ResponseEntity(HttpStatus.OK);
    }

    //get messages
    @GetMapping("/{id}/get")
    public ResponseEntity<List<ChatMessage>> getMessages(
            @PathVariable Long id
    ) {
        return new ResponseEntity(messageRepo.getMessages(id), HttpStatus.OK);
    }


    //leave
    @PostMapping("/{id}/leave")
    public ResponseEntity leave(
            @PathVariable Long id
    ) {
        service.handleLeave(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    //exit
    @PostMapping("/{id}/exit")
    public ResponseEntity exit(
            @PathVariable Long id
    ) {
        service.handleExit(id);
        messageRepo.removeStorage(id);
        return new ResponseEntity(HttpStatus.OK);
    }


}
