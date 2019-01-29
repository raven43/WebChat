package hello;

import chat.common.Role;
import chat.common.message.ChatMessage;
import hello.model.ChatUser;
import hello.model.MessageRepo;
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
    private MessageRepo messageRepo;

    @Before
    public void setUp() {
        template = new SimpMessagingTemplateTestImpl();
        repo = new ChatRepo();
        messageRepo = new MessageRepo();
        service = new MessageService(repo, template, messageRepo);
    }

    @After
    public void clean() {
    }

    @Test
    public void registerAndActivateTest() {

        Long id = UUID.randomUUID().getMostSignificantBits();

        ChatUser agent = new ChatUser(id, "Cooper", Role.AGENT);
        service.handleRegister(agent);
        Assert.assertEquals(1, service.getRepo().getUserMap().size());
        service.activateUser(id);
        Assert.assertEquals(1, service.getRepo().getFreeAgentQ().size());
    }

    @Test
    public void coupleTest() {
        Long clientId = UUID.randomUUID().getMostSignificantBits();
        Long agentId = UUID.randomUUID().getMostSignificantBits();

        ChatUser agent = new ChatUser(agentId, "Cooper", Role.AGENT);
        ChatUser client = new ChatUser(clientId, "Alice", Role.CLIENT);
        service.handleRegister(agent);
        service.activateUser(agentId);
        Assert.assertEquals(1, service.getRepo().getFreeAgentQ().size());

        service.handleRegister(client);
        service.findCompanion(client);

        Assert.assertEquals(agent, client.getChat());
        Assert.assertEquals(client, agent.getChat());

    }

    @Test
    public void handleLeaveTest() {

        Long clientId = UUID.randomUUID().getMostSignificantBits();
        Long agentId = UUID.randomUUID().getMostSignificantBits();

        ChatUser agent = new ChatUser(agentId, "Cooper", Role.AGENT);
        ChatUser client = new ChatUser(clientId, "Alice", Role.CLIENT);
        client.getChat().getMessageHistory().add(new ChatMessage("Alice", "Help"));
        service.handleRegister(agent);
        service.activateUser(agentId);
        Assert.assertEquals(1, service.getRepo().getFreeAgentQ().size());

        service.handleRegister(client);
        service.findCompanion(client);

        service.handleLeave(clientId);

        Assert.assertNull(client.getChat());
        Assert.assertNull(agent.getChat());

        Assert.assertTrue(repo.getFreeAgentQ().contains(agent));

    }

    @Test
    public void handleExitTest() {
        Long agent1Id = UUID.randomUUID().getMostSignificantBits();
        Long agent2Id = UUID.randomUUID().getMostSignificantBits();
        Long clientId = UUID.randomUUID().getMostSignificantBits();

        ChatUser agent1 = new ChatUser(agent1Id, "Cooper", Role.AGENT);
        ChatUser agent2 = new ChatUser(agent2Id, "Smith", Role.AGENT);
        ChatUser client = new ChatUser(clientId, "Alice", Role.CLIENT);
        client.getChat().getMessageHistory().add(new ChatMessage("Alice", "Help"));
        service.handleRegister(agent1);
        service.handleRegister(agent2);
        service.activateUser(agent1Id);
        service.activateUser(agent2Id);


        service.handleRegister(client);
        service.findCompanion(client);
        Assert.assertEquals(agent1, client.getChat());

        service.handleExit(agent1Id);

        Assert.assertFalse(repo.getUserMap().containsKey(agent1Id));
        Assert.assertEquals(agent2, client.getChat());


    }
}
