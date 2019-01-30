package hello.controllers.rest;

import chat.common.Role;
import chat.common.message.View;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.model.ChatRoom;
import hello.model.ChatUser;
import hello.repo.ChatRepo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/info")
public class InfoRestController {

    private static final Logger log = Logger.getLogger(InfoRestController.class);
    private final ChatRepo repo;
    private final ObjectMapper mapper;

    @Autowired
    public InfoRestController(ChatRepo repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    //TODO Docs(SpringRestDocs)

    //AGENTS
    //all agents summary
    @GetMapping("/agents")
    public ResponseEntity<Object> allAgents(
            @RequestParam(required = false, defaultValue = "-1") int pageNumber,
            @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        try {
            List<ChatUser> list = new ArrayList<>();
            for (Map.Entry e : repo.getUserMap().entrySet())
                if (((ChatUser) e.getValue()).getRole().equals(Role.AGENT)) list.add((ChatUser) e.getValue());

            if (pageNumber >= 0) {
                list = page(list, pageNumber, pageSize);
                if (list == null) return new ResponseEntity<>("No such page", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(mapper.writerWithView(View.Summary.class).writeValueAsBytes(list), HttpStatus.OK);
        } catch (Exception e) {
            log.error("REST INFO agents", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //free agents summary
    @GetMapping("/agents/free")
    public ResponseEntity<Object> freeAgents(
            @RequestParam(required = false, defaultValue = "-1") int pageNumber,
            @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {

        try {
            Queue<ChatUser> queue = repo.getFreeAgentQ();
            if (pageNumber >= 0) {
                List list = page(queue, pageNumber, pageSize);
                if (list == null) return new ResponseEntity<>("No such page", HttpStatus.BAD_REQUEST);
                else
                    return new ResponseEntity<>(mapper.writerWithView(View.Summary.class).writeValueAsBytes(list), HttpStatus.OK);
            } else
                return new ResponseEntity<>(mapper.writerWithView(View.Summary.class).writeValueAsBytes(queue), HttpStatus.OK);
        } catch (Exception e) {
            log.error("REST INFO free agents", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //free agents count
    @GetMapping("/agents/free/count")
    public ResponseEntity<Object> agentsCount() {
        try {
            return new ResponseEntity<>(repo.getFreeAgentQ().size(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("REST INFO free agents count", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //agent detail
    @GetMapping("/agents/detail/{id}")
    public ResponseEntity<Object> detailAgents(
            @PathVariable Long id
    ) {
        try {
            if (!repo.getUserMap().containsKey(id) || !repo.getUserMap().get(id).getRole().equals(Role.AGENT))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(mapper.writerWithView(View.Detail.class).writeValueAsBytes(repo.getUser(id)), HttpStatus.OK);
        } catch (Exception e) {
            log.error("REST INFO agent detail", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //CLIENTS
    //all clients summary
    @GetMapping("/clients")
    public ResponseEntity<Object> allClients(
            @RequestParam(required = false, defaultValue = "-1") int pageNumber,
            @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        try {
            List<ChatUser> list = new ArrayList<>();
            for (Map.Entry e : repo.getUserMap().entrySet())
                if (((ChatUser) e.getValue()).getRole().equals(Role.CLIENT)) list.add((ChatUser) e.getValue());

            if (pageNumber >= 0) {
                list = page(list, pageNumber, pageSize);
                if (list == null) return new ResponseEntity<>("No such page", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(mapper.writerWithView(View.Summary.class).writeValueAsBytes(list), HttpStatus.OK);
        } catch (Exception e) {
            log.error("REST INFO clients", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //free clients summary
    @GetMapping("/clients/free")
    public ResponseEntity freeClients(
            @RequestParam(required = false, defaultValue = "-1") int pageNumber,
            @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        try {
            Queue<ChatUser> queue = repo.getFreeClientQ();
            if (pageNumber >= 0) {
                List list = page(queue, pageNumber, pageSize);
                if (list == null) {
                    return new ResponseEntity<>("No such page", HttpStatus.BAD_REQUEST);
                } else
                    return new ResponseEntity<>(mapper.writerWithView(View.Summary.class).writeValueAsBytes(list), HttpStatus.OK);
            } else
                return new ResponseEntity<>(mapper.writerWithView(View.Summary.class).writeValueAsBytes(queue), HttpStatus.OK);
        } catch (Exception e) {
            log.error("REST INFO free clients", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //free clients count
    @GetMapping("/clients/free/count")
    public ResponseEntity<Object> freeClientsCount() {
        try {
            return new ResponseEntity<>(repo.getFreeClientQ().size(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("REST INFO free agents count", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //client detail
    @GetMapping("/clients/detail/{id}")
    public ResponseEntity clientDetail(
            @PathVariable Long id
    ) {
        try {
            if (!repo.getUserMap().containsKey(id) || !repo.getUserMap().get(id).getRole().equals(Role.CLIENT))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            ChatUser user = repo.getUser(id);
            byte[] response = mapper.writerWithView(View.Detail.class).writeValueAsBytes(user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("REST INFO client detail", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //CHATS
    //active chats summary
    @GetMapping("/chats")
    public ResponseEntity activeChats(
            @RequestParam(required = false, defaultValue = "-1") int pageNumber,
            @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {

        try {
            Collection<ChatRoom> values = repo.getChats().values();
            if (pageNumber >= 0) {
                List<ChatRoom> list = page(values, pageNumber, pageSize);
                if (list == null) {
                    return new ResponseEntity<>("No such page", HttpStatus.BAD_REQUEST);
                } else {
                    return new ResponseEntity<>(mapper.writerWithView(View.Summary.class).writeValueAsBytes(list), HttpStatus.OK);
                }
            } else
                return new ResponseEntity<>(mapper.writerWithView(View.Summary.class).writeValueAsBytes(values), HttpStatus.OK);
        } catch (Exception e) {
            log.error("REST INFO chats", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //active chats count
    @GetMapping("/chats/count")
    public ResponseEntity activeChatsCount() {
        try {
            return new ResponseEntity<>(repo.getChats().size(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("REST INFO chats count", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //chat detail
    @GetMapping("/chats/detail/{id}")
    public ResponseEntity chatsDetail(
            @PathVariable Long id
    ) {
        try {
            if (!repo.getChats().containsKey(id)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(mapper.writerWithView(View.Detail.class).writeValueAsBytes(repo.getChats().get(id)), HttpStatus.OK);
        } catch (Exception e) {
            log.error("REST INFO chats detail", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private static <T> List<T> page(Collection<T> collection, int pN, int pS) {

        List<T> list = new ArrayList<>(collection);
        if (pN < 0) return null;
        if (pS >= list.size()) return list;
        int start = pS * pN;
        int end = (pS * (pN + 1)) - 1;
        int last = list.size() - 1;
        if (start > last) return null;
        end = end >= last ? last : end;
        return new ArrayList<>(list.subList(start, end + 1));
    }



}
