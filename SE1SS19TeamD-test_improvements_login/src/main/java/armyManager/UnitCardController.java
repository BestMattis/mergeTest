package armyManager;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import org.json.JSONObject;

import java.util.ArrayList;


public class UnitCardController {

    @FXML
    AnchorPane base;
    @FXML
    Label unitname;
    @FXML
    Canvas picture;

    private String id;
    private Parent parent;
    private ArmyManagerController parentCon;
    private int magnification = 2;

    /**
     * initialize the dragdrop
     */
    public void initialize() {
        base.setOnDragDetected(t -> drag(t));
        base.setOnDragDone(t -> dragdone(t));
    }

    /**
     * gret the id of the unit of this card
     *
     * @return the id of the unit this card has
     */
    public String getID() {
        return id;
    }

    /**
     * called when the dragdrop is finished
     *
     * @param event of the dragged unitcard
     */
    private void dragdone(DragEvent event) {
        if (event.isDropCompleted() == false) {
            base.setVisible(true);
        }
        event.consume();
    }

    /**
     * set the unit of this card
     *
     * @param unitID the Id of the unit this card is going to have
     */
    public void setUnit(String unitID, ArrayList<JSONObject> allDifferentUnitTypes) {

        id = unitID;

        for (JSONObject unitType : allDifferentUnitTypes) {
            if (unitID.equals(unitType.getString("id"))) {
                String name = unitType.getString("type");
                unitname.setText(name);
                try {
                    String name2 = name.replaceAll("\\s+", "");
                    Image img = new Image(("/textures/units/Red/RED_" + name2 + ".png"), 64 * magnification, 128 * magnification, true, false);
                    WritableImage wimg = new WritableImage(32 * magnification, 32 * magnification);
                    wimg.getPixelWriter().setPixels(0, 0, 32 * magnification, 32 * magnification, img.getPixelReader(), 0, 0);
                    picture.getGraphicsContext2D().drawImage((Image) wimg, (picture.getWidth() / 2) - (wimg.getWidth() / 2), (picture.getHeight() / 2) - (wimg.getHeight() / 2));
                } catch (Exception e) {
                }
                break;
            }
        }
    }

    /**
     * method to start the dragdrop, set the icon
     *
     * @param t the event of the dragged card
     */
    private void drag(MouseEvent t) {
        Dragboard db = base.startDragAndDrop(TransferMode.ANY);
        db.setDragView(base.snapshot(null, null));
        /* Put a string on a dragboard */
        ClipboardContent content = new ClipboardContent();
        content.putString(id);
        db.setContent(content);

        if (parent.getClass() == AnchorPane.class) {
            base.setVisible(false);
        }

        t.consume();
    }

    /**
     * set the parent of this unitcard
     *
     * @param parent the parent of this card
     */
    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public void setParentCon(ArmyManagerController con) {
        this.parentCon = con;
    }
}
