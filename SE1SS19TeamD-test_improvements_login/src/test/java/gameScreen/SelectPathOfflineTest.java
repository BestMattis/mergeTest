package gameScreen;

import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.Field;
import model.Model;
import model.Unit;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import registerLogin.LoginRegisterTestUtils;

import java.util.ArrayList;
import java.util.List;

public class SelectPathOfflineTest extends ApplicationTest {

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
     * Test the Select Path User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-222">TD-222</a>.
     * Part 1: Successful selection
     */
    @Test
    public void testSelectPath_successful() {
        /*
         * =============== SITUATION ===============
         */

        // Alice is in a running game.

        JSONArray initialGames = new JSONArray().put(
                new JSONObject()
                        .put("joinedPlayer", 1)
                        .put("name", "My Game")
                        .put("id", "my-game-id")
                        .put("neededPlayer", 2));

        LoginRegisterTestUtils.loginForOfflineTest(this, new JSONArray(), initialGames, new JSONArray(), model);

        List<String> notReadyPlayers = new ArrayList<>();
        notReadyPlayers.add("TeamDTestUser");
        List<String> readyPlayers = new ArrayList<>();
        readyPlayers.add("BobTeamD");
        LoginRegisterTestUtils.joinGameForOfflineTest(this, "my-game-id", "My Game", 2, notReadyPlayers, readyPlayers, model);

        readyPlayers.addAll(notReadyPlayers);

        LoginRegisterTestUtils.startGameForOfflineTest(this, "my-game-id", readyPlayers, "config1", model);

        LoginRegisterTestUtils.waitUntilGameFieldLoaded(this);

        // Alice selected a unit

        while (AdvancedWarsApplication.getInstance().getGameScreenCon().getGameFieldController() == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        GameFieldController controller = AdvancedWarsApplication.getInstance().getGameScreenCon().getGameFieldController();
        Field source = this.getField(14, 24);
        final int xSnapshotOffset = 1800;
        final int ySnapshotOffset = 3200;
        int sourceTrueX = source.getPosX() * controller.getXTILE() * controller.getMagnification() / 2 - xSnapshotOffset;
        int sourceTrueY = source.getPosY() * controller.getYTILE() * controller.getMagnification() / 2 - ySnapshotOffset;
        Field target = this.getField(14, 25);

        Unit unitToMove = source.getOccupiedBy();

        this.clickOn(sourceTrueX, sourceTrueY);

        /*
         * =============== ACTION ===============
         */

        // Alice selects a tile in range

        int targetTrueX = target.getPosX() * controller.getXTILE() * controller.getMagnification() / 2 - xSnapshotOffset;
        int targetTrueY = target.getPosY() * controller.getYTILE() * controller.getMagnification() / 2 - ySnapshotOffset;

        this.clickOn(targetTrueX, targetTrueY);

        /*
         * =============== RESULT ===============
         */

        // The unit agrees to move to the selected tile (the target field is marked as reachable)
        Unit unitOnTarget = target.getOccupiedBy();

        Assert.assertNotNull("There was no unit on the selected field", unitToMove);
        Assert.assertNotNull("The unit hasn't moved to the new field", unitOnTarget);
        Assert.assertEquals("Wrong unit on target position", unitToMove, unitOnTarget);
    }

    private Field getField(int x, int y) {
        for (Field field : model.getApp().getCurrentPlayer().getGame().getGameField().getFields()) {
            if (field.getPosX() == x && field.getPosY() == y) {
                return field;
            }
        }
        return null;
    }
}
