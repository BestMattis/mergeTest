package armyManager;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.FXMLLoad;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;
import static org.hamcrest.CoreMatchers.is;

public class EditTests extends ApplicationTest {

    FXMLLoad armyFXML;

    @SuppressWarnings("unchecked")
    public <T extends Node> T find(final String query) {
        return (T) lookup(query).queryAll().iterator().next();
    }


    public void start(Stage stage){
        armyFXML = new FXMLLoad("/armyManager/ArmyManager.fxml", new ArmyManagerController());
        stage.setScene(armyFXML.getScene());
        stage.setFullScreen(true);
        armyFXML.getController(ArmyManagerController.class).newUnitCard("5cc051bd62083600017db3ba");
        armyFXML.getController(ArmyManagerController.class).newUnitCard("5cc051bd62083600017db3bb");
        armyFXML.getController(ArmyManagerController.class).newUnitCard("5cc051bd62083600017db3b6");
        armyFXML.getController(ArmyManagerController.class).addUnit("5cc051bd62083600017db3ba");
        armyFXML.getController(ArmyManagerController.class).addUnit("5cc051bd62083600017db3ba");
        armyFXML.getController(ArmyManagerController.class).addUnit("5cc051bd62083600017db3b6");
        armyFXML.getController(ArmyManagerController.class).addUnit("5cc051bd62083600017db3ba");
        armyFXML.getController(ArmyManagerController.class).addUnit("5cc051bd62083600017db3b6");
        armyFXML.getController(ArmyManagerController.class).addUnit("5cc051bd62083600017db3b6");
        armyFXML.getController(ArmyManagerController.class).addUnit("5cc051bd62083600017db3ba");
        armyFXML.getController(ArmyManagerController.class).show();
        stage.show();
    }

    @Test
    public void intoTest(){
        FxAssert.verifyThat(armyFXML.getController(ArmyManagerController.class).getUnits().size(), is(7));
        drag("#unitlabel");
        dropTo("#drpane");
        FxAssert.verifyThat(armyFXML.getController(ArmyManagerController.class).getUnits().size(), is(8));
        FxAssert.verifyThat(armyFXML.getController(ArmyManagerController.class).getUnits().get(7), is("5cc051bd62083600017db3ba"));
    }

    @Test
    public void deleteTest(){
        FxAssert.verifyThat(armyFXML.getController(ArmyManagerController.class).getUnits().size(), is(7));
        AnchorPane base = find("#panezero");
        drag((Node)(from(base).lookup("#unitlabel").query()));
        dropTo("#trashbin");
        FxAssert.verifyThat(armyFXML.getController(ArmyManagerController.class).getUnits().size(), is(6));
    }
}
