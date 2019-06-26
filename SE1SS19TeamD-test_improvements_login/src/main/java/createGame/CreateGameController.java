package createGame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import main.AdvancedWarsApplication;
import model.Game;
import model.Model;

public class CreateGameController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField playerNumberField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button createButton;

    @FXML
    private Label messageField;
    private AnchorPane base;

    /**
     * Sets ActionHandler on Buttons.
     */
    @FXML
    public void initialize(){
        cancelButton.setOnAction(evt -> cancelAction(evt));
        createButton.setOnAction(evt -> createAction(evt));
    }

    /**
     * Creates new Game when input is valid.
     *
     * @param evt
     */
    private void createAction(ActionEvent evt) {
        if(nameField.getText().equals("") && playerNumberField.getText().equals("")){
            messageField.setText("choose a name and number of players!");
            return;
        } else if(nameField.getText().equals("")){
            messageField.setText("choose a name!");
            return;
        } else if(playerNumberField.getText().equals("")){
            messageField.setText("choose number of players!");
            return;
        }

        String gameName = nameField.getText();
        try{
            int playerNumber = Integer.parseInt(playerNumberField.getText());
            if(playerNumber!=2 && playerNumber!=4){
                messageField.setText(playerNumber+" number must be 2 or 4");
                return;
            }
            for(Game game: Model.getApp().getAllGames()){
                if(game.getName().equals(gameName)){
                    messageField.setText("The Name "+gameName+" is already taken!");
                    return;
                }
            }
            Game game = new Game();
            Model.getApp().withAllGames(game.setCapacity(playerNumber).setName(gameName)
                    .withPlayers(Model.getApp().getCurrentPlayer()));
            System.out.println("Game: "+gameName+" was created! Maximal amount of Players: "+playerNumber);

            //showGameLobby();
            AdvancedWarsApplication.getInstance().goToGame(game);
            AdvancedWarsApplication.getInstance().getGameScreenCon().getWaitingScreenContoller().update(game);
            AdvancedWarsApplication.getInstance().getGameScreenCon().getWaitingScreenContoller().show();

            cancelAction(evt);

        } catch (NumberFormatException e) {
            messageField.setText(playerNumberField.getText()+" is not an integer!");
        }
    }

    /**
     * Loads the standard LobbyScreen.
     *
     * @param evt
     */
    private void cancelAction(ActionEvent evt) {
        Node node = base.getChildren().get(0);
        Node node1 = base.getChildren().get(1);
        base.getChildren().clear();
        base.getChildren().addAll(node, node1);
    }

    public void setBase(AnchorPane base) {
        this.base = base;
    }
}
