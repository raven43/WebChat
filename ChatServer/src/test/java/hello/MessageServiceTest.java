package hello;

import hello.model.ChatUser;
import hello.model.Role;
import hello.model.message.ChatMessage;
import hello.repo.ChatRepo;
import hello.services.MessageService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class MessageServiceTest {

    private ChatRepo repo;
    private MessageService service;
    private SimpMessagingTemplateTestImpl template;

    @Before
    public void setUp() {
        template = new SimpMessagingTemplateTestImpl();
        repo = new ChatRepo();
        service = new MessageService(repo, template);
    }

    @After
    public void clean() {
    }

    @Test
    public void registerAndActivateTest() {

        String id = UUID.randomUUID().toString();

        ChatUser agent = new ChatUser(id, "Cooper", Role.AGENT);
        service.handleRegister(agent);
        Assert.assertEquals(1, service.getRepo().getUserMap().size());
        service.activateUser(id);
        Assert.assertEquals(1, service.getRepo().getFreeAgentQ().size());
    }

    @Test
    public void coupleTest() {
        String clientId = UUID.randomUUID().toString();
        String agentId = UUID.randomUUID().toString();

        ChatUser agent = new ChatUser(agentId, "Cooper", Role.AGENT);
        ChatUser client = new ChatUser(clientId, "Alice", Role.CLIENT);
        service.handleRegister(agent);
        service.activateUser(agentId);
        Assert.assertEquals(1, service.getRepo().getFreeAgentQ().size());

        service.handleRegister(client);
        service.findCompanion(client);

        Assert.assertEquals(agent, client.getCompanion());
        Assert.assertEquals(client, agent.getCompanion());

    }

    @Test
    public void handleLeaveTest() {

        String clientId = UUID.randomUUID().toString();
        String agentId = UUID.randomUUID().toString();

        ChatUser agent = new ChatUser(agentId, "Cooper", Role.AGENT);
        ChatUser client = new ChatUser(clientId, "Alice", Role.CLIENT);
        client.getMessageHistory().add(new ChatMessage("Alice", "Help"));
        service.handleRegister(agent);
        service.activateUser(agentId);
        Assert.assertEquals(1, service.getRepo().getFreeAgentQ().size());

        service.handleRegister(client);
        service.findCompanion(client);

        service.handleLeave(clientId);

        Assert.assertNull(client.getCompanion());
        Assert.assertNull(agent.getCompanion());

        Assert.assertTrue(repo.getFreeAgentQ().contains(agent));

    }

    @Test
    public void handleExitTest() {
        String agent1Id = UUID.randomUUID().toString();
        String agent2Id = UUID.randomUUID().toString();
        String clientId = UUID.randomUUID().toString();

        ChatUser agent1 = new ChatUser(agent1Id, "Cooper", Role.AGENT);
        ChatUser agent2 = new ChatUser(agent2Id, "Smith", Role.AGENT);
        ChatUser client = new ChatUser(clientId, "Alice", Role.CLIENT);
        client.getMessageHistory().add(new ChatMessage("Alice", "Help"));
        service.handleRegister(agent1);
        service.handleRegister(agent2);
        service.activateUser(agent1Id);
        service.activateUser(agent2Id);


        service.handleRegister(client);
        service.findCompanion(client);
        Assert.assertEquals(agent1, client.getCompanion());

        service.handleExit(agent1Id);

        Assert.assertFalse(repo.getUserMap().containsKey(agent1Id));
        Assert.assertEquals(agent2, client.getCompanion());


    }
}
