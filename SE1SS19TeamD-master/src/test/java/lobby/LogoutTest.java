package lobby;

import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.App;
import model.Model;
import syncCommunication.HttpRequests;
import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.RESTExceptions.RegistrationFailedException;
import syncCommunication.SynchronousUserCommunicator;

public class LogoutTest extends ApplicationTest {
	@Override
	public void start(Stage stage) throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = classLoader.getResource("en-US.properties").openStream();
		ResourceBundle bundle = new PropertyResourceBundle(inputStream);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/lobby/LobbyScreen.fxml"), bundle);
		Parent parent = fxmlLoader.load();
		Scene scene = new Scene(parent);
		stage.setFullScreen(true);
		stage.setTitle(bundle.getString("application.title"));
		stage.setScene(scene);
		stage.show();
		stage.toFront();
		inputStream.close();
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T find(final String query) {
		return (T) lookup(query).queryAll().iterator().next();
	}

	@SuppressWarnings("static-access")
	@Test
	public void successfulRegTest() {
		App app = Model.getInstance().getApp();
		HttpRequests hr = new HttpRequests();
		SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);
		String username = "RandomUserName51595";
		String password = "RandomUserName12";
		try {
			uComm.register(username, password);
			uComm.logIn(username, password);
		} catch (RegistrationFailedException | LoginFailedException e) {
			e.printStackTrace();
		}
		Model.getInstance().getPlayerHttpRequestsHashMap().put(app.getCurrentPlayer(), hr);

		Assert.assertNotNull(uComm.getUserKey());
		System.out.println(uComm.getUserKey());
		clickOn("#logout");
		System.out.println(uComm.getUserKey());
		Assert.assertNull(uComm.getUserKey());
		Assert.assertNull(Model.getInstance().getApp().getCurrentPlayer());	// check if data model got reset
		
	}
}