package gameScreen;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.Model;
import model.Unit;

public class GameLittleUnitController {
    private Unit unit;
    private double maxHP;
    private double maxMP;
    private Model model;
    private boolean highlighted=false;
    private final int YTOPDISTANCE = 80;
    private final int YTILE = 96;
    private final int XTILE = 96;

    @FXML
    ProgressBar hp;
    @FXML
    ProgressBar mp;
    @FXML
    Label type;
    @FXML
    AnchorPane pane;

    /**
     * @return current unit linked to this Controller.
     */
    public Unit getUnit() {
	return unit;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * @param unit set linked Unit to unit.
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
        if(unit != null){
            maxHP = unit.getHp();
            maxMP = unit.getMp();
            unit.addPropertyChangeListener(Unit.PROPERTY_hp,evt -> update());
            unit.addPropertyChangeListener(Unit.PROPERTY_mp,evt -> update());
            update();
        }
    }
    /**
     * Simulates a click on the unit on the unitCanvas
     */
    public void selectTarget() {
        if(unit!=null){
            ScrollPane gameFieldPane = model.getGameFieldController().getGameFieldPane();
            gameFieldPane.setHvalue((gameFieldPane.getHmax()/258) * unit.getPosX());
            gameFieldPane.setVvalue((gameFieldPane.getVmax()/258) * unit.getPosY());

            int xPos = unit.getPosX() * XTILE;
            int yPos = YTOPDISTANCE + (unit.getPosY() * YTILE);
            model.getGameFieldController().clickOnUnitCanvas(xPos,yPos);
        }
    }

    /**
     * Highlights the unitCard when the corresponding unit is selected.
     */
    public void highlight() {
        if(!highlighted){
            pane.setScaleX(1.1);
            pane.setScaleY(1.1);
            pane.setBorder(new Border(
                    new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
            highlighted = true;
        }
    }
    
    /**
     Reverse the highlight of the unitCard when the corresponding unit is selected.
     */
    public void reverseHighlight() {
	    if(highlighted){
            pane.setScaleX(1);
            pane.setScaleY(1);
            pane.setBorder(new Border(new BorderStroke(new Color(0,0,0,0),
                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            highlighted = false;
        }
    }
    
    /**
     * Used to update the unitCard.
     */
    private void update() {
        if(unit != null){
            if(unit.getHp() <1){
                hp.setProgress(0);
                mp.setProgress(0);
                type.setText("RIP: "+unit.getType());
                unit=null;
                pane.setStyle("-fx-background-color: rgba(145,0,0,0.5)");
            } else {
                double hpValue = unit.getHp();
                double mpValue = unit.getMp();
                hp.setProgress(hpValue/maxHP);
                mp.setProgress(mpValue/maxMP);
                type.setText(unit.getType());
            }
        }
    }
}
