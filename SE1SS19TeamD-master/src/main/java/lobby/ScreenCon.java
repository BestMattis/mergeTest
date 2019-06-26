package lobby;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import main.AdvancedWarsApplication;
import model.Model;
import syncCommunication.HttpRequests;
import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.SynchronousUserCommunicator;

import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class ScreenCon {

    @SuppressWarnings("static-access")
    AdvancedWarsApplication app = AdvancedWarsApplication.getInstance();
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
        loadGamelist();
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
     * logout with clearing of the data model and changing to the loginscreen again
     */
    public void logoutButton() {
        boolean loggedOut = false;
        HttpRequests hr = Model.getInstance().getPlayerHttpRequestsHashMap().get(Model.getInstance().getApp().getCurrentPlayer());
        SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);
        System.out.println(uComm.getUserKey() + " from logoutMethod");
        try {
            loggedOut = uComm.logOut();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }
        System.out.println(loggedOut);
        if (loggedOut) {
            Model.getInstance().setApp(null); // clear data model on logout
            if (app != null) {
                app.goToRegisterLogin();
            } else {
                System.out.println("failed showing LoginScreen");
            }
        }
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
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classLoader.getResource("en-US.properties").openStream();
            ResourceBundle bundle = new PropertyResourceBundle(inputStream);
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getClassLoader().getResource("gameList/GameList.fxml"), bundle);
            Parent parent = fxmlLoader.load();
            gamesview.getChildren().addAll(parent.getChildrenUnmodifiable());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
