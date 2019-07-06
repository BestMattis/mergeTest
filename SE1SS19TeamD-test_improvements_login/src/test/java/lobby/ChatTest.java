package lobby;

import asyncCommunication.WebSocketComponent;
import asyncCommunication.WebSocketRequests;
import asyncCommunication.offlineEndpoints.ChatEndpoint;
import asyncCommunication.offlineEndpoints.SystemEndpoint;
import javafx.scene.Node;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.App;
import model.ChatMessage;
import model.Model;
import model.Player;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




public class ChatTest extends ApplicationTest {

    static Stage pri;


    @SuppressWarnings("unchecked")
    public <T extends Node> T find(final String query) {
        return (T) lookup(query).queryAll().iterator().next();
    }

    public void start(Stage stage) throws Exception {
        AdvancedWarsApplication awa = new AdvancedWarsApplication();
        awa.start(stage);

        Player player1 = new Player().setName("testname");
        App app = Model.getApp();
        app.setCurrentPlayer(player1);
        app.withAllPlayers(new Player().setName("client2"));

        pri = stage;
        AdvancedWarsApplication.getInstance().goToLobby();
    }

    @Test
    public void testLobbyChat() {


        ExecutorService executor = Executors.newSingleThreadExecutor();
        ExecutorService executor2 = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            SystemEndpoint systemEndpoint = new SystemEndpoint();
            systemEndpoint.runServer();
            while (true) ;
        });
        executor2.execute(() -> {
            ChatEndpoint chatEndpoint = new ChatEndpoint();
            chatEndpoint.runServer();
            while (true) ;
        });
        URI chatURI = null;
        try {
            chatURI = new URI("ws://localhost:8090/websocket" + "/chat");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        WebSocketComponent webSocketComponent = new WebSocketComponent("testname", chatURI);
        WebSocketRequests chatClient2 = new WebSocketRequests(chatURI);
        Model.setWebSocketComponent(webSocketComponent);
        App app = Model.getApp();

        chatClient2.sendChatMessage("all", "client2", "hello");
        chatClient2.sendChatMessage("private", "client2", "hi", app.getCurrentPlayer().getName());
        chatClient2.sendChatMessage("private", "client2", "hi", app.getCurrentPlayer().getName());
        chatClient2.sendChatMessage("private", "client2", "hii", app.getCurrentPlayer().getName());

        //we have to wait until the messages come
        int waitingDurationInSeconds = 3;
        long endTime = System.currentTimeMillis() + (waitingDurationInSeconds * 1000);
        while (System.currentTimeMillis() < endTime) ;

        ArrayList<ChatMessage> allChatMessages = app.getAllChatMessages();
        ArrayList<ChatMessage> privateMessages = app.getCurrentPlayer().getReceivedMessages();

        //we sent only one all-chat message
        Assert.assertEquals(allChatMessages.size(), 1);

        //we sent only three private messages
        Assert.assertEquals(privateMessages.size(), 3);

        //only one label for the one all-chat-message
        Assert.assertEquals(1, AdvancedWarsApplication.getInstance().getLobbyCon()
                .getChatCon().getAllController().getHistory().getChildren().size());

        //only three labels for the three private messages we received
        Assert.assertEquals(3, AdvancedWarsApplication.getInstance().getLobbyCon()
                .getChatCon().getSingleController().chatTabs.get(0).AllController.getHistory().getChildren().size());
    }
}
