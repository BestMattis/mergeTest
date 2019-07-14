package gameScreen;

import armyManager.ArmyManagerController;
import javafx.application.Application;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import main.FXMLLoad;
import model.Field;
import model.Game;
import model.GameField;

import java.util.ArrayList;

public class TestAppGameScreen extends Application {

    FXMLLoad gameFXML;

    public void start(Stage stage){
        AdvancedWarsApplication awa = new AdvancedWarsApplication();
        AdvancedWarsApplication.advancedWarsApplication = awa;
        awa.offtesting = false;

        Game tg = new Game();
        GamefieldGenerator gen = new GamefieldGenerator();
        GameField tfield = gen.randomField(30,30);
        /*GameField tfield = new GameField();
        Field field = new Field();
        field.setPosX(0).setPosY(0).setType("Water");
        Field field1 = new Field();
        field1.setPosX(1).setPosY(0).setType("Water");
        Field field2 = new Field();
        field2.setPosX(2).setPosY(0).setType("Mountain");
        Field field3 = new Field();
        field3.setPosX(0).setPosY(1).setType("Water");
        Field field4 = new Field();
        field4.setPosX(1).setPosY(1).setType("Water");
        Field field5 = new Field();
        field5.setPosX(2).setPosY(1).setType("Water");
        Field field6 = new Field();
        field6.setPosX(0).setPosY(2).setType("Grass");
        Field field7 = new Field();
        field7.setPosX(1).setPosY(2).setType("Forest");
        Field field8 = new Field();
        field8.setPosX(2).setPosY(2).setType("Forest");


        field.withNeighbour(field1, field4, field3);
        field1.withNeighbour(field, field2, field3, field4, field5);
        field2.withNeighbour(field1, field4, field5);
        field3.withNeighbour(field, field7, field1, field4, field6);
        field4.withNeighbour(field, field2, field3, field5, field6, field7, field8, field1);
        field5.withNeighbour(field1, field2, field7, field4, field8);
        field6.withNeighbour(field4, field7, field3);
        field7.withNeighbour(field6, field8, field3, field4, field5);
        field8.withNeighbour(field4, field5, field7);



        tfield.withFields(field, field2, field3, field5, field6, field7, field8, field1, field4);
        tfield.setSizeX(3);
        tfield.setSizeY(3);*/
        tg.setGameField(tfield);
        System.out.println(tfield.getFields().size());
        System.out.println(tfield.getSizeY());


        gameFXML = new FXMLLoad("/gameScreen/gameScreen.fxml", new GameScreenController(true, tg));
        gameFXML.getController(GameScreenController.class).addScene(gameFXML.getScene());
        stage.setScene(gameFXML.getScene());
        stage.setFullScreen(true);
        stage.show();
        //gameFXML.getController(GameScreenController.class).getGameLobbyController().hide();


    }

}
