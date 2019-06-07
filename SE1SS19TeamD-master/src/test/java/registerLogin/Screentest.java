package registerLogin;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import registerLogin.RegisterLoginController;

import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

public class Screentest extends ApplicationTest {

	@Override
	public void start(Stage stage) throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = classLoader.getResource("en-US.properties").openStream();
		ResourceBundle bundle = new PropertyResourceBundle(inputStream);
		FXMLLoader fxmlLoader = new FXMLLoader(RegisterLoginController.class.getResource("registerLogin.fxml"), bundle);
		RegisterLoginController regLogCont = new RegisterLoginController();
		fxmlLoader.setController(regLogCont);
		Parent parent = fxmlLoader.load();
		Scene scene = new Scene(parent);
		stage.setFullScreen(true);
		stage.setTitle(bundle.getString("Applicationtitle"));
		stage.setScene(scene);
		stage.show();
		stage.toFront();
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T find(final String query) {
		return (T) lookup(query).queryAll().iterator().next();
	}

	@Test
	public void successfulRegTest() { // Successful Registration

		clickOn("#nameTextfield");
		write("testname");
		clickOn("#pwTextfield");
		write("testpw");
		clickOn("#regButton");
		Label label = find("#msgLabel");
		Assert.assertEquals(label.getText(), "User registered");
	}

	@Test
	public void falsInputSpaceNameReg() { // Flase Input: Space in username
		clickOn("#nameTextfield");
		write("test name");
		clickOn("#pwTextfield");
		write("testpw");
		clickOn("#regButton");
		Label label = find("#msgLabel");
		Assert.assertEquals(label.getText(), "the use of '  ' is prohibited in username and password");
	}

	@Test
	public void falsInputSpacePwReg() { // False Input: Space in password
		clickOn("#nameTextfield");
		write("testname");
		clickOn("#pwTextfield");
		write("test pw");
		clickOn("#regButton");
		Label label = find("#msgLabel");
		Assert.assertEquals(label.getText(), "the use of '  ' is prohibited in username and password");
	}

	@Test
	public void falsInputEmptyBothReg() { // False Input: neither name or password entered
		clickOn("#nameTextfield");
		write("");
		clickOn("#pwTextfield");
		write("");
		clickOn("#regButton");
		Label label = find("#msgLabel");
		Assert.assertEquals(label.getText(), "");
	}

	@Test
	public void falsInputEmptyNameReg() { // False Input: only name isn't entered
		clickOn("#nameTextfield");
		write("");
		clickOn("#pwTextfield");
		write("testpw");
		clickOn("#regButton");
		Label label = find("#msgLabel");
		Assert.assertEquals(label.getText(), "");
	}

	@Test
	public void falsInputEmptyPwReg() { // False Input: only password isn't entered
		clickOn("#nameTextfield");
		write("testname");
		clickOn("#pwTextfield");
		write("");
		clickOn("#regButton");
		Label label = find("#msgLabel");
		Assert.assertEquals(label.getText(), "");
	}
}