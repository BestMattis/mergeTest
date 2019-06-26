package gameScreen;

import gameLobby.GameLobbyController_v2;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import main.FXMLLoad;

public class GameScreenController {

    @FXML
    AnchorPane base;

    private FXMLLoad gameLobbyFXML;

    @FXML
    public void initialize() {
        System.out.println("gameScreenLoaded");
        loadWaiting();
    }

    private void loadWaiting(){
        gameLobbyFXML = new FXMLLoad("/gameLobby/GameLobbyScreen_v2.fxml", new GameLobbyController_v2());
        AnchorPane.setTopAnchor(gameLobbyFXML.getParent(), 0d);
        AnchorPane.setRightAnchor(gameLobbyFXML.getParent(), 0d);
        AnchorPane.setBottomAnchor(gameLobbyFXML.getParent(), 0d);
        AnchorPane.setLeftAnchor(gameLobbyFXML.getParent(), 0d);
        base.getChildren().add(gameLobbyFXML.getParent());
    }

    /**
     * @return the controller of the waiting Screen
     */
    public GameLobbyController_v2 getGameLobbyController() {
        return gameLobbyFXML.getController(GameLobbyController_v2.class);
    }
}
