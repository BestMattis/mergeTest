package lobby;

import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import asyncCommunication.WebSocketComponent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.ChatMessage;
import model.Model;
import model.Player;
import registerLogin.LoginRegisterTestUtils;
import syncCommunication.HttpRequests;
import syncCommunication.SynchronousUserCommunicator;
import syncCommunication.RESTExceptions.LoginFailedException;

/**
 * Test the Chat User Stories.
 */
public class ChatStoriesTest extends ApplicationTest {
    
    // Lobby UI Elements
    
    private ListView<String> playerList;
    private VBox messageList;
    private TextInputControl messageText;
    private TabPane privateChatTabPane;
    private Button sendButton;
    private Button logoutButton;

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
     * Test the Open Chat User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-36">TD-36</a>.
     */
    @Test
    public void testOpenChat() {
	
        /*
    	 * =============== SITUATION ===============
    	 */
        
    	// Alice and Bob are logged in and wait in the Lobby.
    	
	// ---- Alice ----
	
	LoginRegisterTestUtils.loginTestUser(this);
    	
    	this.loadLobbyUIElements();
    	
    	// ---- Bob ----
    	
    	HttpRequests req = new HttpRequests();
	SynchronousUserCommunicator communicator = new SynchronousUserCommunicator(req);
	
	try {
	    communicator.logIn("BobTeamD", "geheim");
	} catch (LoginFailedException e) {
	    e.printStackTrace();
	}
	
	// Alice did not chat with Bob before.
	
	// ---- guaranteed on Application startup ----
	
    	/*
    	 * =============== ACTION ===============
    	 */
        
	// Alice clicks on Bob's name in the Player's List.
	
	Node bobNode = this.performWhileBobNotPresent();
	
	this.clickOn(bobNode);
        
    	/*
    	 * =============== RESULT ===============
    	 */
        
    	// A chat with Bob opens in the chat view
    	
        Assert.assertTrue("Alice's Chat with Bob is not displayed",
        	this.privateChatTabPane
        	.getTabs()
        	.stream()
        	.anyMatch(x -> x.getText().equals("BobTeamD")));
        
        /*
    	 * =============== SHUTDOWN ===============
    	 */
    	
    	this.clickOn(this.logoutButton);
    	
    	try {
	    communicator.logOut();
	} catch (LoginFailedException e) {
	    e.printStackTrace();
	}
    }
    
    /**
     * Test the Close Chat User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-37">TD-37</a>.
     */
    @Test
    public void testCloseChat() {
	
        /*
    	 * =============== SITUATION ===============
    	 */
        
    	// Alice and Bob are logged in and wait in the Lobby.
    	
	// ---- Alice ----
	
	LoginRegisterTestUtils.loginTestUser(this);
    	
    	this.loadLobbyUIElements();
    	
    	// ---- Bob ----
    	
    	HttpRequests req = new HttpRequests();
	SynchronousUserCommunicator communicator = new SynchronousUserCommunicator(req);
	
	try {
	    communicator.logIn("BobTeamD", "geheim");
	} catch (LoginFailedException e) {
	    e.printStackTrace();
	}
	
	// Alice did chat with Bob before and the Chat Channel is open.
	
	Node bobNode = this.performWhileBobNotPresent();
	
	this.clickOn(bobNode);
	
    	/*
    	 * =============== ACTION ===============
    	 */
        
	// Alice selects the tab associated with Bob in the Lobby's Chat View.
	
	// ---- done on test startup ----
	
	// Alice clicks the Close Button.
	
	this.clickOn(this.lookup(".tab-close-button").queryAs(StackPane.class));
	
    	/*
    	 * =============== RESULT ===============
    	 */
        
    	// The chat with Bob closes.
    	
        Assert.assertFalse("Alice's Chat with Bob is still displayed",
        	this.privateChatTabPane
        	.getTabs()
        	.stream()
        	.anyMatch(x -> x.getText().equals("BobTeamD")));
        
        /*
    	 * =============== SHUTDOWN ===============
    	 */
    	
    	this.clickOn(this.logoutButton);
    	
    	try {
	    communicator.logOut();
	} catch (LoginFailedException e) {
	    e.printStackTrace();
	}
    }
    
    /**
     * Test the Message to all players User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-35">TD-35</a>.
     * Part 1: Send message
     */
    @Test
    public void testMessageToAllPlayers_send() {
	
        /*
    	 * =============== SITUATION ===============
    	 */
        
    	// Alice and Bob are logged in and wait in the Lobby.
    	
	// ---- Alice ----
	
	LoginRegisterTestUtils.loginTestUser(this);
    	
    	this.loadLobbyUIElements();
    	
    	// ---- Bob ----
    	
    	HttpRequests req = new HttpRequests();
	SynchronousUserCommunicator communicator = new SynchronousUserCommunicator(req);
	
	try {
	    communicator.logIn("BobTeamD", "geheim");
	} catch (LoginFailedException e) {
	    e.printStackTrace();
	}
	
	// Alice did chat with Bob before and the Chat Channel is open.
	
	Node bobNode = this.performWhileBobNotPresent();
	
	this.clickOn(bobNode);
	
    	/*
    	 * =============== ACTION ===============
    	 */
        
	// Alice selects the @all tab in the Lobby's Chat View.
	
	// ---- done on test startup ----
	
	// Alice types "Let's start the game" into the Chat Text Field
	// and hits the Send button.
	
	this.sendChatMessage("Let's start the game");
	
    	/*
    	 * =============== RESULT ===============
    	 */
        
    	// Bob receives the message "Let's start the game"
	// in his Chat channel with Alice.
    	
	// TODO Assert that chat message was received in Bob's Web Socket
        
        /*
    	 * =============== SHUTDOWN ===============
    	 */
    	
    	this.clickOn(this.logoutButton);
    	
    	try {
	    communicator.logOut();
	} catch (LoginFailedException e) {
	    e.printStackTrace();
	}
    }
    
    /**
     * Test the Message to all players User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-35">TD-35</a>.
     * Part 2: Receive message
     */
    @Test
    public void testMessageToAllPlayers_receive() {
	
        /*
    	 * =============== SITUATION ===============
    	 */
        
    	// Alice and Bob are logged in and wait in the Lobby.
    	
	// ---- Alice ----
	
	LoginRegisterTestUtils.loginTestUser(this);
    	
    	this.loadLobbyUIElements();
    	
    	// ---- Bob ----
    	
    	HttpRequests req = new HttpRequests();
	SynchronousUserCommunicator communicator = new SynchronousUserCommunicator(req);
	
	try {
	    communicator.logIn("BobTeamD", "geheim");
	} catch (LoginFailedException e) {
	    e.printStackTrace();
	}
	
	// Alice did chat with Bob before and the Chat Channel is open.
	
	Node bobNode = this.performWhileBobNotPresent();
	
	this.clickOn(bobNode);
	
    	/*
    	 * =============== ACTION ===============
    	 */
        
	// Alice selects the @all tab in the Lobby's Chat View.
	
	// ---- done on test startup ----
	
	// Alice types "Hello everybody" into the Chat Text Field
	// and hits the Send button.
	
	WebSocketComponent component = new WebSocketComponent(
		"BobTeamD", communicator.getUserKey());
	ChatMessage message = new ChatMessage()
		.setApp(Model.getApp())
		.setChannel("all")
		.setMessage("Hello everybody")
		.setSender(new Player().setName("BobTeamD"));
	component.sendChatmessage(message);
	
    	/*
    	 * =============== RESULT ===============
    	 */
        
    	// A chat with Bob opens in the chat view
    	
        this.performWhileMessageNotPresent("Hello everybody");
        
        /*
    	 * =============== SHUTDOWN ===============
    	 */
    	
    	this.clickOn(this.logoutButton);
    	
    	try {
	    communicator.logOut();
	} catch (LoginFailedException e) {
	    e.printStackTrace();
	}
    }
    
    /**
     * Test the Message to a specific player User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-13">TD-13</a>.
     * Part 1: Send message
     */
    @Test
    public void testMessageToSpecificPlayer_send() {
	
        /*
    	 * =============== SITUATION ===============
    	 */
        
    	// Alice and Bob are logged in and wait in the Lobby.
    	
	// ---- Alice ----
	
	LoginRegisterTestUtils.loginTestUser(this);
    	
    	this.loadLobbyUIElements();
    	
    	// ---- Bob ----
    	
    	HttpRequests req = new HttpRequests();
	SynchronousUserCommunicator communicator = new SynchronousUserCommunicator(req);
	
	try {
	    communicator.logIn("BobTeamD", "geheim");
	} catch (LoginFailedException e) {
	    e.printStackTrace();
	}
	
	// Alice did chat with Bob before and the Chat Channel is open.
	
	Node bobNode = this.performWhileBobNotPresent();
	
	this.clickOn(bobNode);
	
    	/*
    	 * =============== ACTION ===============
    	 */
        
	// Alice selects the tab associated with Bob in the Lobby's Chat View.
	
	// ---- done on test startup ----
	
	// Alice types "Hello everybody" into the Chat Text Field
	// and hits the Send button.
	
	this.sendChatMessage("Hello everybody");
	
    	/*
    	 * =============== RESULT ===============
    	 */
        
    	// A chat with Bob opens in the chat view
    	
        // TODO Assert that chat message was received in Bob's Web Socket
        
        /*
    	 * =============== SHUTDOWN ===============
    	 */
    	
    	this.clickOn(this.logoutButton);
    	
    	try {
	    communicator.logOut();
	} catch (LoginFailedException e) {
	    e.printStackTrace();
	}
    }
    
    /**
     * Test the Message to a specific player User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-13">TD-13</a>.
     * Part 2: Receive message
     */
    @Test
    public void testMessageToSpecificPlayer_receive() {
	
        /*
    	 * =============== SITUATION ===============
    	 */
        
    	// Alice and Bob are logged in and wait in the Lobby.
    	
	// ---- Alice ----
	
	LoginRegisterTestUtils.loginTestUser(this);
    	
    	this.loadLobbyUIElements();
    	
    	// ---- Bob ----
    	
    	HttpRequests req = new HttpRequests();
	SynchronousUserCommunicator communicator = new SynchronousUserCommunicator(req);
	
	try {
	    communicator.logIn("BobTeamD", "geheim");
	} catch (LoginFailedException e) {
	    e.printStackTrace();
	}
	
	// Alice did chat with Bob before and the Chat Channel is open.
	
	Node bobNode = this.performWhileBobNotPresent();
	
	this.clickOn(bobNode);
	
    	/*
    	 * =============== ACTION ===============
    	 */
        
	// Alice selects the tab associated with Bob in the Lobby's Chat View.
	
	// ---- done on test startup ----
	
	// Alice types "Hello everybody" into the Chat Text Field
	// and hits the Send button.
	
	WebSocketComponent component = new WebSocketComponent(
		"BobTeamD", communicator.getUserKey());
	ChatMessage message = new ChatMessage()
		.setApp(Model.getApp())
		.setChannel("private")
		.setMessage("Hello everybody")
		.setSender(new Player().setName("BobTeamD"))
		.setReceiver(new Player().setName("AliceTeamD"));
	component.sendChatmessage(message);
	
    	/*
    	 * =============== RESULT ===============
    	 */
        
    	// A chat with Bob opens in the chat view
    	
        this.performWhileMessageNotPresent("Hello everybody");
        
        /*
    	 * =============== SHUTDOWN ===============
    	 */
    	
    	this.clickOn(this.logoutButton);
    	
    	try {
	    communicator.logOut();
	} catch (LoginFailedException e) {
	    e.printStackTrace();
	}
    }

    private void loadLobbyUIElements() {
	this.playerList = this.lookup("#playerList").queryListView();
	this.messageList = this.lookup("#history").queryAs(VBox.class);
	this.privateChatTabPane = this.lookup("#singleTabPane").queryAs(TabPane.class);
	this.messageText = this.lookup("#message").queryTextInputControl();
	this.sendButton = this.lookup("#send").queryButton();
        this.logoutButton = this.lookup("#logout").queryButton();
    }
    
    private Node performWhileBobNotPresent() {
	while (!this.playerList.getItems().stream().anyMatch(e -> e.equals("BobTeamD"))) {
	    try {
		Thread.sleep(100);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
	return this.lookup(".list-cell")
		.queryAllAs(ListCell.class)
		.stream()
		.filter(x -> x.getText().equals("BobTeamD"))
		.findFirst()
		.get();
    }
    
    private Node performWhileMessageNotPresent(String message) {
	while (!this.messageList.getChildren()
		.stream()
		.anyMatch(x -> ((Label) x).getText().equals(message)));
	{
	    try {
		Thread.sleep(100);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
	return (Label) this.messageList.getChildren()
		.stream()
		.filter(x -> ((Label) x).getText().equals(message))
		.findFirst().get();
    }
    
    private void sendChatMessage(String message) {
	this.clickOn(this.messageText);
	this.write(message);
	this.clickOn(this.sendButton);
    }
}
