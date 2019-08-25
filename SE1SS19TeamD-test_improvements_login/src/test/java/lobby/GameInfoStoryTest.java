package lobby;

import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import registerLogin.LoginRegisterTestUtils;

/**
 * Tests the Lobby Info Stories.
 */
public class GameInfoStoryTest extends ApplicationTest {

    // Lobby UI Elements

    private Button logoutButton;
    private ImageView infoLogo;

    // Game Info UI Elements

    private AnchorPane rootNode;
    private TabPane creditPane;
    private Button backButton;

    /**
     * Setup stage and start application.
     *
     * @param primaryStage the stage to display the GUI.
     */
    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        new AdvancedWarsApplication().start(primaryStage);
    }

    /**
     * Test the Info User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-57">TD-57</a>.
     */
    @Test
    public void testInfoButton() {
        /*
         * =============== SITUATION ===============
         */

        // Alice has logged in and waits in the lobby.

        LoginRegisterTestUtils.loginTestUser(this);

        this.loadLobbyUIElements();

        /*
         * =============== ACTION ===============
         */

        // Alice clicks the Info Button.

        this.clickOn(this.infoLogo);

        /*
         * =============== RESULT ===============
         */

        // Alice's client opens a list of project details / developers / ...

        this.loadGameInfoUIElements();

        FxAssert.verifyThat(this.rootNode, NodeMatchers.isVisible());
        Assert.assertEquals("Wrong number of tabs", 2, this.creditPane.getTabs().size());

        /*
         * =============== SHUTDOWN ===============
         */

        this.clickOn(this.backButton);

        this.clickOn(this.logoutButton);
    }

    private void loadLobbyUIElements() {
        this.infoLogo = this.lookup("#logo").queryAs(ImageView.class);
        this.logoutButton = this.lookup("#logout").queryButton();
    }

    private void loadGameInfoUIElements() {
        this.rootNode = this.lookup("#trpane").queryAs(AnchorPane.class);
        this.creditPane = this.lookup("#credpane").queryAs(TabPane.class);
        this.backButton = this.lookup("#back").queryButton();
    }
}
