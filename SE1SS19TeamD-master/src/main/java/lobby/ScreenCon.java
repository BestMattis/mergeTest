package lobby;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class ScreenCon {

    @FXML
    private AnchorPane chat;
    @FXML
    private AnchorPane gamesview;
    @FXML
    private AnchorPane playerview;
    @FXML
    private AnchorPane base;
    @FXML
    private Button newGame;
    @FXML
    private Button options;
    @FXML
    private Button logout;
    @FXML
    private ImageView logo;


    /**
     * called when the lobbyScreen scene is loaded.
     * Set the methods for clicking on the buttons
     */
    public void initialize() {
        newGame.setOnAction(t -> newGameButton());
        options.setOnAction(t -> optionsButton());
        logout.setOnAction(t -> logoutButton());
        logo.setOnMouseClicked(t -> infoImage());
    }

    /**
     * Method to start a new game
     */
    public void newGameButton() {
        System.out.println("newGame");
    }

    /**
     * This method opens the screen for the options
     */
    public void optionsButton() {
        System.out.println("options");
    }

    /**
     * logout and showing the loginscreen again
     */
    public void logoutButton() {
        System.out.println("logout");
    }

    /**
     * This method shows an infoscreen for the game after clicking on the logo
     */
    public void infoImage() {
        System.out.println("info");
    }

    /**
     * load the chat-module into the lobbylayout
     */
    public void loadChat() {

    }

    /**
     * loads the playerlist-module into the lobbylayout
     */
    public void loadPlayerlist() {

    }

    /**
     * loads the gamelist-module into the lobbylayout
     */
    public void loadGamelist() {

    }
}
