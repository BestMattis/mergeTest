package gameLobby;

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

public class SystemMsgStoryTest extends ApplicationTest {
    private Model model;

    /**
     * Setup stage and start application.
     *
     * @param primaryStage the stage to display the GUI.
     */
    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        new AdvancedWarsApplication().start(primaryStage);
    }

    /**
     * Test the GameLobbySystemChat Round Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-206">TD-202</a>.<br />
     */
    @Test
    public void testFinishRoundStory() {
        /*
         * =============== SITUATION ===============
         */

        // starts a new game.
        JSONArray initialGames = new JSONArray().put(new JSONObject().put("joinedPlayer", 0).put("name", "My Game")
                .put("id", "my-game-id").put("neededPlayer", 4));

        // TeamDTestUser and Bob have logged in.
        // Both players are in the lobby.
        Model model = new Model();
        LoginRegisterTestUtils.loginForOfflineTest(this, new JSONArray(), initialGames, new JSONArray(), model); //doesn't log in offline and doesn't open initial game
//		LoginRegisterTestUtils.createGameForOfflineTest(this,"my-game-id","My Game",4, new ArrayList<String>(), new ArrayList<String>(),model);

        sleep(2000);
        // TeamDTestUser joins game
        HBox gameCard = this.lookup("#gameCard").queryAs(HBox.class);
        this.clickOn(gameCard);
        sleep(3000);

        // Set id for TeamDTestUser
        model.getApp().getCurrentPlayer().setId("FakeID");

        // Bob joins the game
        String msgBobUnready = "{data:{color:BLUE,isReady:false,name:Bob,id:PlayerID,currentGame:GameID},action:gameInitObject}";
        model.getWebSocketComponent().getGameClient().onMessage(msgBobUnready);
        sleep(3000);
        // Bob is set ready
        String msgBobReady = "{data:{newValue:\"true\",fieldName:isReady,id:PlayerID},action:gameChangeObject}";
        model.getWebSocketComponent().getGameClient().onMessage(msgBobReady);
        sleep(2000);

        // Karlie joins the game
        String msgKarlieUnready = "{data:{color:BLUE,isReady:false,name:Karlie,id:PlayerID1,currentGame:GameID},action:gameInitObject}";
        model.getWebSocketComponent().getGameClient().onMessage(msgKarlieUnready);
        sleep(2000);

        // Olaph joins the game
        String msgOlaphUnready = "{data:{color:BLUE,isReady:false,name:Olaph,id:PlayerID2,currentGame:GameID},action:gameInitObject}";
        model.getWebSocketComponent().getGameClient().onMessage(msgOlaphUnready);
        sleep(2000);

        // Olaph is ready
        String msgOlaphReady = "{data:{newValue:\"true\",fieldName:isReady,id:PlayerID2},action:gameChangeObject}";
        model.getWebSocketComponent().getGameClient().onMessage(msgOlaphReady);
        sleep(2000);

        // Karlie left the Game
        String msgKarliLeft = "{data:{fieldName:\"allPlayer\",from:GameID,id:PlayerID1},action:gameRemoveObject}";
        model.getWebSocketComponent().getGameClient().onMessage(msgKarliLeft);
        sleep(2000);

        // TeamDTestUser is set ready
        ToggleButton ready = this.lookup("#ready").queryAs(ToggleButton.class);
        this.clickOn(ready);
        sleep(3000);

        // Hans joins the game
        String msgHansUnready = "{data:{color:BLUE,isReady:false,name:Hans,id:PlayerID3,currentGame:GameID},action:gameInitObject}";
        model.getWebSocketComponent().getGameClient().onMessage(msgHansUnready);
        sleep(2000);

        // Hans is ready
        String msgHanshReady = "{data:{newValue:\"true\",fieldName:isReady,id:PlayerID3},action:gameChangeObject}";
        model.getWebSocketComponent().getGameClient().onMessage(msgHanshReady);
        sleep(2000);

        System.out.println(model.getApp().getCurrentPlayer().getGame().getIngameMessages() + " ingameMesges Test");
        /*
         * =============== ACTION ===============
         */

        // Set id for TeamDTestUser
        model.getApp().getCurrentPlayer().setId("FakeID");

        /*
         * =============== RESULT ===============
         */
    }
}
