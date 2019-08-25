package gameScreen;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.Model;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import registerLogin.LoginRegisterTestUtils;

import static junit.framework.TestCase.assertEquals;


public class MinimapTest extends ApplicationTest {

    private Model model;

    public void start(Stage stage) throws Exception {
        AdvancedWarsApplication awa = new AdvancedWarsApplication();
        awa.start(stage);
        model = awa.model;
        AdvancedWarsApplication.offlineTest = true;
    }

    @Test
    public void testMinimap() {

        /*
         * =============== SITUATION ===============
         */
        //Alice is in a running game.
        //The game screen is loaded.
        //Alice sees the top left corner of the map.

        JSONArray initialGames = new JSONArray().put(
                new JSONObject()
                        .put("joinedPlayer", 1)
                        .put("name", "My Game")
                        .put("id", "69")
                        .put("neededPlayer", 2));

        LoginRegisterTestUtils.loginForOfflineTest(this, new JSONArray(), initialGames, new JSONArray(), model);

        Label gamename = this.lookup("#gameName").queryAs(Label.class);
        this.clickOn(gamename);

        JSONObject gameInitBob = new JSONObject();
        gameInitBob.put("action", "gameInitObject");
        JSONObject gameInitDataBob = new JSONObject();
        gameInitDataBob.put("color", "RED");
        gameInitDataBob.put("isReady", true);
        gameInitDataBob.put("name", "BobTeamD");
        gameInitDataBob.put("id", "Player@1");
        gameInitDataBob.put("currentGame", "69");
        gameInitBob.put("data", gameInitDataBob);
        model.getWebSocketComponent().getGameClient().onMessage(gameInitBob.toString());

        JSONObject gameInit = new JSONObject();
        gameInit.put("action", "gameInitObject");
        JSONObject gameInitData = new JSONObject();
        gameInitData.put("color", "BLUE");
        gameInitData.put("isReady", false);
        gameInitData.put("name", "TeamDTestUser");
        gameInitData.put("id", "Player@2");
        gameInitData.put("currentGame", "69");
        gameInit.put("data", gameInitData);
        model.getWebSocketComponent().getGameClient().onMessage(gameInit.toString());

        ToggleButton ready = this.lookup("#ready").queryAs(ToggleButton.class);
        this.clickOn(ready);

        /*
         * =============== ACTION ===============
         */

        //Alice clicks the right bottom corner of the Minimap.

        AnchorPane mappane = this.lookup("#mappane").queryAs(AnchorPane.class);


        this.clickOn(mappane);

        /*
         * =============== RESULT ===============
         */

        //Alice now sees the middle of the map.
        ScrollPane gamefield = this.lookup("#gamefield").queryAs(ScrollPane.class);

        //Assert that the map is in the right position
        assertEquals("Gamefield isn't in the right x position", 0.5, gamefield.getHvalue());
        assertEquals("Gamefield isn't in the right y position", 0.5, gamefield.getVvalue());

    }
}
