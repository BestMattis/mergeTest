package syncCommunication;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import testUtils.JSONTestUtils;

/**
 * Tests the REST User component.
 */
public class HTTPUserTest {
    /**
     * Test the {@link SynchronousUserCommunicator#register(String, String)} method.
     */
    @Test
    public void testRegister() {
	String name = "Testname";
	String password = "myPassword";

	HttpRequests req = new HttpRequests();
	req.setJsonAdapter((url, json) -> {
	    JSONTestUtils.assertJSON(json, name, "name");
	    JSONTestUtils.assertJSON(json, password, "password");
	    req.injectResponse(new JSONObject().put("status", "success"));
	});

	SynchronousUserCommunicator userCommunicator = new SynchronousUserCommunicator(req);
	boolean result = userCommunicator.register(name, password);
	Assert.assertTrue("Registration was successful but register() returned false", result);
    }

    /**
     * Test the {@link SynchronousUserCommunicator#logIn(String, String)} method.
     */
    @Test
    public void testLogin() {
	String name = "Testname";
	String password = "myPassword";

	HttpRequests req = new HttpRequests();
	req.setJsonAdapter((url, json) -> {
	    Assert.assertEquals("URL not correct", "/user/login", url);
	    JSONTestUtils.assertJSON(json, name, "name");
	    JSONTestUtils.assertJSON(json, password, "password");
	    req.injectResponse(new JSONObject().put("status", "success"));
	});

	SynchronousUserCommunicator userCommunicator = new SynchronousUserCommunicator(req);
	userCommunicator.logIn(name, password);
    }

    /**
     * Test the {@link SynchronousUserCommunicator#logOut()} method.
     */
    @Test
    public void testLogout() {
	HttpRequests req = new HttpRequests();
	req.setJsonAdapter((url, json) -> {
	    Assert.assertEquals("URL not correct", "/user/logout", url);
	});

	SynchronousUserCommunicator userCommunicator = new SynchronousUserCommunicator(req);
	userCommunicator.logOut();
    }

    /**
     * Test the {@link SynchronousUserCommunicator#getOnlineUsers()} method.
     */
    @Test
    public void testGetAllUsers() {
	HttpRequests req = new HttpRequests();
	req.setJsonAdapter((url, json) -> {
	    Assert.assertEquals("URL not correct", "/user", url);
	    req.injectResponse(
		    new JSONObject().put("data", new JSONArray().put("Alice").put("Bob")).put("status", "success"));
	});

	SynchronousUserCommunicator userCommunicator = new SynchronousUserCommunicator(req);
	ArrayList<String> result = userCommunicator.getOnlineUsers();
	Assert.assertEquals("User list request was successful but getOnlineUsers() returned a wrong list of players", 2,
		result.size());
	Assert.assertEquals("User list request was successful but getOnlineUsers() returned a wrong list of players",
		"Alice", result.get(0));
    }
}
