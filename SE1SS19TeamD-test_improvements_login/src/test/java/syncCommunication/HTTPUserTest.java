package syncCommunication;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.RESTExceptions.RegistrationFailedException;
import testUtils.JSONTestUtils;

import java.util.ArrayList;

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
        req.setJsonAdapter((method, url, json) -> {
            JSONTestUtils.assertJSON(json, name, "name");
            JSONTestUtils.assertJSON(json, password, "password");
            req.injectResponse(new JSONObject().put("status", "success"));
        });

        SynchronousUserCommunicator userCommunicator = new SynchronousUserCommunicator(req);
        boolean result = false;
        try {
            result = userCommunicator.register(name, password);
        } catch (RegistrationFailedException e) {
            e.printStackTrace();
        }
        Assert.assertTrue("Registration was successful but register() returned false", result);
    }

    /**
     * Test the {@link SynchronousUserCommunicator#registerTempUser()} method.
     */
    @Test
    public void testRegisterTempUser() {
        String tempUser = "TempUser";
        HttpRequests req = new HttpRequests();
        req.setJsonAdapter((method, url, json) -> {
            Assert.assertEquals("URL not correct", "/user/temp", url);
            JSONObject responseData = new JSONObject();
            responseData.put("password", tempUser).put("name", tempUser);
            req.injectResponse(new JSONObject().put("status", "success").put("data", responseData));
        });
        SynchronousUserCommunicator userCommunicator = new SynchronousUserCommunicator(req);
        JSONObject result = null;
        try {
            result = userCommunicator.registerTempUser();
        } catch (RegistrationFailedException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull("Temp User was't received", result);
        String receivedTempUser = result.getString("name");
        Assert.assertEquals("Temp User wasn't received correctly", tempUser, receivedTempUser);
    }

    /**
     * Test the {@link SynchronousUserCommunicator#logIn(String, String)} method.
     */
    @Test
    public void testLogin() {
        String name = "Testname";
        String password = "myPassword";

        HttpRequests req = new HttpRequests();
        req.setJsonAdapter((method, url, json) -> {
            Assert.assertEquals("URL not correct", "/user/login", url);
            JSONTestUtils.assertJSON(json, name, "name");
            JSONTestUtils.assertJSON(json, password, "password");
            JSONObject responseData = new JSONObject();
            responseData.put("userKey", "my-userkey");
            req.injectResponse(new JSONObject().put("status", "success").put("data", responseData));
        });

        SynchronousUserCommunicator userCommunicator = new SynchronousUserCommunicator(req);

        boolean result = false;
        try {
            result = userCommunicator.logIn(name, password);
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }
        Assert.assertTrue("Login was successful but logIn() returned false", result);
    }

    /**
     * Test the {@link SynchronousUserCommunicator#logOut()} method.
     */
    @Test
    public void testLogout() {
        HttpRequests req = new HttpRequests();
        req.setJsonAdapter((method, url, json) -> {
            Assert.assertEquals("URL not correct", "/user/logout", url);
            req.injectResponse(new JSONObject().put("status", "success"));
        });

        SynchronousUserCommunicator userCommunicator = new SynchronousUserCommunicator(req);

        boolean result = false;
        try {
            result = userCommunicator.logOut();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }
        Assert.assertTrue("Logout was successful but logOut() returned false", result);
    }

    /**
     * Test the {@link SynchronousUserCommunicator#getOnlineUsers()} method.
     */
    @Test
    public void testGetAllUsers() {
        HttpRequests req = new HttpRequests();
        req.setJsonAdapter((method, url, json) -> {
            Assert.assertEquals("URL not correct", "/user", url);
            req.injectResponse(
                    new JSONObject().put("data", new JSONArray().put("Alice").put("Bob")).put("status", "success"));
        });

        SynchronousUserCommunicator userCommunicator = new SynchronousUserCommunicator(req);
        ArrayList<String> result = null;
        try {
            result = userCommunicator.getOnlineUsers();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("User list request was successful but getOnlineUsers() returned a wrong list of players", 2,
                result.size());
        Assert.assertEquals("User list request was successful but getOnlineUsers() returned a wrong list of players",
                "Alice", result.get(0));
    }
}
