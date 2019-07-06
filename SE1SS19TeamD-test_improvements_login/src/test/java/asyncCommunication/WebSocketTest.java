package asyncCommunication;


import model.ChatMessage;
import model.Model;
import model.Player;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import syncCommunication.HttpRequests;
import syncCommunication.RESTExceptions.GameIdNotFoundException;
import syncCommunication.RESTExceptions.GameLobbyCreationFailedException;
import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.SynchronousArmyCommunicator;
import syncCommunication.SynchronousGameCommunicator;
import syncCommunication.SynchronousUserCommunicator;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static asyncCommunication.Constants.*;

public class WebSocketTest {

    private WebSocketRequests chatClient;
    private WebSocketRequests systemClient;

    /*
     * the user-key is necessary to put it in the header for the requests to the server.
     * also opens a websocket for chat-messages and for system-messages.
     */
    @Test
    public void testOpenWebsocket() {

        HttpRequests hr = new HttpRequests();
        SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);
        String userName = "aVeryUniqueUsername251";
        String userPassword = "1235813";
        try {
            uComm.logIn(userName, userPassword);
            String userKey = uComm.getUserKey();
            Assert.assertNotNull(userKey);
            WebSocketConfigurator.userKey = userKey;
            this.chatClient = new WebSocketRequests(new URI(BS_WS_URI + CHAT_WS + userName));
            this.systemClient = new WebSocketRequests(new URI(BS_WS_URI + SYSTEM_WS));
            Thread.sleep(1000);
            this.chatClient.stop();
            this.systemClient.stop();
        } catch (LoginFailedException | URISyntaxException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * hold the connection until the test is manually closed
     */

    @Test
    public void testListenToWebsocket() {

        HttpRequests hr = new HttpRequests();
        SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);
        String userName = "seb1";
        String userPassword = "a";
        try {
            uComm.logIn(userName, userPassword);
            String userKey = uComm.getUserKey();
            Assert.assertNotNull(userKey);
            WebSocketConfigurator.userKey = userKey;
            this.chatClient = new WebSocketRequests(new URI(BS_WS_URI + CHAT_WS + userName));
            this.systemClient = new WebSocketRequests(new URI(BS_WS_URI + SYSTEM_WS));
            int testDurationInSeconds = 30;
            long endTime = System.currentTimeMillis() + (testDurationInSeconds * 1000);
            while (System.currentTimeMillis() < endTime) ;
            this.systemClient.stop();
            this.chatClient.stop();
        } catch (LoginFailedException | URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * test the WS component in an easier way
     * the component will run in another thread
     */
    @Test
    public void testWholeComponent() {

        HttpRequests hr = new HttpRequests();
        SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);
        String userName = "aVeryUniqueUsernameWS";
        String userPassword = "1235813";
        try {
            uComm.logIn(userName, userPassword);
            String userKey = uComm.getUserKey();
            WebSocketComponent wsComponent = new WebSocketComponent(userName, userKey);
            Thread.sleep(5000);
            wsComponent.stopComponent();
        } catch (LoginFailedException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testChatMessage() {

        HttpRequests hr = new HttpRequests();
        SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);
        String userName = "seb";
        String userPassword = "a";
        try {
            uComm.logIn(userName, userPassword);
            String userKey = uComm.getUserKey();
            WebSocketConfigurator.userKey = userKey;
            this.chatClient = new WebSocketRequests(new URI(BS_WS_URI + CHAT_WS + userName));
            this.systemClient = new WebSocketRequests(new URI(BS_WS_URI + SYSTEM_WS));
            Thread.sleep(2000);
            this.chatClient.sendChatMessage("all", userName, "hello");
            this.chatClient.sendChatMessage("private", userName, "hi", "seb");
            int testDurationInSeconds = 30;
            long endTime = System.currentTimeMillis() + (testDurationInSeconds * 1000);
            while (System.currentTimeMillis() < endTime) ;
            this.chatClient.stop();
            this.systemClient.stop();
        } catch (LoginFailedException | InterruptedException | URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    /*
     *  This test tries to get the armyID of the first army of the player.
     *  Then it tries to create a game on the server.
     *  After that a GameClient is opened and receives the game initialisation.
     *  The test also sends one private and one all-chat message over the GameClient.
     */
    @Test
    public void testGameSocket() {

        HttpRequests hr = new HttpRequests();
        SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);
        SynchronousArmyCommunicator aComm = new SynchronousArmyCommunicator(hr);
        String userName = "aVeryUniqueUsernameWS";
        String userPassword = "1235813";
        Model.getApp().setCurrentPlayer(new Player().setName(userName));
        WebSocketComponent wsComponent = null;
        try {
            uComm.logIn(userName, userPassword);
            String userKey = uComm.getUserKey();
            wsComponent = new WebSocketComponent(userName, userKey);
            Thread.sleep(5000);

        } catch (LoginFailedException | InterruptedException e) {
            e.printStackTrace();
        }

        String armyID = null;
        try {
            JSONObject army = aComm.getAllOwnedArmies().get(0);
            armyID = army.getString("id");
            System.out.println(army.toString());
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }

        SynchronousGameCommunicator gameComm = new SynchronousGameCommunicator(hr);
        String gameID = null;
        boolean successfull = false;
        try {
            gameID = gameComm.openGame("testTest2", 2);
        } catch (GameLobbyCreationFailedException | LoginFailedException e) {
            e.printStackTrace();
        }

        try {
            successfull = gameComm.joinGame(gameID);
        } catch (GameIdNotFoundException | LoginFailedException e) {
            e.printStackTrace();
        }
        System.out.println("#" + gameID + armyID + "#");
        System.out.println("#" + "#");
        wsComponent.joinGame(gameID, armyID);
        ChatMessage chatMessage = new ChatMessage().setMessage("hi").setChannel("all")
                .setSender(Model.getApp().getCurrentPlayer());
        ChatMessage chatMessage2 = new ChatMessage().setMessage("hi").setChannel("private")
                .setSender(Model.getApp().getCurrentPlayer()).setReceiver(Model.getApp().getCurrentPlayer());
        int testDurationInSeconds = 10;
        long endTime = System.currentTimeMillis() + (testDurationInSeconds * 1000);
        while (System.currentTimeMillis() < endTime);

        //it looks like we can only send messages after the game initialisation is done
        wsComponent.sendGameChatMessage(chatMessage);
        wsComponent.sendGameChatMessage(chatMessage2);

        endTime = System.currentTimeMillis() + (testDurationInSeconds * 1000);
        while (System.currentTimeMillis() < endTime);

        //leaves the Game with a command and closes the component
        wsComponent.leaveGame();
        wsComponent.stopComponent();
    }
}
