package hello.controllers.rest;

import chat.common.Role;
import chat.common.message.View;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.model.user.ChatUser;
import hello.repo.ChatRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class InfoRestController {

    private final ChatRepo repo;
    private final ObjectMapper mapper;

    @Autowired
    public InfoRestController(ChatRepo repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @GetMapping("/users")
    public String usersSummary() throws JsonProcessingException {

        List<ChatUser> list = new ArrayList<>();
        for (Map.Entry e : repo.getUserMap().entrySet()) {
            list.add((ChatUser) e.getValue());
        }
        return mapper.writerWithView(View.Summary.class).writeValueAsString(list);
    }

    @GetMapping("/users/detail")
    public ResponseEntity usersDetail() throws JsonProcessingException {

        List<ChatUser> list = new ArrayList<>();
        for (Map.Entry e : repo.getUserMap().entrySet()) {
            list.add((ChatUser) e.getValue());
        }
        return new ResponseEntity(mapper.writerWithView(View.Detail.class).writeValueAsString(list), HttpStatus.I_AM_A_TEAPOT);
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
        return new ResponseEntity(mapper.writerWithView(View.Summary.class).writeValueAsBytes(list), HttpStatus.OK);
    }

    //free agents summary
    @GetMapping("/agents/free")
    public ResponseEntity freeAgents() throws JsonProcessingException {
        return new ResponseEntity(mapper.writerWithView(View.Summary.class).writeValueAsBytes(repo.getFreeAgentQ()), HttpStatus.OK);
    }

    //free agents count
    @GetMapping("/agents/free/count")
    public ResponseEntity agentsCount() {
        return new ResponseEntity(repo.getFreeAgentQ().size(), HttpStatus.OK);
    }

    //agent detail
    @GetMapping("/agents/detail")
    public ResponseEntity detailAgents(
            @RequestParam(required = true) Long id
    ) throws JsonProcessingException {
        return new ResponseEntity(mapper.writerWithView(View.Detail.class).writeValueAsBytes(repo.getUser(id)), HttpStatus.OK);
    }

    //CLIENTS
    //all clients summary
    @GetMapping("/clients")
    public ResponseEntity allClients() throws JsonProcessingException {
        List<ChatUser> list = new ArrayList<>();
        for (Map.Entry e : repo.getUserMap().entrySet()) {
            if (((ChatUser) e.getValue()).getRole().equals(Role.CLIENT)) list.add((ChatUser) e.getValue());
        }
        return new ResponseEntity(mapper.writerWithView(View.Summary.class).writeValueAsBytes(list), HttpStatus.OK);
    }

    //free clients summary
    @GetMapping("/clients/free")
    public ResponseEntity freeClients() throws JsonProcessingException {
        return new ResponseEntity(mapper.writerWithView(View.Summary.class).writeValueAsBytes(repo.getFreeClientQ()), HttpStatus.OK);
    }

    //free clients count
    @GetMapping("/clients/free/count")
    public ResponseEntity freeClientsCount() {
        return new ResponseEntity(repo.getFreeClientQ().size(), HttpStatus.OK);
    }

    //client detail
    @GetMapping("/clients/detail")
    public ResponseEntity clientDetail(
            @RequestParam(required = true) Long id
    ) throws JsonProcessingException {
        return new ResponseEntity(mapper.writerWithView(View.Detail.class).writeValueAsBytes(repo.getUser(id)), HttpStatus.OK);
    }

    //CHATS
    //active chats summary
    @GetMapping("/chats")
    public ResponseEntity activeChats() throws JsonProcessingException {

        return new ResponseEntity(mapper.writerWithView(View.Summary.class).writeValueAsBytes(repo.getChats()), HttpStatus.OK);
    }

    //active chats count
    @GetMapping("/chats/count")
    public ResponseEntity activeChatsCount() {
        return new ResponseEntity(repo.getChats().size(), HttpStatus.OK);
    }

    //chat detail
    @GetMapping("/chats/detail")
    public ResponseEntity chatsDetail(
            @RequestParam Long id
    ) throws JsonProcessingException {
        return new ResponseEntity(mapper.writerWithView(View.Detail.class).writeValueAsBytes(repo.getChats().get(id)), HttpStatus.OK);
    }

    //CHAT INTERFACE
    //register
    //send message
    //get messages
    //leave chat
    //exit

}
