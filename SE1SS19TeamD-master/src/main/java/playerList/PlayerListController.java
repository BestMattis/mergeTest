package playerList;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import main.AdvancedWarsApplication;
import model.Model;
import model.Player;
import syncCommunication.HttpRequests;
import syncCommunication.SynchronousUserCommunicator;


public class PlayerListController {

    @FXML
    private ListView<String> playerList;
    @FXML
    private Label numberOfPlayers;

    /**
     * Adds Listener on App from Model and calls update().
     */
    @FXML
    public void initialize() {
        HttpRequests httpReq = Model.getPlayerHttpRequestsHashMap().get(
                Model.getApp().getCurrentPlayer());
        SynchronousUserCommunicator userComm = 
                new SynchronousUserCommunicator(httpReq);
        for(String s : userComm.getOnlineUsers())
        {
            new Player().setApp(Model.getApp()).setName(s);
        }
        Model.getApp().addPropertyChangeListener(evt -> update());
        update();
        playerList.setOnMouseClicked(t -> openPlayerChat());
    }

    private void openPlayerChat() {
        String selectedName = playerList.getSelectionModel().getSelectedItem();
        playerList.getSelectionModel().clearSelection();
        Player selectedPlayer = null;
        for (Player player:Model.getApp().getAllPlayers()){
            if (player.getName() == selectedName){
                selectedPlayer = player;
            }
        }
        if (selectedPlayer == Model.getApp().getCurrentPlayer()){
            return;
        }
        AdvancedWarsApplication.getInstance().getLobbyCon().getChatCon().getSingleController().newTab(selectedPlayer);

    }

    /**
     * Clears list and adds allPlayers in app.
     */
    public void update() {
        Platform.runLater(() ->
        {
            playerList.getItems().clear();
        });
        List<String> names = new ArrayList<>();
        for (Player player : Model.getApp().getAllPlayers()) {
            names.add(player.getName());
        }
        Platform.runLater(() ->
        {
            playerList.getItems().addAll(names);
            numberOfPlayers.setText("" + Model.getApp().getAllPlayers().size()); 
        });
    }

    /**
     * For Tests.
     *
     * @return numberOfPlayers
     */
    public Label getNumberOfPlayers() {
        return numberOfPlayers;
    }
}

