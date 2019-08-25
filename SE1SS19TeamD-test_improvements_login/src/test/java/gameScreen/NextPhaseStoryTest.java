package gameScreen;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import asyncCommunication.WebSocketRequests;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
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

public class NextPhaseStoryTest extends ApplicationTest {

	private HBox gameCard;

	/**
	 * Setup stage and start application.
	 * 
	 * @param primaryStage
	 *            the stage to display the GUI.
	 */
	@Override
	public void start(Stage primaryStage) throws InterruptedException {
		new AdvancedWarsApplication().start(primaryStage);
	}

	/**
	 * Test the finish Round Story as described here:
	 * <a href="https://jira.uniks.de/browse/TD-206">TD-202</a>.<br />
	 */
	@Test
	public void testFinishRoundStory() {
		/*
		 * =============== SITUATION ===============
		 */

		// starts a new game.
		JSONArray initialGames = new JSONArray().put(new JSONObject().put("joinedPlayer", 0).put("name", "My Game")
				.put("id", "my-game-id").put("neededPlayer", 2));

		// TeamDTestUser and Bob have logged in.
		// Both players are in the lobby.
		Model model = new Model();
		LoginRegisterTestUtils.loginForOfflineTest(this, new JSONArray(), initialGames, new JSONArray(), model);

		// TeamDTestUser joins game
		this.loadLobbyElements();
		this.clickOn(this.gameCard);

		// TeamDTestUser is set ready
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

		// Set id for TeamDTestUser
		model.getApp().getCurrentPlayer().setId("FakeID");

		// Set Bob as activePlayer
		String msgCurrentPlayer = "{\"data\":{\"newValue\":\"PlayerID\",\"fieldName\":\"currentPlayer\",\"id\":\"Game@73001935\"},\"action\":\"gameChangeObject\"}";
		model.getWebSocketComponent().getGameClient().onMessage(msgCurrentPlayer);
		sleep(50);
		// Set current Phase to movePhase
		String msgCurrentPhase = "{\"data\":{\"newValue\":\"movePhase\",\"fieldName\":\"phase\",\"id\":\"Game@651259b9\"},\"action\":\"gameChangeObject\"}";
		model.getWebSocketComponent().getGameClient().onMessage(msgCurrentPhase);

		/*
		 * =============== RESULT ===============
		 */

		// Asserts that Buttons are disabled and labels are ser right
		Button phaseButton = this.lookup("#basicRound").queryButton();
		Button phaseButtonMenu = this.lookup("#finishRound").queryButton();
		Label activePlayerLabel = this.lookup("#activePlayer").queryAs(Label.class);
		Label phaseLabel = this.lookup("#currentPhaseLabel").queryAs(Label.class);
		Assert.assertTrue(phaseLabel.getText().contains("movePhase"));	//sometimes its fails maybe a timing error
		Assert.assertTrue(activePlayerLabel.getText().contains("Bob"));
		Assert.assertTrue(phaseButton.isDisable());
		Assert.assertTrue(phaseButtonMenu.isDisable());

		sleep(1000);

		// Change active Player to TeamDTestUser
		String msgCurrentPlayerNew = "{\"data\":{\"newValue\":\"FakeID\",\"fieldName\":\"currentPlayer\",\"id\":\"Game@73001935\"},\"action\":\"gameChangeObject\"}";
		model.getWebSocketComponent().getGameClient().onMessage(msgCurrentPlayerNew);

		String msgCurrentPhaseNew = "{\"data\":{\"newValue\":\"atackPhase\",\"fieldName\":\"phase\",\"id\":\"Game@651259b9\"},\"action\":\"gameChangeObject\"}";
		model.getWebSocketComponent().getGameClient().onMessage(msgCurrentPhaseNew);

		// Asserts that Buttons are disabled and labels are ser right
		Button phaseButton2 = this.lookup("#basicRound").queryButton();
		Button phaseButtonMenu2 = this.lookup("#finishRound").queryButton();
		Label activePlayerLabel2 = this.lookup("#activePlayer").queryAs(Label.class);
		Label phaseLabel2 = this.lookup("#currentPhaseLabel").queryAs(Label.class);
		Assert.assertTrue(phaseLabel2.getText().contains("atackPhase"));
		Assert.assertTrue(activePlayerLabel2.getText().contains("TeamDTestUser"));
		Assert.assertFalse(phaseButton2.isDisable());
		Assert.assertFalse(phaseButtonMenu2.isDisable());

	}

	private void loadLobbyElements() {
		this.gameCard = this.lookup("#gameCard").queryAs(HBox.class);
	}
}