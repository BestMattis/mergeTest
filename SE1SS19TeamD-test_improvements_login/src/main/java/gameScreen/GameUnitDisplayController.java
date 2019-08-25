package gameScreen;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import main.FXMLLoad;
import model.Model;
import model.Player;
import model.Unit;

import java.util.ArrayList;
import java.util.List;

public class GameUnitDisplayController {

    private Model model;

    @FXML
    GridPane unitgrid;
    
    private List<GameLittleUnitController> unitCardControllers;

    public GameUnitDisplayController(Model model){
        this.model = model;
    }

    /**
     * load the unitpanes or the stats
     */
    public void initialize(){
        if(model.getApp().getCurrentPlayer().getCurrentUnits()!=null && model.getApp().getCurrentPlayer().getCurrentUnits().size()==10){
            Platform.runLater(()->reloadUnits());
        } else {
            model.getApp().getCurrentPlayer().addPropertyChangeListener(Player.PROPERTY_currentUnits, evt -> {
                if(this.unitCardControllers == null || this.unitCardControllers.size()==0){
                    Runnable run = ()->{
                        reloadUnits();
                    };
                    Platform.runLater(run);
                }
            });
        }
    }
    
    public List<GameLittleUnitController> getUnitCardControllers() {
	return this.unitCardControllers;
    }

    public void reverseHighlight(){
        for(GameLittleUnitController unitController:unitCardControllers){
            unitController.reverseHighlight();
        }
    }

    /**
     * Used to show the Units in the unitgrid. Starts with loading when every Unit is available.
     */
    private void reloadUnits() {
        if(this.unitCardControllers==null){
            this.unitCardControllers = new ArrayList<>();
        }
        ArrayList<Unit> unitsNew = model.getApp().getCurrentPlayer().getCurrentUnits();
        if(unitsNew.size()==10){
            for (int i = 0; i < unitsNew.size(); i++){
                FXMLLoad fxmlLoad= new FXMLLoad("/gameScreen/littleUnit.fxml");
                GameLittleUnitController controller = (GameLittleUnitController)fxmlLoad.getController();
                this.unitCardControllers.add(controller);
                controller.setModel(model);
                controller.setUnit(unitsNew.get(i));
                unitgrid.add(fxmlLoad.getParent(), i%5, i/5);
            }
        }
    }
}
