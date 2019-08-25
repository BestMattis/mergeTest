package gameScreen;

import asyncCommunication.WebSocketComponent;
import javafx.scene.input.KeyCode;
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

public class InGameChatTest extends ApplicationTest {

    FXMLLoad gameFXML;
    Game testgame;
    Stage pri;
    Player alice = new Player().setName("Alice");
    Player bob = new Player().setName("Bob");
    Player charlie = new Player().setName("Charlie");
    private Model model;

    public void start(Stage stage) {

        AdvancedWarsApplication.offtesting = true;
        AdvancedWarsApplication advancedWarsApplication = new AdvancedWarsApplication();
        advancedWarsApplication.start(stage);
        model = advancedWarsApplication.model;
        try {
            model.setWebSocketComponent(new WebSocketComponent(alice.getName(), new URI("bla"), model));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        model.getWebSocketComponent().setInGameForTest();
        App app = new App();
        model.setApp(app);

        pri = stage;
        testgame = new Game().setName("testGame");
        testgame.setCapacity(4);

        testgame.withPlayers(alice, bob, charlie);
        app.setCurrentPlayer(alice);
        app.withAllGames(testgame);

        gameFXML = new FXMLLoad("/gameScreen/gameScreen.fxml", new GameScreenController(testgame));
        stage.setScene(gameFXML.getScene());
        stage.setFullScreen(true);
        stage.show();
        AdvancedWarsApplication.getInstance().setGameScreenController(gameFXML);
    }

    @Test
    public void testSendMessage() {



        /*
         * =============== SITUATION ===============
         */

        // Alice, Bob and Charlie have joined a game and are now ingame.

        // chat is closed
        FxAssert.verifyThat(gameFXML.getController(GameScreenController.class).chatFXML.getParent().isVisible(), is(false));

        /*
         * =============== ACTION ===============
         */

        // Alice opens the Ingame Chat.

        moveTo("#border");
        clickOn("#chat");
        // chat is open
        FxAssert.verifyThat(gameFXML.getController(GameScreenController.class).chatFXML.getParent().isVisible(), is(true));

        // Alice clicks on the text field.
        clickOn("#message");

        // Alice types "Hello everybody" into the Chat Text Field and clicks the Send button.
        gameFXML.getController(GameScreenController.class).chatFXML.getController(GameChatController.class).message
                .setText("Hello everybody");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Alice sends the message
        push(KeyCode.ENTER);

        ArrayList<ChatMessage> messages = model.getApp().getCurrentPlayer().getGame().getIngameMessages();
        Assert.assertEquals(messages.size(), 1);
        int vBoxChildrenSize = gameFXML.getController(GameScreenController.class).chatFXML
                .getController(GameChatController.class).messageList.getController(LobbyChatMessageListController.class)
                .getHistory().getChildren().size();

        FxAssert.verifyThat(vBoxChildrenSize, is(1));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*
         * =============== RESULT ===============
         */

        // The message from Alice is added to the data model.
        // Therefore every other player in the Game will receive the message.
    }

    @Test
    public void testReceiveMessage() {

        /*
         * =============== SITUATION ===============
         */

        // Alice, Bob and Charlie have joined a game and are now ingame.

        // chat is closed
        FxAssert.verifyThat(gameFXML.getController(GameScreenController.class).chatFXML.getParent().isVisible(), is(false));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
        // Therefore Alice will see the message in the ingame chat.

        // verify that the chat is open
        FxAssert.verifyThat(gameFXML.getController(GameScreenController.class).chatFXML.getParent().isVisible(), is(true));

        ArrayList<ChatMessage> messages = model.getApp().getCurrentPlayer().getGame().getIngameMessages();
        Assert.assertEquals(messages.size(), 1);
        int vBoxChildrenSize = gameFXML.getController(GameScreenController.class).chatFXML
                .getController(GameChatController.class).messageList.getController(LobbyChatMessageListController.class)
                .getHistory().getChildren().size();

        // verify that the amount of text labels in the VBox is the same as the amount of the messages in the data model.
        FxAssert.verifyThat(vBoxChildrenSize, is(1));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
