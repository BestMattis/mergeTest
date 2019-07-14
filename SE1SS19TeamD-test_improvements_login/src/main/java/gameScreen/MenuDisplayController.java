package gameScreen;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import main.AdvancedWarsApplication;
import model.Game;
import model.Model;
import model.Player;

import java.util.ArrayList;

public class MenuDisplayController {

    @FXML
    Button leave;
    @FXML
    Button finishRound;
    @FXML
    Button chatbutton;
    @FXML
    Label player1;
    @FXML
    Label player2;
    @FXML
    Label player3;

    private GameScreenController par;

    public MenuDisplayController(GameScreenController p){
        par = p;
    }


    /**
     * add the action for the leave-button and the button to open the chat
     */
    public void initialize(){
        leave.setOnAction(t -> leaveGame());
        chatbutton.setOnAction(t -> showChat());
    }

    /**
     * display the chat
     */
    private void showChat() {
        par.chatFXML.getController(GameChatController.class).animateUsingTimeline();
    }

    /**
     * set the screen to the lobby
     */
    public void leaveGame(){
        Model.getWebSocketComponent().leaveGame();
        AdvancedWarsApplication.getInstance().goToLobby();
    }

    /**
     * displays the names of the other players in the menu
     */
    public void setPlayers(){
        Game cgame = Model.getApp().getCurrentPlayer().getGame();
        ArrayList<Player> players = (ArrayList<Player>) cgame.getPlayers().clone();
        players.remove(Model.getApp().getCurrentPlayer());
        if (players.size()>0){
            player1.setText(players.get(0).getName());
            player1.setVisible(true);
        }
        if (players.size()>1){
            player2.setText(players.get(1).getName());
            player2.setVisible(true);
        }
        if (players.size()>2){
            player3.setText(players.get(2).getName());
            player3.setVisible(true);
        } else {
            player3.setText("You could invite more friends...");
            player3.setVisible(true);
        }
    }

}
