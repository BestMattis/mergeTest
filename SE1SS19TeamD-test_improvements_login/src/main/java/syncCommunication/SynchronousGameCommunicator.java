package syncCommunication;

import org.json.JSONException;
import org.json.JSONObject;
import syncCommunication.RESTExceptions.GameIdNotFoundException;
import syncCommunication.RESTExceptions.GameLobbyCreationFailedException;
import syncCommunication.RESTExceptions.LoginFailedException;

import java.util.ArrayList;

public class SynchronousGameCommunicator extends SynchronousCommunicator {

    private HTTPGameHandler gHandler;

    /**
     * @param httpReq Used to assign the user. Same HttpRequests leads to the same logged in user.
     */
    public SynchronousGameCommunicator(HttpRequests httpReq) {
        super(httpReq);
        gHandler = new HTTPGameHandler(httpReq);
    }

    /**
     * Open a new game on the server.
     *
     * @param gameName  Name of the game
     * @param userCount Amount of users that are allowed. 2 or 4.
     * @return String of the opened game's id
     * @throws LoginFailedException if the user was not logged in
     * @throws GameLobbyCreationFailedException if no game was created
     */
    public String openGame(String gameName, int userCount)
            throws GameLobbyCreationFailedException, LoginFailedException {

        try {
            return gHandler.openGameLobby(gHandler.getUserKey(), gameName, userCount);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Join a game by the ID given.
     *
     * @param gameID ID of the game on the server
     * @return boolean true if successful else false
     * @throws LoginFailedException    if the user was not logged in
     * @throws GameIdNotFoundException if no game was found with that id
     */
    public boolean joinGame(String gameID)
            throws GameIdNotFoundException, LoginFailedException {

        try {
            return gHandler.joinGameLobby(gHandler.getUserKey(), gameID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete a game by the ID given.
     *
     * @param gameID ID of the game on the server
     * @return boolean true if successful else false
     * @throws LoginFailedException    if the user was not logged in
     * @throws GameIdNotFoundException if no game was found with that id
     */
    public boolean deleteGame(String gameID)
            throws GameIdNotFoundException, LoginFailedException {

        try {
            return gHandler.deleteGameLobby(gHandler.getUserKey(), gameID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get the list of all games currently open.
     *
     * @return Return a list of JSOnObjects of all games.
     * @throws LoginFailedException if the user was not logged in
     */
    public ArrayList<JSONObject> getAllGames() throws LoginFailedException {
        ArrayList<JSONObject> gamesList = null;

        try {
            gamesList = gHandler.getAllGameLobbies(gHandler.getUserKey());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return gamesList;
    }

    public HTTPGameHandler getGameHandler() {
        return gHandler;
    }

    public String getUserKey() {
        return gHandler.getUserKey();
    }
}
