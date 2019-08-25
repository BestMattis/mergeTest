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
        Label txtlabel = new Label(getStringWithBreaks(text));
        history.getChildren().add(txtlabel);
    }

    /**
     * add a system message to the vbox and display it
     *
     * @param text the text to display
     */
    public void displayMessageSystem(String sysMsg) {
        Label sysMsgLabel = new Label(getStringWithBreaks(sysMsg));
        sysMsgLabel.setStyle("-fx-text-fill:rgb(255,176,0)");
        sysMsgLabel.setMaxWidth(390);
        history.getChildren().add(sysMsgLabel);
    }


    /*
     * This method addes a line break when the string gets to long
     *
     * @param msg The message to show in the gui
     */
    public String getStringWithBreaks(String msg) {
        StringBuilder build = new StringBuilder(msg);
        if (build.length() > 50) {
            if (build.substring(50, 50).equals(" ")) {
                build.replace(50, 50, "\n");
            } else {
                int i = 50;
                while (!build.substring(i, i + 1).equals(" ")) {
                    i--;
                    if (build.substring(i, i + 1).equals(" ")) {
                        String s = build.substring(i, build.length());
                        build.replace(i, build.length(), "\n                                  ");
                        build.append(s);
                        break;
                    }
                }
            }
        }
        return build.toString();
    }
}
