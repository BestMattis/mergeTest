package registerLogin;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/*
 * This Class is now only used for testing purposes
 */

public class RegisterLogin extends Application {

	/*
	 * Loads the registerLogin.fxml and displays it on the stage loads resource
	 * bundle for javafx Strings and set RegisterLoginController as Controller
	 * blablablablablablablablablablablablablablabla
	 * @param primaryStage a Stage
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream inputStream = classLoader.getResource("en-US.properties").openStream();
			ResourceBundle bundle = new PropertyResourceBundle(inputStream);
			FXMLLoader fxmlLoader = new FXMLLoader(RegisterLoginController.class.getResource("registerLogin.fxml"),
					bundle);
			RegisterLoginController regLogCont = new RegisterLoginController();
			fxmlLoader.setController(regLogCont);
			Parent parent = fxmlLoader.load();
			Scene scene = new Scene(parent);
			primaryStage.setFullScreen(true);
			primaryStage.setTitle(bundle.getString("Applicationtitle"));
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
