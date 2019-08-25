package registerLogin;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class RegisterInvalidInputTest extends ApplicationTest {

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
     * Test with user name {@code test name} and password {@code unknown}.
     */
    @Test
    public void testUnsuccessfulRegistration_spaceInName() {
        String actualMsg = this.tryRegistrationWith("test name", "unknown");
        String expectedMsg = this.bundle.getString("regLog.SpaceErrorRegister");
        Assert.assertEquals("Wrong error message", expectedMsg, actualMsg);
    }

    /**
     * Test with user name {@code name} and password {@code not known}.
     */
    @Test
    public void testUnsuccessfulRegistration_spaceInPassword() {
        String actualMsg = this.tryRegistrationWith("name", "not known");
        String expectedMsg = this.bundle.getString("regLog.SpaceErrorRegister");
        Assert.assertEquals("Wrong error message", expectedMsg, actualMsg);
    }

    /**
     * Test with user name {@code <empty>} and password {@code unknown}.
     */
    @Test
    public void testUnsuccessfulRegistration_emptyName() {
        String actualMsg = this.tryRegistrationWith("", "unknown");
        Assert.assertEquals("Wrong error message", "", actualMsg);
    }

    /**
     * Test with user name {@code name} and password {@code <empty>}.
     */
    @Test
    public void testUnsuccessfulRegistration_emptyPassword() {
        String actualMsg = this.tryRegistrationWith("name", "");
        Assert.assertEquals("Wrong error message", "", actualMsg);
    }

    /**
     * Test with user name {@code <empty>} and password {@code <empty>}.
     */
    @Test
    public void testUnsuccessfulRegistration_emptyNameAndPassword() {
        String actualMsg = this.tryRegistrationWith("", "");
        Assert.assertEquals("Wrong error message", "", actualMsg);
    }

    private void loadUIElements() {
        this.nameText = this.lookup("#nameTextfield").queryTextInputControl();
        this.passwordText = this.lookup("#pwTextfield").queryTextInputControl();
        this.msgLabel = this.lookup("#msgLabel").queryAs(Label.class);
        this.registerButton = this.lookup("#regButton").queryButton();
    }

    private String tryRegistrationWith(String name, String password) {
        this.clickOn(this.nameText);
        this.write(name);
        this.clickOn(this.passwordText);
        this.write(password);
        this.clickOn(this.registerButton);

        return this.msgLabel.getText();
    }
}
