package hello.controllers.rest;

import chat.common.Role;
import chat.common.message.ChatMessage;
import hello.model.ChatUser;
import hello.model.MessageRepo;
import hello.repo.ChatRepo;
import hello.services.MessageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
public class ChatRestController {

    private final Logger log = Logger.getLogger(ChatRestController.class);
    private final MessageService service;
    private final ChatRepo repo;
    private final MessageRepo messageRepo;

    @Autowired
    public ChatRestController(MessageService service, ChatRepo repo, MessageRepo messageRepo) {
        this.service = service;
        this.repo = repo;
        this.messageRepo = messageRepo;
    }

    //register
    @PostMapping("/register")
    public ResponseEntity<Object> register(
            @RequestParam String name,
            @RequestParam String role
    ) {
        try {
            Role r = null;
            try {
                r = Role.valueOf(role);
            } catch (Throwable ignored) {
            }
            if (r == null)
                return new ResponseEntity<>("Unrecognised role", HttpStatus.BAD_REQUEST);
            long id = UUID.randomUUID().getMostSignificantBits();
            ChatUser user = new ChatUser(id, name, Role.valueOf(role), ChatUser.ConnectionType.HTTP);
            service.handleRegister(user);
            service.activateUser(id);
            return new ResponseEntity<>(id, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("HTTP register error", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //send message
    @PostMapping("/{id}")
    public ResponseEntity sendMessage(
            @PathVariable Long id,
            @RequestParam String message
    ) {
        try {
            if (!repo.getUserMap().containsKey(id))
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            service.handleMessage(id, message);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            log.error("HTTP send message error", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //get messages
    @GetMapping("/{id}")
    public ResponseEntity<List<ChatMessage>> getMessages(
            @PathVariable Long id
    ) {
        try {
            if (!repo.getUserMap().containsKey(id)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            if (!messageRepo.hasStorage(id)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(messageRepo.getMessages(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error("HTTP get message error", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //leave
    @PostMapping("/{id}/leave")
    public ResponseEntity leave(
            @PathVariable Long id
    ) {
        try {
            if (!repo.getUserMap().containsKey(id)) return new ResponseEntity(HttpStatus.BAD_REQUEST);
            if (repo.getUserMap().get(id).getRole().equals(Role.AGENT))
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            service.handleLeave(id);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            log.error("HTTP leave error", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //exit
    @DeleteMapping("/{id}")
    public ResponseEntity exit(
            @PathVariable Long id
    ) {
        try {
            if (!repo.getUserMap().containsKey(id)) return new ResponseEntity(HttpStatus.BAD_REQUEST);
            service.handleExit(id);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            log.error("HTTP exit error", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
