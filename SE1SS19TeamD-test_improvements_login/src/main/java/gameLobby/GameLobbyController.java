package gameLobby;

import armyManager.ArmyManagerController;
import gameScreen.MenuDisplayController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import lobby.LobbyChatMessageListController;
import lobby.OptionsController;
import main.AdvancedWarsApplication;
import main.FXMLLoad;
import model.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameLobbyController {

    @FXML
    public AnchorPane base;
    @FXML
    protected ChoiceBox<String> choice;
    protected FXMLLoad messageList;
    @FXML
    protected TextField sendfield;
    FXMLLoad optionsFXML;
    @FXML
    Button manager;
    @FXML
    Button options;
    FXMLLoad armymanagerFXML;
    private Model model;
    @FXML
    private AnchorPane playersBase;
    @FXML
    private Button back;
    @FXML
    private Label gameName;
    @FXML
    private Label playerCounter;
    @FXML
    private Label pl1;
    @FXML
    private Label pl2;
    @FXML
    private Label pl3;
    @FXML
    private Label pl4;
    @FXML
    private Label wait;
    @FXML
    private SplitPane chatSplit;
    @FXML
    private AnchorPane messagebase;
    @FXML
    private Button send;
    @FXML
    private ProgressIndicator wait1;
    @FXML
    private ProgressIndicator wait2;
    @FXML
    private ProgressIndicator wait3;
    @FXML
    private ProgressIndicator wait4;
    @FXML
    private Label dots;
    @FXML
    private ToggleButton ready;
    Callable<Void> gameClientCallable = () -> {
        System.out.println("gameClient (Callable): " + model.getWebSocketComponent().getGameClient());
        boolean result = model.getWebSocketComponent().getGameClient() != null;
        if (result) {
            ready.setDisable(false);
        }
        return null;
    };
    private double pBstartheight;
    private double lsize;
    private double csize;
    private ArrayList<Label> players = new ArrayList<>();
    private ArrayList<ProgressIndicator> indies = new ArrayList<>();
    private double change = 1;
    private Game shownGame = null;
    private Player currentPlayer;
    private PropertyChangeListener gameLobbyChatListener;
    private LobbyChatMessageListController chatController;


    public GameLobbyController(Model model) {
        this.model = model;
    }

    /**
     * load the waitinglobbyScreen and set the actions
     */
    public void initialize() {
        hide();
        /*
         if the user types @ in the sendfield, we want to suggest him the other player names to send them
         private messages
        */
        VBox playerNames = new VBox();
        playerNames.setVisible(false);
        playerNames.setMinHeight(messagebase.getLayoutY());
        playerNames.setMinWidth(messagebase.getLayoutX());
        AnchorPane.setRightAnchor(playerNames, 0d); 
        AnchorPane.setBottomAnchor(playerNames, 0d);
        AnchorPane.setLeftAnchor(playerNames, 0d);

        // add a label for every player in the game to the vBox
        ArrayList<Player> playerArrayList = model.getApp().getCurrentPlayer().getGame().getPlayers();

        for (int i = 0; i < playerArrayList.size(); i++) {

            if (!playerArrayList.get(i).getName().equals(model.getApp().getCurrentPlayer().getName())) {

                Label label = new Label();
                label.setText("@" + playerArrayList.get(i).getName());
                label.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { 
                    public void handle(MouseEvent event) {
                        sendfield.setText(label.getText());
                        sendfield.positionCaret(label.getText().length());
                    }
                });
                playerNames.getChildren().add(label);
            }
        }

        // in case new players join or someone leaves
        model.getApp().getCurrentPlayer().getGame().addPropertyChangeListener(Game.PROPERTY_players, evt -> {
            if (model.getApp().getCurrentPlayer().getGame() != null) {
                ArrayList<Player> playerList = model.getApp().getCurrentPlayer().getGame().getPlayers();
                Platform.runLater(() -> playerNames.getChildren().removeAll(playerNames.getChildren()));
                for (int i = 0; i < playerList.size(); i++) {

                    if (!playerList.get(i).getName().equals(model.getApp().getCurrentPlayer().getName())) {

                        Label label = new Label();
                        label.setText("@" + playerList.get(i).getName());
                        label.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent event) {
                                sendfield.setText(label.getText());
                                sendfield.positionCaret(label.getText().length());
                            }
                        });
                        Platform.runLater(() -> playerNames.getChildren().add(label));
                    }
                }
            }
        });


        sendfield.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {

                if (oldValue == null && newValue.indexOf('@') == 0) {
                    playerNames.setVisible(true);

                } else if (oldValue != null && newValue.indexOf('@') == 0) {

                    StringBuilder builder = new StringBuilder(newValue);

                    // delete the "@" from the string
                    builder.deleteCharAt(0);
                    boolean similar = false;
                    int nameLength = 0;

                    ArrayList<Player> players = model.getApp().getCurrentPlayer().getGame().getPlayers();

                    for (int i = 0; i < players.size(); i++) {

                        if (!players.get(i).getName().equals(model.getApp().getCurrentPlayer().getName())) {

                            if (builder.length() < players.get(i).getName().length() && players.get(i).getName().startsWith(builder.toString())) {

                                similar = true;
                                nameLength = players.get(i).getName().length();

                            } else if (builder.length() == players.get(i).getName().length() && players.get(i).getName().equals(builder.toString())) {

                                similar = true;
                                nameLength = players.get(i).getName().length();

                            } else if (builder.length() > players.get(i).getName().length() &&
                                    builder.substring(0, players.get(i).getName().length()).equals(players.get(i).getName())) {

                                // correct name
                                if (builder.charAt(players.get(i).getName().length()) == ' ') {

                                    similar = true;
                                    nameLength = players.get(i).getName().length();
                                }
                            }
                        }
                    }
                    //cases
                    if (similar) {

                        if (newValue.indexOf('@') == 0 && newValue.length() > oldValue.length() && builder.length() < nameLength) {

                            playerNames.setVisible(true);
                        }
                        //
                        else if (newValue.indexOf('@') == 0 && newValue.length() > oldValue.length() && builder.length() == nameLength) {

                            playerNames.setVisible(false);

                        }
                        // the user adds symbols to his message
                        else if (newValue.indexOf('@') == 0 && newValue.length() > oldValue.length()
                                && builder.charAt(nameLength) == ' ' && builder.length() > nameLength) {

                            playerNames.setVisible(false);
                        }
                        // the user deleted a symbol of his message
                        else if (newValue.indexOf('@') == 0 && newValue.length() <= oldValue.length()
                                && builder.length() > nameLength && builder.charAt(nameLength) == ' ') {

                            playerNames.setVisible(false);
                        }
                        // the user changes a symbol of the player name he wants to enter
                        else if (newValue.indexOf('@') == 0 && newValue.length() < oldValue.length()
                                && builder.length() < nameLength) {

                            playerNames.setVisible(true);
                        }
                    } else if (!similar) {

                        // only the @ sign is in the field
                        if (builder.length() == 0) {
                            playerNames.setVisible(true);
                        } else {

                            ExecutorService executorService = Executors.newSingleThreadExecutor();
                            executorService.execute(() -> {
                                sendfield.setEditable(false);
                                sendfield.setStyle("-fx-background-color: red;");
                                try {
                                    executorService.awaitTermination(1, TimeUnit.SECONDS);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                sendfield.setText("@");
                                sendfield.setEditable(true);
                                Platform.runLater(() -> sendfield.positionCaret(1));
                                sendfield.setStyle("-fx-text-fill: #FFFFFF;" +
                                        "-fx-background-color: rgba(255,255,255,0.1);" +
                                        "-fx-background-radius: 0px;" +
                                        "-fx-border-color: #DEDDFF;" +
                                        "-fx-border-radius: 0px;" +
                                        "-fx-border-width: 1px;");
                                executorService.shutdown();
                            });
                        }
                    }
                } else {
                    playerNames.setVisible(false);
                }
            }
        });

        currentPlayer = model.getApp().getCurrentPlayer();
        players.add(pl1);
        players.add(pl2);
        players.add(pl3);
        players.add(pl4);
        indies.add(wait1);
        indies.add(wait2);
        indies.add(wait3);
        indies.add(wait4);
        back.setOnAction(t -> backClicked());
        pBstartheight = playersBase.getPrefHeight();
        lsize = pl1.getFont().getSize();
        csize = playerCounter.getFont().getSize();
        playersBase.heightProperty().addListener((obs, oldVal, newVal) -> updateChange((double) newVal));
        resizeFont();
        messageList = new FXMLLoad("/lobby/LobbyChatMessageList.fxml", new LobbyChatMessageListController());
        messagebase.getChildren().add(messageList.getParent());
        chatController = messageList.getController(LobbyChatMessageListController.class);
        AnchorPane.setTopAnchor(messageList.getParent(), 0d);
        AnchorPane.setRightAnchor(messageList.getParent(), 0d);
        AnchorPane.setBottomAnchor(messageList.getParent(), 0d);
        AnchorPane.setLeftAnchor(messageList.getParent(), 0d);
        sendfield.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                sendMessage();
            }
        });
        send.setOnAction(t -> sendMessage());
        animateUsingTimeline(dots);
        loadManager();
        manager.setOnAction(t -> showManager());


        shownGame = model.getApp().getCurrentPlayer().getGame();

        //adding property change listeners to all joined players isReady attribute
        currentPlayer.addPropertyChangeListener(Player.PROPERTY_isReady, t -> {
            testFullAndReady(shownGame);
            setPlayersReadyInGUI(shownGame);
        });
        shownGame.addPropertyChangeListener(Game.PROPERTY_players, evt -> {
            Player newPlayer = (Player) evt.getNewValue();
            if (newPlayer != null && !newPlayer.getName().equals(model.getApp().getCurrentPlayer().getName())) {
                newPlayer.addPropertyChangeListener(Player.PROPERTY_isReady, t -> {
                    setPlayersReadyInGUI(shownGame);
                    testFullAndReady(shownGame);
                });
            }
        });

        //inizialize ArmyConfigurations choice box
        initializeArmyConfigChoose();
        choice.setOnAction(t -> chooseArmyConfiguration(choice.getValue()));
        currentPlayer.addPropertyChangeListener(Player.PROPERTY_armyConfigurations, t -> initializeArmyConfigChoose());


        //initialize ready button
        ready.setDisable(true);
        ready.setOnAction(t -> toggleReady(currentPlayer));
        messagebase.getChildren().add(playerNames);

        loadOptions();


        options.setOnAction(t -> optionsFXML.getController(OptionsController.class).show());
    }

	public void setGameLobbyListeners() {

        if (model.getWebSocketComponent().isInGameLobby()) {

			gameLobbyChatListener = new PropertyChangeListener() {
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
                                    Platform.runLater(() -> displayPrivateMsg(message));
                                }
                            }
                        });
                        executor.shutdown();

					} else if (message.getChannel() != null && message.getChannel().equals("all")) {
						
                            if (message.getSender().getName().equals(model.getApp()
                                    .getCurrentPlayer().getName())) {
                                Platform.runLater(() -> model.getWebSocketComponent().sendGameChatMessage(message));
                            } else if (!message.getSender().getName().equals(model.getApp().getCurrentPlayer().getName())) {
                                if (message.getSender().getName().equals("System")) {
                                    Platform.runLater(() -> displayAllChatSysMsg(message));
                                } else {
                                    // display allchat message in gamelobby or in ingamechat.
                                    Platform.runLater(() -> displayAllChatMsg(message));
                                }
                            }
                        }
                    }
            };
            model.getApp().getCurrentPlayer().getGame().addPropertyChangeListener(Game.PROPERTY_ingameMessages, gameLobbyChatListener);
        }
    }

    public void removeGameLobbyListeners() {
        if (gameLobbyChatListener != null) {
            model.getApp().getCurrentPlayer().getGame().removePropertyChangeListener(Game.PROPERTY_ingameMessages, gameLobbyChatListener);
        }
    }

	/**
	 * calculates the new factor for the font sizing
	 *
	 * @param newH
	 *            new height of the anchorpane the playernames are displayed on
	 */
	private void updateChange(double newH) {
		change = newH / pBstartheight;
		resizeFont();
	}

	/**
	 * resize the font to match the new panesize
	 */
	public void resizeFont() {
		players.get(0).setFont(new Font(lsize * change));
		players.get(1).setFont(new Font(lsize * change));
		players.get(2).setFont(new Font(lsize * change));
		players.get(3).setFont(new Font(lsize * change));
		wait.setFont(new Font(lsize * change));
		dots.setFont(new Font(lsize * change));
		playerCounter.setFont(new Font(csize * change));
		wait1.setMaxHeight(lsize * change * 2);
		wait2.setMaxHeight(lsize * change * 2);
		wait3.setMaxHeight(lsize * change * 2);
		wait4.setMaxHeight(lsize * change * 2);
	}

    /**
     * method to leave the waiting
     */
    public void backClicked() {
        model.getWebSocketComponent().leaveGame();
        AdvancedWarsApplication.getInstance().goToLobby();
        hide();
        removeGameLobbyChatListeners();
        model.getApp().getCurrentPlayer().setGame(null);
        AdvancedWarsApplication.getInstance().resetGameScreenFXML();
    }

	/**
	 * shows the waitinglobby
	 */
	public void show() {
		base.setVisible(true);
	}

	/**
	 * hides the waitinglobby
	 */
	public void hide() {
		base.setVisible(false);
	}

	/**
	 * update the screen to match the infos of the game to join
	 *
	 * @param game
	 *            the game to join
	 */
	public void update(Game game) {

		gameName.setText(game.getName());
		if (shownGame != null) {
			shownGame.removePropertyChangeListener(evt -> propUpdate(shownGame));
		}
		shownGame = game;

		shownGame.addPropertyChangeListener(Game.PROPERTY_players, evt -> {
			Platform.runLater(() -> {
				propUpdate(shownGame);
			});
		});

		for (int i = 0; i < players.size(); i++) {
			if (i >= game.getCapacity()) {
				players.get(i).setVisible(false);
				indies.get(i).setVisible(false);
			} else {
				players.get(i).setVisible(true);
				indies.get(i).setVisible(true);
			}
		}
		propUpdate(shownGame);
	}

	/**
	 * called if a player changes its ready attribute
	 *
	 * @param shownGame
	 *            the game displayed at the moment
	 */
	private void testFullAndReady(Game shownGame) {
		System.out.println("test if game can start");
		boolean canStart = true;
		for (Player player : shownGame.getPlayers()) {
			if (!player.getIsReady() && !player.getObserver()) {
				canStart = false;
			}
		}
		ArrayList<Player> playersInGame = shownGame.getPlayers();
		// get real playerCount and filter out observer
		for (int i = 0; i < shownGame.getPlayers().size(); i++) {
			if (playersInGame.get(i).getObserver()) {
				playersInGame.remove(i);
			}
		}
		System.out.println("players got filtered");
		System.out.println(shownGame.getPlayers() + " all players" + playersInGame + " players without observer");
		if (shownGame.getCapacity() == playersInGame.size() && canStart) {
			// go to game
			hide();
			System.out.println("game gets startet with WS");
			joinGame(shownGame);

			// set playerNames in menue
			Platform.runLater(() -> {
				for (int j = 0; j < playersInGame.size(); j++) {
					AdvancedWarsApplication.getInstance().getGameScreenCon().getGameMenuController().getPlayerLabels()
							.get(j).setText(playersInGame.get(j).getName());
				}
			});
			if (!model.getApp().getCurrentPlayer().getName()
					.equals(model.getApp().getCurrentPlayer().getGame().getActivePlayer())) {
				AdvancedWarsApplication.getInstance().getGameScreenCon().disableOnUnActive();
			}
		}
	}


	private void setPlayersReadyInGUI(Game shownGame) {
		// change name color in GUI when player is ready
		for (Player player : shownGame.getPlayers()) {
			if (player.getIsReady()) {
				for (int i = 0; i < players.size(); i++) {
					if (player.getName().equals(players.get(i).getText())) {
						players.get(i).setTextFill(Color.LIGHTGREEN);
						System.out.println(players.get(i).getText() + " Stelle: " + i);
					}
				}
			}
		}
		// TODO: restore old color when player becomes unready again (currently not
		// supported)
	}

	/**
	 * update the screen when a player joins
	 * 
	 * @param game
	 *            the game displayed at the moment
	 */
	private void propUpdate(Game game) {
		gameName.setText(game.getName());
		ArrayList<Player> playersInGame = game.getPlayers();
		int maxPlayers = game.getCapacity();
		// get real playerCount and filter out observer
		for (int i = 0; i < game.getPlayers().size(); i++) {
			if (playersInGame.get(i).getObserver()) {
				playersInGame.remove(i);
			}
		}
		int playerCount = playersInGame.size();
		playerCounter.setText(playerCount + "/" + maxPlayers);
		for (int i = 0; i < players.size(); i++) {
			if (i < game.getCapacity()) {
				if (i >= playerCount) {
					players.get(i).setVisible(false);
					indies.get(i).setVisible(true);
				} else {
					players.get(i).setVisible(true);
					players.get(i).setText(playersInGame.get(i).getName());
					indies.get(i).setVisible(false);

				}
			}
		}
		setPlayersReadyInGUI(game);
	}

    /**
     * join the viewed game if ready
     *
     * @param gameID the game to be displayed and joined
     */
    public void joinGameLobby(String gameID) {
        // join the game lobby
        model.getWebSocketComponent().joinGameLobby(gameID, gameClientCallable);
    }

    /**
     * join the viewed game if ready
     *
     * @param game the game to be displayed and joined
     * @return if joining was successful
     */
    public void joinGame(Game game) {
        // start the game and join the game-Socket
        model.getWebSocketComponent().startGameFromLobby();
        model.getWebSocketComponent().joinGame(game.getGameId(), model.getApp().getCurrentPlayer()
                .getCurrentArmyConfiguration().getId());
        removeGameLobbyChatListeners();
    }

    private void removeGameLobbyChatListeners() {
        model.getApp().getCurrentPlayer().getGame().removePropertyChangeListener(gameLobbyChatListener);
    }

    public void joinGameAsObserver(Game game) {
        model.getWebSocketComponent().joinGameSocketAsObserver(game.getGameId());
    }

    /**
     * method to send messages in the waitinglobby
     */
    public void sendMessage() {

        if (model.getWebSocketComponent().isInGameLobby()) {
            String text = sendfield.getText();
            sendfield.clear();
            if (text.length() > 0) {
                if (text.indexOf('@') == 0) {
                    StringBuilder builder = new StringBuilder(text);

                    // delete the "@" from the string
                    builder.deleteCharAt(0);

                    ArrayList<Player> players = model.getApp().getCurrentPlayer().getGame().getPlayers();

                    for (int i = 0; i < players.size(); i++) {

                        if (!players.get(i).getName().equals(model.getApp().getCurrentPlayer().getName())) {

                            if (builder.length() > players.get(i).getName().length() &&
                                    builder.substring(0, players.get(i).getName().length()).equals(players.get(i).getName())) {

                                // correct name
                                if (builder.charAt(players.get(i).getName().length()) == ' ') {
                                    String privateMessage = builder.substring(players.get(i).getName().length(), builder.length());
                                    ChatMessage message = new ChatMessage().setChannel("private").setMessage(privateMessage)
                                            .setSender(model.getApp().getCurrentPlayer())
                                            .setReceiver(players.get(i));
                                    message.setDate(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
                                    displayPrivateMsg(message);
                                    model.getApp().getCurrentPlayer().getGame().withIngameMessages(message);
                                }
                            }
                        }
                    }
                } else {
                    ChatMessage message = new ChatMessage().setChannel("all").setMessage(text)
                            .setSender(model.getApp().getCurrentPlayer());
                    message.setDate(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
                    model.getApp().getCurrentPlayer().getGame().withIngameMessages(message);
                    displayAllChatMsg(message);
                }
            }
        }
    }

	private void displayPrivateMsg(ChatMessage message) {

        if (model.getWebSocketComponent().isInGameLobby()) {

            if (!message.getReceiver().equals(model.getApp().getCurrentPlayer().getName())) {
                chatController.displayMessage("[" + message.getDate() + "] " + "["
                        + "to:" + message.getReceiver().getName() + "] " + message.getMessage());
            } else {
                chatController.displayMessage("[" + message.getDate() + "] " + "["
                        + "from:" + message.getSender().getName() + "] "
                        + message.getMessage());
            }
        }
    }

    public void displayAllChatMsg(ChatMessage message) {

        if (model.getWebSocketComponent().isInGameLobby()) {
            chatController.displayMessage("[" + message.getDate() + "] " + "["
                    + message.getSender().getName() + "] " + message.getMessage());
        }
    }


    public void displayAllChatSysMsg(ChatMessage message) {
        if (model.getWebSocketComponent().isInGameLobby()) {
            chatController.displayMessageSystem("[" + message.getDate() + "] " + "["
                    + message.getSender().getName() + "] " + message.getMessage());
        }
    }


    /**
     * animates the waiting label
     *
     * @param wfplayers the label displaying the waiting message
     */
    private void animateUsingTimeline(Label wfplayers) {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = null;
        ResourceBundle bundletmp = null;
        try {
            inputStream = classLoader.getResource("en-US.properties").openStream();
            bundletmp = new PropertyResourceBundle(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final ResourceBundle bundle = bundletmp;

        Timeline beat = new Timeline(
                new KeyFrame(Duration.ZERO, event -> wfplayers.setText(bundle.getString("gamelobby.wait1"))),
                new KeyFrame(Duration.seconds(0.5), event -> wfplayers.setText(bundle.getString("gamelobby.wait2"))),
                new KeyFrame(Duration.seconds(1), event -> wfplayers.setText(bundle.getString("gamelobby.wait3"))),
                new KeyFrame(Duration.seconds(1.5), event -> wfplayers.setText(bundle.getString("gamelobby.wait4")))
        );
        beat.setAutoReverse(true);
        beat.setCycleCount(Timeline.INDEFINITE);
        beat.play();
    }

    /**
     * loads the ArmyManager
     */
    public void loadManager() {
        if (AdvancedWarsApplication.getInstance() != null && !AdvancedWarsApplication.offtesting) {
            armymanagerFXML = AdvancedWarsApplication.getInstance().getLobbyCon().armymanagerFXML;
            base.getChildren().add(armymanagerFXML.getParent());
        } else {
            armymanagerFXML = new FXMLLoad("/armyManager/ArmyManager.fxml", "en-US.properties", new ArmyManagerController(model));
            base.getChildren().add(armymanagerFXML.getParent());
            AnchorPane.setRightAnchor(armymanagerFXML.getParent(), 0d);
            AnchorPane.setBottomAnchor(armymanagerFXML.getParent(), 0d);
            AnchorPane.setLeftAnchor(armymanagerFXML.getParent(), 0d);
            AnchorPane.setTopAnchor(armymanagerFXML.getParent(), 0d);
            armymanagerFXML.getController(ArmyManagerController.class).hide();
        }
    }

	/**
	 * displays the Armymanager
	 */
	public void showManager() {
		armymanagerFXML.getController(ArmyManagerController.class).show();
	}

	/**
	 * Method that sets the currentArmyConfiguration attribute of the Player
	 *
	 * @param selectedConfigName
	 *            name of the config the user selected
	 */
	public void chooseArmyConfiguration(String selectedConfigName) {
		for (ArmyConfiguration armyConfiguration : currentPlayer.getArmyConfigurations()) {
			if (armyConfiguration.getName().equals(selectedConfigName)) {
				currentPlayer.setCurrentArmyConfiguration(armyConfiguration);
				System.out.println("Selected Configuration: " + currentPlayer.getCurrentArmyConfiguration().getName());
				// ready.selectedProperty().setValue(false); TODO uncomment if toggle ready is
				// possible on server
				break;
			}
		}
	}

	/**
     * method to add all armyConfigs to the ConfigChooseList and select the first
     */
    public void initializeArmyConfigChoose() {
        choice.getItems().remove(0, choice.getItems().size());
        for (ArmyConfiguration armyConfiguration : (currentPlayer.getArmyConfigurations())) {
            choice.getItems().add(armyConfiguration.getName());
        }
        choice.getSelectionModel().selectFirst();
        chooseArmyConfiguration(choice.getValue());
    }

    /**
     * method to set the player ready when the ready button is clicked
     *
     * @param player the current player
     */
    public void toggleReady(Player player) {
        if (!player.getIsReady()) {
            //TODO changes when toggle ready is possible on server
            //player.setIsReady(ready.selectedProperty().getValue());

            model.getWebSocketComponent().selectArmyInGameLobby(currentPlayer.getCurrentArmyConfiguration().getId());
            model.getWebSocketComponent().playerIsReadyForGame();
            this.choice.setDisable(true);
            player.setIsReady(true);
        }
    }

    public boolean isOpen() {
        return base.isVisible();
    }

    /**
     * this methode hides / disables all useable elements for the observer
     */
    public void observerModeGameLobby() {
        chatSplit.setDividerPositions(1);
        sendfield.setVisible(false);
        ready.setVisible(false);
        manager.setVisible(false);
        send.setVisible(false);
        choice.setVisible(false);
        AdvancedWarsApplication.getInstance().getGameScreenCon().getMenuFXML().getController(MenuDisplayController.class).aiTakeover.setVisible(false);
    }

    public TextField getSendField() {
        return sendfield;
    }


    /**
     * this methode loads the Optionsgui for the gamelobby
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
