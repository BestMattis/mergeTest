package gameScreen;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lobby.LobbyChatMessageListController;
import main.FXMLLoad;
import model.ChatMessage;
import model.Game;
import model.Model;
import model.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameChatController {

    protected FXMLLoad messageList;
    @FXML
    TextField message;
    @FXML
    AnchorPane base;
    @FXML
    AnchorPane messageBase;
    Timeline beat;
    private Model model;
    private VBox playerNames;
    private boolean inChat = false;
    private boolean forceClose = false;
    private LobbyChatMessageListController chatController;

    public GameChatController(Model model) {
        this.model = model;
    }

    /**
     * add the closing on trying to send an empty message
     * set the action for entering and exiting the chat area with the mouse
     */
    public void initialize() {
        loadChatHistory();
        message.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    if (message.getText().length() == 0) {
                        inChat = false;
                        forceClose = true;
                        beat.jumpTo(Duration.seconds(4.6));
                    } else {
                        sendMessage();
                        inChat = true;
                        forceClose = false;
                    }
                }
            }
        });

          /*
         if the user types @ in the sendfield, we want to suggest him the other player names to send them
         private messages
        */
        this.playerNames = new VBox();
        playerNames.setVisible(false);
        playerNames.setMinHeight(messageBase.getLayoutY());
        playerNames.setMinWidth(messageBase.getLayoutX());
        AnchorPane.setRightAnchor(playerNames, 0d);
        AnchorPane.setBottomAnchor(playerNames, 0d);
        AnchorPane.setLeftAnchor(playerNames, 0d);

        // add a label for every player in the game to the vBox
        ArrayList<Player> playerArrayList = model.getApp().getCurrentPlayer().getGame().getPlayers();
        for (int i = 0; i < playerArrayList.size(); i++) {

            if (!playerArrayList.get(i).getName().equals(model.getApp().getCurrentPlayer().getName())) {

                Label label = new Label();
                label.setText("@" + playerArrayList.get(i).getName());
                label.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent event) {
                        message.setText(label.getText());
                        message.positionCaret(label.getText().length());
                    }
                });
                playerNames.getChildren().add(label);
            }
        }

        // in case new players join or someone leaves
        model.getApp().getCurrentPlayer().getGame().addPropertyChangeListener(Game.PROPERTY_players, evt -> {
            if (model.getApp().getCurrentPlayer().getGame() != null) {
                ArrayList<Player> playerList = model.getApp().getCurrentPlayer().getGame().getPlayers();
                Platform.runLater(() -> playerNames.getChildren().removeAll(playerNames.getChildren()));
                for (int i = 0; i < playerList.size(); i++) {

                    if (!playerList.get(i).getName().equals(model.getApp().getCurrentPlayer().getName())) {

                        if (!playerList.get(i).getName().equals(model.getApp().getCurrentPlayer().getName())) {

                            Label label = new Label();
                            label.setText("@" + playerList.get(i).getName());
                            label.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                                public void handle(MouseEvent event) {
                                    message.setText(label.getText());
                                    message.positionCaret(label.getText().length());
                                }
                            });
                            Platform.runLater(() -> playerNames.getChildren().add(label));
                        }

                    }
                }
            }
        });


        message.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {

                if (oldValue == null && newValue.charAt(0) == '@') {
                    playerNames.setVisible(true);

                } else if (oldValue != null && newValue.charAt(0) == '@') {

                    StringBuilder builder = new StringBuilder(newValue);

                    // delete the "@" from the string
                    builder.deleteCharAt(0);
                    boolean similar = false;
                    int nameLength = 0;

                    ArrayList<Player> players = model.getApp().getCurrentPlayer().getGame().getPlayers();

                    for (int i = 0; i < players.size(); i++) {

                        if (!players.get(i).getName().equals(model.getApp().getCurrentPlayer().getName())) {

                            if (builder.length() < players.get(i).getName().length() && players.get(i).getName().startsWith(builder.toString())) {

                                similar = true;
                                nameLength = players.get(i).getName().length();

                            } else if (builder.length() == players.get(i).getName().length() && players.get(i).getName().equals(builder.toString())) {

                                similar = true;
                                nameLength = players.get(i).getName().length();

                            } else if (builder.length() > players.get(i).getName().length() &&
                                    builder.substring(0, players.get(i).getName().length()).equals(players.get(i).getName())) {

                                // correct name
                                if (builder.charAt(players.get(i).getName().length()) == ' ') {

                                    similar = true;
                                    nameLength = players.get(i).getName().length();
                                }
                            }
                        }
                    }
                    //cases
                    if (similar) {

                        if (newValue.charAt(0) == '@' && newValue.length() > oldValue.length() && builder.length() < nameLength) {

                            playerNames.setVisible(true);
                        }
                        //
                        else if (newValue.charAt(0) == '@' && newValue.length() > oldValue.length() && builder.length() == nameLength) {

                            playerNames.setVisible(false);

                        }
                        // the user adds symbols to his message
                        else if (newValue.charAt(0) == '@' && newValue.length() > oldValue.length()
                                && builder.charAt(nameLength) == ' ' && builder.length() > nameLength) {

                            playerNames.setVisible(false);
                        }
                        // the user deleted a symbol of his message
                        else if (newValue.charAt(0) == '@' && newValue.length() <= oldValue.length()
                                && builder.length() > nameLength && builder.charAt(nameLength) == ' ') {

                            playerNames.setVisible(false);
                        }
                        // the user changes a symbol of the player name he wants to enter
                        else if (newValue.charAt(0) == '@' && newValue.length() < oldValue.length()
                                && builder.length() < nameLength) {

                            playerNames.setVisible(true);
                        }
                    } else if (!similar) {

                        // only the @ sign is in the field
                        if (builder.length() == 0) {
                            playerNames.setVisible(true);
                        } else {

                            ExecutorService executorService = Executors.newSingleThreadExecutor();
                            executorService.execute(() -> {
                                message.setEditable(false);
                                message.setStyle("-fx-background-color: red;");
                                try {
                                    executorService.awaitTermination(1, TimeUnit.SECONDS);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                message.setText("@");
                                message.setEditable(true);
                                Platform.runLater(() -> message.positionCaret(1));
                                message.setStyle("-fx-text-fill: #FFFFFF;" +
                                        "-fx-background-color: rgba(255,255,255,0.1);" +
                                        "-fx-background-radius: 0px;" +
                                        "-fx-border-color: #DEDDFF;" +
                                        "-fx-border-radius: 0px;" +
                                        "-fx-border-width: 1px;");
                                executorService.shutdown();
                            });
                        }
                    }
                } else {
                    playerNames.setVisible(false);
                }
            }
        });


        base.setOnMouseEntered(t -> {
            inChat = true;
        });
        base.setOnMouseExited(t -> {
            inChat = false;
            if (!forceClose) {
                animateUsingTimeline();
            }
            forceClose = false;
        });
        base.setVisible(false);
        messageBase.getChildren().add(playerNames);
    }

    private void loadChatHistory() {
        messageList = new FXMLLoad("/lobby/LobbyChatMessageList.fxml", new LobbyChatMessageListController());
        chatController = messageList.getController(LobbyChatMessageListController.class);
        messageBase.getChildren().add(messageList.getParent());

        AnchorPane.setBottomAnchor(messageList.getParent(), 0d);
        AnchorPane.setLeftAnchor(messageList.getParent(), 0d);
        AnchorPane.setTopAnchor(messageList.getParent(), 0d);
        AnchorPane.setRightAnchor(messageList.getParent(), 0d);
    }

    private void sendMessage() {

        if (model.getWebSocketComponent().isIngame()) {
            String text = message.getText();
            message.clear();
            if (text.length() > 0) {
                if (text.indexOf('@') == 0) {
                    StringBuilder builder = new StringBuilder(text);

                    // delete the "@" from the string
                    builder.deleteCharAt(0);

                    ArrayList<Player> players = model.getApp().getCurrentPlayer().getGame().getPlayers();

                    for (int i = 0; i < players.size(); i++) {

                        if (!players.get(i).getName().equals(model.getApp().getCurrentPlayer().getName())) {

                            if (builder.length() > players.get(i).getName().length() &&
                                    builder.substring(0, players.get(i).getName().length()).equals(players.get(i).getName())) {

                                // correct name
                                if (builder.charAt(players.get(i).getName().length()) == ' ') {
                                    String privateMessage = builder.substring(players.get(i).getName().length(), builder.length());
                                    ChatMessage message = new ChatMessage().setChannel("private").setMessage(privateMessage)
                                            .setSender(model.getApp().getCurrentPlayer())
                                            .setReceiver(players.get(i));
                                    message.setDate(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
                                    displayPrivateMsg(message);
                                    model.getApp().getCurrentPlayer().getGame().withIngameMessages(message);
                                }
                            }
                        }
                    }
                } else {
                    ChatMessage message = new ChatMessage().setChannel("all").setMessage(text)
                            .setSender(model.getApp().getCurrentPlayer());
                    message.setDate(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
                    model.getApp().getCurrentPlayer().getGame().withIngameMessages(message);
                    displayAllChatMsg(message);
                }
            }
        }
    }

    public void displayPrivateMsg(ChatMessage message) {

        if (model.getWebSocketComponent().isIngame()) {

            if (!message.getReceiver().equals(model.getApp().getCurrentPlayer().getName())) {
                chatController.displayMessage("[" + message.getDate() + "] " + "["
                        + "to:" + message.getReceiver().getName() + "] " + message.getMessage());
            } else {
                chatController.displayMessage("[" + message.getDate() + "] " + "["
                        + "from:" + message.getSender().getName() + "] "
                        + message.getMessage());
            }
        }

    }

    public void displayAllChatMsg(ChatMessage message) {

        if (!isOpen()) {
            base.setVisible(true);
        }
        String msg = "[" + message.getDate() + "] " + "[" + message.getSender().getName() + "] " + message.getMessage();
        chatController.displayMessage(msg);
    }

    /**
     * called to hide the chat
     */
    private void overChat() {
        if (!inChat) {
            base.setOpacity(0);
            base.setVisible(false);
            this.playerNames.setVisible(false);
        }
    }

    /**
     * animates the hiding of the chat 5 seconds after leaving it
     */
    public void animateUsingTimeline() {
        if (model.getWebSocketComponent().isIngame()) {
            beat = new Timeline(
                    new KeyFrame(Duration.ZERO, event -> {
                        base.setOpacity(1);
                        base.setVisible(true);
                    }),
                    new KeyFrame(Duration.seconds(5), event -> overChat())
            );
            beat.setAutoReverse(false);
            beat.setCycleCount(1);
            beat.play();
        }
    }

    public boolean isOpen() {
        if (base.isVisible()) {
            return true;
        }
        return false;
    }

    /**
     * this methode hides / disables all useable elements
     * for the observer
     */
    public void observerModeGameScreenChat() {
        message.setVisible(false);
    }

    public void displayAllChatMsgSyst(ChatMessage message) {
        if (!isOpen()) {
            base.setVisible(true);
        }
        String msg = "[" + message.getDate() + "] " + "[" + message.getSender().getName() + "] " + message.getMessage();
        chatController.displayMessageSystem(msg);
    }
}
