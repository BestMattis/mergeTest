package syncCommunication;

import syncCommunication.RESTExceptions.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

public class TestREST {



    /*
     * Testing the endUser API. Not as extensive tests as the ones for the UserHandler
     * and GameLobbyHandler classes, since SynchronousGameCommunicator is essentially just executing
     * their methods but regulates them and stores information like the userKey for
     * easier and safer method calls.
     */
    @Test
    public void testUserApi() {
        HttpRequests hr = new HttpRequests();

        SynchronousGameCommunicator gComm = new SynchronousGameCommunicator(hr);
        SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);

        String username = "aVeryUniqueUsername" + new Random().nextInt();
        String userPassword = "1235813";

        // Two registrations, one redundant to see if the same exception of UserHandler still fires
        boolean reg1 = false;
        boolean reg2 = false;
        System.out.println("Registration:");

        try {
            reg1 = uComm.register(username, userPassword);
            reg2 = uComm.register(username, userPassword);
        } catch (RegistrationFailedException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(reg1);
        Assert.assertFalse(reg2);

        // Login twice, same as above with registration
        boolean login1 = false;
        boolean login2 = false;
        System.out.println("Login:");

        try {
            login1 = uComm.logIn(username, userPassword);
            login2 = uComm.logIn(username, userPassword + "Typo");
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(login1);
        Assert.assertFalse(login2);

        // Getting all users, without userKey unlike with raw UserHandler
        ArrayList<String> userList = null;

        try {
            userList = uComm.getOnlineUsers();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(userList);
        System.out.println(userList);
        System.out.println("Opening game");

        // Open two games, again, no userKey required
        boolean gameOpened = false;

        try {
            gameOpened = gComm.openGame("The coolest game on the block", 2);
        } catch (GameLobbyCreationFailedException | LoginFailedException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(gameOpened);

        // List all games and get ID of first one
        ArrayList<JSONObject> gamesList = null;
        String gameID = null;
        System.out.println("Getting games list");

        try {
            gamesList = gComm.getAllGames();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(gamesList);

        if (gamesList.size() > 0) {

            try {
                System.out.println(gamesList);
                gameID = gamesList.get(0).getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (gameID != null) {

            // Joining a game
            boolean gameJoined = false;
            System.out.println("Joining game");

            try {
                gameJoined = gComm.joinGame(gameID);

            } catch (GameIdNotFoundException | LoginFailedException e) {
                e.printStackTrace();
            }

            Assert.assertTrue(gameJoined);

            // Deleting joined game
            boolean gameDeleted = false;
            System.out.println("Deleting game");

            try {
                gameDeleted = gComm.deleteGame(gameID);

            } catch (GameIdNotFoundException | LoginFailedException e) {
                e.printStackTrace();
            }

            Assert.assertTrue(gameDeleted);

        }

        //Logout
        boolean loggedOut = false;
        System.out.println("Logging out");

        Assert.assertNotNull(gComm.getUserKey());
        Assert.assertNotNull(uComm.getUserKey());

        try {
            loggedOut = uComm.logOut();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }


        Assert.assertTrue(loggedOut);

        Assert.assertNull(gComm.getUserKey());
        Assert.assertNull(uComm.getUserKey());
    }

    /*
     * Registering users, then logging them in, listing all users currently online,
     * then logging them out. Two attempts each, first should always be successful,
     * second should fail.
     */
    @Test
    public void testDirectLogin(){
        HttpRequests hr = new HttpRequests();
        SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);

        UserHandler uHandler = uComm.getUserHandler();

        // Random username or else we can't test if a name has been taken or not
        String username = "aVeryUniqueUsername" + new Random().nextInt();
        String userPassword = "1235813";

        // Two registration attempts, both identical, so the second will fail
        boolean firstRegistration = false;
        boolean aSecondRegistration = false;
        System.out.println("Registration:");

        try {
            firstRegistration = uHandler.registerUser(username, userPassword);
            aSecondRegistration = uHandler.registerUser(username, userPassword);

        } catch (JSONException | RegistrationFailedException e) {
            Assert.assertTrue(e instanceof RegistrationFailedException);
            e.printStackTrace();
        }

        Assert.assertTrue(firstRegistration);
        Assert.assertFalse(aSecondRegistration);

        // Two login attempts with previously registered user, wrong password on second one
        // Only first user login will get a userKey
        String userKey1 = null;
        String userKey2 = null;
        System.out.println("Login:");

        try {
            userKey1 = uHandler.loginAs(username, userPassword);
            userKey2 = uHandler.loginAs(username, userPassword + "Typo");
        } catch (JSONException | LoginFailedException e) {
            Assert.assertTrue(e instanceof LoginFailedException);
            e.printStackTrace();
        }
        Assert.assertNotNull(userKey1);
        Assert.assertNull(userKey2);

        System.out.println("User1's key is: " + userKey1 + "\n");

        // Two attempts of getting all currently online users,
        // only the successfully logged in user will succeed
        ArrayList<String> userList1;
        ArrayList<String> userList2 = null;
        System.out.println("Listing all users:");

        try {
            userList1 = uHandler.getAllUsers(userKey1);
            Assert.assertNotNull(userList1);

            for (String s : userList1) {
                System.out.println(s + " is logged in.");
            }

            userList2 = uHandler.getAllUsers("No key");

        } catch (LoginFailedException | JSONException e) {
            Assert.assertTrue(e instanceof LoginFailedException);
            e.printStackTrace();
        }

        Assert.assertNull(userList2);

        // Log out two users, one's logged in, the other isn't
        SynchronousUserCommunicator uComm2;
        // Linebreak for readability
        uComm2 = new SynchronousUserCommunicator(new HttpRequests(null));

        boolean firstLogOut = false;
        boolean secondLogOut = false;
        System.out.println("Logging out:");

        try {
            firstLogOut = uHandler.logOut();
            secondLogOut = uComm2.logOut();

        } catch (LoginFailedException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(firstLogOut);
        Assert.assertFalse(secondLogOut);

    }

    /*
     * Registering a user, then logging it in, so we can get its userKey
     * to create a game, list all games, join a game and finally delete
     * a game.
     */
    @Test
    public void testDirectGameLobbies() {
        HttpRequests hr = new HttpRequests();
        SynchronousGameCommunicator gComm = new SynchronousGameCommunicator(hr);
        SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);

        GameLobbyHandler gHandler = gComm.getGameHandler();
        UserHandler uHandler = uComm.getUserHandler();

        // Random username or else we can't test if a name has been taken or not
        String username = "aVeryUniqueUsername" + new Random().nextInt();
        String userPassword = "1235813";
        String userKey = "";

        try {
            uHandler.registerUser(username, userPassword);
            userKey = uHandler.loginAs(username, userPassword);

        } catch (JSONException | RegistrationFailedException | LoginFailedException e) {
            e.printStackTrace();
        }

        Random rng = new Random();

        // Creating three games, one correctly, one with an incorrect amount of players.
        // Third one has an incorrect userKey
        boolean firstGame = false;
        boolean secondGame = false;
        boolean thirdGame = false;
        System.out.println("Creating games:");

        String gameName = "Game Lobby" + rng.nextInt();

        try {
            firstGame = gHandler.openGameLobby(userKey, gameName, 2);
            secondGame = gHandler.openGameLobby(userKey, gameName, 3);

        } catch (JSONException | GameLobbyCreationFailedException | LoginFailedException e) {
            Assert.assertTrue(e instanceof GameLobbyCreationFailedException);
            e.printStackTrace();
        }

        try {
            thirdGame = gHandler.openGameLobby("No key", gameName, 2);

        } catch (JSONException | GameLobbyCreationFailedException | LoginFailedException e) {
            Assert.assertTrue(e instanceof LoginFailedException);
            e.printStackTrace();
        }

        Assert.assertTrue(firstGame);
        Assert.assertFalse(secondGame);
        Assert.assertFalse(thirdGame);

        // Two attempts to get all games, one as it should be, the other without a userKey
        ArrayList<JSONObject> lobbyList1 = null;
        ArrayList<JSONObject> lobbyList2 = null;
        System.out.println("Getting all games:");

        try {
            lobbyList1 = gHandler.getAllGameLobbies(userKey);
            Assert.assertNotNull(lobbyList1);
            for (JSONObject j : lobbyList1) {
                System.out.println(j.toString() + " is a game.");
            }

            lobbyList2 = gHandler.getAllGameLobbies("No key");

        } catch (LoginFailedException | JSONException e) {
            Assert.assertTrue(e instanceof LoginFailedException);
            e.printStackTrace();
        }

        Assert.assertNull(lobbyList2);

        String gameId = "No ID";

        if (lobbyList1 != null && lobbyList1.size() > 0) {
            try {
                gameId = lobbyList1.get(0).getString("id");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Three attempts to join, first one's correct, second is without an ID
        // and third without a userKey
        boolean firstJoin = false;
        boolean secondJoin = false;
        boolean thirdJoin = false;
        System.out.println("Joining games:");

        try {
            firstJoin = gHandler.joinGameLobby(userKey, gameId);
            secondJoin = gHandler.joinGameLobby(userKey, "No ID");

        } catch (LoginFailedException | JSONException | GameIdNotFoundException e) {
            Assert.assertTrue(e instanceof GameIdNotFoundException);
            e.printStackTrace();
        }
        try {
            thirdJoin = gHandler.joinGameLobby("No key", gameId);

        } catch (LoginFailedException | JSONException | GameIdNotFoundException e) {
            Assert.assertTrue(e instanceof LoginFailedException);
            e.printStackTrace();
        }

        Assert.assertTrue(firstJoin);
        Assert.assertFalse(secondJoin);
        Assert.assertFalse(thirdJoin);

        // Three game deletions, one correctly, one with no correct id
        // and one with an incorrect userKey
        boolean firstDelete = false;
        boolean secondDelete = false;
        boolean thirdDelete = false;
        System.out.println("Deleting games:");

        try {
            firstDelete = gHandler.deleteGameLobby(userKey, gameId);
            secondDelete = gHandler.deleteGameLobby(userKey, "No ID");

        } catch (LoginFailedException | JSONException | GameIdNotFoundException e) {
            Assert.assertTrue(e instanceof GameIdNotFoundException);
            e.printStackTrace();
        }
        try {
            thirdDelete = gHandler.deleteGameLobby("No key", gameId);

        } catch (LoginFailedException | JSONException | GameIdNotFoundException e) {
            Assert.assertTrue(e instanceof  LoginFailedException);
            e.printStackTrace();
        }

        Assert.assertTrue(firstDelete);
        Assert.assertFalse(secondDelete);
        Assert.assertFalse(thirdDelete);

        try {
            Assert.assertFalse(gHandler.checkID(userKey, gameId));

        } catch (LoginFailedException | JSONException e) {
            e.printStackTrace();
        }


    }
}
