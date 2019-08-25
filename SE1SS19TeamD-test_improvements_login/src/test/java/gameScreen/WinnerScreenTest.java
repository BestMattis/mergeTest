package gameScreen;

import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.Model;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import registerLogin.LoginRegisterTestUtils;

public class WinnerScreenTest extends ApplicationTest {

    private Model model;
    private HBox gameCard;

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
     * Test the Game View: Show Winner as described here:
     * <a href="https://jira.uniks.de/browse/TD-202">TD-202</a>.<br />
     */
    @Test
    public void testShowWinnerScreenStory() {
        /*
         * =============== SITUATION ===============
         */
        // Alice has logged in
        // Alice has joined a game
        // The Game was started
        // Alice is not in Observer mode
        // Alice has two units, Bob has one unit
        // No more players live

        // starts a new game.
        JSONArray initialGames = new JSONArray().put(new JSONObject().put("joinedPlayer", 0).put("name", "My Game")
                .put("id", "my-game-id").put("neededPlayer", 2));

        // Alice and Bob have logged in.
        // Both players are in the lobby.
        LoginRegisterTestUtils.loginForOfflineTest(this, new JSONArray(), initialGames, new JSONArray(), model);

        // Alice joins game
        this.loadLobbyElements();
        this.clickOn(this.gameCard);

        // Alice is set ready
        ToggleButton ready = this.lookup("#ready").queryAs(ToggleButton.class);
        this.clickOn(ready);

        // Bob joins the game
        String msgBobUnready = "{data:{color:BLUE,isReady:false,name:Bob,id:PlayerID,currentGame:GameID},action:gameInitObject}";
        model.getWebSocketComponent().getGameClient().onMessage(msgBobUnready);

        // Bob is set ready
        String msgBobReady = "{data:{newValue:\"true\",fieldName:isReady,id:PlayerID},action:gameChangeObject}";
        model.getWebSocketComponent().getGameClient().onMessage(msgBobReady);


        /*
         * =============== ACTION ===============
         */

        // Alice kills Bob's last unit

        // winner msg gets send from server
        String msgWinner = "{\"data\":{\"newValue\":\"PlayerID\",\"fieldName\":\"winner\",\"id\":\"Game@1c216e5e\"},\"action\":\"gameChangeObject\"}";
        model.getWebSocketComponent().getGameClient().onMessage(msgWinner);
        sleep(50);

        /*
         * =============== RESULT ===============
         */


        // click leave Button to get back to the Lobby
        Button leaveButton = this.lookup("#backButtonWinner").queryButton();
        this.clickOn(leaveButton); // TODO fix this

    }

    private void loadLobbyElements() {
        this.gameCard = this.lookup("#gameCard").queryAs(HBox.class);
    }
}
