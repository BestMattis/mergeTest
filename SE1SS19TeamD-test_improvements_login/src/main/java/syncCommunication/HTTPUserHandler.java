package syncCommunication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.RESTExceptions.RegistrationFailedException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class HTTPUserHandler {

    private HttpRequests hr;

    HTTPUserHandler(HttpRequests httpReq) {
        hr = httpReq;
    }

    /*
     * Registers a new user on the server.
     * Returns true if successful.
     * Throws JSONException and RegistrationFailedException.
     */
    public boolean registerUser(String username, String password)
            throws JSONException, RegistrationFailedException {

        JSONObject userData = new JSONObject();
        userData.put("name", username);
        userData.put("password", password);

        try {
            JSONObject response = hr.postJson(userData, "/user");
            if (response.getString("status").equals("success")) {
                return true;
            } else {
                throw new RegistrationFailedException(response.getString("message"));
            }

        } catch (ExecutionException | InterruptedException | LoginFailedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * Registers a new temporary user on the server.
     * Returns true if successful.
     * Throws JSONException and RegistrationFailedException.
     */
    public JSONObject registerTempUser()
            throws JSONException, RegistrationFailedException {

        try {
            JSONObject response = hr.getJson("/user/temp");
            if (response.getString("status").equals("success")) {
                JSONObject tempUser = response.getJSONObject("data");
                return tempUser;

            } else {
                throw new RegistrationFailedException(response.getString("message"));
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Lists all users currently logged in. UserKey required.
     * Returns ArrayList of all playerNames.
     * Throws JSONException and LoginFailedException
     */
    public ArrayList<String> getAllUsers(String userKey) throws LoginFailedException, JSONException {
        try {
            JSONObject response = hr.getJsonAsUser(userKey, "/user");

            if (response.getString("status").equals("success")) {
                JSONArray jArray = response.getJSONArray("data");
                ArrayList<String> userList = new ArrayList<>();

                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); ++i) {
                        userList.add(jArray.getString(i));
                    }
                    return userList;

                } else {
                    return null;
                }
            } else {
                throw new LoginFailedException(response.getString("message"));
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * Logs a user into the server.
     * Returns the assigned userKey as a String.
     * Throws JSONException and LoginFailedException
     */
    public String loginAs(String username, String password) throws JSONException, LoginFailedException {
        hr.loginAs(username, password);
        return hr.getUserKey();
    }

    /*
     * Logs a user out of the server. UserKey required.
     * Returns true if successful.
     * Throws JSONException and LoginFailedException
     */
    public boolean logOut() throws LoginFailedException, JSONException {
        hr.logOut();
        return hr.getUserKey() == null;
    }

    String getUserKey() {
        return hr.getUserKey();
    }
}
