package registerLogin;

import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import syncCommunication.HttpRequests;
import syncCommunication.SynchronousUserCommunicator;
import syncCommunication.RESTExceptions.LoginFailedException;

/**
 * Utility class to supply a test user.
 *
 */
public class LoginRegisterTestUtils {

    private static final String TEST_USER_NAME = "AliceTeamD";
    private static final String TEST_USER_PASSWORD = "geheim";

    private static boolean alreadyChecked = false;

    /**
     * Return the name of the test user.
     * 
     * @return the name as String
     */
    public static String getTestUserName() {
	LoginRegisterTestUtils.ensureTestUserExists();
	return LoginRegisterTestUtils.TEST_USER_NAME;
    }
    
    /**
     * Return the password of the test user.
     * 
     * @return the password as String
     */
    public static String getTestUserPassword() {
	LoginRegisterTestUtils.ensureTestUserExists();
	return LoginRegisterTestUtils.TEST_USER_PASSWORD;
    }
    
    /**
     * Log in the test user on the given test instance.
     * 
     * @param test the test instance to log in the user
     */
    public static void loginTestUser(ApplicationTest test)
    {
	LoginRegisterTestUtils.loginUser(
		test,
		LoginRegisterTestUtils.getTestUserName(),
		LoginRegisterTestUtils.getTestUserPassword());
    }
    
    /**
     * Log in a user on the given test instance.
     * 
     * @param test the test instance to log in the user
     * @param name the name of the user to log in
     * @param password the password of the user to log in
     */
    public static void loginUser(ApplicationTest test, String name, String password) {
	TextInputControl nameText = test.lookup("#nameTextfield").queryTextInputControl();
	TextInputControl passwordText = test.lookup("#pwTextfield").queryTextInputControl();
	Button loginButton = test.lookup("#logButton").queryButton();
	
	nameText.setText(name);
	passwordText.setText(password);
	
	test.clickOn(loginButton);
    }

    private static void ensureTestUserExists() {
	if (!LoginRegisterTestUtils.alreadyChecked) {
	    HttpRequests requests = new HttpRequests();
	    SynchronousUserCommunicator communicator = new SynchronousUserCommunicator(requests);
	    try {
		communicator.logIn(TEST_USER_NAME, TEST_USER_PASSWORD);
		communicator.logOut();
	    } catch (LoginFailedException e) {
		communicator.register(TEST_USER_NAME, TEST_USER_PASSWORD);
	    }

	    LoginRegisterTestUtils.alreadyChecked = true;
	}
    }
}
