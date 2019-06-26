package armyManager;

import armyManager.ArmyManagerController;
import javafx.application.Application;
import javafx.stage.Stage;
import main.FXMLLoad;

public class TestAppArmy extends Application {

    FXMLLoad armyFXML;
    FXMLLoad unit1;

    public void start(Stage stage){
        armyFXML = new FXMLLoad("/armyManager/ArmyManager.fxml", new ArmyManagerController());
        stage.setScene(armyFXML.getScene());
        stage.setFullScreen(true);
        stage.show();
        armyFXML.getController(ArmyManagerController.class).show();
        armyFXML.getController(ArmyManagerController.class).newUnitCard("testUNIT1");
        armyFXML.getController(ArmyManagerController.class).newUnitCard("testUNIT2");
        armyFXML.getController(ArmyManagerController.class).newUnitCard("5cc051bd62083600017db3bb");
        armyFXML.getController(ArmyManagerController.class).showError("testERROR");

    }

}
