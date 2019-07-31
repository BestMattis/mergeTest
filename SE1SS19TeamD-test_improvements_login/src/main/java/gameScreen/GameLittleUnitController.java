package gameScreen;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.Unit;

public class GameLittleUnitController {
    private Unit unit;
    private double maxHP;
    private double maxMP;

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

    /**
     * @param unit set linked Unit to unit.
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
        if(unit != null){
            maxHP = unit.getHp();
            maxMP = unit.getMp();
            unit.addPropertyChangeListener(evt -> update());
            unit.addPropertyChangeListener(Unit.PROPERTY_selectedBy, evt -> highlight());
            update();
        }
    }

    /**
     * Highlights the unitCard when the corresponding  unit is selected.
     */
    private void highlight() {
        if(unit.getSelectedBy() != null){
            pane.setScaleX(1.1);
            pane.setScaleY(1.1);
            pane.setBorder(new Border(new BorderStroke(Color.WHITE,
                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        } else {
            pane.setScaleX(1);
            pane.setScaleY(1);
            pane.setBorder(new Border(new BorderStroke(new Color(0,0,0,0),
                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        }
    }

    /**
     * Used to update the unitCard.
     */
    private void update(){
        if(unit != null){
            double hpValue = unit.getHp();
            double mpValue = unit.getMp();
            hp.setProgress(maxHP/hpValue);
            mp.setProgress(maxMP/mpValue);
            type.setText(unit.getType());

        }
    }
}
