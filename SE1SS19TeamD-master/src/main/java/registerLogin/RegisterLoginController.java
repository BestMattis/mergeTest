package registerLogin;

import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import main.AdvancedWarsApplication;
import syncCommunication.HttpRequests;
import syncCommunication.SynchronousUserCommunicator;
import syncCommunication.RESTExceptions.RegistrationFailedException;

public class RegisterLoginController {
	@FXML
	private AnchorPane basePane;
	@FXML
	private Label topLabel;
	@FXML
	private Label nameLabel;
	@FXML
	private Label pwLabel;
	@FXML
	public Label msgLabel;	//public for the tests
	@FXML
	public TextField nameTextfield;
	@FXML
	public PasswordField pwTextfield;
	@FXML
	private Button logButton;
	@FXML
	public Button regButton;

	String NAME;
	String PW;
	
	@SuppressWarnings("static-access")
	static AdvancedWarsApplication app = new AdvancedWarsApplication().getInstance();

	/*
	 * called when the registerLogin scene is loaded. set the methods for clicking
	 * on the buttons and the user input via text fields
	 */
	public void initialize() {
		msgLabel.setVisible(false);

		/* textfield Listener */
		nameTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
			msgLabel.setVisible(false);
			try {
				NAME = newValue;
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		pwTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
			msgLabel.setVisible(false);
			try {
				PW = newValue;
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		/* Button-Methoden aufrufe */
		logButton.setOnAction(e -> loginUser());
		regButton.setOnAction(e -> {
			try {
				registerUser();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}

	/*
	 * called from logButton loads the Login-module into the registerLoginScreen
	 */
	private void loginUser() {
		app.goToLobby(); // ersetzen mit Login
	}

	/*
	 * called from regButton takes NAME and PW and registers a user on the server.
	 * filters user input, shows error messages on false input or failed registration
	 */
	private void registerUser() throws IOException {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = null;
		inputStream = classLoader.getResource("en-US.properties").openStream();
		ResourceBundle bundle = new PropertyResourceBundle(inputStream);
		String userName = NAME;
		String userPw = PW;
		String errorMsg = null;

		Boolean success = false;

		if (userName != null && userName.length() > 0 && !userName.contains(" ") && // dont allow empty user names or
																					// pws
				userPw != null && userPw.length() > 0 && !userPw.contains(" ")) { // dont allow spaces

			nameTextfield.clear();
			pwTextfield.clear();
			msgLabel.setVisible(true);

			try {
				HttpRequests hr = new HttpRequests();
				success = new SynchronousUserCommunicator(hr).register(userName, userPw);
			} catch (RegistrationFailedException e) {
				errorMsg = e.getMessage();
			}

			if (success) {
				msgLabel.setTextFill(Color.LIGHTGREEN); // registrierung successful
				msgLabel.setText(bundle.getString("regLog.SuccRegister"));
			} else {
				msgLabel.setTextFill(Color.RED); // registrierung unsuccessful
				msgLabel.setText(bundle.getString("regLog.FailedRegistration")+": "+errorMsg);
			}
		}
		if (userName != null && userPw != null) {
			if (userName.contains(" ") || userPw.contains(" ")) { // errorMsg false input
				msgLabel.setVisible(true);
				msgLabel.setTextFill(Color.RED);
				msgLabel.setText(bundle.getString("regLog.SpaceErrorRegister"));
			}
		}
	}
}
