package gameScreen;

import gameController.gameLoop.GameLoop;
import gameLobby.GameLobbyController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lobby.LobbyChatMessageListController;
import lobby.OptionsController;
import main.AdvancedWarsApplication;
import main.FXMLLoad;
import model.ChatMessage;
import model.Game;
import model.Model;
import model.Player;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameScreenController {

    public FXMLLoad chatFXML;
    public FXMLLoad optionsFXML;
    public Game gameToLoad;
    @FXML
    protected AnchorPane base;
    @FXML
    private AnchorPane changePane;
    @FXML
    private AnchorPane opmen;
    @FXML
    private AnchorPane mappane;
    @FXML
    private ScrollPane gamefield;
    @FXML
    private VBox loadingVBox;
    @FXML
    private ProgressBar loadingProgress;
    @FXML
    Label loadingLabel;
    private Model model;
    @FXML
    Label activePlayer;
    @FXML
	public
    Button basicRound;
    @FXML
    private AnchorPane roundButtonPane;
    @FXML
    private Label currentPhaseLabel;

    private FXMLLoad gameLobbyFXML;
    private FXMLLoad unitFXML;
    private FXMLLoad menuFXML;
    private FXMLLoad winnerScreenFXML;
    private Scene ownScene;
    private boolean hidden = false;
    private GameLoop gameLoop;
    private PropertyChangeListener ingameListener;


    private boolean winningShow = false;

    private Minimap minimap;
    private boolean minimapWasReady = true;

    private LobbyChatMessageListController chatController;
    private GameFieldController gamepane;

    private GameUnitDisplayController unitDisplayController;

    /**
     * Constructor to load game in the data model.
     */
    public GameScreenController(Model model) {
        this.model = model;
        if (!AdvancedWarsApplication.offtesting) {
            gameToLoad = model.getApp().getCurrentPlayer().getGame();
        }
    }

    /**
     * Constructor to load a given game.
     *
     * @param game game to load from
     */
    public GameScreenController(Game game) {
        gameToLoad = game;

    }

    /**
     * @return the ScrollPane where the gameField is located in.
     */
    public ScrollPane getGamefield() {
        return gamefield;
    }

    public Minimap getMinimap() {
        return minimap;
    }

    public boolean getMinimapWasReady() {
        return minimapWasReady;
    }

    public void setMinimapWasReady(boolean minimapWasReady) {
        this.minimapWasReady = minimapWasReady;
    }

    /**
     * set the actions for key-events with enter and h in the gamescreen-scene
     *
     * @param own the scene this is the controller of
     */
    public void addScene(Scene own) {
        ownScene = own;
        gamefield.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                chatFXML.getController(GameChatController.class).animateUsingTimeline();
            }
            if (ke.getCode().equals(KeyCode.H)) {
                if (hidden) {
                    mappane.setVisible(true);
                    changePane.setVisible(true);
                    hidden = false;
                } else {
                    mappane.setVisible(false);
                    changePane.setVisible(false);
                    hidden = true;
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
        gameLoop = new GameLoop();
        gameLoop.startLoop();
        
        model.getApp().getCurrentPlayer().getGame().addPropertyChangeListener(Game.PROPERTY_currentPhase,t -> {
        	String newPhase = (String) t.getNewValue();
        	String standard = "Current Phase: ";
        	if(newPhase != null) {
	        	Platform.runLater(()-> {
					currentPhaseLabel.setText(standard+newPhase);
				});
        	}
        });

        model.getApp().getCurrentPlayer().getGame().addPropertyChangeListener(Game.PROPERTY_activePlayer, t -> {
        	String oldPlayer = model.getApp().getCurrentPlayer().getGame().getActivePlayer();
        	String newActiveName = (String) t.getNewValue();
        	String standard = "Active Player: ";
        	boolean rightName = false;
        	
        	if(newActiveName != null) {
        		if(newActiveName.equals(model.getApp().getCurrentPlayer().getName())) {
        			enableButton();
        		}
        		if(!oldPlayer.equals(newActiveName)) {
        			String roundCount = getGameMenuController().getRoundCount().getText();
        			int count = Integer.parseInt(roundCount);
        			count++;
        			getGameMenuController().getRoundCount().setText(Integer.toString(count));
        		}
        		for(Player p : model.getApp().getCurrentPlayer().getGame().getPlayers()) {
        			if(p.getName().equals(newActiveName)) {
        				rightName = true;
        			}
        		}
        		if(rightName) {
        			Platform.runLater(()-> {
        				activePlayer
        				.setText(
        						standard
        						+newActiveName);
        			});
        		}
        	}
        });
        
        basicRound.setOnAction(e -> endRound());
        if(winningShow) {
            loadWinnerScreen();
        }
        model.getApp().getCurrentPlayer().getGame().addPropertyChangeListener(Game.PROPERTY_winner, evt -> {
            String winnerPlayer = (String) evt.getNewValue();
            if (winnerPlayer != null) {
                Platform.runLater(() -> {
                    loadWinnerScreen();
                });
            }
        });

        System.out.println(this.getClass().toString()+": gameScreenLoaded");  

        System.out.println(this.getClass().toString()+": gameScreenLoaded");
        loadingVBox.setMinWidth(base.getWidth());
        loadingVBox.setMinHeight(base.getHeight());
        base.widthProperty().addListener((observable, oldValue, newValue) -> loadingVBox.setMinWidth(newValue.doubleValue()));
        base.heightProperty().addListener((observable, oldValue, newValue) -> loadingVBox.setMinHeight(newValue.doubleValue()));
        
        //without offlineTest check, because of new automated offlinetest logins
        if (!AdvancedWarsApplication.offtesting) {
            loadWaiting();
        }
        loadUnitPane();
        loadChatPane();
        loadMenuPane();
        loadOptions();

        if (AdvancedWarsApplication.offlineTest) {
            Game tmp = model.getApp().getCurrentPlayer().getGame();
            tmp.setGameField(GamefieldGenerator.randomField(32, 32));
            this.gamepane = new GameFieldController(model.getApp().getCurrentPlayer().getGame(),loadingProgress, gameLoop, unitDisplayController, gamefield, model);
            Platform.runLater(() -> {
                gamefield.setContent(gamepane.getGamefield());
                minimap = new Minimap(tmp, mappane.getChildren().get(0), gamefield, gamepane, model);
                minimap.createMinimap();
                //for offline testing
                minimap.addUnitsTestMethod();
            });
        } else {
            if (!AdvancedWarsApplication.offtesting) {
                Runnable loadGameField = () -> {
                    System.out.println(model.getApp().getCurrentPlayer().getGame().getGameField() + " check gamefield");
                    while (model.getApp().getCurrentPlayer().getGame() != null && !model.getApp().getCurrentPlayer().getGame().getGameField().getGameFieldLoaded()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (model.getApp().getCurrentPlayer().getGame() != null) {
                        reloadField();
                    }
                };
                Executors.newSingleThreadExecutor().execute(loadGameField);
            }
        }
        basicRound.setDisable(AdvancedWarsApplication.getInstance().isAiActive());
        AdvancedWarsApplication.getInstance().addPropertyChangeListener("aiActive", t -> basicRound.setDisable(AdvancedWarsApplication.getInstance().isAiActive()));
    }

    public void setIngameListeners() {

        if (model.getWebSocketComponent().isIngame()) {
            ingameListener = new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {

                    ChatMessage message = (ChatMessage) evt.getNewValue();
                    if (message.getChannel() != null && message.getChannel().equals("private")) {
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> {

                            while (message.getReceiver() == null || message.getSender() == null) ;
                            if (message.getMessage() != null) {

                                Player currentPlayer = model.getApp().getCurrentPlayer();
                                Player sender = message.getSender();

                                if (sender.getName().equals(currentPlayer.getName())) {
                                    Platform.runLater(() -> model.getWebSocketComponent().sendGameChatMessage(message));
                                } else {
                                    // display private messages in gamelobby or ingamechat.
                                    Platform.runLater(() -> chatFXML.getController(GameChatController.class).displayPrivateMsg(message));
                                }
                            }
                        });
                        executor.shutdown();

                    } else if (message.getChannel() != null && message.getChannel().equals("all")) {
                        if (message.getSender() != null && message.getMessage() != null) {
                            if (message.getSender().getName().equals(model.getApp()
                                    .getCurrentPlayer().getName())) {
                                Platform.runLater(() -> model.getWebSocketComponent().sendGameChatMessage(message));
                            } else if (!message.getSender().getName().equals(model.getApp().getCurrentPlayer().getName())) {
                                if (message.getSender().getName().equals("System")) {
                                    Platform.runLater(() -> chatFXML.getController(GameChatController.class).displayAllChatMsgSyst(message));
                                } else {
                                    // display allchat message in gamelobby or in ingamechat.
                                    Platform.runLater(() -> chatFXML.getController(GameChatController.class).displayAllChatMsg(message));
                                }
                            }
                        }
                    }
                }
            };
            model.getApp().getCurrentPlayer().getGame().addPropertyChangeListener(Game.PROPERTY_ingameMessages, ingameListener);
        }
    }

    public void removeIngameListeners() {
        if (this.ingameListener != null && model.getWebSocketComponent().isIngame() == false) {
            model.getApp().getCurrentPlayer().getGame().removePropertyChangeListener(this.ingameListener);
        }
    }

	public void endRound() {
		model.getWebSocketComponent().nextPhase();
		System.out.println("nextPhase");
	}

    /**
     * Reloads the gameField and displays it on the screen.
     */
    public void reloadField() {
        System.out.println(this.getClass().toString() + ": tryToLoadServerField, Field = " + model.getApp().getCurrentPlayer().getGame().getGameField());
        if (model.getApp().getCurrentPlayer().getGame().getGameField() != null) {
            GamefieldGenerator.generateGameField(model.getApp().getCurrentPlayer().getGame().getGameField());
            Runnable run = () -> {
                gamepane = new GameFieldController(model.getApp().getCurrentPlayer().getGame(),loadingProgress, gameLoop, unitDisplayController, gamefield, model);
                Platform.runLater(() -> {
                    gamefield.setContent(gamepane.getGamefield());
                    loadingVBox.setVisible(false);
                    System.out.println(this.getClass().toString() + ": realFieldLoaded");

                    //create new minimap
                    minimap = new Minimap(model.getApp().getCurrentPlayer().getGame(), mappane.getChildren().get(0), gamefield, gamepane, model);
                    minimap.createMinimap();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //wait a second to make sure that minimapWasReady has the correct value
                    if (!minimapWasReady) {
                        minimap.addUnitsToMinimap();
                    }
                });
            };
            Executors.newSingleThreadExecutor().execute(run);
        }
    }

    /**
     * load the waitingscreen as overlay
     */
    private void loadWaiting() {
        gameLobbyFXML = new FXMLLoad("/gameLobby/GameLobbyScreen.fxml", new GameLobbyController(model));
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
    private void loadUnitPane() {
        unitFXML = new FXMLLoad("/gameScreen/unitDisplay.fxml", new GameUnitDisplayController(model));
        this.unitDisplayController = unitFXML.getController(GameUnitDisplayController.class);
        AnchorPane.setTopAnchor(unitFXML.getParent(), 0d);
        AnchorPane.setRightAnchor(unitFXML.getParent(), 0d);
        AnchorPane.setBottomAnchor(unitFXML.getParent(), 0d);
        AnchorPane.setLeftAnchor(unitFXML.getParent(), 0d);
        changePane.getChildren().add(unitFXML.getParent());
    }

    /**
     * load the chat-part of the gui
     */
    private void loadChatPane() {
        chatFXML = new FXMLLoad("/gameScreen/chatDisplay.fxml", new GameChatController(model));
        AnchorPane.setBottomAnchor(chatFXML.getParent(), 0d);
        AnchorPane.setLeftAnchor(chatFXML.getParent(), 0d);
        base.getChildren().add(chatFXML.getParent());
    }

    /**
     * load the winnerScreen
     */
    private void loadWinnerScreen() {
        winnerScreenFXML = new FXMLLoad("/gameScreen/winnerScreen.fxml", new WinnerScreenController(model));
        AnchorPane.setTopAnchor(winnerScreenFXML.getParent(), 0d);
        AnchorPane.setRightAnchor(winnerScreenFXML.getParent(), 0d);
        AnchorPane.setBottomAnchor(winnerScreenFXML.getParent(), 0d);
        AnchorPane.setLeftAnchor(winnerScreenFXML.getParent(), 0d);
        base.getChildren().add(winnerScreenFXML.getParent());
    }

    /**
     * load the menu
     */
    private void loadMenuPane() {
        menuFXML = new FXMLLoad("/gameScreen/menuDisplay.fxml", new MenuDisplayController(this, model));
        AnchorPane.setTopAnchor(menuFXML.getParent(), 0d);
        AnchorPane.setRightAnchor(menuFXML.getParent(), 0d);
        AnchorPane.setLeftAnchor(menuFXML.getParent(), 0d);
        base.getChildren().add(menuFXML.getParent());
        menuFXML.getParent().setVisible(false);
        menuFXML.getParent().setOnMouseEntered(t -> menuFXML.getParent().setOpacity(1));
        menuFXML.getParent().setOnMouseExited(t -> {
            menuFXML.getParent().setOpacity(0);
            menuFXML.getParent().setVisible(false);
        });
        opmen.setOnMouseEntered(t -> {
            menuFXML.getParent().setOpacity(1);
            menuFXML.getParent().setVisible(true);
        });
    }

    /**
     * @return the controller of the game field
     */
    public GameFieldController getGameFieldController() {
        return this.gamepane;
    }


    /**
     * @return the controller of the waiting Screen
     */
    public GameLobbyController getGameLobbyController() {
        return gameLobbyFXML.getController(GameLobbyController.class);
    }

    /**
     * Return the GameChatController
     *
     * @return GameChatController
     */
    public GameChatController getGameChatController() {
        return chatFXML.getController(GameChatController.class);
    }

    /**
     * Places Units on Field to test other methods
     */
//    public void textUnitOnGameField(){
//        for (int i = 0; i < model.getApp().getCurrentPlayer().getCurrentArmyConfiguration().getUnits().size(); i++) {
//            model.getApp().getCurrentPlayer().getGame().getGameField().getFields().get(i).setCurrentUnitOnField(
//                    model.getApp().getCurrentPlayer().getCurrentArmyConfiguration().getUnits().get(i));
//        }
//    }
    
	public MenuDisplayController getGameMenuController() {
		return menuFXML.getController(MenuDisplayController.class);
	}

	/**
	 * this methode hides / disables all useable elements for the observer
	 */
	public void observerModeGameScreen() {
		getGameChatController().observerModeGameScreenChat();
		getGameMenuController().observerModeGameScreenMenu();
		roundButtonPane.setVisible(false);
	}
	
	public Label getActivePlayerLabel() {
		return activePlayer;
	}
	
	public void disableOnUnActive() {
		basicRound.setDisable(true);
		getGameMenuController().getEndRound().setDisable(true);
	}
	
	public void enableButton() {
		basicRound.setDisable(false);
		getGameMenuController().getEndRound().setDisable(false);
	}
	
	/**
     * this methode loads the gui of the Options fo the game int the gameScreen
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

    public FXMLLoad getMenuFXML(){
        return menuFXML;
    }
}

