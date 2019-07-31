package armyManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.FXMLLoad;
import model.ArmyConfiguration;
import model.Model;
import model.Player;
import model.Unit;
import registerLogin.LoginRegisterTestUtils;
import syncCommunication.HttpRequests;
import syncCommunication.SynchronousUserCommunicator;
import syncCommunication.RESTExceptions.LoginFailedException;

public class ArmyManagerControllerTest extends ApplicationTest {

    private FXMLLoad armyFXML;
    private ArmyManagerController armyManagerController;
    private SynchronousUserCommunicator synchronousUserCommunicator;
    private HttpRequests httpReq;
    private Player currentPlayer;

    private Button newButton;
    private Button save;
    private VBox delete;
    private ComboBox<String> configlist;

    /**
     * Setup stage and start application.
     *
     * @param primaryStage stage to display
     * @throws IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        //TODO create offline test with LoginRegisterTestUtils
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

            //load the armyManager fxml and add the controller
            armyFXML = new FXMLLoad("/armyManager/ArmyManager.fxml", new ArmyManagerController());
            armyManagerController = armyFXML.getController(ArmyManagerController.class);

            //setup the scene and show it at the stage
            primaryStage.setScene(armyFXML.getScene());
            primaryStage.setFullScreen(true);
            primaryStage.show();
            armyManagerController.show();

            loadArmyManagerGUIElements();

        } catch (LoginFailedException e) {
            e.printStackTrace();
        }
    }

    private void loadArmyManagerGUIElements() {
        this.newButton = this.lookup("#newButton").queryButton();
        this.save = this.lookup("#save").queryButton();
        this.delete = this.lookup("#delete").query();
        this.configlist = this.lookup("#configlist").queryComboBox();
}

    /**
     * Test the ArmyManager: CreateNewConfiguration User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-162">TD-162</a>.
     */
    @Test
    public void createConfigurationTest() {

        /*
         * =============== SITUATION ===============
         */

        // Alice is in the ArmyManager.

        int oldSize = armyManagerController.configlist.getItems().size();

        /*
         * =============== ACTION ===============
         */

        // Alice clicks on the "new" button

        clickOn(this.newButton);

        /*
         * =============== RESULT ===============
         */

        // GUI updates and shows empty configuration in the ArmyManager.

        if (armyManagerController.configs.size() < 7) {
            //Assert that the number of configs increased and that the config is empty
            assertEquals(armyManagerController.configlist.getItems().size(), oldSize + 1);
            assertEquals(armyManagerController.configlist.getSelectionModel().getSelectedItem(), "config" + armyManagerController.configlist.getItems().size());
            for (UnitCardController unitCardController:armyManagerController.unitCardControllers){
                assertEquals(unitCardController, null);
            }
        }else{
            //Assert that no new configs are created, when the configs limit is reached
            assertEquals(armyManagerController.configs.size(), 7);
        }
    }

    /**
     * Test the ArmyManager: SaveConfiguration User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-146">TD-146</a>.
     */
    @Test
    public void saveConfigurationTest() {

        /*
         * =============== SITUATION ===============
         */

        //Alice is in the ArmyManager.
        //Alice has chosen 5x Soldiers and 5x Tanks.

        clickOn(this.delete, MouseButton.SECONDARY);
        Node infantryCard = armyManagerController.units.get(0);
        Node heavyTankCard = armyManagerController.units.get(4);
        for (int i = 0; i < 5; ++i) {

            drag(infantryCard, MouseButton.PRIMARY);
            dropTo(armyManagerController.unitgrid);
            drag(heavyTankCard, MouseButton.PRIMARY);
            dropTo(armyManagerController.unitgrid);
        }
        //TODO: BUG with DragRobot -> user mouse movement needed

        /*
         * =============== ACTION ===============
         */

        // Alice clicks on the "save" Button.

        clickOn(this.save);

        /*
         * =============== RESULT ===============
         */

        // The configuration is saved in the DataModel with 5x Soldiers and 5x Tanks.
            String savedConfigName = armyManagerController.configlist.getSelectionModel().getSelectedItem();
//            for(ArmyConfiguration armyConfiguration : currentPlayer.getArmyConfigurations()) {
//                if(armyConfiguration.getName().equals(savedConfigName)) {
//                    armyManagerController.deleteConfiguration(armyConfiguration);
//                    System.out.println("SaveConfigurationTest - deleted existing config");
//                    break;
//                }
//            }

            boolean configExists = false;
            ArmyConfiguration savedConfiguration = new ArmyConfiguration();
            int infantryCount = 0;
            int heavyTankCount = 0;

            for(ArmyConfiguration armyConfiguration: currentPlayer.getArmyConfigurations()){
                if(armyConfiguration.getName().equals(savedConfigName)){
                    configExists = true;
                    savedConfiguration = armyConfiguration;
                    break;
                }
            }

            for(Unit unit: savedConfiguration.getUnits()){
                if(unit.getType().equals("Infantry")){
                    ++infantryCount;
                }else if(unit.getType().equals("Heavy Tank")){
                    ++heavyTankCount;
                }
            }

            //Assert that the config exists in the data model and that the number of units is correct
            assertTrue(configExists);
            assertEquals(savedConfiguration.getUnits().size(), 10);
            assertEquals(infantryCount, 5);
            assertEquals(heavyTankCount, 5);

        // The REST-Component sends the configuration to the Server.
        ArmyConfiguration savedServerConfiguration = armyManagerController.getSingleArmyConfiguration(savedConfiguration.getId());
        int infantryCountServer = 0;
        int heavyTankCountServer = 0;
        for(Unit unit: savedServerConfiguration.getUnits()){
            if(unit.getType().equals("Infantry")){
                ++infantryCountServer;
            }else if(unit.getType().equals("Heavy Tank")){
                ++heavyTankCountServer;
            }
        }
        //Assertions
        assertEquals(savedServerConfiguration.getUnits().size(), 10);
        assertEquals(infantryCountServer, 5);
        assertEquals(heavyTankCountServer, 5);
    }

    /**
     * Test the ArmyManager: LoadConfiguration User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-145">TD-145</a>.
     */
    @Test
    public void loadConfigurationTest(){

        /*
         * =============== SITUATION ===============
         */

        //Alice is in the ArmyManager.

            //make sure configLoadTest exists
            String chooseConfigName = "configLoadTest";
            ArrayList<String> chooseUnitList = new ArrayList<String>();

        for(int i = 0; i < 10; ++i) {
            JSONObject unitType = armyManagerController.getAllDifferentUnitTypes().get(i % armyManagerController.getAllDifferentUnitTypes().size());
            chooseUnitList.add(unitType.getString("id"));
        }

            int configsSize = armyManagerController.configs.size();
            //Delete last config and save config2
            if(configsSize > 1) {
                armyManagerController.deleteConfiguration(currentPlayer.getArmyConfigurations().get(configsSize - 1));
            }
            armyManagerController.configs.add(chooseConfigName);
            armyManagerController.configlist.getSelectionModel().selectLast();
            armyManagerController.saveConfiguration(chooseConfigName, chooseUnitList, currentPlayer);
            int index;

            for(int i = 0; i < armyManagerController.configlist.getItems().size(); ++i){
                if(armyManagerController.configlist.getItems().get(i).equals(chooseConfigName)){
                    index = i;
                    System.out.println("index of configLoadTest: " + index);
                    break;
                }
            }

        /*
         * =============== ACTION ===============
         */

        // Alice clicks on the ConfigList and selects "config2".

        for (Node child : configlist.getChildrenUnmodifiable()) {
            if (child.getStyleClass().contains("arrow-button")) {
                Node arrowRegion = ((Pane) child).getChildren().get(0);
                clickOn(arrowRegion);
                try {
                Thread.sleep(100);
                }catch (Exception e) {
                e.printStackTrace();
                }
                clickOn(chooseConfigName);
            }
        }

        /*
         * =============== RESULT ===============
         */

        // GUI updates and shows loaded config in the ArmyManager.

        String loadedConfigName = armyManagerController.configlist.getSelectionModel().getSelectedItem();
        HashMap<String, Integer> hashUnits = new HashMap<>();
        hashUnits.put("Infantry", 0);
        hashUnits.put("Bazooka Trooper", 0);
        hashUnits.put("Jeep", 0);
        hashUnits.put("Light Tank", 0);
        hashUnits.put("Heavy Tank", 0);
        hashUnits.put("Chopper", 0);


        for(int i = 0; i < 10; ++i) {
            JSONObject unitType = armyManagerController.getAllDifferentUnitTypes().get(i % armyManagerController.getAllDifferentUnitTypes().size());
            hashUnits.put(unitType.getString("type"), hashUnits.get(unitType.getString("type")) + 1);
        }

        //Assert that the config is loaded and shows the correct units (10)
        assertEquals("Wrong number of units, type: Infantry", 2, hashUnits.get("Infantry").intValue());
        assertEquals("Wrong number of units, type: Bazooka Trooper", 1, hashUnits.get("Bazooka Trooper").intValue());
        assertEquals("Wrong number of units, type: Jeep", 1, hashUnits.get("Jeep").intValue());
        assertEquals("Wrong number of units, type: Light Tank", 2, hashUnits.get("Light Tank").intValue());
        assertEquals("Wrong number of units, type: Heavy Tank", 2, hashUnits.get("Heavy Tank").intValue());
        assertEquals("Wrong number of units, type: Chopper", 2, hashUnits.get("Chopper").intValue());
        assertEquals(loadedConfigName, chooseConfigName);
    }

}
