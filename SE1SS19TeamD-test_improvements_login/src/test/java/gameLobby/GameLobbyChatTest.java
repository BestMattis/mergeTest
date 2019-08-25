package gameLobby;

import asyncCommunication.WebSocketComponent;
import javafx.stage.Stage;
import lobby.LobbyChatMessageListController;
import main.AdvancedWarsApplication;
import main.FXMLLoad;
import model.*;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static org.hamcrest.CoreMatchers.is;

public class GameLobbyChatTest extends ApplicationTest {

    FXMLLoad gameLobbyFXML;
    Game testgame;
    Stage pri;
    Player alice = new Player().setName("Alice");
    Player bob = new Player().setName("Bob");
    Player charlie = new Player().setName("Charlie");
    private Model model;
    private GameLobbyController gameLobbyController;

    public void start(Stage stage) {

        /*
         * =============== SITUATION ===============
         */

        // Alice, Bob and Charlie have joined a game lobby.
        // The Game is not started yet
        AdvancedWarsApplication advancedWarsApplication = new AdvancedWarsApplication();
        advancedWarsApplication.start(stage);
        AdvancedWarsApplication.offtesting = true;
        model = advancedWarsApplication.model;
        try {
            model.setWebSocketComponent(new WebSocketComponent(alice.getName(), new URI("bla"), model));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        model.getWebSocketComponent().setInGameLobbyForTest();
        App app = new App();
        model.setApp(app);

        pri = stage;
        testgame = new Game().setName("testGame");
        testgame.setCapacity(4);

        testgame.withPlayers(alice, bob, charlie);
        app.setCurrentPlayer(alice);
        app.withAllGames(testgame);

        //load the Game Lobby Screen fxml and add the controller
        gameLobbyFXML = new FXMLLoad("/gameLobby/GameLobbyScreen.fxml", new GameLobbyController(model));
        gameLobbyController = gameLobbyFXML.getController(GameLobbyController.class);

        //setup the scene and show it at the stage
        stage.setScene(gameLobbyFXML.getScene());
        stage.setFullScreen(true);
        stage.show();
        gameLobbyController.show();
        gameLobbyController.update(testgame);
    }

    @Test
    public void testSendGameLobbyChatMessage() {


        /*
         * =============== ACTION ===============
         */

        // Alice clicks on the textfield.


        clickOn("#sendfield");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Alice types "Hello everybody" into the Chat Text Field

        gameLobbyController.sendfield.setText("Hello everybody");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Alice hits the Send Button
        clickOn("#send");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*
         * =============== RESULT ===============
         */

        // The message from Alice is added to the datamodel.
        // Therefore every other player in the Game will receive the message.

        ArrayList<ChatMessage> messages = model.getApp().getCurrentPlayer().getGame().getIngameMessages();
        Assert.assertEquals(messages.size(), 1);

        int vBoxChildrenSize = gameLobbyController.messageList.getController(LobbyChatMessageListController.class)
                .getHistory().getChildren().size();

        FxAssert.verifyThat(vBoxChildrenSize, is(1));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void receiveGameLobbyChatMessage() {

        /*
         * =============== ACTION ===============
         */

        // Bob sends the message "Hello everybody" into the ingame Chat.

        ChatMessage bobsMessage = new ChatMessage().setMessage("Hello everybody").setChannel("all").setSender(bob);
        bobsMessage.setDate(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        model.getApp().getCurrentPlayer().getGame().withIngameMessages(bobsMessage);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*
         * =============== RESULT ===============
         */

        // The message that we received from Bob is added to the data model.
        // The chat opens itself.
        // Therefore Alice will see the message in the game lobby chat.

        ArrayList<ChatMessage> messages = model.getApp().getCurrentPlayer().getGame().getIngameMessages();
        Assert.assertEquals(messages.size(), 1);

        int vBoxChildrenSize = gameLobbyController.messageList.getController(LobbyChatMessageListController.class)
                .getHistory().getChildren().size();

        FxAssert.verifyThat(vBoxChildrenSize, is(1));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPrivateMessaging() {
        while (true) ;
    }
}
