package armyManager;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


public class UnitCardController {

    @FXML
    AnchorPane base;
    @FXML
    Label unitname;
    @FXML
    ImageView picture;

    private String id;
    private Parent parent;
    private ArmyManagerController parentCon;

    /**
     * initialize the dragdrop
     */
    public void initialize(){
        base.setOnDragDetected(t -> drag(t));
        base.setOnDragDone(t -> dragdone(t));
    }

    /** gret the id of the unit of this card
     * @return the id of the unit this card has
     */
    public String getID(){
        return id;
    }

    /** called when the dragdrop is finished
     * @param event of the dragged unitcard
     */
    private void dragdone(DragEvent event) {
        if (event.isDropCompleted() == false){
            base.setVisible(true);
        }
        event.consume();
    }

    /** set the unit of this card
     * @param unitID the Id of the unit this card is going to have
     */
    public void setUnit(String unitID){

        String propertiespath = "en-US.properties";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = null;
        ResourceBundle bundle = null;
        URL loc = null;
        try {
            inputStream = classLoader.getResource(propertiespath).openStream();
            bundle = new PropertyResourceBundle(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        id = unitID;
        String name = "unknown Unit: " + unitID;
        if (unitID.equals("5cc051bd62083600017db3b7")) {
            name = bundle.getString("unit.bazTrooper");
        } else if (unitID.equals("5cc051bd62083600017db3bb")) {
            name = bundle.getString("unit.chopper");
        } else if (unitID.equals("5cc051bd62083600017db3ba")) {
            name = bundle.getString("unit.heavyT");
        } else if (unitID.equals("5cc051bd62083600017db3b6")) {
            name = bundle.getString("unit.Infantry");
        } else if (unitID.equals("5cc051bd62083600017db3b8")) {
            name = bundle.getString("unit.jeep");
        } else if (unitID.equals("5cc051bd62083600017db3b9")) {
            name = bundle.getString("unit.lightTy");
        }
        unitname.setText(name);

    }

    /** method to start the dragdrop, set the icon
     * @param t the event of the dragged card
     */
    private void drag(MouseEvent t) {
        Dragboard db = base.startDragAndDrop(TransferMode.ANY);
        db.setDragView(base.snapshot(null, null));
        /* Put a string on a dragboard */
        ClipboardContent content = new ClipboardContent();
        content.putString(id);
        db.setContent(content);

        if (parent.getClass() == AnchorPane.class){
            base.setVisible(false);
        }

        t.consume();
    }

    /** set the parent of this unitcard
     * @param parent the parent of this card
     */
    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public void setParentCon(ArmyManagerController con) {
        this.parentCon = con;
    }
}
