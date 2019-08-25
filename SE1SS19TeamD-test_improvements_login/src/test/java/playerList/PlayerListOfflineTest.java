package playerList;

import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.Model;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import registerLogin.LoginRegisterTestUtils;
import testUtils.JSONTestUtils;

public class PlayerListOfflineTest extends ApplicationTest {

    // Lobby UI Elements

    private ListView<String> playerList;
    private Labeled numberOfPlayersLabel;
    private Model model;

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
     * Test the Player List Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-8">TD-8</a>.<br />
     * Part 1: Current Player's list contains online players.
     */
    @Test
    public void testPlayerList_getListOnLogin() {
        /*
         * =============== SITUATION ===============
         */

        // Bob has logged in and waits in the lobby.

        // ---- Test evaluation with Web Socket Mock, so no need to login Bob ----

        // The Lobby view is open
        // The lobby view contains the player list.

        // ---- done on Application startup ----

        /*
         * =============== ACTION ===============
         */

        // Alice logs in.

        LoginRegisterTestUtils.loginForOfflineTest(this, new JSONArray().put("BobTeamD"), new JSONArray(), new JSONArray(), model);

        this.loadLobbyUIElements();

        /*
         * =============== RESULT ===============
         */

        // The Player List in Alice's view displays Bob's username.

        this.performWhileNotPresent();

        // The Player Count updates

        int actual = model.getApp().getAllPlayers().size();
        int expected = Integer.parseInt(this.numberOfPlayersLabel.getText());
        Assert.assertEquals("Wrong number of players displayed", actual, expected);
    }

    /**
     * Test the Player List Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-8">TD-8</a>.<br />
     * Part 2: Current player's list updates to display joined player.
     */
    @Test
    public void testPlayerList_updateListOnPlayerJoined() {
        /*
         * =============== SITUATION ===============
         */

        // Alice has logged in and waits in the lobby.

        LoginRegisterTestUtils.loginForOfflineTest(this, model);

        this.loadLobbyUIElements();

        // The Lobby view is open
        // The lobby view contains the player list.

        // ---- done on Application startup ----

        /*
         * =============== ACTION ===============
         */

        // Bob enters his username and password into his own Login / Register screen and
        // clicks Login.

        JSONObject userJoined = new JSONObject();
        userJoined.put("action", "userJoined");
        JSONObject userJoinedData = new JSONObject();
        userJoinedData.put("name", "BobTeamD");
        userJoined.put("data", userJoinedData);
        model.getWebSocketComponent().getSystemClient().onMessage(userJoined.toString());

        /*
         * =============== RESULT ===============
         */

        // The Player List in Alice's view displays Bob's username.

        this.performWhileNotPresent();
    }

    /**
     * Test the Player List Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-8">TD-8</a>.<br />
     * Part 3: Other player receive message when current player logs in.
     */
    @Test
    public void testPlayerList_notifyServerOnLogin() {
        /*
         * =============== SITUATION ===============
         */

        // Bob has logged in and waits in the lobby.

        // ---- Messages to Bob verified by JSON adapter ----

        // The Lobby view is open
        // The lobby view contains the player list.

        // ---- done on Application startup ----

        /*
         * =============== ACTION ===============
         */

        // Alice enters her username and password into his own Login / Register screen and
        // clicks Login.

        JSONObject msg = LoginRegisterTestUtils.startupMessageSentTest(this, "/user/login", "POST", model);

        /*
         * =============== RESULT ===============
         */

        // The Player List in Bob's view displays Alice's username.

        JSONTestUtils.assertJSON(msg, LoginRegisterTestUtils.getTestUserName(), "name");
        JSONTestUtils.assertJSON(msg, LoginRegisterTestUtils.getTestUserPassword(), "password");
    }

    private void loadLobbyUIElements() {
        this.playerList = this.lookup("#playerList").queryListView();
        this.numberOfPlayersLabel = this.lookup("#numberOfPlayers").queryLabeled();
    }

    private void performWhileNotPresent() {
        while (!this.playerList.getItems().stream().anyMatch(e -> e.equals("BobTeamD"))) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}