package lobby;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.Model;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import registerLogin.LoginRegisterTestUtils;
import testUtils.JSONTestUtils;

/**
 * Offline Tests for the Chat User Stories.
 */
public class ChatOfflineTests extends ApplicationTest {

    // Lobby UI Elements

    private ListView<String> playerList;
    private VBox messageList;
    private TextInputControl messageText;
    private TabPane privateChatTabPane;
    private Button sendButton;
    private Button logoutButton;
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

        LoginRegisterTestUtils.loginForOfflineTest(this, model);

        this.loadLobbyUIElements();

        // ---- Bob ----

        this.sendUserJoinedMessage();

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

        LoginRegisterTestUtils.loginForOfflineTest(this, model);

        this.loadLobbyUIElements();

        // ---- Bob ----

        this.sendUserJoinedMessage();

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

        LoginRegisterTestUtils.loginForOfflineTest(this, model);

        this.loadLobbyUIElements();

        // ---- Bob ----

        this.sendUserJoinedMessage();

        /*
         * =============== WEBSOCKET MOCKING ===============
         */

        model.getWebSocketComponent().getChatClient().setJSONAdapter((method, url, json) -> {
            JSONTestUtils.assertJSON(json, "AliceTeamD", "from");
            JSONTestUtils.assertJSON(json, "all", "channel");
            JSONTestUtils.assertJSON(json, "Hello everybody", "message");
        });

        /*
         * =============== ACTION ===============
         */

        // Alice selects the @all tab in the Lobby's Chat View.

        // ---- done on test startup ----

        // Alice types "Hello everybody" into the Chat Text Field
        // and hits the Send button.

        this.sendChatMessage("Hello everybody");

        /*
         * =============== RESULT ===============
         */

        // Bob receives the message "Hello everybody"
        // in his Chat channel with Alice.

        // ---- checked by mock ----

        /*
         * =============== SHUTDOWN ===============
         */

        this.clickOn(this.logoutButton);
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

        LoginRegisterTestUtils.loginForOfflineTest(this, model);

        this.loadLobbyUIElements();

        // ---- Bob ----

        this.sendUserJoinedMessage();

        /*
         * =============== ACTION ===============
         */

        // Bob sends the message "Hello everybody"
        // to the @all channel

        JSONObject chat = new JSONObject();
        chat.put("channel", "all");
        chat.put("from", "BobTeamD");
        chat.put("message", "Hello everybody");
        model.getWebSocketComponent().getChatClient().onMessage(chat.toString());

        /*
         * =============== RESULT ===============
         */

        // Alice receives the message "Hello everybody" in her @all Chat Tab.

        this.performWhilePublicMessageNotPresent("Hello everybody");

        /*
         * =============== SHUTDOWN ===============
         */

        this.clickOn(this.logoutButton);
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

        LoginRegisterTestUtils.loginForOfflineTest(this, model);

        this.loadLobbyUIElements();

        // ---- Bob ----

        this.sendUserJoinedMessage();

        /*
         * =============== WEBSOCKET MOCKING ===============
         */

        model.getWebSocketComponent().getChatClient().setJSONAdapter((method, url, json) -> {
            JSONTestUtils.assertJSON(json, "AliceTeamD", "from");
            JSONTestUtils.assertJSON(json, "BobTeamD", "to");
            JSONTestUtils.assertJSON(json, "private", "channel");
            JSONTestUtils.assertJSON(json, "Let's start the game", "message");
        });

        /*
         * =============== ACTION ===============
         */

        // Alice selects the tab associated with Bob in the Lobby's Chat View.

        Node bobNode = this.performWhileBobNotPresent();

        this.clickOn(bobNode);

        // Alice types "Let's start the game" into the Chat Text Field
        // and hits the Send button.

        this.sendChatMessage("Let's start the game");

        /*
         * =============== RESULT ===============
         */

        // Bob receives the message "Let's start the game"
        // in his Chat channel with Alice.

        // ---- checked by mock ----

        /*
         * =============== SHUTDOWN ===============
         */

        this.clickOn(this.logoutButton);
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

        LoginRegisterTestUtils.loginForOfflineTest(this, model);

        this.loadLobbyUIElements();

        // ---- Bob ----

        this.sendUserJoinedMessage();

        /*
         * =============== ACTION ===============
         */

        // Bob sends the message "Let's start the game"
        // to his channel with Alice.

        JSONObject chat = new JSONObject();
        chat.put("channel", "private");
        chat.put("from", "BobTeamD");
        chat.put("to", "AliceTeamD");
        chat.put("message", "Let's start the game");
        model.getWebSocketComponent().getChatClient().onMessage(chat.toString());

        /*
         * =============== RESULT ===============
         */

        // Alice receives the message in her Chat Tab with Bob.

        this.performWhilePrivateMessageNotPresent("Let's start the game");

        /*
         * =============== SHUTDOWN ===============
         */

        this.clickOn(this.logoutButton);
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

    private Node performWhilePublicMessageNotPresent(String message) {
        while (!this.messageList.getChildren()
                .stream()
                .anyMatch(x -> ((Label) x).getText().endsWith(message))) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return (Label) this.messageList.getChildren()
                .stream()
                .filter(x -> ((Label) x).getText().endsWith(message))
                .findFirst().get();
    }

    private Node performWhilePrivateMessageNotPresent(String message) {
        while (!this.lookup("#history").queryAllAs(VBox.class)
                .stream()
                .filter(x -> x.getChildren().size() > 0)
                .anyMatch(x -> ((Label) x.getChildren().get(0)).getText().endsWith(message))) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return (Label) this.lookup("#history").queryAllAs(VBox.class)
                .stream()
                .filter(x -> x.getChildren().size() > 0)
                .filter(x -> ((Label) x.getChildren().get(0)).getText().endsWith(message))
                .findFirst().get().getChildren().get(0);
    }

    private void sendChatMessage(String message) {
        this.clickOn(this.messageText);
        this.write(message);
        this.clickOn(this.sendButton);
    }

    private void sendUserJoinedMessage() {
        JSONObject login = new JSONObject();
        login.put("action", "userJoined");
        JSONObject loginData = new JSONObject();
        loginData.put("name", "BobTeamD");
        login.put("data", loginData);
        model.getWebSocketComponent().getSystemClient().onMessage(login.toString());
    }
}
