package lobby;

import armyManager.ArmyManagerController;
import createGame.CreateGameController;
import gameList.GameListController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import main.FXMLLoad;
import model.App;
import model.Model;
import playerList.PlayerListController;
import syncCommunication.HttpRequests;
import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.SynchronousUserCommunicator;

public class LobbyScreenController {

    public FXMLLoad armymanagerFXML;
    public FXMLLoad optionsFXML;
    @SuppressWarnings("static-access")
    App app;
    AdvancedWarsApplication application = AdvancedWarsApplication.getInstance();
    private Model model;
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
    @FXML
    private Button manager;
    private FXMLLoad infoFXML;
    private FXMLLoad chatFXML;

    public LobbyScreenController(Model model) {
        this.model = model;
        app = model.getApp();
    }

    /**
     * called when the lobbyScreen scene is loaded.
     * Set the methods for clicking on the buttons
     */
    public void initialize() {
        newGame.setOnAction(t -> newGameButtonClicked());
        options.setOnAction(t -> optionsButtonClicked());
        logout.setOnAction(t -> logoutButtonClicked());
        logo.setOnMouseClicked(t -> infoImageClicked());
        manager.setOnAction(t -> managerButtonclicked());
        loadChat();
        loadGamelist();
        loadPlayerlist();
        loadInfo();
        loadManager();
        loadOptions();
    }

    /**
     * Method to start a new game
     */
    public void newGameButtonClicked() {

        FXMLLoad newGame = new FXMLLoad("/createGame/CreateGame.fxml", new CreateGameController(model));
        Parent parent = newGame.getParent();
        CreateGameController createGameController = newGame.getController(CreateGameController.class);
        createGameController.setBase(base);
        createGameController.setParent(parent);
        base.getChildren().add(parent);
        AnchorPane.setRightAnchor(parent, 0d);
        AnchorPane.setBottomAnchor(parent, 0d);
        AnchorPane.setLeftAnchor(parent, 0d);
        AnchorPane.setTopAnchor(parent, 0d);
        //base.getChildren().addAll(parent.getChildrenUnmodifiable());

    }

    /**
     * This method opens the screen for the options
     */
    public void optionsButtonClicked() {
        System.out.println("options");
        optionsFXML.getController(OptionsController.class).show();
    }

    /**
     * logout and showing the loginscreen again
     */
    @SuppressWarnings("static-access")
    public void logoutButtonClicked() {
        boolean loggedOut = false;
        HttpRequests hr = model.getPlayerHttpRequestsHashMap().get(model.getApp().getCurrentPlayer());
        SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);
        try {
            loggedOut = uComm.logOut();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }
        if (loggedOut) {
            model.setApp(null); // clear data model on logout
            model.getWebSocketComponent().stopComponent();
            if (application != null) {
                //when logging out, primary stage gets restarted and the main FXMLs are reset
                application.primaryStage.close();
                application.resetFXML();
                application.start(new Stage());
            } else {
                System.out.println("failed showing LoginScreen");    // for test only
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
    public void loadChat() {
        chatFXML = new FXMLLoad("/lobby/LobbyChat.fxml", new LobbyChatController(model));
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
        FXMLLoad playerlist = new FXMLLoad("/playerList/PlayerList.fxml", new PlayerListController(model));
        Parent parent = playerlist.getParent();
        playerview.getChildren().addAll(parent.getChildrenUnmodifiable());
    }

    /**
     * load the gamelist-module into the lobbylayout
     */
    public void loadGamelist() {
        FXMLLoad gamelist = new FXMLLoad("/gameList/GameList.fxml", new GameListController(model));
        Parent parent = gamelist.getParent();
        gamesview.getChildren().addAll(parent.getChildrenUnmodifiable());

    }

    public void loadInfo() {
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

    public void loadManager() {
        armymanagerFXML = new FXMLLoad("/armyManager/ArmyManager.fxml", "en-US.properties", new ArmyManagerController(model));
        base.getChildren().add(armymanagerFXML.getParent());
        AnchorPane.setRightAnchor(armymanagerFXML.getParent(), 0d);
        AnchorPane.setBottomAnchor(armymanagerFXML.getParent(), 0d);
        AnchorPane.setLeftAnchor(armymanagerFXML.getParent(), 0d);
        AnchorPane.setTopAnchor(armymanagerFXML.getParent(), 0d);
        armymanagerFXML.getController(ArmyManagerController.class).hide();
    }

    public void managerButtonclicked() {
        armymanagerFXML.getController(ArmyManagerController.class).show();
    }

    /**
     * this methode loads the Optionsgui for the lobby
     */
    public void loadOptions() {
        optionsFXML = new FXMLLoad("/lobby/Options.fxml", new OptionsController());
        AnchorPane.setLeftAnchor(optionsFXML.getParent(), 0d);
        AnchorPane.setTopAnchor(optionsFXML.getParent(), 0d);
        AnchorPane.setRightAnchor(optionsFXML.getParent(), 0d);
        AnchorPane.setBottomAnchor(optionsFXML.getParent(), 0d);
        base.getChildren().add(optionsFXML.getParent());
        optionsFXML.getController(OptionsController.class).hide();
    }
}
