package registerLogin;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testfx.framework.junit.ApplicationTest;

import asyncCommunication.WebSocketComponent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import model.Model;
import model.Player;
import syncCommunication.HttpRequests;
import syncCommunication.SynchronousArmyCommunicator;
import syncCommunication.SynchronousGameCommunicator;
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
	    	LoginRegisterTestUtils.startupOfflineTest(test, name, password, new JSONArray(), new JSONArray());
	}
	
	/**
	 * Log in 2 users and let both join the same game
	 *
	 * @param test the ApplicationTest calling this method
	 */
	public static void createGameForOfflineTest(ApplicationTest test){
	    	String myGameId = "69";
	    	
	    	JSONArray initialUsers = new JSONArray().put("BobTeamD");
	    	JSONArray initialGames = new JSONArray().put(
	    		new JSONObject().put("joinedPlayer", 1).put("name", "My Game")
	    		.put("id", myGameId).put("neededPlayer", 2));
	    	
	    	LoginRegisterTestUtils.startupOfflineTest(test, LoginRegisterTestUtils.TEST_USER_NAME,
	    		LoginRegisterTestUtils.TEST_USER_PASSWORD, initialUsers, initialGames);
		
		HttpRequests requests = Model.getPlayerHttpRequestsHashMap().get(Model.getApp().getCurrentPlayer());
		SynchronousGameCommunicator synchronousGameCommunicator = new SynchronousGameCommunicator(requests);
		synchronousGameCommunicator.setJsonAdapter((method, url, json) -> {
			if(method.equals("POST") && url.equals("/game")) {
				JSONObject response = new JSONObject();
				response.put("status", "success");
				JSONObject responseData = new JSONObject();
				responseData.put("gameId", myGameId);
				response.put("data", responseData);
				synchronousGameCommunicator.injectResponse(response);
			}
			else if(url.equals("/game")) {
	        	    	JSONObject response = new JSONObject();
	        	    	response.put("status", "success");
	        	    	response.put("data", initialGames);
	        	    	synchronousGameCommunicator.injectResponse(response);
		        }
		});
		
		Label gameLabel = LoginRegisterTestUtils.performWhileNotPresent(test);
		test.clickOn(gameLabel);
	}

	private static void startupOfflineTest(ApplicationTest test, String name, String password, JSONArray initialUsers, JSONArray initialGames) {
	    Player currentPlayer = new Player().setApp(Model.getApp())
	    	.setName(name);
	    HttpRequests httpRequests = new HttpRequests();
	    Model.getApp().setCurrentPlayer(currentPlayer);
	    Model.getPlayerHttpRequestsHashMap().put(currentPlayer, httpRequests);
	    Model.setWebSocketComponent(
	    	new WebSocketComponent(name, "myuserkey", false));
	    
	    SynchronousUserCommunicator userCom =
	    	new SynchronousUserCommunicator(httpRequests);
	    SynchronousGameCommunicator gameCom =
		    new SynchronousGameCommunicator(httpRequests);
	    SynchronousArmyCommunicator armyCom =
		    new SynchronousArmyCommunicator(httpRequests);
	    userCom.setJsonAdapter((method, url, json) -> {
	        if(url.equals("/user/login")) {
        	    	JSONObject response = new JSONObject();
        	    	response.put("status", "success");
        	    	JSONObject responseData = new JSONObject();
        	    	responseData.put("userKey", "myuserkey");
        	    	response.put("data", responseData);
        	    	userCom.injectResponse(response);
	        }
	        
	        else if(url.equals("/user")) {
        	    	JSONObject response = new JSONObject();
        	    	response.put("status", "success");
        	    	response.put("data", initialUsers);
        	    	userCom.injectResponse(response);
	        }
	        
	        else if(url.equals("/game")) {
        	    	JSONObject response = new JSONObject();
        	    	response.put("status", "success");
        	    	response.put("data", initialGames);
        	    	gameCom.injectResponse(response);
	        }
	        
	        else if(method.equals("GET") && url.equals("/army/units")) {
		    JSONObject response = new JSONObject("{\"data\":["
				+ "{\"canAttack\":[\"Infantry\",\"Bazooka Trooper\",\"Jeep\",\"Light Tank\",\"Heavy Tank\"],\"mp\":3,\"hp\":10,\"id\":\"5cc051bd62083600017db3b6\",\"type\":\"Infantry\"},"
				+ "{\"canAttack\":[\"Jeep\",\"Light Tank\",\"Heavy Tank\",\"Chopper\"],\"mp\":2,\"hp\":10,\"id\":\"5cc051bd62083600017db3b7\",\"type\":\"Bazooka Trooper\"},"
				+ "{\"canAttack\":[\"Infantry\",\"Bazooka Trooper\",\"Jeep\"],\"mp\":8,\"hp\":10,\"id\":\"5cc051bd62083600017db3b8\",\"type\":\"Jeep\"},"
				+ "{\"canAttack\":[\"Infantry\",\"Bazooka Trooper\",\"Jeep\",\"Light Tank\",\"Heavy Tank\"],\"mp\":6,\"hp\":10,\"id\":\"5cc051bd62083600017db3b9\",\"type\":\"Light Tank\"},"
				+ "{\"canAttack\":[\"Infantry\",\"Bazooka Trooper\",\"Jeep\",\"Light Tank\",\"Heavy Tank\",\"Chopper\"],\"mp\":4,\"hp\":10,\"id\":\"5cc051bd62083600017db3ba\",\"type\":\"Heavy Tank\"},"
				+ "{\"canAttack\":[\"Infantry\",\"Bazooka Trooper\",\"Jeep\",\"Light Tank\",\"Heavy Tank\"],\"mp\":6,\"hp\":10,\"id\":\"5cc051bd62083600017db3bb\",\"type\":\"Chopper\"}],"
				+ "\"message\":\"\",\"status\":\"success\"}");
		    armyCom.injectResponse(response);
		}
	        
	        else if(method.equals("GET") && url.equals("/army")) {
		    JSONObject response = new JSONObject().put("status", "success").put("data", new JSONArray());
		    armyCom.injectResponse(response);
		}
	        
	        else if(method.equals("POST") && url.equals("/army")) {
	            JSONObject response = new JSONObject();
	            response.put("status", "success");
	            JSONObject responseData = new JSONObject();
	            responseData.put("id", "my-army-id");
	            response.put("data", responseData);
	            armyCom.injectResponse(response);
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

	private static Label performWhileNotPresent(ApplicationTest test) {
	    while (!test.lookup("#gameName").queryAllAs(Label.class).stream()
		.anyMatch(x -> x.getText().equals("My Game"))) {
		try {
		    Thread.sleep(100);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	    return (Label) test.lookup("#gameName").queryAllAs(Label.class)
			.stream()
			.filter(x -> x.getText().equals("My Game"))
			.findFirst().get();
	}
}
