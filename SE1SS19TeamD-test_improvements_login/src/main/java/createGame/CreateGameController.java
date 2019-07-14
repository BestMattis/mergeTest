package createGame;

import gameList.GameListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import main.AdvancedWarsApplication;
import model.Game;
import model.Model;
import syncCommunication.HttpRequests;
import syncCommunication.RESTExceptions.GameIdNotFoundException;
import syncCommunication.RESTExceptions.GameLobbyCreationFailedException;
import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.SynchronousGameCommunicator;

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
    private Parent parent;

    /**
     * Sets ActionHandler on Buttons.
     */
    @FXML
    public void initialize() {
        cancelButton.setOnAction(evt -> cancelAction(evt));
        createButton.setOnAction(evt -> createAction(evt));
    }

    /**
     * Creates new Game when input is valid.
     *
     * @param evt
     */
    private void createAction(ActionEvent evt) {
        if (nameField.getText().equals("") && playerNumberField.getText().equals("")) {
            messageField.setText("choose a name and number of players!");
            return;
        } else if (nameField.getText().equals("")) {
            messageField.setText("choose a name!");
            return;
        } else if (playerNumberField.getText().equals("")) {
            messageField.setText("choose number of players!");
            return;
        }

        String gameName = nameField.getText();
        try {
            int playerNumber = Integer.parseInt(playerNumberField.getText());
            if (playerNumber != 2 && playerNumber != 4) {
                messageField.setText(playerNumber + " number must be 2 or 4");
                return;
            }
            for (Game game : Model.getApp().getAllGames()) {
                if (game.getName().equals(gameName)) {
                    messageField.setText("The Name " + gameName + " is already taken!");
                    return;
                }
            }
            // create the game on the server
            HttpRequests httpReq = Model.getPlayerHttpRequestsHashMap().get(
                    Model.getApp().getCurrentPlayer());
            SynchronousGameCommunicator gameComm = new SynchronousGameCommunicator(httpReq);
            String gameID = null;
            boolean successfull = false;
            try {
               gameID = gameComm.openGame(gameName, playerNumber);
            } catch (GameLobbyCreationFailedException | LoginFailedException e) {
                e.printStackTrace();
            }

            try {
                successfull = gameComm.joinGame(gameID);
            } catch (GameIdNotFoundException | LoginFailedException e) {
                e.printStackTrace();
            }

            Game game = null;
            if (successfull) {
                for (int i = 0; i < 10; ++i) {
                    game = GameListener.getGameByID(gameID);
                    if (game != null) {
                        break;
                    }
                    Thread.sleep(1);
                }
            }

            if (game == null) {
                System.out.println("Game was not found in time");
                return;
            }

            game.withPlayers(Model.getApp().getCurrentPlayer());

            Model.getWebSocketComponent().joinGameLobby(gameID);


            System.out.println("Game: " + gameName + " was created. Maximal amount of Players: " + playerNumber);

            cancelAction(evt);

            //showGameLobby();
            AdvancedWarsApplication.getInstance().goToGame(game);
            AdvancedWarsApplication.getInstance().getGameScreenCon().getGameLobbyController().update(game);
            AdvancedWarsApplication.getInstance().getGameScreenCon().getGameLobbyController().show();


        } catch (NumberFormatException | InterruptedException e) {
            messageField.setText(playerNumberField.getText() + " is not an integer!");
        }
    }

    /**
     * Loads the standard LobbyScreen.
     *
     * @param evt
     */
    private void cancelAction(ActionEvent evt) {
        //Node node = base.getChildren().get(0);
        //Node node1 = base.getChildren().get(1);
        //Node node2 = base.getChildren().get(2);
        //base.getChildren().clear();
        //base.getChildren().addAll(node, node1, node2);
        base.getChildren().remove(parent);
    }

    public void setBase(AnchorPane base) {
        this.base = base;
    }

    public void setParent(Parent parent1){
        parent = parent1;
    }
}
