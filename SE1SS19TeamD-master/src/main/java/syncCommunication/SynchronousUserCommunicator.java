package syncCommunication;

import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.RESTExceptions.RegistrationFailedException;
import org.json.JSONException;

import java.util.ArrayList;

public class SynchronousUserCommunicator extends SynchronousCommunicator{

    private UserHandler uHandler;

    /**
     * @param httpReq Used to assign the user. Same HttpRequests leads to the same logged in user.
     */
    public SynchronousUserCommunicator(HttpRequests httpReq) {
        super(httpReq);
        uHandler = new UserHandler(httpReq);
    }

    /**
     * Register the user.
     *
     * @param username Username
     *
     * @param password Password
     *
     * @return Return true if successful.
     *
     * @exception RegistrationFailedException if the server can't perform the registration
     */
    public boolean register(String username, String password)
            throws RegistrationFailedException {

        try {
            return uHandler.registerUser(username, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Log user in and save their userKey.
     *
     * @param username Username
     *
     * @param password Password
     *
     * @return Return true if the userKey changed.
     *
     * @exception LoginFailedException if the user was not logged in
     */
    public boolean logIn(String username, String password) throws LoginFailedException {
        String newUserKey = null;

        try {
            newUserKey = uHandler.loginAs(username, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return newUserKey != null;
    }

    /**
     * Log user out and set userKey back to null.
     *
     * @return Return true if the userKey changed.
     *
     * @exception LoginFailedException if the user was not logged in
     */
    public boolean logOut() throws LoginFailedException {
        boolean loggedOut = false;

        try {
            loggedOut = uHandler.logOut();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return loggedOut;
    }

    /**
     * Get the list of all users currently logged in.
     *
     * @return Returns a list of Strings of userNames.
     *
     * @exception LoginFailedException if the user was not logged in
     */
    public ArrayList<String> getOnlineUsers() throws LoginFailedException {
        ArrayList<String> userList = null;

        try {
            userList = uHandler.getAllUsers(uHandler.getUserKey());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userList;
    }

    public UserHandler getUserHandler() {
        return uHandler;
    }

    public String getUserKey() {
        return uHandler.getUserKey();
    }
}
