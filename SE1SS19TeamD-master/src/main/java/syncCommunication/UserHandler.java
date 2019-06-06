package syncCommunication;

import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.RESTExceptions.RegistrationFailedException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class UserHandler {

    private HttpRequests hr;

    UserHandler(HttpRequests httpReq) {
        hr = httpReq;
    }

    /*
     * Registers a new user on the server.
     * Returns true if successful.
     * Throws JSONException and RegistrationFailedException
     */
    public boolean registerUser(String username, String password)
            throws JSONException, RegistrationFailedException {

        JSONObject userData = new JSONObject();
        userData.put("name", username);
        userData.put("password", password);

        try {
            JSONObject response = hr.postJSON(userData, "/user");
            if (response.getString("status").equals("success")) {
                return true;
            } else {
                throw new RegistrationFailedException(response.getString("message"));
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * Lists all users currently logged in. UserKey required.
     * Returns ArrayList of all playerNames.
     * Throws JSONException and LoginFailedException
     */
    public ArrayList<String> getAllUsers(String userKey) throws LoginFailedException, JSONException {
        if (userKey == null) {
            throw new LoginFailedException("Log in first");
        }
        try {
            JSONObject response = hr.getAsUser(userKey, "/user");

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
