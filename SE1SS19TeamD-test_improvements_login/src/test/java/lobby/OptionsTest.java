package lobby;

import gameController.gameLoop.sprites.UnitSprite;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.Model;
import model.Unit;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import registerLogin.LoginRegisterTestUtils;

public class OptionsTest extends ApplicationTest {

    private Model model;
    private AdvancedWarsApplication awa;

    public void start(Stage stage) throws Exception {
        awa = new AdvancedWarsApplication();
        awa.start(stage);
        model = awa.model;
        AdvancedWarsApplication.offlineTest = true;
        LoginRegisterTestUtils.loginForOfflineTest(this, model);
    }

    @Test
    public void optionsGuiTest() {
        Assert.assertFalse(awa.getLobbyCon().optionsFXML.getParent().isVisible());
        clickOn("#options");
        Assert.assertTrue(awa.getLobbyCon().optionsFXML.getParent().isVisible());
        clickOn("#optionsBack");
        Assert.assertFalse(awa.getLobbyCon().optionsFXML.getParent().isVisible());
    }

    @Test
    public void optionsToVariableTest() {
        clickOn("#options");
        moveTo("#mSpeedSlider");
        Assert.assertEquals(awa.getLobbyCon().optionsFXML.getController(OptionsController.class).mSpeed.getValue(), 4, 0);
        Assert.assertEquals(awa.getMovementSpeed(), 8);
        awa.getLobbyCon().optionsFXML.getController(OptionsController.class).mSpeed.setValue(6);
        clickOn("#mSpeedSlider");
        Assert.assertEquals(awa.getLobbyCon().optionsFXML.getController(OptionsController.class).mSpeed.getValue(), 6, 0);
        Assert.assertEquals(awa.getMovementSpeed(), 12);
    }

    @Test
    public void optionsToSpriteTest() {
        UnitSprite unitSprite = new UnitSprite(new Unit(), "textures/units/Black/BLACK_BazookaTrooper.png", 1, new Canvas());
        clickOn("#options");
        moveTo("#mSpeedSlider");
        Assert.assertEquals(unitSprite.getSubPixelMovementSteps(), 8);
        awa.getLobbyCon().optionsFXML.getController(OptionsController.class).mSpeed.setValue(6);
        clickOn("#mSpeedSlider");
        Assert.assertEquals(unitSprite.getSubPixelMovementSteps(), 12);

    }


}
