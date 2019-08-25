package lobby;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import main.AdvancedWarsApplication;

public class OptionsController {

    @FXML
    protected Slider mSpeed;
    @FXML
    private AnchorPane base;
    @FXML
    private Button leave;

    /**
     * this methode sets the actions for the sliders
     */
    public void initialize() {
        leave.setOnAction(t -> hide());
        mSpeed.setBlockIncrement(2);
        mSpeed.setOnMouseReleased(t -> setSpeed());
    }

    /**
     * this methode writes the new speed into a variable in the application
     */
    private void setSpeed() {
        AdvancedWarsApplication.getInstance().setMovementSpeed(((int) mSpeed.getValue()) * 2);
    }

    /**
     * hide the options
     */
    public void hide() {
        base.setVisible(false);
    }

    /**
     * show the options
     */
    public void show() {
        base.setVisible(true);
    }

}
