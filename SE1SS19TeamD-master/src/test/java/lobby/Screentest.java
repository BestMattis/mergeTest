package lobby;

import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;

import static org.hamcrest.CoreMatchers.is;


public class Screentest extends ApplicationTest {

    static Stage pri;

    public void start (Stage stage) throws Exception {
        new AdvancedWarsApplication().start(stage);
        pri = stage;
    }

    @Test
    public void test() {
        FxAssert.verifyThat(true, is(pri.isFullScreen()));
        FxAssert.verifyThat("Addwars", is(pri.getTitle()));
        FxAssert.verifyThat(pri, is(AdvancedWarsApplication.getInstance().primaryStage));
    }

}
