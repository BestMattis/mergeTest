package gameScreen;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.ChatMessage;
import model.Model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GameChatController {

    @FXML
    TextField message;
    @FXML
    AnchorPane base;

    @FXML
    VBox chatBox;


    Timeline beat;

    private boolean inChat = false;
    private boolean forceClose = false;

    /**
     * add the closing on trying to send an empty message
     * set the action for entering and exiting the chat area with the mouse
     */
    public void initialize(){
        message.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    System.out.println("bla");
                    if (message.getText().length() == 0){
                        inChat = false;
                        forceClose = true;
                        beat.jumpTo(Duration.seconds(4.6));
                    }
                }
            }
        });
        message.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    sendMessage();
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
    }

    private void sendMessage() {

        String text = message.getText();
        message.clear();
        if (text.length() > 0) {
            ChatMessage message = new ChatMessage().setChannel("all").setMessage(text)
                    .setSender(Model.getApp().getCurrentPlayer());
            message.setDate(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
            Model.getApp().getCurrentPlayer().getGame().withIngameMessages(message);
            displayMessage(message);
        }

    }

    public void displayMessage(ChatMessage message) {
        chatBox.getChildren().add(new Label("[" + message.getDate() + "] " + "[" + message.getSender() + "] "
                + message.getMessage()));
    }

    /**
     * called to hide the chat
     */
    private void overChat(){
        if (!inChat){
            base.setOpacity(0);
            base.setVisible(false);
        }
    }

    /**
     * animates the hiding of the chat 5 seconds after leaving it
     */
    public void animateUsingTimeline() {
        beat = new Timeline(
                new KeyFrame(Duration.ZERO, event -> {base.setOpacity(1); base.setVisible(true);}),
                new KeyFrame(Duration.seconds(5), event -> overChat())
        );
        beat.setAutoReverse(false);
        beat.setCycleCount(1);
        beat.play();

    }

    public boolean isOpen() {
        if(base.isVisible()) {
            return true;
        }
        return false;
    }
}
