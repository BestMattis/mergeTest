package armyManager;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.FXMLLoad;
import model.Model;
import model.Player;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;
import registerLogin.LoginRegisterTestUtils;
import syncCommunication.HttpRequests;
import syncCommunication.SynchronousUserCommunicator;

import static org.hamcrest.CoreMatchers.is;

public class EditTests extends ApplicationTest {

    private FXMLLoad armyFXML;
    private HttpRequests httpReq;
    private SynchronousUserCommunicator synchronousUserCommunicator;
    private Player currentPlayer;
    private int currentConfigsSize;

    @SuppressWarnings("unchecked")
    public <T extends Node> T find(final String query) {
        return (T) lookup(query).queryAll().iterator().next();
    }


    public void start(Stage stage){
        try {
            //player login at the server and add it to the model
            httpReq = new HttpRequests();
            synchronousUserCommunicator = new SynchronousUserCommunicator(httpReq);
            synchronousUserCommunicator.logIn(LoginRegisterTestUtils.getTestUserName(), LoginRegisterTestUtils.getTestUserPassword());

            currentPlayer = new Player().setName(LoginRegisterTestUtils.getTestUserName())
                    .setPassword(LoginRegisterTestUtils.getTestUserPassword()).setApp(Model.getApp());
            Model.getApp().setCurrentPlayer(currentPlayer);
            Model.getPlayerHttpRequestsHashMap()
                    .put(currentPlayer, httpReq);
        }catch(Exception e){
            e.printStackTrace();
        }

        //initialize scene
        armyFXML = new FXMLLoad("/armyManager/ArmyManager.fxml", new ArmyManagerController());
        stage.setScene(armyFXML.getScene());
        stage.setFullScreen(true);

        //initialize ArmyManager
        currentConfigsSize = armyFXML.getController(ArmyManagerController.class).configs.size();
        if(currentConfigsSize==7){
            armyFXML.getController(ArmyManagerController.class).deleteConfiguration(currentPlayer.getArmyConfigurations().get(currentConfigsSize - 1));
        }
        armyFXML.getController(ArmyManagerController.class).createConfiguration();
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
        drag("#unitlabel").dropTo("#drpane");
        FxAssert.verifyThat(armyFXML.getController(ArmyManagerController.class).getUnits().size(), is(8));
        FxAssert.verifyThat(armyFXML.getController(ArmyManagerController.class).getUnits().get(7), is("5cc051bd62083600017db3b6"));
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
