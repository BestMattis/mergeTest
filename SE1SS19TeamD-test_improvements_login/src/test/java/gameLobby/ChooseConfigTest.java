package gameLobby;

import armyManager.ArmyManagerController;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import main.FXMLLoad;
import model.ArmyConfiguration;
import model.Model;
import model.Player;
import model.Unit;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import registerLogin.LoginRegisterTestUtils;
import syncCommunication.HttpRequests;
import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.SynchronousUserCommunicator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class ChooseConfigTest extends ApplicationTest {

    private FXMLLoad screenFXML;

    private ToggleButton ready;
    protected ChoiceBox<String> choice;

    private Player currentPlayer;
    private ArrayList<ArmyConfiguration> armyConfigurations;
    private SynchronousUserCommunicator synchronousUserCommunicator;
    private HttpRequests httpReq;
    private ArmyManagerController armyManagerController;
    private GameLobbyController gameLobbyController;

    @Override
    public void start(Stage stage) throws IOException {
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

            //load the Game Lobby Screen fxml and add the controller
            screenFXML = new FXMLLoad("/gameLobby/GameLobbyScreen_v2.fxml", new GameLobbyController());
            gameLobbyController = screenFXML.getController(GameLobbyController.class);

            //setup the scene and show it at the stage
            stage.setScene(screenFXML.getScene());
            stage.setFullScreen(true);
            stage.show();
            gameLobbyController.show();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }
    }

    private void loadGameLobbyGUIElements() {
        this.ready = this.lookup("#ready").query();
        this.choice = this.lookup("#choice").query();
    }


    /**
     * Test the GameLobby: Select ArmyConfiguration User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-163">TD-163</a>.
     */
    @Test
    public void TestChooseConfig(){
        /*
         * =============== SITUATION ===============
         */

        // Alice has logged in and created a game named "Alice's Game" with 4 players.

            loadGameLobbyGUIElements();

            armyManagerController = screenFXML.getController(GameLobbyController.class).armymanagerFXML.getController(ArmyManagerController.class);
            armyConfigurations = currentPlayer.getArmyConfigurations();
            String chooseConfigName = "ChooseTestConfig";

            //make sure "ChooseTestConfig" exists
            boolean configExists = false;
            for(ArmyConfiguration armyConfiguration:armyConfigurations){
                if(armyConfiguration.getName().equals("ChooseTestConfig")){
                    configExists = true;
                    break;
                }
            }

            //create "ChooseTestConfig" when it doesn't exist
            if(!configExists){
                String[] chooseUnitArr = {"5cc051bd62083600017db3b7", "5cc051bd62083600017db3b7", "5cc051bd62083600017db3bb",
                        "5cc051bd62083600017db3ba", "5cc051bd62083600017db3b6", "5cc051bd62083600017db3b6",
                        "5cc051bd62083600017db3b8", "5cc051bd62083600017db3b8", "5cc051bd62083600017db3b9",
                        "5cc051bd62083600017db3b9"};
                List<String> chooseList = Arrays.asList(chooseUnitArr);
                ArrayList<String> chooseUnitList = new ArrayList<String>();
                chooseUnitList.addAll(chooseList);

                int configsSize = armyManagerController.getConfigs().size();

                //delete last config and save "ChooseTestConfig"
                if(configsSize > 1) {
                    armyManagerController.deleteConfiguration(currentPlayer.getArmyConfigurations().get(configsSize - 1));
                }
                armyManagerController.getConfigs().add(chooseConfigName);
                armyManagerController.getConfiglist().getSelectionModel().selectLast();
                armyManagerController.saveConfiguration(chooseConfigName, chooseUnitList, currentPlayer);
            }


        /*
         * =============== ACTION ===============
         */

        // Alice selects the army configuration "ChooseTestConfig" in the selection bar.

        this.clickOn("#choice");
        this.clickOn(chooseConfigName);

        /*
         * =============== RESULT ===============
         */

        // The selected army configuration is now set on "ChooseTestConfig".

        int b7 = 0;
        int bb = 0;
        int ba = 0;
        int b6 = 0;
        int b8 = 0;
        int b9 = 0;

        for(Unit unit:currentPlayer.getCurrentArmyConfiguration().getUnits()){
            if(unit.getId().equals("5cc051bd62083600017db3b7")){
                ++b7;
            }else if(unit.getId().equals("5cc051bd62083600017db3bb")){
                ++bb;
            }else if(unit.getId().equals("5cc051bd62083600017db3ba")){
                ++ba;
            }else if(unit.getId().equals("5cc051bd62083600017db3b6")){
                ++b6;
            }else if(unit.getId().equals("5cc051bd62083600017db3b8")){
                ++b8;
            }else if(unit.getId().equals("5cc051bd62083600017db3b9")){
                ++b9;
            }
        }

        //Assert that the currentArmyConfiguration is the correct one
        assertEquals(b7, 2);
        assertEquals(bb, 1);
        assertEquals(ba, 1);
        assertEquals(b6, 2);
        assertEquals(b8, 2);
        assertEquals(b9, 2);
        assertEquals(chooseConfigName, gameLobbyController.choice.getValue());
        assertEquals(chooseConfigName, currentPlayer.getCurrentArmyConfiguration().getName());
    }
}
