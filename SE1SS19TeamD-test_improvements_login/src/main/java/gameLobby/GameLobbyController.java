package gameLobby;

import armyManager.ArmyManagerController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import lobby.LobbyChatMessageListController;
import main.AdvancedWarsApplication;
import main.FXMLLoad;
import model.*;
import syncCommunication.RESTExceptions.GameIdNotFoundException;
import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.SynchronousGameCommunicator;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class GameLobbyController {

    @FXML
    public AnchorPane base;
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
    private AnchorPane messagebase;
    @FXML
    private TextField sendfield;
    @FXML
    private Button send;
    @FXML
    protected ChoiceBox<String> choice;
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
    Button manager;
    @FXML
    Button options;
	@FXML
    private ToggleButton ready;


    FXMLLoad armymanagerFXML;

    private double pBstartheight;
    private double lsize;
    private double csize;
    private ArrayList<Label> players = new ArrayList<>();
    private ArrayList<ProgressIndicator> indies = new ArrayList<>();
    private double change = 1;

    private Game shownGame = null;

    private FXMLLoad messageList;
    private Player currentPlayer;

    /**
     * load the waitinglobbyScreen and set the actions
     */
    public void initialize(){
        hide();
        currentPlayer = Model.getApp().getCurrentPlayer();
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
        System.out.println(lsize);
        System.out.println(pBstartheight);
        playersBase.heightProperty().addListener((obs, oldVal, newVal) -> updateChange((double)newVal));
        resizeFont();
        messageList = new FXMLLoad("/lobby/LobbyChatMessageList.fxml", new LobbyChatMessageListController());
        messagebase.getChildren().add(messageList.getParent());
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

        //remove later:
        options.setOnAction(t -> hide());
        
        shownGame = Model.getApp().getCurrentPlayer().getGame();

        //adding property change listeners to all joined players isReady attribute
        currentPlayer.addPropertyChangeListener(Player.PROPERTY_isReady, t -> {
            testFullAndReady(shownGame);
            setPlayersReadyInGUI(shownGame);
        });
        shownGame.addPropertyChangeListener(Game.PROPERTY_players, evt -> {
            Player newPlayer = (Player) evt.getNewValue();
            if (newPlayer != null && !newPlayer.getName().equals(Model.getApp().getCurrentPlayer().getName())) {
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
        ready.setOnAction(t -> toggleReady(currentPlayer));
    }

    /** calculates the new factor for the font sizing
     * @param newH new height of the anchorpane the playernames are displayed on
     */
    private void updateChange(double newH) {
        change = newH/pBstartheight;
        resizeFont();
    }


    /**
     * resize the font to match the new panesize
     */
    public void resizeFont(){
        players.get(0).setFont(new Font(lsize*change));
        players.get(1).setFont(new Font(lsize*change));
        players.get(2).setFont(new Font(lsize*change));
        players.get(3).setFont(new Font(lsize*change));
        wait.setFont(new Font(lsize*change));
        dots.setFont(new Font(lsize*change));
        playerCounter.setFont(new Font(csize*change));
        wait1.setMaxHeight(lsize*change*2);
        wait2.setMaxHeight(lsize*change*2);
        wait3.setMaxHeight(lsize*change*2);
        wait4.setMaxHeight(lsize*change*2);
    }

    /**
     * method to leave the waiting
     */
    public void backClicked(){
        Model.getWebSocketComponent().leaveGame(); //TODO: beim leaven aus DM austragen, falls nicht currentPlayer
        AdvancedWarsApplication.getInstance().goToLobby();
        hide();
        Model.getApp().getCurrentPlayer().setGame(null);
    }

    /**
     * shows the waitinglobby
     */
    public void show(){
        base.setVisible(true);
    }

    /**
     * hides the waitinglobby
     */
    public void hide(){
        base.setVisible(false);
    }

    /** update the screen to match the infos of the game to join
     * @param game the game to join
     */
    public void update(Game game){
        gameName.setText(game.getName());
        if (shownGame != null){
            shownGame.removePropertyChangeListener(evt -> propUpdate(shownGame));
        }
        shownGame = game;

        shownGame.addPropertyChangeListener(Game.PROPERTY_players, evt -> {
            Platform.runLater(() -> {
                propUpdate(shownGame);
            });
        });

        for (int i = 0; i < players.size(); i++){
            if (i >= game.getCapacity()){
                players.get(i).setVisible(false);
                indies.get(i).setVisible(false);
            } else {
                players.get(i).setVisible(true);
                indies.get(i).setVisible(true);
            }
        }
        propUpdate(shownGame);
    }

    /**called if a player changes its ready attribute
     * @param shownGame the game displayed at the moment
     */
    private void testFullAndReady(Game shownGame) {
        System.out.println("test if game can start");
        boolean canStart = true;
        for(Player player:shownGame.getPlayers()){
            if(!player.getIsReady()){
                canStart = false;
            }
        }
        if (shownGame.getCapacity() == shownGame.getPlayers().size() && canStart) {
            //go to game
            hide();
            joinGame(shownGame);
        }
    }

    private void setPlayersReadyInGUI(Game shownGame){
        //change name color in GUI when player is ready
        for(Player player : shownGame.getPlayers()) {
            if(player.getIsReady()) {
                for (int i = 0; i < players.size(); i++) {
                    if (player.getName().equals(players.get(i).getText())) {
                        players.get(i).setTextFill(Color.LIGHTGREEN);
                        System.out.println(players.get(i).getText() + " Stelle: " + i);
                    }
                }
            }
        }
        // TODO: restore old color when player becomes unready again (currently not supported)
    }

    /**update the screen when a player joins
     * @param game the game displayed at the moment
     */
    private void propUpdate(Game game) {
        gameName.setText(game.getName());
        int playerCount = game.getPlayers().size();
        int maxPlayers = game.getCapacity();
        playerCounter.setText(playerCount+"/"+maxPlayers);
        for (int i = 0; i < players.size(); i++){
            if (i < game.getCapacity()){

                if(i >= game.getPlayers().size()){
                    players.get(i).setText("...");
                    players.get(i).setVisible(false);
                    indies.get(i).setVisible(true);
                } else {
                    players.get(i).setVisible(true);
                    players.get(i).setText(game.getPlayers().get(i).getName());
                    indies.get(i).setVisible(false);
                }
            }
        }
        setPlayersReadyInGUI(game);
    }

    /** join the viewed game if ready
     * @param game the game to be displayed and joined
     * @return if joining was successful
     */
    public boolean joinGameLobby(Game game){
        boolean success = false;

        SynchronousGameCommunicator synchronousGameCommunicator = new SynchronousGameCommunicator(AdvancedWarsApplication.getInstance().getHttpRequests());
        try {
            success = synchronousGameCommunicator.joinGame(game.getGameId());
        } catch (GameIdNotFoundException e) {
            e.printStackTrace();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }
        // join the game lobby
        Model.getWebSocketComponent().joinGameLobby(game.getGameId());


        return success;
    }

    /** join the viewed game if ready
     * @param game the game to be displayed and joined
     * @return if joining was successful
     */
    public void joinGame(Game game){
        // start the game and join the game-Socket
        Model.getWebSocketComponent().startGameFromLobby();
        Model.getWebSocketComponent().joinGame(game.getGameId(), Model.getApp().getCurrentPlayer()
                .getCurrentArmyConfiguration().getId());
    }

    /**
     * method to send messages in the waitinglobby
     */
    public void sendMessage(){
        String text = sendfield.getText();
        sendfield.clear();
        if (text.length() > 0) {
            ChatMessage message = new ChatMessage().setChannel("all").setMessage(text)
                    .setSender(Model.getApp().getCurrentPlayer());
            message.setDate(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
            Model.getApp().getCurrentPlayer().getGame().withIngameMessages(message);
            displayMessage(message);
        }
    }

    public void displayMessage(ChatMessage message) {
        messagebase.getChildren().add(new Label("[" + message.getDate() + "] " + "["
                + message.getSender() + "] " + message.getMessage()));

    }


    /** animates the waiting label
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
    public void loadManager(){
        if (AdvancedWarsApplication.getInstance() != null && !AdvancedWarsApplication.getInstance().offtesting) {
            armymanagerFXML = AdvancedWarsApplication.getInstance().getLobbyCon().armymanagerFXML;
            base.getChildren().add(armymanagerFXML.getParent());
        } else {
            armymanagerFXML = new FXMLLoad("/armyManager/ArmyManager.fxml", "en-US.properties", new ArmyManagerController());
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
    public void showManager(){
        armymanagerFXML.getController(ArmyManagerController.class).show();
    }

    /**
     * Method that sets the currentArmyConfiguration attribute of the Player
     * @param selectedConfigName name of the config the user selected
     */
    public void chooseArmyConfiguration(String selectedConfigName) {
        for(ArmyConfiguration armyConfiguration: currentPlayer.getArmyConfigurations()){
            if(armyConfiguration.getName().equals(selectedConfigName)){
                currentPlayer.setCurrentArmyConfiguration(armyConfiguration);
                System.out.println("Selected Configuration: " + currentPlayer.getCurrentArmyConfiguration().getName());
                //ready.selectedProperty().setValue(false); TODO uncomment if toggle ready is possible on server
                break;
            }
        }
    }

    /**
     * method to add all armyConfigs to the ConfigChooseList and select the first
     */
    public void initializeArmyConfigChoose() {
        choice.getItems().remove(0,choice.getItems().size());
        for(ArmyConfiguration armyConfiguration:(currentPlayer.getArmyConfigurations())) {
            choice.getItems().add(armyConfiguration.getName());
        }
        choice.getSelectionModel().selectFirst();
        chooseArmyConfiguration(choice.getValue());
    }

    /**
     * method to set the player ready when the ready button is clicked
     * @param player the current player
     */
    public void toggleReady(Player player) {
        if(!player.getIsReady()) { //TODO changes when toggle ready is possible on server
            //player.setIsReady(ready.selectedProperty().getValue());
            player.setIsReady(true);
            Model.getWebSocketComponent().selectArmyInGameLobby(currentPlayer.getCurrentArmyConfiguration().getId());
            Model.getWebSocketComponent().playerIsReadyForGame();
        }
    }

    public boolean isOpen() {
        if(base.isVisible()) {
            return true;
        }
        return false;
    }
}