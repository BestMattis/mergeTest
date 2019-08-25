package lobby;

import javafx.scene.control.Button;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.query.EmptyNodeQueryException;
import registerLogin.LoginRegisterTestUtils;

/**
 * Test the Logout User Stories.
 */
public class LogoutStoriesTest extends ApplicationTest {

    // Lobby UI Elements

    private Button logoutButton;


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
     * Test the Logout User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-14">TD-14</a>.
     */
    @Test
    public void testLogout() {
        /*
         * =============== SITUATION ===============
         */

        // Alice has logged in and waits in the lobby.

        LoginRegisterTestUtils.loginTestUser(this);

        this.loadLobbyUIElements();

        /*
         * =============== ACTION ===============
         */

        // Alice clicks the Logout Button.

        this.clickOn(this.logoutButton);

        /*
         * =============== RESULT ===============
         */

        // Alice's game client displays the login screen.

        try {
            this.lookup("#logButton").queryButton();
        } catch (EmptyNodeQueryException e) {
            Assert.fail("Client does not display Login Screen");
        }
    }

    private void loadLobbyUIElements() {
        this.logoutButton = this.lookup("#logout").queryButton();
    }
}
