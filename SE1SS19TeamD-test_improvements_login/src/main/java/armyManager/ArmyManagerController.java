package armyManager;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import main.AdvancedWarsApplication;
import main.FXMLLoad;
import model.ArmyConfiguration;
import model.Model;
import model.Player;
import model.Unit;
import org.json.JSONArray;
import org.json.JSONObject;
import syncCommunication.HttpRequests;
import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.SynchronousArmyCommunicator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class ArmyManagerController {

    private final int neededArmySize = 10;
    private final int maxConfigs = 7;
    @FXML
    protected ListView unitlist;
    @FXML
    protected AnchorPane droppane;
    @FXML
    protected GridPane unitgrid;
    @FXML
    VBox delete;
    @FXML
    ComboBox<String> configlist;
    @FXML
    Button save;
    @FXML
    ImageView trash;
    @FXML
    Button newButton;
    @FXML
    Label count;
    @FXML
    VBox errorbox;
    @FXML
    Label except;
    @FXML
    GridPane base;
    @FXML
    Button back;
    ObservableList<String> configs;
    ObservableList<Parent> units;
    UnitCardController[] unitCardControllers = new UnitCardController[15];
    private Model model;
    private Player currentPlayer;
    private HttpRequests httpReq;
    private SynchronousArmyCommunicator armyCommunicator;
    private ResourceBundle bundle;
    private String currentConfigName;
    private ArrayList<JSONObject> allDifferentUnitTypes;

    public ArmyManagerController(Model model) {
        this.model = model;
    }

    /**
     * initialize the drag and drop and ArmyConfigurations
     */
    public void initialize() {
        //load the resource
        try {
            String propertiespath = "en-US.properties"; //Changeable for other languages
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classLoader.getResource(propertiespath).openStream();
            bundle = new PropertyResourceBundle(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //initialize Player and syncCommunication
        currentPlayer = model.getApp().getCurrentPlayer();
        httpReq = model.getPlayerHttpRequestsHashMap().get(currentPlayer);
        armyCommunicator = new SynchronousArmyCommunicator(httpReq);

        //initialize units, unitlist
        units = FXCollections.observableArrayList();
        unitlist.setItems(units);

        if (!AdvancedWarsApplication.offtesting) {
            //initialize configs
            configs = FXCollections.observableArrayList();
            //ON EVERY LOGIN
            loadConfigurationsFromServer(currentPlayer); //load the current players configs from the server at the start
            configs.addAll(configurationNames(currentPlayer));
            configlist.setItems(configs);
            if (configs.size() > 0) {
                //load first config from data model and select it
                configlist.getSelectionModel().selectFirst();
                currentConfigName = configlist.getSelectionModel().getSelectedItem();
                loadConfiguration(currentConfigName, currentPlayer);
            } else {
                //ON FIRST LOGIN
                //create the players first config with a basic unit configuration already
                createStandardConfiguration();
            }
        }

        if (!AdvancedWarsApplication.offtesting) {
            //initialize UnitCards
            for (JSONObject jsonUnit : allDifferentUnitTypes) {
                String unitID = jsonUnit.getString("id");
                newUnitCard(unitID);
            }
        }

        //EventListener
        droppane.setOnDragOver(t -> dragOver(t));
        droppane.setOnDragDropped(t -> dragdrop(t));
        delete.setOnDragOver(t -> dragOver(t));
        delete.setOnDragDropped(t -> dragdropdelete(t));
        delete.setOnMouseClicked(t -> clickclear(t));
        trash.fitHeightProperty().bind(delete.heightProperty());
        errorbox.setVisible(false);
        base.setVisible(false);
        back.setOnAction(t -> hide());

        save.setOnAction(t -> saveConfiguration(currentConfigName, getUnits(), currentPlayer));
        configlist.setOnHiding(t -> loadConfiguration(configlist.getSelectionModel()
                .getSelectedItem(), currentPlayer));
        newButton.setOnAction(t -> createConfiguration());
        System.out.println("ArmyManagerController initialized");
    }

    /**
     * clearing the configurator on rightclick on the delete label
     *
     * @param event for checking for a right click
     */
    public void clickclear(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            clearUnits();
        }
    }

    /**
     * Add a new Unit to the list to choose from
     *
     * @param id the id of the unit
     */
    public void newUnitCard(String id) {
        FXMLLoad unit = new FXMLLoad("/armyManager/UnitCard.fxml", new UnitCardController());
        unit.getController(UnitCardController.class).setUnit(id, allDifferentUnitTypes);
        units.add(unit.getParent());
        unit.getController(UnitCardController.class).setParent(unitlist);
        unit.getController(UnitCardController.class).setParentCon(this);
    }

    /**
     * changes icon if draged over
     *
     * @param event the dragevent
     */
    private void dragOver(DragEvent event) {
        /* accept it only if it is  not dragged from the same node
         * and if it has a string data */
        if (event.getGestureSource() != droppane &&
                event.getDragboard().hasString()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }

    /**
     * The mehtod to manage the drop to add a unit to the config
     *
     * @param event of the dragged unitcard
     */
    private void dragdrop(DragEvent event) {
        /* if there is a string data on dragboard, read it and use it */
        AnchorPane pane = (AnchorPane) event.getGestureSource();
        if (pane.getParent().getClass() != AnchorPane.class) {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                success = addUnit(db.getString());
            }
            /* let the source know whether the string was successfully
             * transferred and used */
            event.setDropCompleted(success);

            event.consume();
        } else {
            pane.setVisible(true);
        }
    }

    /**
     * Add a unit to the config
     *
     * @param id specifing the unit
     * @return the addiotion of the unit to the army was successful
     */
    public boolean addUnit(String id) {
        boolean success = true;
        int index = 0;
        //System.out.println(unitgrid.getChildren().size());
        L:
        for (int i = 0; i < unitgrid.getChildren().size(); i++) {
            if (unitgrid.getChildren().get(i).getClass() == AnchorPane.class) {
                AnchorPane pane = (AnchorPane) unitgrid.getChildren().get(i);
                if (pane.getChildren().isEmpty()) {
                    success = true;
                    UnitCardController unitCardController = new UnitCardController();
                    FXMLLoad unitfxml = new FXMLLoad("/armyManager/UnitCard.fxml",
                            unitCardController, false);
                    unitfxml.getController(UnitCardController.class).setUnit(id, allDifferentUnitTypes);
                    unitCardControllers[i] = unitCardController;
                    pane.getChildren().add(index, unitfxml.getParent());
                    unitfxml.getController(UnitCardController.class).setParent(pane);
                    unitfxml.getController(UnitCardController.class).setParentCon(this);
                    AnchorPane.setRightAnchor(unitfxml.getParent(), 3d);
                    AnchorPane.setBottomAnchor(unitfxml.getParent(), 3d);
                    AnchorPane.setLeftAnchor(unitfxml.getParent(), 3d);
                    AnchorPane.setTopAnchor(unitfxml.getParent(), 3d);
                    pane.setStyle("-fx-background-color: transparent;");
                    break L;
                }
            }
        }
        setTheCount();
        return success;
    }

    /**
     * Mehtod to delet the controllers of removed unitcards
     */
    private void deletelostControllers() {
        for (int i = 0; i < unitgrid.getChildren().size(); i++) {
            if (unitgrid.getChildren().get(i).getClass() == AnchorPane.class) {
                AnchorPane pane = (AnchorPane) unitgrid.getChildren().get(i);
                if (pane.getChildren().isEmpty()) {
                    unitCardControllers[i] = null;
                }
            }
        }
        setTheCount();

    }

    /**
     * show a red error message for 3 seconds
     *
     * @param text the error to display
     */
    public void showError(String text) {
        except.setText(text);
        Timeline ex = new Timeline(
                new KeyFrame(Duration.ZERO, event -> {
                    errorbox.setVisible(true);
                    except.setVisible(true);
                }),
                new KeyFrame(Duration.seconds(3.0), event -> {
                    errorbox.setVisible(false);
                    except.setVisible(false);
                })
        );
        ex.setCycleCount(1);
        ex.play();
    }

    /**
     * updates the unitnumber label
     */
    private void setTheCount() {
        int i = 0;
        for (UnitCardController unitCardController : unitCardControllers) {
            if (unitCardController != null) {
                i++;
            }
        }
        count.setText(i + "/10");
    }

    /**
     * get the config as a List of unit-IDs
     *
     * @return the string of unit ids
     */
    public ArrayList<String> getUnits() {
        ArrayList<String> ids = new ArrayList<>();
        for (UnitCardController unitCardController : unitCardControllers) {
            if (unitCardController != null) {
                ids.add(unitCardController.getID());
            }
        }
        return ids;
    }

    /**
     * removes all units from the config
     */
    public void clearUnits() {
        for (int i = 0; i < unitgrid.getChildren().size(); i++) {
            if (unitgrid.getChildren().get(i).getClass() == AnchorPane.class) {
                AnchorPane pane = (AnchorPane) unitgrid.getChildren().get(i);
                if (!pane.getChildren().isEmpty()) {
                    pane.getChildren().remove(0);
                }
                pane.setStyle("-fx-background-color: transparent;");
            }
        }
        deletelostControllers();
    }

    /**
     * visualizes the missing units to add
     */
    public void noUnits() {
        for (int i = 0; i < unitgrid.getChildren().size(); i++) {
            if (unitgrid.getChildren().get(i).getClass() == AnchorPane.class) {
                AnchorPane pane = (AnchorPane) unitgrid.getChildren().get(i);
                if (pane.getChildren().isEmpty()) {
                    pane.setStyle("-fx-background-color: rgba(255, 0, 0, 0.4);");
                }
            }
        }
    }

    /**
     * Method to delete a single unit with dragdrop
     *
     * @param event of the dragged unitcard
     */
    private void dragdropdelete(DragEvent event) {
        AnchorPane pane = (AnchorPane) event.getGestureSource();
        if (pane.getParent().getClass() == AnchorPane.class) {
            AnchorPane parent = (AnchorPane) pane.getParent();
            parent.getChildren().remove(0);
            deletelostControllers();
        }
        event.setDropCompleted(true);
    }

    /**
     * shows the manager
     */
    public void show() {
        base.setVisible(true);
    }

    /**
     * hides the manager
     */
    public void hide() {
        base.setVisible(false);
    }

    /**
     * Method to create a list of Unit type units from JSONObject type units
     * Uses list of all available unit types and its data
     *
     * @param unitIdList list of unit ids
     * @param unitTypes  list of all different available unit types
     * @return list of units as Unit
     */
    public ArrayList<Unit> createUnitsFromJSON(ArrayList<String> unitIdList,
                                               ArrayList<JSONObject> unitTypes) {

        ArrayList<Unit> units = new ArrayList<>();

        for (String jsonUnit : unitIdList) {


            Unit currentUnit = new Unit();


            //find blueprint in unitTypes and create currentUnit after it
            for (JSONObject unitType : unitTypes) {

                if (jsonUnit.equals(unitType.getString("id"))) {
                    currentUnit.setType(unitType.getString("type"));
                    currentUnit.setId(unitType.getString("id"));
                    currentUnit.setHp(unitType.getInt("hp"));
                    currentUnit.setMp(unitType.getInt("mp"));

                    ArrayList<String> canAttack = new ArrayList<>();
                    JSONArray jArray = unitType.getJSONArray("canAttack");
                    if (jArray != null) {
                        for (int i = 0; i < jArray.length(); ++i) {
                            canAttack.add((String) jArray.get(i));
                        }
                    }
                    currentUnit.setCanAttack(canAttack);
                    break;
                }

            }
            units.add(currentUnit);
        }
        return units;
    }

    /**
     * Method to create new entry in the config list and select it
     */
    public void createConfiguration() {
        if (configs.size() < maxConfigs) {
            currentConfigName = "config" + (configs.size() + 1);
            configs.add(currentConfigName);
            clearUnits();
            configlist.getSelectionModel().selectLast();
        } else {
            showError(bundle.getString("manager.configlimit"));
        }
    }

    public void createStandardConfiguration() {
        if (configs.size() == 0) {//if should never be false!
            currentConfigName = "config1";
            ArrayList<String> sUnitList = new ArrayList<String>();
            for (int i = 0; i < 10; ++i) {
                JSONObject unitType = allDifferentUnitTypes.get(i % allDifferentUnitTypes.size());
                sUnitList.add(unitType.getString("id"));
            }

            configs.add(currentConfigName);
            configlist.getSelectionModel().selectLast();
            saveConfiguration(currentConfigName, sUnitList, currentPlayer);
            loadConfiguration(currentConfigName, currentPlayer);
        }
    }

    /**
     * Method to save (create or update) a configuration in the data model and at the server
     *
     * @param configurationName name of the config to save
     * @param unitIDs           list of the IDs of units in the configuration
     * @param player            current player
     */
    public boolean saveConfiguration(String configurationName, ArrayList<String> unitIDs,
                                     Player player) {
        if (unitIDs.size() == neededArmySize) {
            if (configlist.getSelectionModel().getSelectedItem().length() > 0) {
                ArmyConfiguration armyConfiguration;
                ArrayList<String> oldConfigs = configurationNames(currentPlayer);
                boolean isOld = false;
                boolean nameAlreadyTaken = false;

                for (String oldConfig : oldConfigs) {
                    if (configurationName.equals(oldConfig)) {
                        isOld = true;
                    }
                    if (configlist.getSelectionModel().getSelectedItem().equals(oldConfig)
                            && !configurationName.equals(oldConfig)) {

                        nameAlreadyTaken = true;
                    }
                }

                if (!nameAlreadyTaken) {
                    armyConfiguration = new ArmyConfiguration();
                    armyConfiguration.setName(configurationName);

                    //use old ArmyConfiguration when already existing
                    if (isOld == true) {
                        ArrayList<ArmyConfiguration> armyConfigs = currentPlayer.getArmyConfigurations();
                        for (ArmyConfiguration armyConfig : armyConfigs) {
                            if (armyConfig.getName().equals(configurationName)) {
                                //when exiting, delete configuration from data model and add it again later
                                currentPlayer.withoutArmyConfigurations(armyConfig);
                                armyConfiguration = armyConfig;
                                //switch currentConfigName and configs entry with new config name
                                for (int i = 0; i < configs.size(); ++i) {
                                    //at least 1 config name is always equal, when isOld == true
                                    if (configs.get(i).equals(configurationName)) {
                                        currentConfigName = configlist.getSelectionModel().getSelectedItem();
                                        configs.remove(i);
                                        configs.add(i, currentConfigName);
                                        configlist.getSelectionModel().select(i);
                                        break;
                                    }
                                }
                                armyConfiguration.setName(currentConfigName);
                                break;
                            }
                        }

                    } else if (!configlist.getSelectionModel().getSelectedItem().equals(configurationName)) {
                        int index = configlist.getSelectionModel().getSelectedIndex();
                        currentConfigName = configlist.getSelectionModel().getSelectedItem();
                        configs.remove(index);
                        configs.add(index, currentConfigName);
                        configlist.getSelectionModel().select(index);
                        armyConfiguration.setName(currentConfigName);
                    }

                    armyConfiguration.getUnits().clear();
                    armyConfiguration.withUnits(createUnitsFromJSON(unitIDs, allDifferentUnitTypes));

                    //save cofiguration on server and add it to the player
                    try {
                        if (!isOld) {
                            String id = armyCommunicator.createArmyOnServer(armyConfiguration);
                            //set id after getting it from the server
                            armyConfiguration.setId(id);
                            player.withArmyConfigurations(armyConfiguration);

                            System.out.println("Successfully created on server");
                            return true;
                        } else {
                            boolean updated = false;
                            updated = armyCommunicator.updateArmyOnServer(armyConfiguration);
                            player.withArmyConfigurations(armyConfiguration);

                            System.out.println("Successfully updated on server");
                            return updated;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        showError(bundle.getString("manager.servererror"));
                    }
                    return false;
                } else {
                    showError(bundle.getString("manager.exists"));
                    return false;
                }
            } else {
                showError(bundle.getString("manager.emptyname"));
                return false;
            }
        } else {
            //gui shows missing units
            noUnits();
            return false;
        }
    }

    /**
     * Load all configs of the current player from the server and save it in the data model
     * Also loads all types of units that are available
     *
     * @param player current player
     */
    public void loadConfigurationsFromServer(Player player) {
        try {
            //get all possible unit types from the server as JSON
            allDifferentUnitTypes = armyCommunicator.getAllUnitTypes();

            //get all armyConfigurations of the user from the server
            ArrayList<JSONObject> jsonArmyConfigurations = armyCommunicator.getAllOwnedArmies();
            System.out.println("Configs loaded from server: " + jsonArmyConfigurations);
            //create configs from JSON
            for (JSONObject jsonArmyConfiguration : jsonArmyConfigurations) {
                ArmyConfiguration armyConfiguration = new ArmyConfiguration();
                armyConfiguration.setName(jsonArmyConfiguration.getString("name"));
                armyConfiguration.setId(jsonArmyConfiguration.getString("id"));

                //create units for data model
                ArrayList<String> unitIDs = new ArrayList<>();
                JSONArray jArray = jsonArmyConfiguration.getJSONArray("units");
                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); ++i) {
                        unitIDs.add(jArray.get(i).toString());
                    }
                }
                armyConfiguration.withUnits(createUnitsFromJSON(unitIDs, allDifferentUnitTypes));

                player.withArmyConfigurations(armyConfiguration);
            }
        } catch (LoginFailedException e) {
            e.printStackTrace();
            showError(bundle.getString("manager.loginerror"));
        }
    }

    /**
     * Method for returning configuration names for a specific player
     *
     * @param player for current player
     * @return names of configurations belonging to current player
     */
    public ArrayList<String> configurationNames(Player player) {
        ArrayList<ArmyConfiguration> armyConfigurations = player.getArmyConfigurations();
        ArrayList<String> armyNames = new ArrayList<String>();
        for (ArmyConfiguration armyConfiguration : armyConfigurations) {
            armyNames.add(armyConfiguration.getName());
        }
        return armyNames;
    }

    /**
     * Method to load a selected configuration from data model and show it in the GUI
     *
     * @param configurationName for the name of the configuration to load
     * @param player            for current player
     */
    public void loadConfiguration(String configurationName, Player player) {
        ArrayList<ArmyConfiguration> armyConfigurations = player.getArmyConfigurations();
        clearUnits();
        for (ArmyConfiguration armyConfiguration : armyConfigurations) {
            if (armyConfiguration.getName().equals(configurationName)) {
                ArrayList<Unit> units = armyConfiguration.getUnits();
                for (Unit unit : units) {
                    addUnit(unit.getId());
                }
            }
        }
        currentConfigName = configurationName;
    }

    /**
     * Method to delete a specific ArmyConfiguration from the server
     *
     * @param armyConfiguration the configuration in the data model that should be deleted
     * @return sucessfullyDeleted boolean
     */
    public boolean deleteConfiguration(ArmyConfiguration armyConfiguration) {
        boolean successfullyDeleted = false;
        if (currentPlayer.getArmyConfigurations().size() > 1) {
            try {
                //remove config from server
                successfullyDeleted = armyCommunicator.deleteArmyOnServer(armyConfiguration.getId());
                //delete config from data model
                currentPlayer.withoutArmyConfigurations(armyConfiguration);
                for (int i = 0; i < configs.size(); ++i) {
                    if (configs.get(i).equals(armyConfiguration.getName())) {
                        configs.remove(i);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showError(bundle.getString("manager.servererror"));
            }
        } else {
            showError(bundle.getString("manager.lastconfig"));
        }
        return successfullyDeleted;
    }

    public ComboBox<String> getConfiglist() {
        return configlist;
    }

    public ObservableList<String> getConfigs() {
        return configs;
    }

    public ArrayList<JSONObject> getAllDifferentUnitTypes() {
        return allDifferentUnitTypes;
    }

}
