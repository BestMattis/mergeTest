package gameLobby;

import javafx.application.Application;
import javafx.stage.Stage;
import main.FXMLLoad;

public class TestApp extends Application {

    private FXMLLoad lobbyFxml;

    public void start(Stage stage){
        lobbyFxml = new FXMLLoad("/gameLobby/GameLobbyScreen_v2.fxml", new GameLobbyController_v2());
        stage.setScene(lobbyFxml.getScene());
        stage.setFullScreen(true);
        stage.show();
        lobbyFxml.getController(GameLobbyController_v2.class).show();
    }
}
