package registerLogin;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;

import static org.hamcrest.CoreMatchers.is;

public class LoginTest extends ApplicationTest {

    static Stage pri;

    public void start (Stage stage) throws Exception {
        new AdvancedWarsApplication().start(stage);
        pri = stage;
    }

    @SuppressWarnings("unchecked")
    public <T extends Node> T find(final String query) {
        return (T) lookup(query).queryAll().iterator().next();
    }

    @Test
    public void loginTest(){
        clickOn("#nameTextfield");
        write("testname");
        clickOn("#pwTextfield");
        write("testpw");
        clickOn("#regButton");

        clickOn("#nameTextfield");
        write("testname");
        clickOn("#pwTextfield");
        write("testpw");
        clickOn("#logButton");

        FxAssert.verifyThat(pri.getScene(), is(AdvancedWarsApplication.getInstance().getLobbyScene()));

    }

    @Test
    public void falsInputSpaceNameReg() { // Flase Input: Space in username
        clickOn("#nameTextfield");
        write("test name");
        clickOn("#pwTextfield");
        write("testpw");
        clickOn("#logButton");
        Label label = find("#msgLabel");
        Assert.assertEquals(label.getText(), "the use of '  ' is prohibited in username and password");
    }

    @Test
    public void falsInputSpacePwReg() { // False Input: Space in password
        clickOn("#nameTextfield");
        write("testname");
        clickOn("#pwTextfield");
        write("test pw");
        clickOn("#logButton");
        Label label = find("#msgLabel");
        Assert.assertEquals(label.getText(), "the use of '  ' is prohibited in username and password");
    }

    @Test
    public void falsInputEmptyBothReg() { // False Input: neither name or password entered
        clickOn("#nameTextfield");
        write("");
        clickOn("#pwTextfield");
        write("");
        clickOn("#logButton");
        Label label = find("#msgLabel");
        Assert.assertEquals(label.getText(), "");
    }

    @Test
    public void falsInputEmptyNameReg() { // False Input: only name isn't entered
        clickOn("#nameTextfield");
        write("");
        clickOn("#pwTextfield");
        write("testpw");
        clickOn("#logButton");
        Label label = find("#msgLabel");
        Assert.assertEquals(label.getText(), "");
    }

    @Test
    public void falsInputEmptyPwReg() { // False Input: only password isn't entered
        clickOn("#nameTextfield");
        write("testname");
        clickOn("#pwTextfield");
        write("");
        clickOn("#logButton");
        Label label = find("#msgLabel");
        Assert.assertEquals(label.getText(), "");
    }
}
