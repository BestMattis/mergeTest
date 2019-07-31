package gameScreen;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.Game;
import model.Model;
import registerLogin.LoginRegisterTestUtils;

public class WinnerScreenTest extends ApplicationTest {

	private HBox gameCard;

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
	 * Test the Join Game as Observer Story as described here:
	 * <a href="https://jira.uniks.de/browse/TD-195">TD-195</a>.<br />
	 */
	@Test
	public void testJoinGameAsObserverStory() {
		/*
		 * =============== SITUATION ===============
		 */

		// Alice starts a new game.
		JSONArray initialGames = new JSONArray().put(new JSONObject().put("joinedPlayer", 0).put("name", "My Game")
				.put("id", "my-game-id").put("neededPlayer", 2));

		// Alice and Bob have logged in.
		// Both players are in the lobby.
		LoginRegisterTestUtils.loginForOfflineTest(this, new JSONArray(), initialGames);

		/*
		 * =============== ACTION ===============
		 */

		// Alice clicks the Observe Button of the "Fancy Game"
		this.loadLobbyElements();
		this.clickOn(this.gameCard);
		
		/*
		 * =============== RESULT ===============
		 */
		ToggleButton ready = this.lookup("#ready").queryAs(ToggleButton.class);
		
		this.clickOn(ready);
		
		// Bob joins the game
		String msgBobUnready = "{data:{color:BLUE,isReady:false,name:Bob,id:PlayerID,currentGame:GameID},action:gameInitObject}";
		Model.getWebSocketComponent().getGameClient().onMessage(msgBobUnready);
		
		// Bob is set ready
		String msgBobReady = "{data:{newValue:\"true\",fieldName:isReady,id:PlayerID},action:gameChangeObject}";
		Model.getWebSocketComponent().getGameClient().onMessage(msgBobReady);
		
		String msgWinner = "{data:{playerName:Bob},action:winner}";
		Model.getWebSocketComponent().getGameClient().onMessage(msgWinner);
		sleep(1000);
	}

	private void loadLobbyElements() {
		this.gameCard = this.lookup("#gameCard").queryAs(HBox.class);
	}
}
