package createGame;

import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import registerLogin.LoginRegisterTestUtils;

/**
 * Test the Create Game User Stories.
 */
public class CreateGameStoriesTest extends ApplicationTest {
    
    // Lobby UI Elements
    
    private Button newGameButton;
    private Button logoutButton;
    
    // Create Game UI Elements
    
    private TextInputControl nameText;
    private TextInputControl playerNumberText;
    private Button createGameButton;
    
    // Game UI Elements
    
    private Parent rootNode;
    private Button backButton;

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
     * Test the Create Game User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-15">TD-15</a>.
     */
    @Test
    public void testCreateGame() {
        /*
    	 * =============== SITUATION ===============
    	 */
        
    	// Alice is logged in and waits in the lobby.
	
        LoginRegisterTestUtils.loginTestUser(this);
    	
    	this.loadLobbyUIElements();
        
    	/*
    	 * =============== ACTION ===============
    	 */
        
        // Alice clicks the New Game button.
    	
    	this.clickOn(this.newGameButton);
    	this.loadNewGameUIElements();
    	
        // Alice types in the name ("Alice's game")
    	// and the number of wanted players (4).
        
    	this.clickOn(this.nameText);
    	this.write("Alice's game");
    	this.clickOn(this.playerNumberText);
    	this.write("4");
    	
    	// Alice clicks the Start Game button.
    	
    	this.clickOn(this.createGameButton);
    	
    	this.loadGameLobbyUIElements();
        
    	/*
    	 * =============== RESULT ===============
    	 */
        
    	// Alice's client opens the game view.
    	
        FxAssert.verifyThat(this.rootNode, NodeMatchers.isNotNull());
    	
    	/*
    	 * =============== SHUTDOWN ===============
    	 */
    	
        this.clickOn(this.backButton);
    	this.clickOn(this.logoutButton);
    }

    private void loadLobbyUIElements() {
	this.newGameButton = this.lookup("#newGame").queryButton();
        this.logoutButton = this.lookup("#logout").queryButton();
    }
    
    private void loadNewGameUIElements() {
	this.nameText = this.lookup("#nameField").queryTextInputControl();
	this.playerNumberText = this.lookup("#playerNumberField").queryTextInputControl();
	this.createGameButton = this.lookup("#createButton").queryButton();
    }
    
    private void loadGameLobbyUIElements() {
	this.rootNode = this.lookup("#waitingbase").queryParent();
	this.backButton = this.lookup("#back").queryButton();
    }
}
