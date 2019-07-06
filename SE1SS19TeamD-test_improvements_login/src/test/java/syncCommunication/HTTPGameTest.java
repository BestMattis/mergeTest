package syncCommunication;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import testUtils.JSONTestUtils;

/**
 * Tests the REST Game component.
 */
public class HTTPGameTest {

    /**
     * Test the {@link SynchronousGameCommunicator#openGame(String, int)} method.
     */
    @Test
    public void testOpenGame() {
	String gameName = "My Game";
	int noOfNeededPlayers = 4;

	HttpRequests req = new HttpRequests();
	req.setJsonAdapter((url, json) -> {
	    JSONTestUtils.assertJSON(json, gameName, "name");
	    JSONTestUtils.assertJSON(json, noOfNeededPlayers, "neededPlayer");
	    req.injectResponse(new JSONObject().put("status", "success"));
	});
	SynchronousGameCommunicator gameCommunicator = new SynchronousGameCommunicator(req);
	boolean result = gameCommunicator.openGame(gameName, noOfNeededPlayers);
	Assert.assertTrue("Game creation was successful but openGame() returned false", result);
    }

    /**
     * Test the {@link SynchronousGameCommunicator#joinGame(String) method.
     */
    @Test
    public void testJoinGame() {
	String gameID = "123456789";

	HttpRequests req = this.setupHTTPRequests(gameID);
	SynchronousGameCommunicator gameCommunicator = new SynchronousGameCommunicator(req);
	boolean result = gameCommunicator.joinGame(gameID);
	Assert.assertTrue("Join was successful but joinGame() returned false", result);
    }

    /**
     * Test the {@link SynchronousGameCommunicator#deleteGame(String) method.
     */
    @Test
    public void testDeleteGame() {
	String gameID = "123456789";

	HttpRequests req = this.setupHTTPRequests(gameID);
	SynchronousGameCommunicator gameCommunicator = new SynchronousGameCommunicator(req);
	boolean result = gameCommunicator.deleteGame(gameID);
	Assert.assertTrue("Deletion was successful but deleteGame() returned false", result);
    }

    /**
     * Test the {@link SynchronousGameCommunicator#getAllGames() method.
     */
    @Test
    public void testGetAllGames() {
	String gameID = "123456789";
	HttpRequests req = new HttpRequests();
	req.setJsonAdapter((url, json) -> {
	    req.injectResponse(new JSONObject().put("data", new JSONArray().put(new JSONObject().put("id", gameID)))
		    .put("status", "success"));
	});

	SynchronousGameCommunicator gameCommunicator = new SynchronousGameCommunicator(req);
	ArrayList<JSONObject> result = gameCommunicator.getAllGames();
	Assert.assertEquals("GET Games was successful but getAllGames() returned a wrong game list", gameID,
		result.get(0).get("id"));
    }

    private HttpRequests setupHTTPRequests(String gameID) {
	final AtomicBoolean alreadyCalled = new AtomicBoolean(false);
	HttpRequests req = new HttpRequests();
	req.setJsonAdapter((url, json) -> {

	    if (!alreadyCalled.get()) {
		req.injectResponse(new JSONObject().put("data", new JSONArray().put(new JSONObject().put("id", gameID)))
			.put("status", "success"));
		alreadyCalled.set(true);
	    } else {
		Assert.assertEquals("URL does not reference game", url.substring(url.length() - gameID.length()),
			gameID);
		req.injectResponse(new JSONObject().put("status", "success"));
	    }
	});
	return req;
    }
}