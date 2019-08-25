package registerLogin;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import static org.hamcrest.CoreMatchers.is;

/**
 * Tests the Login User Stories.
 */
public class LoginStoriesTest extends ApplicationTest {

    private Stage primaryStage;
    private ResourceBundle bundle;

    // Login UI elements

    private TextInputControl nameText;
    private TextInputControl passwordText;
    private Label msgLabel;
    private Button loginButton;

    // Lobby UI elements

    private Button logoutButton;

    /**
     * Setup stage and start application.
     *
     * @param primaryStage the stage to display the GUI.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        new AdvancedWarsApplication().start(primaryStage);
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream inputStream = classLoader.getResource("en-US.properties").openStream();
        this.bundle = new PropertyResourceBundle(inputStream);

        this.loadLoginUIElements();
    }

    /**
     * Test the Successful Login User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-20">TD-20</a>.
     */
    @Test
    public void testSuccessfulLogin() {
        /*
         * =============== SITUATION ===============
         */

        // Alice has typed in her username and password.

        String name = LoginRegisterTestUtils.getTestUserName();
        String password = LoginRegisterTestUtils.getTestUserPassword();

        this.enterCredentials(name, password);

        /*
         * =============== ACTION ===============
         */

        // Alice clicks the login button.

        this.clickOn(this.loginButton);

        /*
         * =============== RESULT ===============
         */

        // The Game Client opens the lobby screen.

        this.loadLobbyUIElements();

        FxAssert.verifyThat(this.primaryStage.getScene(), is(AdvancedWarsApplication.getInstance().getLobbyScene()));

        /*
         * =============== SHUTDOWN ===============
         */

        this.clickOn(this.logoutButton);
    }

    /**
     * Test the Unsuccessful Login User Story as described here:
     * <https://jira.uniks.de/browse/TD-23">TD-23</a>.
     */
    @Test
    public void testUnsuccessfulLogin() {

        String name = LoginRegisterTestUtils.getTestUserName();
        String password = LoginRegisterTestUtils.getTestUserPassword() + "__wrong";

        /*
         * =============== SITUATION ===============
         */

        // Alice has typed in her username, but a wrong password.

        this.enterCredentials(name, password);

        /*
         * =============== ACTION ===============
         */

        // Alice clicks the "Login" button.

        this.clickOn(this.loginButton);

        /*
         * =============== RESULT ===============
         */

        // An error message appears in the Login / Register View.

        String expectedMsgPrefix = this.bundle.getString("regLog.FailedLogin");
        String actualMsg = this.msgLabel.getText();
        Assert.assertTrue("Wrong massage displayed", actualMsg.startsWith(expectedMsgPrefix));

    }

    private void loadLoginUIElements() {
        this.nameText = this.lookup("#nameTextfield").queryTextInputControl();
        this.passwordText = this.lookup("#pwTextfield").queryTextInputControl();
        this.msgLabel = this.lookup("#msgLabel").queryAs(Label.class);
        this.loginButton = this.lookup("#logButton").queryButton();
    }

    private void loadLobbyUIElements() {
        this.logoutButton = this.lookup("#logout").queryButton();
    }

    private void enterCredentials(String name, String password) {
        this.clickOn(this.nameText);
        this.write(name);
        this.clickOn(this.passwordText);
        this.write(password);
    }
}
