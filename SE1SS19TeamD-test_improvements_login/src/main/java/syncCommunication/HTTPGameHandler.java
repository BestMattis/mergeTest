package syncCommunication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import syncCommunication.RESTExceptions.GameIdNotFoundException;
import syncCommunication.RESTExceptions.GameLobbyCreationFailedException;
import syncCommunication.RESTExceptions.LoginFailedException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class HTTPGameHandler {

    private HttpRequests hr;

    HTTPGameHandler(HttpRequests httpReq) {
        hr = httpReq;
    }

    /*
     * Opens a game on the server as the user via the userKey.
     * Returns the String of the id, once it was opened correctly.
     * Throws JSONException, GameLobbyCreationFailedException and LoginFailedException
     */
    public String openGameLobby(String userKey, String name, int playerCount)
            throws JSONException, GameLobbyCreationFailedException, LoginFailedException {

        JSONObject gameData = new JSONObject();
        gameData.put("name", name);
        gameData.put("neededPlayer", playerCount);

        try {
            JSONObject response = hr.postJsonAs(userKey, gameData, "/game");
            if (response.getString("status").equals("success")) {
                return response.getJSONObject("data").getString("gameId");
            } else {

                String error = response.getString("message");

                if (error.equals("Log in first")) {
                    throw new LoginFailedException(error);
                } else {
                    throw new GameLobbyCreationFailedException(error);
                }
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Lists all games on the server. UserKey required
     * Returns an JSON ArrayList of all open games.
     * Throws JSONException and LoginFailedException
     */
    public ArrayList<JSONObject> getAllGameLobbies(String userKey) throws LoginFailedException, JSONException {

        try {
            JSONObject response = hr.getJsonAsUser(userKey, "/game");
            if (response.getString("status").equals("success")) {
                JSONArray jArray = response.getJSONArray("data");
                ArrayList<JSONObject> lobbyList = new ArrayList<>();

                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); ++i) {
                        lobbyList.add(jArray.getJSONObject(i));
                    }
                }
                return lobbyList;
            } else {
                throw new LoginFailedException(response.getString("message"));
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * Joins the user behind the userKey into a game with the gameID
     * Returns true if successful.
     * Throws JSONException, GameIdNotFoundException and LoginFailedException
     */
    public boolean joinGameLobby(String userKey, String gameID, boolean observer)
            throws LoginFailedException, JSONException, GameIdNotFoundException {

        try {
            if (!checkID(userKey, gameID)) {
                throw new GameIdNotFoundException("There is no game with that ID");
            }
            JSONObject response;
            if (observer) {
                response = hr.getJsonAsUser(userKey, "/game/" + gameID + "?spectator=true");
            } else {
                response = hr.getJsonAsUser(userKey, "/game/" + gameID);
            }

            if (response.getString("status").equals("success")) {
                return true;

            } else {
                String error = response.getString("message");

                if (error.equals("Log in first")) {
                    throw new LoginFailedException(error);
                } else {
                    throw new GameIdNotFoundException(response.getString(error));
                }

            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * Deletes a game with the gameID. UserKey required.
     * Returns true if successful.
     * Throws JSONException, GameIdNotFoundException and LoginFailedException
     */
    public boolean deleteGameLobby(String userKey, String gameID)
            throws LoginFailedException, JSONException, GameIdNotFoundException {

        try {
            if (!checkID(userKey, gameID)) {
                throw new GameIdNotFoundException("There is no game with that ID");
            }

            JSONObject response = hr.deleteAsUser(userKey, "/game/" + gameID);

            if (response.getString("status").equals("success")) {
                return true;

            } else {
                String error = response.getString("message");

                if (error.equals("Log in first")) {
                    throw new LoginFailedException(error);
                } else {
                    throw new GameIdNotFoundException(response.getString(error));
                }
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * Checks if a game with the given ID is currently open.
     * Returns true if any are found.
     * Throws JSONException and LoginFailedException
     */
    public boolean checkID(String userKey, String gameID) throws LoginFailedException, JSONException {
        ArrayList<JSONObject> gamesList;
        boolean idExists = false;

        gamesList = getAllGameLobbies(userKey);
        for (JSONObject json : gamesList) {
            if (json.get("id").equals(gameID)) {
                idExists = true;
                break;
            }
        }

        return idExists;
    }

    String getUserKey() {
        return hr.getUserKey();
    }
}
