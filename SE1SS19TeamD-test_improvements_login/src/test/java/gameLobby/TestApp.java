package gameLobby;

import javafx.application.Application;
import javafx.stage.Stage;
import main.FXMLLoad;
import model.Model;

public class TestApp extends Application {

    private FXMLLoad lobbyFxml;

    public void start(Stage stage) {
        lobbyFxml = new FXMLLoad("/gameLobby/GameLobbyScreen.fxml", new GameLobbyController(new Model()));
        stage.setScene(lobbyFxml.getScene());
        stage.setFullScreen(true);
        stage.show();
        lobbyFxml.getController(GameLobbyController.class).show();
    }
}
