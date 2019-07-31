package gameList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.Model;
import registerLogin.LoginRegisterTestUtils;
import testUtils.JSONTestUtils;

public class GameListOfflineTest extends ApplicationTest {

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
     * Test the Game List Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-12">TD-12</a>.<br />
     * Part 1: Current player's list contains game created before login.
     */
    @Test
    public void testGameList_getListOnLogin() {
	/*
	 * =============== SITUATION ===============
	 */

	// Alice and Bob have logged in.
	// Both players are in the lobby.

	LoginRegisterTestUtils.loginForOfflineTest(this);

	/*
	 * =============== ACTION ===============
	 */

	// Alice starts a new game.
	
	JSONObject gameCreated = new JSONObject();
	gameCreated.put("action", "gameCreated");
	JSONObject gameCreatedData = new JSONObject();
	gameCreatedData.put("id", "my-game-id");
	gameCreatedData.put("name", "My Game");
	gameCreatedData.put("neededPlayer", 4);
	gameCreated.put("data", gameCreatedData);
	Model.getWebSocketComponent().getSystemClient().onMessage(gameCreated.toString());

	/*
	 * =============== RESULT ===============
	 */

	// The Player List in Alice's view displays Bob's username.

	this.performWhileNotPresent();
    }

    /**
     * Test the Game List Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-12">TD-12</a>.<br />
     * Part 2: Current player's list updates to display game created by other player.
     */
    @Test
    public void testGameList_updateListOnGameCreated() {
	/*
	 * =============== SITUATION ===============
	 */

	// Alice and Bob have logged in.
	// Both players are in the lobby.
	
	JSONArray games = new JSONArray();
		
	JSONObject myGame = new JSONObject();
	myGame.put("joinedPlayer", 1);
	myGame.put("name", "My Game");
	myGame.put("id", "my-game");
	myGame.put("neededPlayer", 4);
	
	games.put(myGame);
	
	LoginRegisterTestUtils.loginForOfflineTest(this, new JSONArray(), games);

	/*
	 * =============== ACTION ===============
	 */

	// Bob starts a new game.
	
	JSONObject gameCreated = new JSONObject();
	gameCreated.put("action", "gameCreated");
	JSONObject gameCreatedData = new JSONObject();
	gameCreatedData.put("id", "my-game-id");
	gameCreatedData.put("name", "My Game");
	gameCreatedData.put("neededPlayer", 4);
	gameCreated.put("data", gameCreatedData);
	Model.getWebSocketComponent().getSystemClient().onMessage(gameCreated.toString());

	/*
	 * =============== RESULT ===============
	 */

	// The Player List in Alice's view displays Bob's username.

	this.performWhileNotPresent();
    }
    
    /**
     * Test the Game List Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-12">TD-12</a>.<br />
     * Part 3: Other player receive message when current player creates a game.
     */
    @Test
    public void testGameList_notifyServerOnGameCreated() {
        /*
         * =============== SITUATION ===============
         */

        // Alice and Bob have logged in.
        // Both players are in the lobby.
        
	LoginRegisterTestUtils.loginForOfflineTest(this);
	
	// ---- Messages to Bob verified by JSON adapter ----
	
        /*
         * =============== ACTION ===============
         */

        // Alice starts a new game.
        
	JSONObject msg = LoginRegisterTestUtils.createGameForMessageSentTest(this, "/game", "POST");
	
        /*
         * =============== RESULT ===============
         */

        // The Player List in Alice's view displays Bob's username.

	JSONTestUtils.assertJSON(msg, "My Game", "name");
	JSONTestUtils.assertJSON(msg, 2, "neededPlayer");
    }
    
    /**
     * Test the Game List Mouse Events.
     */
    @Test
    public void testGameList_mouseEvents() {
	/*
         * =============== SITUATION ===============
         */

        // Alice has logged in.
	// A game was started.
	
	JSONArray initialGames = new JSONArray().put(
		new JSONObject()
		.put("joinedPlayer", 0)
		.put("name", "My Game")
		.put("id", "my-game-id")
		.put("neededPlayer", 2));
	
	LoginRegisterTestUtils.loginForOfflineTest(this, new JSONArray(), initialGames);
	
	/*
         * =============== ACTION ===============
         */

        // Alice hovers to the game in the game list.
	
	HBox gameCard = this.lookup("#gameCard").queryAs(HBox.class);
	String style = gameCard.getStyle();
	this.moveTo(gameCard);
	
	/*
         * =============== RESULT ===============
         */

        // The game card style updates accordingly.
	
	Assert.assertNotEquals("Style does not change on mouse entered", style, gameCard.getStyle());
	
	// When the mouse exits, the style changes back.
	
	this.moveTo("#newGame");
	Assert.assertEquals("Style does not change back on mouse left", style, gameCard.getStyle());
    }
    
    /**
     * Test the Game Full Story.
     */
    @Test
    public void testGameList_gameFull() {
	/*
         * =============== SITUATION ===============
         */

        // Alice has logged in.
	// A game was started and 2 players joined.
	
	JSONArray initialGames = new JSONArray().put(
		new JSONObject()
		.put("joinedPlayer", 2)
		.put("name", "My Game")
		.put("id", "my-game-id")
		.put("neededPlayer", 2));
	
	/*
         * =============== ACTION ===============
         */

        // Alice logs in.
	
	LoginRegisterTestUtils.loginForOfflineTest(this, new JSONArray(), initialGames);
	
	/*
         * =============== RESULT ===============
         */

        // The game card style is red indicating that the game is full.
	
	HBox gameCard = this.lookup("#gameCard").queryAs(HBox.class);
	Assert.assertNotEquals("Style does not change on mouse entered", "-fx-background-color: #2f2c31", gameCard.getStyle());
    }

    private void performWhileNotPresent() {
	while (!this.lookup("#gameName").queryAllAs(Label.class).stream()
		.anyMatch(x -> x.getText().equals("My Game"))) {
	    try {
		Thread.sleep(100);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }
}
