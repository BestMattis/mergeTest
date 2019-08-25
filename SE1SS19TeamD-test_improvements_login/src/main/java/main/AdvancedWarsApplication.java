package main;

import gameScreen.GameScreenController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import lobby.LobbyScreenController;
import model.Game;
import model.Model;
import registerLogin.RegisterLoginController;
import syncCommunication.HttpRequests;
import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.SynchronousUserCommunicator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class AdvancedWarsApplication extends Application {

    public static final String PROPERTY_mSpeed = "mSpeed";
    public static AdvancedWarsApplication advancedWarsApplication;
    public static boolean offlineTest = false;
    public static boolean offtesting = false;
    public Model model;
    public Stage primaryStage;
    protected PropertyChangeSupport listeners = null;
    private Scene gameScene;
    private GameScreenController gameScreenCon;
    private FXMLLoad lobbyFxml;
    private FXMLLoad registerLoginFXML;
    private FXMLLoad gameFXML;
    private boolean isFullscreen = true;
    private int movementSpeed = 8;
    public static final String PROPERTY_aiActive = "aiActive";
    private boolean aiActive = false;

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

        model = new Model();

        // To keep 60FPS no matter which machine it's run on
        System.setProperty("quantum.multithreaded", "false");

        advancedWarsApplication = this;
        primaryStage = priStage;
        primaryStage.setFullScreen(isFullscreen);
        primaryStage.setTitle("Title can not be loaded");
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream;
            inputStream = classLoader.getResource("en-US.properties").openStream();
            ResourceBundle bundle = new PropertyResourceBundle(inputStream);
            primaryStage.setTitle(bundle.getString("application.title"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        goToRegisterLogin();

        primaryStage.show();

        //Set Listener for closing the window
        primaryStage.setOnCloseRequest(e -> closeWindowAndLogout());
    }

    /**
     * Loads the RegisterLogin.fxml and displays it on the stage given(the primaryStage)
     */
    public void goToLobby() {
        if (lobbyFxml == null) {
            lobbyFxml = new FXMLLoad("/lobby/LobbyScreen.fxml", new LobbyScreenController(model));
        }

        Scene currentScene = lobbyFxml.getScene();
        primaryStage.setScene(currentScene);
        currentScene.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ESCAPE)) {
                if (isFullscreen) {
                    isFullscreen = false;
                } else {
                    isFullscreen = true;
                }
            }
            primaryStage.setFullScreen(isFullscreen);
        });

        primaryStage.setFullScreen(isFullscreen);
    }

    public void goToRegisterLogin() {
        if (registerLoginFXML == null) {
            registerLoginFXML = new FXMLLoad("/registerLogin/registerLogin.fxml",
                    new RegisterLoginController(model));
        }
        Scene currentScene = registerLoginFXML.getScene();
        primaryStage.setScene(currentScene);

        currentScene.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ESCAPE)) {
                if (isFullscreen) {
                    isFullscreen = false;
                } else {
                    isFullscreen = true;
                }
            }
            primaryStage.setFullScreen(isFullscreen);
        });

        primaryStage.setFullScreen(isFullscreen);
    }

    public void goToGameAsObserver(Game game) {
        model.getApp().getCurrentPlayer().setGame(game);
        model.getApp().getCurrentPlayer().setObserver(true);
        if (gameFXML == null) {
            gameFXML = new FXMLLoad("/gameScreen/gameScreen.fxml", new GameScreenController(model));
            gameFXML.getController(GameScreenController.class).addScene(gameFXML.getScene());
        }
        primaryStage.setScene(gameFXML.getScene());
        gameScene = gameFXML.getScene();
        gameScreenCon = gameFXML.getController(GameScreenController.class);
        primaryStage.setFullScreen(true);
    }

    public void goToGame(Game game) {
        model.getApp().getCurrentPlayer().setGame(game);
        if (gameFXML == null) {
            gameFXML = new FXMLLoad("/gameScreen/gameScreen.fxml", new GameScreenController(model));
            gameFXML.getController(GameScreenController.class).addScene(gameFXML.getScene());
        }
        primaryStage.setScene(gameFXML.getScene());
        gameScene = gameFXML.getScene();

        gameScene.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ESCAPE)) {
                if (isFullscreen) {
                    isFullscreen = false;
                } else {
                    isFullscreen = true;
                }
            }
            primaryStage.setFullScreen(isFullscreen);
        });

        gameScreenCon = gameFXML.getController(GameScreenController.class);
        primaryStage.setFullScreen(isFullscreen);
    }

    /**
     * @return to get the httprequests with the user
     */
    public HttpRequests getHttpRequests() {
        return model.getPlayerHttpRequestsHashMap().get(model.getApp().getCurrentPlayer());
    }

    /**
     * @return the Controller of the game Scene
     */
    public GameScreenController getGameScreenCon() {
        return gameFXML.getController(GameScreenController.class);
    }

    public void setGameScreenController(FXMLLoad gameFXML) {

        this.gameFXML = gameFXML;
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
    public SynchronousUserCommunicator getSynchronousUserCommunicator() {
        return new SynchronousUserCommunicator(model.getPlayerHttpRequestsHashMap().get(model.getApp().getCurrentPlayer()));
    }

    public Scene getLobbyScene() {
        return lobbyFxml.getScene();
    }

    /**
     * resets the FXMLs (on logout)
     */
    public void resetFXML() {
        lobbyFxml = null;
        gameFXML = null;
        registerLoginFXML = null;
    }

    /**
     * resets the GameScreen FXML (when leaving a game)
     */
    public void resetGameScreenFXML() {
        gameFXML = null;
    }

    /**
     * closes the window and logs the player out, when logged in
     */
    public void closeWindowAndLogout() {
        Platform.exit();

        //when logged in, logout
        boolean loggedOut = false;
        HttpRequests hr = model.getPlayerHttpRequestsHashMap().get(model.getApp().getCurrentPlayer());
        SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);

        if (hr != null) {
            try {
                loggedOut = uComm.logOut();
            } catch (LoginFailedException e) {
                e.printStackTrace();
            }
            if (loggedOut) {
                model.setApp(null); // clear data model on logout
                model.getWebSocketComponent().stopComponent();
            }
        }
        System.out.println("logged out: " + loggedOut);
        System.exit(0);
    }

    /**
     * to get the current speed of the Units displayed on the map
     *
     * @return the speed of the Units
     */
    public int getMovementSpeed() {
        return movementSpeed;
    }

    /**
     * set speed for the Units on the Map
     *
     * @param movementSpeed the speed to set
     */
    public void setMovementSpeed(int movementSpeed) {
        this.movementSpeed = movementSpeed;
        System.out.println("Speed set to: " + movementSpeed);
        firePropertyChange("mSpeed", 0, movementSpeed);
    }

    public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (listeners != null) {
            listeners.firePropertyChange(propertyName, oldValue, newValue);
            return true;
        }
        return false;
    }

    public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listeners == null) {
            listeners = new PropertyChangeSupport(this);
        }
        listeners.addPropertyChangeListener(propertyName, listener);
        return true;
    }

    /**
     * @return a boolean to know the activity state of the AI
     */
    public boolean isAiActive() {
        return aiActive;
    }

    /** set the activity boolean of the ai
     * @param aiActive
     */
    public void setAiActive(boolean aiActive) {
        boolean old = this.aiActive;
        this.aiActive = aiActive;
        firePropertyChange("aiActive", old, aiActive);
    }
}
