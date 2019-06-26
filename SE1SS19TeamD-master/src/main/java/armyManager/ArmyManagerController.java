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
import main.FXMLLoad;

import java.nio.Buffer;
import java.util.ArrayList;

public class ArmyManagerController {

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
    Label count;
    @FXML
    VBox errorbox;
    @FXML
    Label except;
    @FXML
    GridPane base;
    @FXML
    Button back;

    ObservableList<Parent> units;
    UnitCardController[] unitCardControllers = new UnitCardController[15];

    /**
     * initialize the drag and drop
     */
    public void initialize(){
        units = FXCollections.observableArrayList();
        unitlist.setItems(units);
        droppane.setOnDragOver(t -> dragOver(t));
        droppane.setOnDragDropped(t -> dragdrop(t));
        delete.setOnDragOver(t -> dragOver(t));
        delete.setOnDragDropped(t -> dragdropdelete(t));
        delete.setOnMouseClicked(t -> clickclear(t));
        trash.fitHeightProperty().bind(delete.heightProperty());
        errorbox.setVisible(false);
        base.setVisible(false);
        back.setOnAction(t -> hide());
    }

    /** clearing the configurator on rightclick on the delete label
     * @param event for checking for a right click
     */
    public void clickclear(MouseEvent event){
        if (event.getButton() == MouseButton.SECONDARY){
            clearUnits();
        }
    }

    /** Add a new Unit to the list to choose from
     * @param name the id of the unit
     */
    public void newUnitCard(String name){
        FXMLLoad unit = new FXMLLoad("/armyManager/UnitCard.fxml", new UnitCardController());
        unit.getController(UnitCardController.class).setUnit(name);
        units.add(unit.getParent());
        unit.getController(UnitCardController.class).setParent(unitlist);
    }

    /** changes icon if draged over
     * @param event the dragevent
     */
    private void dragOver(DragEvent event){
        /* accept it only if it is  not dragged from the same node
         * and if it has a string data */
        if (event.getGestureSource() != droppane &&
                event.getDragboard().hasString()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }

    /**The mehtod to manage the drop to add a unit to the config
     * @param event of the dragged unitcard
     */
    private void dragdrop(DragEvent event){
        /* if there is a string data on dragboard, read it and use it */
        AnchorPane pane = (AnchorPane)event.getGestureSource();
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

    /**Add a unit to the config
     * @param id specifing the unit
     * @return the addiotion of the unit to the army was successful
     */
    public boolean addUnit(String id){
        boolean success = true;
        int index = 0;
        System.out.println(unitgrid.getChildren().size());
        L:
        for(int i = 0; i<unitgrid.getChildren().size(); i++) {
            if (unitgrid.getChildren().get(i).getClass() == AnchorPane.class) {
                AnchorPane pane = (AnchorPane) unitgrid.getChildren().get(i);
                if (pane.getChildren().isEmpty()) {
                    success = true;
                    UnitCardController unitCardController = new UnitCardController();
                    FXMLLoad unitfxml = new FXMLLoad("/armyManager/UnitCard.fxml", unitCardController, false);
                    unitfxml.getController(UnitCardController.class).setUnit(id);
                    unitCardControllers[i] = unitCardController;
                    pane.getChildren().add(index, unitfxml.getParent());
                    unitfxml.getController(UnitCardController.class).setParent(pane);
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
    private void deletelostControllers(){
        for(int i = 0; i < unitgrid.getChildren().size(); i++){
            if (unitgrid.getChildren().get(i).getClass() == AnchorPane.class) {
                AnchorPane pane = (AnchorPane) unitgrid.getChildren().get(i);
                if (pane.getChildren().isEmpty()) {
                    unitCardControllers[i] = null;
                }
            }
        }
        setTheCount();

    }

    /** show a red error message for 3 seconds
     * @param text the error to display
     */
    public void showError(String text){
        except.setText(text);
        Timeline ex = new Timeline(
                new KeyFrame(Duration.ZERO, event -> {errorbox.setVisible(true); except.setVisible(true);}),
                new KeyFrame(Duration.seconds(3.0), event ->  {errorbox.setVisible(false); except.setVisible(false);})
        );
        ex.setCycleCount(1);
        ex.play();
    }

    /**
     * updates the unitnumber label
     */
    private void setTheCount(){
        int i = 0;
        for (UnitCardController unitCardController:unitCardControllers){
            if(unitCardController != null){
                i++;
            }
        }
        count.setText(i + "/10");
    }

    /** get the config as a List of unit-IDs
     * @return the string of unit ids
     */
    public ArrayList<String> getUnits(){
        ArrayList<String> ids = new ArrayList<>();
        for (UnitCardController unitCardController:unitCardControllers){
            if (unitCardController != null) {
                ids.add(unitCardController.getID());
            }
        }
        return ids;
    }

    /**
     * removes all units from the config
     */
    public void clearUnits(){
        for(int i = 0; i < unitgrid.getChildren().size(); i++){
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
    public void noUnits(){
        for(int i = 0; i < unitgrid.getChildren().size(); i++){
            if (unitgrid.getChildren().get(i).getClass() == AnchorPane.class) {
                AnchorPane pane = (AnchorPane) unitgrid.getChildren().get(i);
                if (pane.getChildren().isEmpty()) {
                    pane.setStyle("-fx-background-color: rgba(255, 0, 0, 0.4);");
                }
            }
        }
    }

    /**Method to delete a single unit with dragdrop
     * @param event of the dragged unitcard
     */
    private void dragdropdelete(DragEvent event){
        AnchorPane pane = (AnchorPane) event.getGestureSource();
        if (pane.getParent().getClass() == AnchorPane.class){
            AnchorPane parent = (AnchorPane) pane.getParent();
            parent.getChildren().remove(0);
            deletelostControllers();
        }
    }

    /**
     * shows the manager
     */
    public void show(){
        base.setVisible(true);
    }

    /**
     * hides the manager
     */
    public void hide(){
        base.setVisible(false);
    }

}
