package registerLogin;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Tests the Register User Stories.
 */
public class RegisterStoriesTest extends ApplicationTest {

    private ResourceBundle bundle;
    private TextInputControl nameText;
    private TextInputControl passwordText;
    private Label msgLabel;
    private Button registerButton;

    /**
     * Setup stage and start application.
     *
     * @param primaryStage the stage to display the GUI.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        new AdvancedWarsApplication().start(primaryStage);
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream inputStream = classLoader.getResource("en-US.properties").openStream();
        this.bundle = new PropertyResourceBundle(inputStream);

        this.loadUIElements();
    }

    /**
     * Test the Successful Registration User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-6">TD-6</a>.
     */
    @Test
    public void testSuccessfulRegistration() {
        /*
         * =============== SITUATION ===============
         */

        // Alice has started the game client.

        // ---- done by the start(Stage)-Method ----

        // The Login / Register screen is open.

        // ---- done on Application startup ----

        // Alice has typed in a username that is not present and a password.

        String name = "Alice" + new Random().nextInt();
        String password = "myPassword";

        this.enterCredentials(name, password);

        /*
         * =============== ACTION ===============
         */

        // Alice clicks the register button.

        this.clickOn(this.registerButton);

        /*
         * =============== RESULT ===============
         */

        // The GUI shows a "User registered" message.

        Assert.assertThat(this.msgLabel, LabeledMatchers.hasText(this.bundle.getString("regLog.SuccRegister")));

        // The user / password fields are empty.

        Assert.assertThat(this.nameText, TextInputControlMatchers.hasText(""));
        Assert.assertThat(this.passwordText, TextInputControlMatchers.hasText(""));

        // Alice may now log in.

        // ---- nothing to test here ----
    }

    /**
     * Test the Unsuccessful Registration User Story as described here:
     * <a href="https://jira.uniks.de/browse/TD-24">TD-24</a>.
     */
    @Test
    public void testUnsuccessfulRegistration() {

        String name = LoginRegisterTestUtils.getTestUserName();
        String password = LoginRegisterTestUtils.getTestUserPassword();

        /*
         * =============== SITUATION ===============
         */

        // Alice has typed in her username that still exists and a password.

        this.enterCredentials(name, password);

        /*
         * =============== ACTION ===============
         */

        // Alice clicks the "Register" button.

        this.clickOn(this.registerButton);

        /*
         * =============== RESULT ===============
         */

        // An error message appears in the Login / Register View.

        String expectedMsgPrefix = this.bundle.getString("regLog.FailedRegistration");
        String actualMsg = this.msgLabel.getText();
        Assert.assertTrue("Wrong massage displayed", actualMsg.startsWith(expectedMsgPrefix));
    }

    private void loadUIElements() {
        this.nameText = this.lookup("#nameTextfield").queryTextInputControl();
        this.passwordText = this.lookup("#pwTextfield").queryTextInputControl();
        this.msgLabel = this.lookup("#msgLabel").queryAs(Label.class);
        this.registerButton = this.lookup("#regButton").queryButton();
    }

    private void enterCredentials(String name, String password) {
        this.clickOn(this.nameText);
        this.write(name);
        this.clickOn(this.passwordText);
        this.write(password);
    }
}
