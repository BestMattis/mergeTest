package lobby;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import main.FXMLLoad;
import model.App;
import model.ChatMessage;
import model.Model;
import msgToAllPlayers.WSChatEndpoint;

import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class LobbyChatController {

    private static LobbyChatMessageListController allController;
    @FXML
    public TextField message;
    @FXML
    private Button chatAll;
    @FXML
    private Button chatPlayers;
    @FXML
    private AnchorPane messageView;
    @FXML
    private Button send;
    private LobbyChatSingleTabsController singleController;
	private FXMLLoad all;
    /**
     * the get-method for the allcontroller
     *
     * @return allcontroller
     */
    public static LobbyChatMessageListController getAllController() {
        return allController;
    }

    /**
     * Initialize the overall Chat fxml and set the function of the buttons, load
     * further fxml modules
     */
    public void initialize() {
        loadAll();
        loadPlayers();
        WSChatEndpoint.getInstance().setListeners();
        chatAll.setOnAction(t -> setToAll());
        chatPlayers.setOnAction(t -> setToPlayers());
        send.setOnAction(t -> sendMessage());
        message.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    sendMessage();
                }
            }
        });
    }

    /**
     * the get-method for the singlecontroller
     *
     * @return the singleController
     */
    public LobbyChatSingleTabsController getSingleController() {
        return singleController;
    }

	/**
	 * This method loads the fxml for the chat with all players
	 */
	public void loadAll() {
		all = new FXMLLoad("/lobby/LobbyChatMessageList.fxml", new LobbyChatMessageListController());
		allController = all.getController(LobbyChatMessageListController.class);
        messageView.getChildren().add(all.getParent());
        AnchorPane.setBottomAnchor(all.getParent(), 0d);
        AnchorPane.setLeftAnchor(all.getParent(), 0d);
        AnchorPane.setTopAnchor(all.getParent(), 0d);
        AnchorPane.setRightAnchor(all.getParent(), 0d);
        messageView.getChildren().get(0).setVisible(true);
	}

    /**
     * similar to the loadAll-method just for the fxml for chating with a specific
     * player
     */
    public void loadPlayers() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classLoader.getResource("en-US.properties").openStream();
            ResourceBundle bundle = new PropertyResourceBundle(inputStream);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LobbyChatSingleTabs.fxml"), bundle);
            Parent parent = fxmlLoader.load();
            singleController = fxmlLoader.getController();
            messageView.getChildren().add(parent);
            AnchorPane.setBottomAnchor(parent, 0d);
            AnchorPane.setLeftAnchor(parent, 0d);
            AnchorPane.setTopAnchor(parent, 0d);
            AnchorPane.setRightAnchor(parent, 0d);
            messageView.getChildren().get(1).setVisible(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method displays the chat with all players
     */
    public void setToAll() {
        messageView.getChildren().get(1).setVisible(false);
        messageView.getChildren().get(0).setVisible(true);
    }

    /**
     * This method displays the tabpane for chatting with singleplayers
     */
    public void setToPlayers() {
        messageView.getChildren().get(0).setVisible(false);
        messageView.getChildren().get(1).setVisible(true);
    }

    /**
     * This methode selects the panel the send text is displayed in(all or single) and gets the message from the textfield
     */
    public void sendMessage() {
        String text = message.getText();
        message.clear();
        if (text.length() > 0) {
            if (messageView.getChildren().get(0).isVisible()) {
                sendToAll(text);
            } else {
                sendToSingle(text);
            }
        }
    }

    /**
     * This method creates new Chat Object with the message and puts this chat
     * object into the data model. It pushes the message to the chatpanel controller
     *
     * @param text the String to be send
     */
    @SuppressWarnings("static-access")
    public void sendToAll(String text) {
        App app = Model.getInstance().getApp();
        ChatMessage chatToSend = new ChatMessage().setChannel("all").setMessage(text)
                .setSender(app.getCurrentPlayer());
        app.withAllChatMessages(chatToSend); // add message to data model

        allController.displayMessage("[" + app.getCurrentPlayer().getName() + "] " + text); // show your own message in chatbox
    }

    /**
     * This method pushes the message to the chatpanel controller
     *
     * @param text the text to send
     */
    public void sendToSingle(String text) {
        singleController.addMessage(text);
    }
}
