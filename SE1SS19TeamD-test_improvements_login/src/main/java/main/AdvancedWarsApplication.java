package main;

import gameScreen.GameScreenController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lobby.LobbyScreenController;
import model.Game;
import model.Model;
import model.Player;
import registerLogin.RegisterLoginController;
import syncCommunication.HttpRequests;
import syncCommunication.SynchronousUserCommunicator;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class AdvancedWarsApplication extends Application {

    public static AdvancedWarsApplication advancedWarsApplication;
    public Stage primaryStage;
    public boolean offtesting = false;
    private Scene gameScene;
    private GameScreenController gameScreenCon;
    private FXMLLoad lobbyFxml;
    private FXMLLoad registerLoginFXML;
    private FXMLLoad gameFXML;


    /**
     * launching the app
     *
     * @param args commandline param
     */
    public static void main(String[] args) {
        Application.launch(AdvancedWarsApplication.class, args);
    }


    /**
     * This method returns the instance of the Ad.WarsApp class
     *
     * @return AdvancedWarsApplication
     */
    static public AdvancedWarsApplication getInstance() {
        return advancedWarsApplication;
    }


    /**
     * This method starts the first Stage, displays it in Fullscreen, and sets the Title. is calle from the main method
     *
     * @param priStage the Stage the game is shown in
     */
    @Override
    public void start(Stage priStage) {

        advancedWarsApplication = this;
        primaryStage = priStage;
        primaryStage.setFullScreen(true);
        primaryStage.setTitle("Title can not be loaded");
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = null;
            inputStream = classLoader.getResource("en-US.properties").openStream();
            ResourceBundle bundle = new PropertyResourceBundle(inputStream);
            primaryStage.setTitle(bundle.getString("application.title"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        goToRegisterLogin();

        primaryStage.show();
    }

    /**
     * Loads the RegisterLogin.fxml and displays it on the stage given(the primaryStage)
     */
    public void goToLobby() {
        if (lobbyFxml == null){
            lobbyFxml = new FXMLLoad("/lobby/LobbyScreen.fxml");
    }
        primaryStage.setScene(lobbyFxml.getScene());
        primaryStage.setFullScreen(true);
    }

    public void goToRegisterLogin() {
        if (registerLoginFXML == null) {
            registerLoginFXML = new FXMLLoad("/registerLogin/registerLogin.fxml",
                    new RegisterLoginController());
        }
        primaryStage.setScene(registerLoginFXML.getScene());
        primaryStage.setFullScreen(true);
    }

    public void goToGame(Game game){
        Model.getApp().getCurrentPlayer().setGame(game);
        if(gameFXML == null) {
            gameFXML = new FXMLLoad("/gameScreen/gameScreen.fxml", new GameScreenController());
            gameFXML.getController(GameScreenController.class).addScene(gameFXML.getScene());
        }
        primaryStage.setScene(gameFXML.getScene());
		gameScene = gameFXML.getScene();
		gameScreenCon = gameFXML.getController(GameScreenController.class);
        primaryStage.setFullScreen(true);
    }
    
    public void goToGameAsObserver(Game game){
        Model.getApp().getCurrentPlayer().setGame(game);
        Model.getApp().getCurrentPlayer().getGame().setObservingPlayers(new ArrayList<Player>());
        Model.getApp().getCurrentPlayer().getGame().getObservingPlayers().add(Model.getApp().getCurrentPlayer());
        if(gameFXML == null) {
            gameFXML = new FXMLLoad("/gameScreen/gameScreen.fxml", new GameScreenController());
            gameFXML.getController(GameScreenController.class).addScene(gameFXML.getScene());
        }
        primaryStage.setScene(gameFXML.getScene());
		gameScene = gameFXML.getScene();
		gameScreenCon = gameFXML.getController(GameScreenController.class);
        primaryStage.setFullScreen(true);
    }

    /**
     * @return to get the httprequests with the user
     */
    public HttpRequests getHttpRequests() {
        return Model.getPlayerHttpRequestsHashMap().get(Model.getApp().getCurrentPlayer());
    }

    /**
     * @return the Controller of the game Scene
     */
    public GameScreenController getGameScreenCon(){
        return gameFXML.getController(GameScreenController.class);
    }

    /**
     * @return the Controller of the Lobby Scene
     */
    public LobbyScreenController getLobbyCon() {
        return lobbyFxml.getController(LobbyScreenController.class);
    }

    /**
     * @return the syncUserCom to always have the Userkey
     */
    public SynchronousUserCommunicator getsynchronousUserCommunicator() {
        return new SynchronousUserCommunicator(Model.getPlayerHttpRequestsHashMap().get(Model.getApp().getCurrentPlayer()));
    }

    public Scene getLobbyScene() {
        return lobbyFxml.getScene();
    }
}
