package gameScreen;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class GameChatController {

    @FXML
    TextField message;
    @FXML
    AnchorPane base;

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

}
