package playerList;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import main.AdvancedWarsApplication;
import model.App;
import model.Model;
import model.Player;
import syncCommunication.HttpRequests;
import syncCommunication.SynchronousUserCommunicator;

import java.util.ArrayList;
import java.util.List;


public class PlayerListController {

    private int playerCount = 0;

    @FXML
    private ListView<String> playerList;
    @FXML
    private Label numberOfPlayers;

    /**
     * Adds Listener on App from Model and calls update().
     */
    @FXML
    public void initialize() {
        HttpRequests httpReq = Model.getPlayerHttpRequestsHashMap()
                .get(Model.getApp().getCurrentPlayer());
        SynchronousUserCommunicator userComm = new SynchronousUserCommunicator(httpReq);

        try {
            for(String s : userComm.getOnlineUsers()) {
                if(!s.equals(Model.getApp().getCurrentPlayer().getName())) {
                    new Player().setName(s).setApp(Model.getApp());
                }
            }
        } finally{
        	
        }
        
        Model.getApp().addPropertyChangeListener(App.PROPERTY_allPlayers, e -> {
            if (e.getNewValue() != null) {
                // new Player logged on
                Platform.runLater(() ->
                {
                    playerList.getItems().addAll(((Player)e.getNewValue()).getName());
                    numberOfPlayers.setText("" + (++playerCount));
                });
            } else {
                // Player logged off
                Platform.runLater(() ->
                {
                    playerList.getItems().removeAll(((Player) e.getOldValue()).getName());
                    numberOfPlayers.setText("" + (--playerCount));
                });
            }
        });
        update();
        playerList.setOnMouseClicked(t -> openPlayerChat());
    }

    private void openPlayerChat() {
        String selectedName = playerList.getSelectionModel().getSelectedItem();
        playerList.getSelectionModel().clearSelection();
        Player selectedPlayer = null;
        for (Player player : Model.getApp().getAllPlayers()) {
            if (player.getName().equals(selectedName)) {
                selectedPlayer = player;
                break;
            }
        }
        if (selectedPlayer == Model.getApp().getCurrentPlayer()) {
            return;
        }
        AdvancedWarsApplication.getInstance().getLobbyCon()
                .getChatCon().getSingleController().newTab(selectedPlayer);

    }

    /**
     * Clears list and adds allPlayers in app.
     */
    private void update() {
        Platform.runLater(() -> playerList.getItems().clear());

        List<String> names = new ArrayList<>();
        for (Player player : Model.getApp().getAllPlayers()) {
            names.add(player.getName());
        }
        Platform.runLater(() ->
        {
            playerList.getItems().addAll(names);
            playerCount = Model.getApp().getAllPlayers().size();
            numberOfPlayers.setText("" + playerCount);
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

