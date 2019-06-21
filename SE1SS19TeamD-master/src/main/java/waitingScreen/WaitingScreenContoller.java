package waitingScreen;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import main.AdvancedWarsApplication;
import model.Game;
import model.Player;
import syncCommunication.RESTExceptions.GameIdNotFoundException;
import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.SynchronousGameCommunicator;

import java.beans.PropertyChangeListener;


public class WaitingScreenContoller {

    @FXML
    private AnchorPane base;
    @FXML
    private Button back;
    @FXML
    private Label gameName;
    @FXML
    private Label playerCounter;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private VBox playerBox;

    private Game shownGame = null;

    public void initialize(){
        hide();
        back.setOnAction(t -> backClicked());
    }

    public void backClicked(){
        AdvancedWarsApplication.getInstance().goToLobby();
        hide();
    }

    public void show(){
        base.setVisible(true);
    }

    public void hide(){
        base.setVisible(false);
    }

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
        propUpdate(shownGame);

    }

    private void testFull(Game shownGame) {
        if (shownGame.getCapacity() == shownGame.getPlayers().size()){
            //go to game
        }
    }

    private void propUpdate(Game game) {
        gameName.setText(game.getName());
        int playerCount = game.getPlayers().size();
        int maxPlayers = game.getCapacity();
        playerCounter.setText(playerCount+"/"+maxPlayers);
        progressBar.setProgress((double)playerCount/maxPlayers);
        playerBox.getChildren().removeAll();
        for (Player player: game.getPlayers()){
            Label label = new Label();
            label.getStyleClass().add("standart-text");
            label.setText(player.getName());
            playerBox.getChildren().add(label);
        }
    }

    public boolean joinGame(Game game){
        boolean success = false;

        SynchronousGameCommunicator synchronousGameCommunicator = new SynchronousGameCommunicator(AdvancedWarsApplication.getInstance().getHttpRequests());
        try {
            synchronousGameCommunicator.joinGame(game.getGameId());
        } catch (GameIdNotFoundException | LoginFailedException e) {
            e.printStackTrace();
        }

        return success;
    }

}
