package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import gameScreen.GameScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lobby.LobbyScreenController;
import model.Game;
import model.Model;
import registerLogin.RegisterLoginController;
import syncCommunication.HttpRequests;
import syncCommunication.SynchronousUserCommunicator;

public class AdvancedWarsApplication extends Application {

    static AdvancedWarsApplication advancedWarsApplication;
    public Stage primaryStage;
    private Scene gameScene;
    private GameScreenController gameScreenCon;
    public boolean offtesting = false;

    private FXMLLoad lobbyFxml;
    private FXMLLoad registerLoginFXML;


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
        lobbyFxml = new FXMLLoad("/lobby/LobbyScreen.fxml");
        primaryStage.setScene(lobbyFxml.getScene());
        primaryStage.setFullScreen(true);
    }

    public void goToRegisterLogin() {
        registerLoginFXML = new FXMLLoad("/registerLogin/registerLogin.fxml",
                new RegisterLoginController());
        primaryStage.setScene(registerLoginFXML.getScene());
        primaryStage.setFullScreen(true);
    }
    
    public void goToGame(Game game){
        if (gameScene == null) {
            try {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                InputStream inputStream = classLoader.getResource("en-US.properties").openStream();
                ResourceBundle bundle = new PropertyResourceBundle(inputStream);
                URL loc = getClass().getResource("/gameScreen/gameScreen.fxml");
                if (loc == null) {
                    System.out.println("loc null");
                }
                FXMLLoader fxmlLoader = new FXMLLoader(loc, bundle);
                Parent parent = fxmlLoader.load();
                gameScreenCon = fxmlLoader.getController();
                Scene scene = new Scene(parent);
                gameScene = scene;
                primaryStage.setTitle(bundle.getString("application.title"));
                primaryStage.setScene(scene);
                primaryStage.setFullScreen(true);

                primaryStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            primaryStage.setScene(gameScene);
            primaryStage.setFullScreen(true);
            //primaryStage.show();

        }
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
        return gameScreenCon;
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
    public SynchronousUserCommunicator getsynchronousUserCommunicator(){
        return new SynchronousUserCommunicator(Model.getPlayerHttpRequestsHashMap().get(Model.getApp().getCurrentPlayer()));
    }

    public Scene getLobbyScene(){
        return lobbyFxml.getScene();
    }
}
