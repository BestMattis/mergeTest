package model;

import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import registerLogin.LoginRegisterTestUtils;

public class TwinModelTest extends ApplicationTest {

    private Model model;

    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        AdvancedWarsApplication awa = new AdvancedWarsApplication();
        awa.start(primaryStage);
        model = awa.model;
    }

    @Test
    public void twinTest() {
        LoginRegisterTestUtils.loginForOfflineTest(this, model);
        Player player = model.getApp().getCurrentPlayer();
        Model model2 = new Model();
        model2.getApp().setCurrentPlayer(new Player());
        Assert.assertEquals(model.getApp().getCurrentPlayer(), player);
    }

}
