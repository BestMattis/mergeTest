package gameScreen;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import main.FXMLLoad;

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
        for (int i = 0; i < 10; i++){
            udlist.add(new FXMLLoad("/gameScreen/littleUnit.fxml"));
            unitgrid.add(udlist.get(i).getParent(), i%5, i/5);
        }
    }

}
