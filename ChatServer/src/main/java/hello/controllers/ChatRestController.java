package hello.controllers;

import chat.common.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.model.ChatUser;
import hello.repo.ChatRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ChatRestController {

    private final ChatRepo repo;
    private final ObjectMapper mapper;

    @Autowired
    public ChatRestController(ChatRepo repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @GetMapping("/users")
    public String usersSummary() throws JsonProcessingException {

        List<ChatUser> list = new ArrayList<>();
        for (Map.Entry e : repo.getUserMap().entrySet()) {
            list.add((ChatUser) e.getValue());
        }
        return mapper.writerWithView(ChatUser.Summary.class).writeValueAsString(list);
    }

    @GetMapping("/users/detail")
    public ResponseEntity usersDetail() throws JsonProcessingException {

        List<ChatUser> list = new ArrayList<>();
        for (Map.Entry e : repo.getUserMap().entrySet()) {
            list.add((ChatUser) e.getValue());
        }
        return new ResponseEntity(mapper.writerWithView(ChatUser.Detail.class).writeValueAsString(list), HttpStatus.I_AM_A_TEAPOT);
    }

    //GET
    //AGENTS
    //all agents summary
    @GetMapping("/agents")
    public ResponseEntity allAgents() throws JsonProcessingException {
        List<ChatUser> list = new ArrayList<>();
        for (Map.Entry e : repo.getUserMap().entrySet()) {
            if (((ChatUser) e.getValue()).getRole().equals(Role.AGENT)) list.add((ChatUser) e.getValue());
        }
        return new ResponseEntity(mapper.writerWithView(ChatUser.Summary.class).writeValueAsBytes(list), HttpStatus.OK);
    }

    //free agents summary
    @GetMapping("/agents/free")
    public ResponseEntity freeAgents() throws JsonProcessingException {
        return new ResponseEntity(mapper.writerWithView(ChatUser.Summary.class).writeValueAsBytes(repo.getFreeAgentQ()), HttpStatus.OK);
    }

    //free agents count
    @GetMapping("/agents/free/count")
    public ResponseEntity agentsCount() {
        return new ResponseEntity(repo.getFreeAgentQ().size(), HttpStatus.OK);
    }

    //agent detail
    @GetMapping("/agents/detail")
    public ResponseEntity detailAgents(
            @RequestParam(required = true) String id
    ) throws JsonProcessingException {
        return new ResponseEntity(mapper.writerWithView(ChatUser.Detail.class).writeValueAsBytes(repo.getUser(id)), HttpStatus.OK);
    }

    //CLIENTS
    //all clients summary
    @GetMapping("/clients")
    public ResponseEntity allClients() throws JsonProcessingException {
        List<ChatUser> list = new ArrayList<>();
        for (Map.Entry e : repo.getUserMap().entrySet()) {
            if (((ChatUser) e.getValue()).getRole().equals(Role.CLIENT)) list.add((ChatUser) e.getValue());
        }
        return new ResponseEntity(mapper.writerWithView(ChatUser.Summary.class).writeValueAsBytes(list), HttpStatus.OK);
    }

    //free clients summary
    @GetMapping("/clients/free")
    public ResponseEntity freeClients() throws JsonProcessingException {
        return new ResponseEntity(mapper.writerWithView(ChatUser.Summary.class).writeValueAsBytes(repo.getFreeClientQ()), HttpStatus.OK);
    }

    //free clients count
    @GetMapping("/clients/free/count")
    public ResponseEntity freeClientsCount() {
        return new ResponseEntity(repo.getFreeClientQ().size(), HttpStatus.OK);
    }

    //client detail
    @GetMapping("/clients/detail")
    public ResponseEntity clientDetail(
            @RequestParam(required = true) String id
    ) throws JsonProcessingException {
        return new ResponseEntity(mapper.writerWithView(ChatUser.Detail.class).writeValueAsBytes(repo.getUser(id)), HttpStatus.OK);
    }

    //CHATS
    //active chats summary
    @GetMapping("/chats")
    public ResponseEntity activeChats() throws JsonProcessingException {
        List list = new ArrayList();
        for (Map.Entry e : repo.getUserMap().entrySet()) {
            if (((ChatUser) e.getValue()).getRole().equals(Role.AGENT) && !((ChatUser) e.getValue()).isFree()) {
                Map map = new HashMap();
                map.put("Agent", e.getValue());
                map.put("Client", ((ChatUser) e.getValue()).getCompanion());
                list.add(map);
            }
        }
        return new ResponseEntity(mapper.writerWithView(ChatUser.Summary.class).writeValueAsBytes(list), HttpStatus.OK);
    }

    //active chats count
    @GetMapping("/chats/count")
    public ResponseEntity activeChatsCount() {
        int count = 0;
        for (Map.Entry e : repo.getUserMap().entrySet()) {
            if (((ChatUser) e.getValue()).getRole().equals(Role.AGENT) && !((ChatUser) e.getValue()).isFree()) count++;
        }
        return new ResponseEntity(count, HttpStatus.OK);
    }

    //chat detail
    @GetMapping("/chats/detail")
    public ResponseEntity chatsDetail(
            @RequestParam String id
    ) throws JsonProcessingException {
        ChatUser user = repo.getUser(id);
        if (user == null) return new ResponseEntity("No such user", HttpStatus.NOT_FOUND);
        if (user.isFree()) return new ResponseEntity("No such chat", HttpStatus.NOT_FOUND);

        Map map = new HashMap();
        map.put("Agent", user.getRole().equals(Role.AGENT) ? user : user.getCompanion());
        map.put("Client", user.getRole().equals(Role.CLIENT) ? user : user.getCompanion());
        return new ResponseEntity(mapper.writerWithView(ChatUser.Detail.class).writeValueAsBytes(map), HttpStatus.OK);
    }

    //CHAT INTERFACE
    //register
    //send message
    //get messages
    //leave chat
    //exit

}
