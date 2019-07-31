package gameScreen;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import main.FXMLLoad;
import model.Model;
import model.Unit;

import java.util.ArrayList;

public class GameUnitDisplayController {

    @FXML
    GridPane unitgrid;

    ArrayList<FXMLLoad> udlist;

    /**
     * load the unitpanes or the stats
     */
    public void initialize(){
        udlist = new ArrayList();
        ArrayList<Unit> units = Model.getApp().getCurrentPlayer().getCurrentArmyConfiguration().getUnits();
        for (int i = 0; i < units.size(); i++){
            FXMLLoad fxmlLoad= new FXMLLoad("/gameScreen/littleUnit.fxml");
            ((GameLittleUnitController)fxmlLoad.getController()).setUnit(units.get(i));
            udlist.add(fxmlLoad);
            unitgrid.add(udlist.get(i).getParent(), i%5, i/5);
        }
    }

}
