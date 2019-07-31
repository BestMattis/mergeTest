package gameLobby;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.Model;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import registerLogin.LoginRegisterTestUtils;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class GetReadyTest extends ApplicationTest {

    /**
     * Setup stage and start application
     * @param primaryStage the stage to display
     */
    @Override
    public void start(Stage primaryStage)throws IOException {
        new AdvancedWarsApplication().start(primaryStage);
    }

    /**
     * Test the "Game Lobby: Get ready for game" User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-194">TD-194</a>.
     */
    @Test
    public void testGetReady(){
        /*
         * =============== SITUATION ===============
         */

        //Alice and Bob have logged in.
        //Alice and Bob have joined a game.
        //The Game was not started and Alice is in the Game Lobby.
        //Alice has selected an army.

        JSONArray initialGames = new JSONArray().put(
                new JSONObject()
                        .put("joinedPlayer", 1)
                        .put("name", "My Game")
                        .put("id", "69")
                        .put("neededPlayer", 2));

        LoginRegisterTestUtils.loginForOfflineTest(this, new JSONArray(), initialGames);

        Label gamename = this.lookup("#gameName").queryAs(Label.class);
        this.clickOn(gamename);

        //send gameInitObject when other player joins
        JSONObject gameInit = new JSONObject();
        gameInit.put("action", "gameInitObject");
        JSONObject gameInitData = new JSONObject();
        gameInitData.put("color", "BLUE");
        gameInitData.put("isReady", false);
        gameInitData.put("name", "BobTeamD");
        gameInitData.put("id", "Player@1");
        gameInitData.put("currentGame", "69");
        gameInit.put("data", gameInitData);
        Model.getWebSocketComponent().getGameClient().onMessage(gameInit.toString());

        //TODO fix problem with first players name label
        Label player1 = this.lookup("#pl1").queryAs(Label.class);
        Label player2 = this.lookup("#pl2").queryAs(Label.class);

        //check if players are shown as not ready
        //assertEquals("you are displayed as ready", Color.WHITE, player1.getTextFill());
        assertEquals("other player is displayed as ready", Color.WHITE, player2.getTextFill());

        //check if other player has connected properly
        assertEquals("player shouldn't be ready here", false, Model.getApp().getCurrentPlayer().getGame().getPlayers().get(1).getIsReady());

        /*
         * =============== ACTION ===============
         */

        //Alice clicks the Ready Button.
        ToggleButton ready = this.lookup("#ready").queryAs(ToggleButton.class);
        this.clickOn(ready);

        /*
         * =============== RESULT ===============
         */

        //Bob sees that Alice is ready.
        //When all players are ready, Alice's client displays the Game View.
        //It displays a minimap, all players, the current round / phase
        // and a button to leave the game.


        //send gameChangeObject when other player gets ready
        JSONObject gameChange = new JSONObject();
        gameChange.put("action", "gameChangeObject");
        JSONObject gameChangeData = new JSONObject();
        gameChangeData.put("newValue", "true");
        gameChangeData.put("fieldName", "isReady");
        gameChangeData.put("id", "Player@1");
        gameChange.put("data", gameChangeData);
        Model.getWebSocketComponent().getGameClient().onMessage(gameChange.toString());

        //check if both players are ready now
        assertEquals("other player should be ready here", true, Model.getApp().getCurrentPlayer().getGame().getPlayers().get(1).getIsReady());
        assertEquals("you should be ready here", true, Model.getApp().getCurrentPlayer().getIsReady());

        //check if players are shown as ready
        assertEquals("you aren't displayed as ready", Color.LIGHTGREEN, player1.getTextFill());
        assertEquals("other player isn't displayed as ready", Color.LIGHTGREEN, player2.getTextFill());

        Parent gamefield = this.lookup("#gamefield").queryAs(Parent.class);
        Assert.assertNotNull("GameScreen is not displayed.", gamefield);
    }
}
