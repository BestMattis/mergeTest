package registerLogin;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testfx.framework.junit.ApplicationTest;

import asyncCommunication.WebSocketComponent;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import model.Model;
import model.Player;
import syncCommunication.HttpRequests;
import syncCommunication.SynchronousUserCommunicator;
import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.RESTExceptions.RegistrationFailedException;

/**
 * Utility class to supply a test user.
 *
 */
public class LoginRegisterTestUtils {

	private static final String TEST_USER_NAME = "TeamDTestUser";
	private static final String TEST_USER_PASSWORD = "geheim";
	
	/**
	 * Return the name of the test user.
	 * 
	 * @return the name as String
	 */
	public static String getTestUserName() {
		return LoginRegisterTestUtils.TEST_USER_NAME;
	}

	/**
	 * Return the password of the test user.
	 * 
	 * @return the password as String
	 */
	public static String getTestUserPassword() {
		return LoginRegisterTestUtils.TEST_USER_PASSWORD;
	}

	/**
	 * Log in the test user on the given test instance.
	 * 
	 * @param test
	 *            the test instance to log in the user
	 */
	public static void loginTestUser(ApplicationTest test) {
		LoginRegisterTestUtils.ensureUserExists(LoginRegisterTestUtils.TEST_USER_NAME,
			LoginRegisterTestUtils.TEST_USER_PASSWORD);
	    	LoginRegisterTestUtils.loginUser(test, LoginRegisterTestUtils.TEST_USER_NAME,
			LoginRegisterTestUtils.TEST_USER_PASSWORD);
	}

	/**
	 * Log in a user on the given test instance.
	 * 
	 * @param test
	 *            the test instance to log in the user
	 * @param name
	 *            the name of the user to log in
	 * @param password
	 *            the password of the user to log in
	 */
	public static void loginUser(ApplicationTest test, String name, String password) {
		TextInputControl nameText = test.lookup("#nameTextfield").queryTextInputControl();
		TextInputControl passwordText = test.lookup("#pwTextfield").queryTextInputControl();
		Button loginButton = test.lookup("#logButton").queryButton();

		nameText.setText(name);
		passwordText.setText(password);

		test.clickOn(loginButton);
	}
	
	/**
	 * Log in the test user for offline tests on the given test instance.
	 * 
	 * @param test
	 *            the test instance to log in the user
	 */
	public static void loginForOfflineTest(ApplicationTest test) {
	    LoginRegisterTestUtils.loginForOfflineTest(test, 
		    LoginRegisterTestUtils.TEST_USER_NAME,
		    LoginRegisterTestUtils.TEST_USER_PASSWORD);
	}
	
	/**
	 * Log in a user for offline tests on the given test instance.
	 * 
	 * @param test
	 *            the test instance to log in the user
	 * @param name
	 *            the name of the user to log in
	 * @param password
	 *            the password of the user to log in
	 */
	public static void loginForOfflineTest(ApplicationTest test, String name, String password) {
	    	Player currentPlayer = new Player().setApp(Model.getApp())
			.setName(name);
		HttpRequests httpRequests = new HttpRequests();
		Model.getApp().setCurrentPlayer(currentPlayer);
		Model.getPlayerHttpRequestsHashMap().put(currentPlayer, httpRequests);
		Model.setWebSocketComponent(
			new WebSocketComponent(name, "myuserkey", false));
		
		SynchronousUserCommunicator communicator =
			new SynchronousUserCommunicator(httpRequests);
		
		communicator.setJsonAdapter((url, json) -> {
		    if(url.equals("/user/login")) {
			JSONObject response = new JSONObject();
			response.put("status", "success");
			JSONObject responseData = new JSONObject();
			responseData.put("userKey", "myuserkey");
			response.put("data", responseData);
			communicator.injectResponse(response);
		    }
		    
		    else if(url.equals("/user")) {
			JSONObject response = new JSONObject();
			response.put("status", "success");
			JSONArray responseData = new JSONArray();
			response.put("data", responseData);
			communicator.injectResponse(response);
		    }
		    
		    else if(url.equals("/game")) {
			JSONObject response = new JSONObject();
			response.put("status", "success");
			JSONArray responseData = new JSONArray();
			response.put("data", responseData);
		    }
		});
		
		LoginRegisterTestUtils.loginUser(test, name, password);
	}

	private static void ensureUserExists(String username, String password) {
		HttpRequests requests = new HttpRequests();
		SynchronousUserCommunicator communicator = new SynchronousUserCommunicator(requests);
		try {
			communicator.logIn(username, password);
			communicator.logOut();
		} catch (LoginFailedException e) {
			try {
				communicator.register(username, password);
			} catch (RegistrationFailedException e1) {
				e1.printStackTrace();
			}
		}
	}
}
