package gameScreen;

import gameLobby.GameLobbyController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import main.AdvancedWarsApplication;
import main.FXMLLoad;
import model.Game;
import model.GameField;
import model.Model;
import model.Player;

import java.util.concurrent.Executors;

public class GameScreenController {

    private boolean test = false;

    @FXML
    AnchorPane base;
    @FXML
    AnchorPane changePane;
    @FXML
    AnchorPane opmen; 
    @FXML
    AnchorPane mappane;
    @FXML
    ScrollPane gamefield;
    @FXML
    VBox loadingVBox;
    @FXML
    ProgressBar loadingProgress;
    @FXML
    Label loadingLabel;

    private FXMLLoad gameLobbyFXML;
    private FXMLLoad unitFXML;
    public FXMLLoad chatFXML;
    private FXMLLoad menuFXML;
    private FXMLLoad winnerScreenFXML;

    private Scene ownScene;
    private boolean hidden = false;

    public Game gameToLoad;

	private boolean winningShowTest = false;

    public ScrollPane getGamefield(){
        return gamefield;
    }

    public GameScreenController(){
        if(!AdvancedWarsApplication.getInstance().offtesting) {
            gameToLoad = Model.getApp().getCurrentPlayer().getGame();
        }
    }
    public GameScreenController(Game game){
        gameToLoad = game;

    }
    public GameScreenController(boolean test2, Game game){
        test = test2;
        gameToLoad = game;
    }


    /** set the actions for key-events with enter and h in the gamescreen-scene
     * @param own the scene this is the controller of
     */
    public void addScene(Scene own){
        ownScene = own;
        ownScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    chatFXML.getController(GameChatController.class).animateUsingTimeline();
                }
                if (ke.getCode().equals(KeyCode.H)) {
                    if (hidden){
                        mappane.setVisible(true);
                        changePane.setVisible(true);
                        hidden = false;
                    } else {
                        mappane.setVisible(false);
                        changePane.setVisible(false);
                        hidden = true;
                    }
                }
            }
        });
    }

    /**
     * load all elements for the gamescreen
     * set the actions for the opacity change of the map and unitsdisplay
     */
    @FXML
    public void initialize() {
        System.out.println(this.getClass().toString()+": gameScreenLoaded");
        if(winningShowTest) {
        	loadWinnerScreen();
        }
        Model.getApp().getCurrentPlayer().getGame().addPropertyChangeListener(Game.PROPERTY_winner, evt -> {
        	System.out.println(evt.getNewValue());
        	String winnerPlayer = (String)evt.getNewValue();
        	if(winnerPlayer != null) {
        		Platform.runLater(() -> {
        			loadWinnerScreen();
        		});
        	}
        });
        loadingVBox.setMinWidth(base.getWidth());
        loadingVBox.setMinHeight(base.getHeight());
        base.widthProperty().addListener((observable, oldValue, newValue) -> loadingVBox.setMinWidth(newValue.doubleValue()));
        base.heightProperty().addListener((observable, oldValue, newValue) -> loadingVBox.setMinHeight(newValue.doubleValue()));

        if (!test) {
            loadWaiting();
        }
        loadUnitPane();
        loadChatPane();
        loadMenuPane();

        if (!test) {
            Model.getApp().getCurrentPlayer().getGame().getGameField().addPropertyChangeListener(GameField.PROPERTY_gameFieldLoaded, evt -> {
                if(evt.getNewValue().equals(true)){
                    Model.getApp().getCurrentPlayer().getGame().getGameField().setGameFieldLoaded(false);
                    System.out.println(this.getClass().toString()+": field is in data model and can be shown (property change)");
                    reloadField();
                }
            });
            if(Model.getApp().getCurrentPlayer().getGame().getGameField().getGameFieldLoaded()){
                Model.getApp().getCurrentPlayer().getGame().getGameField().setGameFieldLoaded(false);
                System.out.println(this.getClass().toString()+": field is in data model and can be shown (missed property change)");
                reloadField();
            }
        } else {
            Game tmp = new Game();
            tmp.setGameField(GamefieldGenerator.randomField(30, 30));
            GameFieldCon gamepane = new GameFieldCon(tmp,loadingProgress);
            gamefield.setContent(gamepane.getGamefield());
        }
    }

    public void reloadField(){
        System.out.println(this.getClass().toString()+": tryToLoadServerField, Field = "+Model.getApp().getCurrentPlayer().getGame().getGameField());
        if (Model.getApp().getCurrentPlayer().getGame().getGameField() != null) {
            GamefieldGenerator.generateGameField(Model.getApp().getCurrentPlayer().getGame().getGameField());
            Runnable run = () -> {
                GameFieldCon gamepane = new GameFieldCon(Model.getApp().getCurrentPlayer().getGame(),loadingProgress);
                Platform.runLater(() -> {
                    gamefield.setContent(gamepane.getGamefield());
                    loadingVBox.setVisible(false);
                    System.out.println(this.getClass().toString()+": realFieldLoaded");
                });
            };
            Executors.newSingleThreadExecutor().execute(run);
        }
    }


    /**
     * load the waitingscreen as overlay
     */
    private void loadWaiting(){
        gameLobbyFXML = new FXMLLoad("/gameLobby/GameLobbyScreen_v2.fxml", new GameLobbyController());
        AnchorPane.setTopAnchor(gameLobbyFXML.getParent(), 0d);
        AnchorPane.setRightAnchor(gameLobbyFXML.getParent(), 0d);
        AnchorPane.setBottomAnchor(gameLobbyFXML.getParent(), 0d);
        AnchorPane.setLeftAnchor(gameLobbyFXML.getParent(), 0d);
        base.getChildren().add(gameLobbyFXML.getParent());
        gameLobbyFXML.getController(GameLobbyController.class).update(gameToLoad);
        gameLobbyFXML.getController(GameLobbyController.class).show();
    }

    /**
     * load the unitstats pane
     */
    private void loadUnitPane(){
        unitFXML = new FXMLLoad("/gameScreen/unitDisplay.fxml", new GameUnitDisplayController());
        AnchorPane.setTopAnchor(unitFXML.getParent(), 0d);
        AnchorPane.setRightAnchor(unitFXML.getParent(), 0d);
        AnchorPane.setBottomAnchor(unitFXML.getParent(), 0d);
        AnchorPane.setLeftAnchor(unitFXML.getParent(), 0d);
        changePane.getChildren().add(unitFXML.getParent());
    }

    /**
     * load the chat-part of the gui
     */
    private void loadChatPane(){
        chatFXML = new FXMLLoad("/gameScreen/chatDisplay.fxml", new GameChatController());
        AnchorPane.setBottomAnchor(chatFXML.getParent(), 0d);
        AnchorPane.setLeftAnchor(chatFXML.getParent(), 0d);
        base.getChildren().add(chatFXML.getParent());
    }
    
    /**
     * load the winnerScreen
     */
    private void loadWinnerScreen(){
        winnerScreenFXML = new FXMLLoad("/gameScreen/winnerScreen.fxml", new WinnerScreenController());
        AnchorPane.setTopAnchor(winnerScreenFXML.getParent(), 0d);
        AnchorPane.setRightAnchor(winnerScreenFXML.getParent(), 0d);
        AnchorPane.setBottomAnchor(winnerScreenFXML.getParent(), 0d);
        AnchorPane.setLeftAnchor(winnerScreenFXML.getParent(), 0d);
        base.getChildren().add(winnerScreenFXML.getParent());
    }

    /**
     * load the menu
     */
    private void loadMenuPane(){
        menuFXML = new FXMLLoad("/gameScreen/menuDisplay.fxml", new MenuDisplayController(this));
        AnchorPane.setTopAnchor(menuFXML.getParent(), 0d);
        AnchorPane.setRightAnchor(menuFXML.getParent(), 0d);
        AnchorPane.setLeftAnchor(menuFXML.getParent(), 0d);
        base.getChildren().add(menuFXML.getParent());
        menuFXML.getParent().setVisible(false);
        menuFXML.getParent().setOnMouseEntered(t -> menuFXML.getParent().setOpacity(1));
        menuFXML.getParent().setOnMouseExited(t -> {menuFXML.getParent().setOpacity(0);menuFXML.getParent().setVisible(false);});
        opmen.setOnMouseEntered(t -> {menuFXML.getParent().setOpacity(1); menuFXML.getParent().setVisible(true);});
    }

    /**
     * @return the controller of the waiting Screen
     */
    public GameLobbyController getGameLobbyController() {
        return gameLobbyFXML.getController(GameLobbyController.class);
    }

    /**
     * Return the GameChatController
     * @return GameChatController
     */
    public GameChatController getGameChatController() {
        return chatFXML.getController(GameChatController.class);
    }

    /**
     * Places Units on Field to test other methods
     */
    public void textUnitOnGameField(){
        for (int i = 0; i < Model.getApp().getCurrentPlayer().getCurrentArmyConfiguration().getUnits().size(); i++) {
            Model.getApp().getCurrentPlayer().getGame().getGameField().getFields().get(i).setCurrentUnitOnField(
                    Model.getApp().getCurrentPlayer().getCurrentArmyConfiguration().getUnits().get(i));
        }
    }
}
