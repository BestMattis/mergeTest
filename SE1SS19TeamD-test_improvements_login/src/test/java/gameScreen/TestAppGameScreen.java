package gameScreen;

import armyManager.ArmyManagerController;
import javafx.application.Application;
import javafx.stage.Stage;
import main.FXMLLoad;

public class TestAppGameScreen extends Application {

    FXMLLoad gameFXML;

    public void start(Stage stage){
        gameFXML = new FXMLLoad("/gameScreen/gameScreen.fxml");
        gameFXML.getController(GameScreenController.class).addScene(gameFXML.getScene());
        stage.setScene(gameFXML.getScene());
        stage.setFullScreen(true);
        stage.show();
        gameFXML.getController(GameScreenController.class).getGameLobbyController().hide();


    }

}
