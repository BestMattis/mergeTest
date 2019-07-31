package gameScreen;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.Field;
import model.Unit;
import registerLogin.LoginRegisterTestUtils;

public class SelectPathOfflineTest extends ApplicationTest {

    private Canvas colorCanvas;

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

        LoginRegisterTestUtils.loginForOfflineTest(this, new JSONArray(), initialGames);
        
        List<String> notReadyPlayers = new ArrayList<>();
	notReadyPlayers.add("TeamDTestUser");
	List<String> readyPlayers = new ArrayList<>();
	readyPlayers.add("BobTeamD");
        LoginRegisterTestUtils.joinGameForOfflineTest(this, "my-game-id", "My Game", 2, notReadyPlayers, readyPlayers); 
        
        readyPlayers.addAll(notReadyPlayers);
        
        LoginRegisterTestUtils.startGameForOfflineTest(this, "my-game-id", notReadyPlayers, "config1");
	
        LoginRegisterTestUtils.waitUntilGameFieldLoaded(this);
        
    	this.loadGameUIElements();
        
        // Alice selected a unit
    	
    	// TODO use Unit on dedicated field
    	Unit u = null;
    	int posX = u.getOccupiesField().getPosX();
	int posY = u.getOccupiesField().getPosY();
    	
    	// TODO use field that can be reached
    	Field f = null;
    	
	this.clickOn(posX, posY);
    	
    	/*
    	 * =============== ACTION ===============
    	 */
        
    	// Alice selects a tile in range
    	
    	this.clickOn(f.getPosX(), f.getPosY());
        
    	/*
    	 * =============== RESULT ===============
    	 */
        
    	// The unit agrees to move to the selected tile(the path is green)
    	
        WritableImage image = this.colorCanvas.snapshot(null, null);
        Color color = image.getPixelReader().getColor(posX, posX);
	Assert.assertTrue("Path was not painted green", color.getGreen() > color.getRed());
    }

    private void loadGameUIElements() {
        this.colorCanvas = this.lookup("#colorCanvas").queryAs(Canvas.class);
    }
}
