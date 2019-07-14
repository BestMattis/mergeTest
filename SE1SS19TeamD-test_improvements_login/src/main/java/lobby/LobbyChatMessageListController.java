package lobby;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;


public class LobbyChatMessageListController {

    @FXML
    private VBox history;
    @FXML
    private ScrollPane scrollhistory;

    /**
     * This method sets the scrollpane to the end, so the last message is visible
     */
    public void initialize() {
        history.heightProperty().addListener(observable -> scrollhistory.setVvalue(1D));
    }

    /**
     * @return history the vbox the messages are listed in
     */
    public VBox getHistory() {
        return history;
    }

    /**
     * add a message to the vbox and display it
     *
     * @param text the text to display
     */
    public void displayMessage(String text) {
        Label txtlabel = new Label(text);
        history.getChildren().add(txtlabel);
    }
}
