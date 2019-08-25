package gameScreen;

import javafx.application.Application;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import main.FXMLLoad;
import model.Game;
import model.GameField;

public class TestAppGameScreen extends Application {

    FXMLLoad gameFXML;

    public void start(Stage stage) {
        AdvancedWarsApplication awa = new AdvancedWarsApplication();
        AdvancedWarsApplication.advancedWarsApplication = awa;
        awa.offtesting = false;
        AdvancedWarsApplication.offlineTest = true;
        Game tg = new Game();
        GamefieldGenerator gen = new GamefieldGenerator();
        GameField tfield = GamefieldGenerator.randomField(30, 30);

        tg.setGameField(tfield);
        System.out.println(tfield.getFields().size());
        System.out.println(tfield.getSizeY());


        gameFXML = new FXMLLoad("/gameScreen/gameScreen.fxml", new GameScreenController(tg));
        gameFXML.getController(GameScreenController.class).addScene(gameFXML.getScene());
        stage.setScene(gameFXML.getScene());
        stage.setFullScreen(true);
        stage.show();
        //gameFXML.getController(GameScreenController.class).getGameLobbyController().hide();


    }

}
