package armyManager;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.Model;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import registerLogin.LoginRegisterTestUtils;
import syncCommunication.SynchronousArmyCommunicator;
import testUtils.JSONTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ArmyManagerOfflineTest extends ApplicationTest {
    Model model;
    private Button armyManagerButton;
    private Button backButton;
    private Button newButton;
    private ComboBox<String> configList;
    private Node trashbinIcon;
    private Parent unitCard;
    private Button saveButton;
    private Button gameLobbyArmyManagerButton;

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
     * Test the Open Army Manager Story (no Jira Story available).
     */
    @Test
    public void testOpenArmyManager() {
        /*
         * =============== SITUATION ===============
         */

        // Alice has logged in.

        LoginRegisterTestUtils.loginForOfflineTest(this, model);
        this.loadLobbyUIElements();

        /*
         * =============== ACTION ===============
         */

        // Alice clicks the Army Manager Button.

        this.clickOn(this.armyManagerButton);
        this.loadArmyManagerUIElements();

        /*
         * =============== RESULT ===============
         */

        // The Army Manager opens.

        Assert.assertNotNull(this.backButton);
    }

    /**
     * Test the Army Manager Create New Configuration Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-162">TD-162</a>.<br />
     */
    @Test
    public void testArmyManager_createNewConfiguration() {
        /*
         * =============== SITUATION ===============
         */

        // Alice is in the Army Manager.

        this.startArmyManager(new JSONArray());

        /*
         * =============== ACTION ===============
         */

        // Alice clicks on "Select Army-Configuration" and clicks on the "+-Button".

        this.clickOn(this.newButton);

        /*
         * =============== RESULT ===============
         */

        // GUI updates and shows empty configuration in the ArmyManager.

        Assert.assertTrue(this.configList.getItems().contains("config2"));
    }

    /**
     * Test the Army Manager Edit Configuration Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-147">TD-147</a> (slightly modified).<br />
     */
    @Test
    public void testArmyManager_editConfiguration() {
        /*
         * =============== SITUATION ===============
         */

        // Alice is in the Army Manager.

        this.startArmyManager(new JSONArray());

        // Alice has selected config1.

        // ---- done on Application startup ----

        /*
         * =============== ACTION ===============
         */

        // Alice removes the first Unit from the configuration.

        this.drag(unitCard).dropTo(this.trashbinIcon);

        // Alice adds a Chopper to the configuration.

        Label newUnitLabel = this.lookup("#unitlabel").queryAllAs(Label.class).stream().filter(l -> l.getText().equals("Chopper")).findFirst().get();
        this.drag(newUnitLabel).dropTo(unitCard);

        /*
         * =============== RESULT ===============
         */

        // GUI updates and shows the configuration in the ArmyManager with the new Chopper.

        Assert.assertThat((Label) unitCard.lookup("#unitlabel"), LabeledMatchers.hasText("Chopper"));
    }

    /**
     * Test the Army Manager Load Configuration Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-145">TD-145</a> (slightly modified).<br />
     */
    @Test
    public void testArmyManager_loadConfiguration() {
        /*
         * =============== SITUATION ===============
         */

        // Alice has two units.

        JSONArray initialArmies = new JSONArray("[{\"name\":\"config1\",\"id\":\"5d2ddcd181487d0001a39f81\",\"units\":[\"5cc051bd62083600017db3b6\",\"5cc051bd62083600017db3b7\",\"5cc051bd62083600017db3b8\",\"5cc051bd62083600017db3b9\",\"5cc051bd62083600017db3ba\",\"5cc051bd62083600017db3bb\",\"5cc051bd62083600017db3b6\",\"5cc051bd62083600017db3b7\",\"5cc051bd62083600017db3b8\",\"5cc051bd62083600017db3b9\"]}, {\"name\":\"config2\",\"id\":\"5d3c89bdeff5dd00018e51cd\",\"units\":[\"5cc051bd62083600017db3bb\",\"5cc051bd62083600017db3bb\",\"5cc051bd62083600017db3bb\",\"5cc051bd62083600017db3bb\",\"5cc051bd62083600017db3bb\",\"5cc051bd62083600017db3bb\",\"5cc051bd62083600017db3bb\",\"5cc051bd62083600017db3bb\",\"5cc051bd62083600017db3bb\",\"5cc051bd62083600017db3bb\"]}]");

        // Alice is in the Army Manager.

        this.startArmyManager(initialArmies);

        // Alice has selected config1.

        // ---- done on Application startup ----

        /*
         * =============== ACTION ===============
         */

        // Alice clicks on "Select Army-Configuration" and selects config2.

        this.configList.getSelectionModel().select(1);

        FutureTask<Void> configListFuture = new FutureTask<>(() -> {
            this.configList.show();
            this.configList.hide();
            return null;
        });
        Platform.runLater(configListFuture);
        try {
            configListFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        /*
         * =============== RESULT ===============
         */

        // GUI updates and shows the configuration in the ArmyManager with the new Chopper.

        Assert.assertThat((Label) this.unitCard.lookup("#unitlabel"), LabeledMatchers.hasText("Chopper"));
    }

    /**
     * Test the Army Manager Save Configuration Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-146">TD-146</a> (slightly modified).<br />
     */
    @Test
    public void testArmyManager_saveConfiguration() {
        /*
         * =============== SITUATION ===============
         */

        JSONArray initialArmies = new JSONArray("[{\"name\":\"config1\",\"id\":\"my-config-id1\",\"units\":[\"5cc051bd62083600017db3b6\",\"5cc051bd62083600017db3b7\",\"5cc051bd62083600017db3b8\",\"5cc051bd62083600017db3b9\",\"5cc051bd62083600017db3ba\",\"5cc051bd62083600017db3bb\",\"5cc051bd62083600017db3b6\",\"5cc051bd62083600017db3b7\",\"5cc051bd62083600017db3b8\",\"5cc051bd62083600017db3b9\"]}]");

        // Alice is in the Army Manager.

        this.startArmyManager(initialArmies);

        SynchronousArmyCommunicator communicator = new SynchronousArmyCommunicator(model.getPlayerHttpRequestsHashMap().get(model.getApp().getCurrentPlayer()));
        communicator.setJsonAdapter((method, url, json) -> {
            if (url.startsWith("/army") && method.equals("PUT")) {
                Assert.assertEquals("URL contains wrong ID", "/army/my-config-id1", url);
                JSONTestUtils.assertJSON(json, "config1", "name");
                String[] units = (String[]) json.get("units");
                Assert.assertEquals(1, this.countUnits(units, "5cc051bd62083600017db3b6"));
                Assert.assertEquals(2, this.countUnits(units, "5cc051bd62083600017db3b7"));
                Assert.assertEquals(2, this.countUnits(units, "5cc051bd62083600017db3b8"));
                Assert.assertEquals(2, this.countUnits(units, "5cc051bd62083600017db3b9"));
                Assert.assertEquals(1, this.countUnits(units, "5cc051bd62083600017db3ba"));
                Assert.assertEquals(2, this.countUnits(units, "5cc051bd62083600017db3bb"));

                JSONObject response = new JSONObject();
                response.put("status", "success");
                communicator.injectResponse(response);
            }
        });

        // Alice has selected config1.

        // ---- done on Application startup ----

        /*
         * =============== ACTION ===============
         */

        // Alice removes the first Unit from the configuration.

        this.drag(unitCard).dropTo(this.trashbinIcon);

        // Alice adds a Chopper to the configuration.

        Label newUnitLabel = this.lookup("#unitlabel").queryAllAs(Label.class).stream().filter(l -> l.getText().equals("Chopper")).findFirst().get();
        this.drag(newUnitLabel).dropTo(unitCard);

        // Alice clicks the Save Button

        this.clickOn(this.saveButton);

        /*
         * =============== RESULT ===============
         */

        // The updated configuration is stored.

        // ---- Checked by Message Sent Test ----
    }

    /**
     * Test the Army Manager Back To Lobby Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-170">TD-170</a> (slightly modified).<br />
     */
    @Test
    public void testArmyManager_backToLobby() {
        /*
         * =============== SITUATION ===============
         */

        // Alice is in the Army Manager.

        this.startArmyManager(new JSONArray());

        /*
         * =============== ACTION ===============
         */

        // Alice clicks the Back-Button.

        this.clickOn(this.backButton);

        /*
         * =============== RESULT ===============
         */

        // Screen changes to LobbyScreen.

        this.loadLobbyUIElements();

        Assert.assertNotNull(this.armyManagerButton);
    }

    /**
     * Test the Army Manager Back To Game Lobby Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-170">TD-170</a> (slightly modified).<br />
     */
    @Test
    public void testArmyManager_backToGameLobby() {
        /*
         * =============== SITUATION ===============
         */

        // Alice is in the Army Manager.

        JSONArray initialGames = new JSONArray().put(
                new JSONObject()
                        .put("joinedPlayer", 1)
                        .put("name", "My Game")
                        .put("id", "my-game-id")
                        .put("neededPlayer", 2));

        LoginRegisterTestUtils.loginForOfflineTest(this, new JSONArray(), initialGames, new JSONArray(), model);
        List<String> notReadyPlayers = new ArrayList<>();
        notReadyPlayers.add("TeamDTestUser");
        notReadyPlayers.add("BobTeamD");
        LoginRegisterTestUtils.joinGameForOfflineTest(this, "my-game-id", "My Game", 2, notReadyPlayers, new ArrayList<>(), model);
        this.loadGameLobbyUIElements();
        this.clickOn(this.gameLobbyArmyManagerButton);
        this.loadArmyManagerUIElements();

        /*
         * =============== ACTION ===============
         */

        // Alice clicks the Back-Button.

        this.clickOn(this.backButton);

        /*
         * =============== RESULT ===============
         */

        // Screen changes to LobbyScreen.

        this.loadGameLobbyUIElements();

        Assert.assertNotNull(this.gameLobbyArmyManagerButton);
    }

    private void startArmyManager(JSONArray initialArmies) {
        LoginRegisterTestUtils.loginForOfflineTest(this, new JSONArray(), new JSONArray(), initialArmies, model);
        this.loadLobbyUIElements();

        this.clickOn(this.armyManagerButton);
        this.loadArmyManagerUIElements();
    }

    private void loadLobbyUIElements() {
        this.armyManagerButton = this.lookup("#armymanager").queryButton();
    }

    private void loadArmyManagerUIElements() {
        this.newButton = this.lookup("#newButton").queryButton();
        this.saveButton = this.lookup("#save").queryButton();
        this.backButton = this.lookup("#back").queryButton();
        this.configList = this.lookup("#configlist").queryComboBox();
        this.trashbinIcon = this.lookup("#trashbin").query();
        this.unitCard = this.lookup("#panezero").queryParent();
    }

    private void loadGameLobbyUIElements() {
        this.gameLobbyArmyManagerButton = this.lookup("#manager").queryButton();
    }

    private int countUnits(String[] units, String unitID) {
        int result = Arrays.asList(units).stream().mapToInt(x -> x.equals(unitID) ? 1 : 0).sum();
        return result;
    }
}
