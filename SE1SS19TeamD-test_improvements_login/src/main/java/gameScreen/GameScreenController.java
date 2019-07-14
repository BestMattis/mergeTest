package gameScreen;

import gameLobby.GameLobbyController_v2;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import main.AdvancedWarsApplication;
import main.FXMLLoad;
import model.Game;
import model.GameField;
import model.Model;

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

    private FXMLLoad gameLobbyFXML;
    private FXMLLoad unitFXML;
    public FXMLLoad chatFXML;
    private FXMLLoad menuFXML;

    private Scene ownScene;
    private boolean hidden = false;

    public Game gameToLoad;

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
        System.out.println("gameScreenLoaded");
        if (!test) {
            loadWaiting();
        }
        loadUnitPane();
        loadChatPane();
        loadMenuPane();

        if (!test) {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(20), t -> reloadField())
            );
            timeline.setCycleCount(1);
            timeline.setAutoReverse(false);
            timeline.play();
        } else {
            Game tmp = new Game();
            tmp.setGameField(GamefieldGenerator.randomField(30, 30));
            GameFieldCon gamepane = new GameFieldCon(tmp);
            gamefield.setContent(gamepane.getGamefield());
        }


    }

    public void reloadField(){
        System.out.println("tryToLoadServerField");
        System.out.println(Model.getApp().getCurrentPlayer().getGame().getGameField());
        if (Model.getApp().getCurrentPlayer().getGame().getGameField() != null) {
            GameField tmp = GamefieldGenerator.generateGameField(Model.getApp().getCurrentPlayer().getGame().getGameField().getFields());

                Game game = new Game();
                game.setGameField(tmp);
                GameFieldCon gamepane = new GameFieldCon(game);
                gamefield.setContent(gamepane.getGamefield());
                System.out.println("realFieldLoaded");


        }
    }


    /**
     * load the waitingscreen as overlay
     */
    private void loadWaiting(){
        gameLobbyFXML = new FXMLLoad("/gameLobby/GameLobbyScreen_v2.fxml", new GameLobbyController_v2());
        AnchorPane.setTopAnchor(gameLobbyFXML.getParent(), 0d);
        AnchorPane.setRightAnchor(gameLobbyFXML.getParent(), 0d);
        AnchorPane.setBottomAnchor(gameLobbyFXML.getParent(), 0d);
        AnchorPane.setLeftAnchor(gameLobbyFXML.getParent(), 0d);
        base.getChildren().add(gameLobbyFXML.getParent());
        gameLobbyFXML.getController(GameLobbyController_v2.class).update(gameToLoad);
        gameLobbyFXML.getController(GameLobbyController_v2.class).show();
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
    public GameLobbyController_v2 getGameLobbyController() {
        return gameLobbyFXML.getController(GameLobbyController_v2.class);
    }

    public GameChatController getGameChatController() {
        return chatFXML.getController(GameChatController.class);
    }

}
