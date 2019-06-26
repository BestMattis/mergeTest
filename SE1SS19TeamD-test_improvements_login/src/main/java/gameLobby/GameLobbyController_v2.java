package gameLobby;

import armyManager.ArmyManagerController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import lobby.LobbyChatMessageListController;
import main.AdvancedWarsApplication;
import main.FXMLLoad;
import model.Game;
import model.Model;
import syncCommunication.RESTExceptions.GameIdNotFoundException;
import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.SynchronousGameCommunicator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class GameLobbyController_v2 {

    @FXML
    private AnchorPane base;
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


    FXMLLoad armymanagerFXML;

    private double pBstartheight;
    private double lsize;
    private double csize;
    private ArrayList<Label> players = new ArrayList<>();
    private ArrayList<ProgressIndicator> indies = new ArrayList<>();
    private double change = 1;

    private Game shownGame = null;

    private FXMLLoad messageList;

    /**
     * load the waitinglobbyScreen and set the actions
     */
    public void initialize(){
        hide();
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
        sendfield.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    sendMessage();
                }
            }
        });;
        send.setOnAction(t -> sendMessage());
        animateUsingTimeline(dots);
        loadManager();
        manager.setOnAction(t -> showManager());
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
        shownGame.addPropertyChangeListener(evt -> {
            propUpdate(shownGame);
            testFull(shownGame);
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

    /**called if the game has all needed players
     * @param shownGame the game displayed at the moment
     */
    private void testFull(Game shownGame) {
        if (shownGame.getCapacity() == shownGame.getPlayers().size()){
            //go to game
        }
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

    }

    /** join the viewd game if ready
     * @param game the game to be displayed and joined
     * @return if joining was successful
     */
    public boolean joinGame(Game game){
        boolean success = false;

        SynchronousGameCommunicator synchronousGameCommunicator = new SynchronousGameCommunicator(AdvancedWarsApplication.getInstance().getHttpRequests());
        try {
            synchronousGameCommunicator.joinGame(game.getGameId());
        } catch (GameIdNotFoundException e) {
            e.printStackTrace();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }

        return success;
    }

    /**
     * method to send messages in the waitinglobby
     */
    public void sendMessage(){
        String text = sendfield.getText();
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
        armymanagerFXML = new FXMLLoad("/armyManager/ArmyManager.fxml", new ArmyManagerController());
        base.getChildren().add(armymanagerFXML.getParent());
        AnchorPane.setRightAnchor(armymanagerFXML.getParent(), 0d);
        AnchorPane.setBottomAnchor(armymanagerFXML.getParent(), 0d);
        AnchorPane.setLeftAnchor(armymanagerFXML.getParent(), 0d);
        AnchorPane.setTopAnchor(armymanagerFXML.getParent(), 0d);
    }

    /**
     * displays the Armymanager
     */
    public void showManager(){
        armymanagerFXML.getController(ArmyManagerController.class).show();
    }

}
