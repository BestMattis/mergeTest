package observer;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.Model;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import registerLogin.LoginRegisterTestUtils;

/**
 * Tests the JoinGameAsObserver User Stories.
 */
public class ObserverStoriesTest extends ApplicationTest {

    private Model model;
    private VBox observerBox;

    /**
     * Setup stage and start application.
     *
     * @param primaryStage the stage to display the GUI.
     */
    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        AdvancedWarsApplication awa = new AdvancedWarsApplication();
        awa.start(primaryStage);
        model = awa.model;
    }

    /**
     * Test the Join Game as Observer Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-195">TD-195</a>.<br />
     */
    @Test
    public void testJoinGameAsObserverStory() {
        /*
         * =============== SITUATION ===============
         */

        // Alice starts a new game.
        JSONArray initialGames = new JSONArray()
                .put(new JSONObject()
                        .put("joinedPlayer", 0)
                        .put("name", "My Game")
                        .put("id", "my-game-id")
                        .put("neededPlayer", 2));

        // Alice and Bob have logged in.
        // Both players are in the lobby.
        LoginRegisterTestUtils.loginForOfflineTest(this, new JSONArray(), initialGames, new JSONArray(), model);


        /*
         * =============== ACTION ===============
         */

        // Alice clicks the Observe Button of the "Fancy Game"
        this.loadLobbyElements();
        this.clickOn(this.observerBox);

        /*
         * =============== RESULT ===============
         */

        // The gameLobby is shown in observerMode
        TextField sendField = this.lookup("#sendfield").queryAs(TextField.class);
        ToggleButton ready = this.lookup("#ready").queryAs(ToggleButton.class);
        Button managerButton = this.lookup("#manager").queryButton();
        Button sendButton = this.lookup("#send").queryButton();
        ChoiceBox<String> choice = this.lookup("#choice").queryAs(ChoiceBox.class);

        // Functional Buttons are hidden
        Assert.assertFalse(sendField.isVisible());
        Assert.assertFalse(ready.isVisible());
        Assert.assertFalse(managerButton.isVisible());
        Assert.assertFalse(sendButton.isVisible());
        Assert.assertFalse(choice.isVisible());

        // Bob joins the game
        String msgBobUnready = "{data:{color:BLUE,isReady:false,name:Bob,id:PlayerID,currentGame:GameID},action:gameInitObject}";
        model.getWebSocketComponent().getGameClient().onMessage(msgBobUnready);

        // Charlie joins the game
        String msgCharlieUnready = "{data:{color:GREEN,isReady:false,name:Charlie,id:PlayerID2,currentGame:GameID2},action:gameInitObject}";
        model.getWebSocketComponent().getGameClient().onMessage(msgCharlieUnready);

        System.out.println(model.getApp().getCurrentPlayer().getGame().getIngameMessages() + " : current joinde players");

        // Bob is set ready
        String msgBobReady = "{data:{newValue:\"true\",fieldName:isReady,id:PlayerID},action:gameChangeObject}";

        model.getWebSocketComponent().getGameClient().onMessage(msgBobReady);

        // Charlie is set ready
        String msgCharlieReady = "{data:{newValue:\"true\",fieldName:isReady,id:PlayerID2},action:gameChangeObject}";
        model.getWebSocketComponent().getGameClient().onMessage(msgCharlieReady);

        // The GameScreen is shown in observerMode
        TextField messageField = this.lookup("#message").queryAs(TextField.class);
        Button finishRound = this.lookup("#finishRound").queryButton();
        AnchorPane roundButtonPane = this.lookup("#roundButtonPane").queryAs(AnchorPane.class);
        AnchorPane base = this.lookup("#waitingbase").queryAs(AnchorPane.class);

        // GameLobby is hidden
        Assert.assertFalse(base.isVisible());

        // Functional buttons are hidden
        Assert.assertFalse(messageField.isVisible());
        Assert.assertFalse(finishRound.isVisible());
        Assert.assertFalse(roundButtonPane.isVisible());
    }

    private void loadLobbyElements() {
        this.observerBox = this.lookup("#observerBox").queryAs(VBox.class);
    }
}
