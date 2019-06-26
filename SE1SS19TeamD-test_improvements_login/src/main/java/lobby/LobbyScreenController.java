package lobby;

import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import createGame.CreateGameController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import main.AdvancedWarsApplication;
import model.App;
import model.Model;
import syncCommunication.HttpRequests;
import syncCommunication.SynchronousUserCommunicator;
import syncCommunication.RESTExceptions.LoginFailedException;
import main.FXMLLoad;

public class LobbyScreenController {

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

    private FXMLLoad infoFXML;
    private FXMLLoad chatFXML;

    @SuppressWarnings("static-access")
	App app = Model.getInstance().getApp();
    AdvancedWarsApplication application = AdvancedWarsApplication.getInstance();

    /**
     * called when the lobbyScreen scene is loaded.
     * Set the methods for clicking on the buttons
     */
    public void initialize() {
        newGame.setOnAction(t -> newGameButtonClicked());
        options.setOnAction(t -> optionsButtonClicked());
        logout.setOnAction(t -> logoutButtonClicked());
        logo.setOnMouseClicked(t -> infoImageClicked());
        loadChat();
        loadGamelist();
        loadPlayerlist();
        loadInfo();
    }

    /**
     * Method to start a new game
     */
    public void newGameButtonClicked(){
        try{
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classLoader.getResource("en-US.properties").openStream();
            ResourceBundle bundle = new PropertyResourceBundle(inputStream);
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getClassLoader().getResource("createGame/CreateGame.fxml"),bundle);
            Parent parent = fxmlLoader.load();
            CreateGameController createGameController = fxmlLoader.getController();
            createGameController.setBase(base);
            base.getChildren().addAll(parent.getChildrenUnmodifiable());
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    /**
     * This method opens the screen for the options
     */
    public void optionsButtonClicked() {
        System.out.println("options");
    }

    /**
     * logout and showing the loginscreen again
     */
    @SuppressWarnings("static-access")
	public void logoutButtonClicked(){
    	boolean loggedOut = false;
    	HttpRequests hr = Model.getInstance().getPlayerHttpRequestsHashMap().get(Model.getInstance().getApp().getCurrentPlayer());
    	SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);
    	try {
            loggedOut = uComm.logOut();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }
    	if(loggedOut) {
    		Model.getInstance().setApp(null); // clear data model on logout
    		Model.getWebSocketComponent().stopComponent();
			if(application != null){
				application.goToRegisterLogin();
			} else {
				System.out.println("failed showing LoginScreen");	// for test only
			}
    	}
    }

    /**
     * This method shows an infoscreen for the game after clicking on the logo
     */
    public void infoImageClicked() {
        System.out.println("info");
        infoFXML.getController(LobbyGameInfoController.class).show();
    }

    /**
     * load the chat-module into the lobbylayout
     */
    public void loadChat(){
        chatFXML = new FXMLLoad("/lobby/LobbyChat.fxml");
        AnchorPane.setBottomAnchor(chatFXML.getParent(), 3d);
        AnchorPane.setLeftAnchor(chatFXML.getParent(), 3d);
        AnchorPane.setTopAnchor(chatFXML.getParent(), 3d);
        AnchorPane.setRightAnchor(chatFXML.getParent(), 3d);
        chat.getChildren().add(chatFXML.getParent());
    }

    /**
     * load the playerlist-module into the lobbylayout
     */
    public void loadPlayerlist() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classLoader.getResource("en-US.properties").openStream();
            ResourceBundle bundle = new PropertyResourceBundle(inputStream);
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getClassLoader().getResource("playerList/PlayerList.fxml"), bundle);
            Parent parent = fxmlLoader.load();
            playerview.getChildren().addAll(parent.getChildrenUnmodifiable());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * load the gamelist-module into the lobbylayout
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

    public void loadInfo(){
        infoFXML = new FXMLLoad("/lobby/LobbyGameInfo.fxml");
        AnchorPane.setLeftAnchor(infoFXML.getParent(), 0d);
        AnchorPane.setTopAnchor(infoFXML.getParent(), 0d);
        AnchorPane.setRightAnchor(infoFXML.getParent(), 0d);
        AnchorPane.setBottomAnchor(infoFXML.getParent(), 0d);
        base.getChildren().add(infoFXML.getParent());
    }
    
    /**
     * return the chat controller
     *
     * @return the controller of the loaded chat module
     */
    public LobbyChatController getChatCon() {
        return chatFXML.getController(LobbyChatController.class);
    }
}