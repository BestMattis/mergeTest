package registerLogin;

import asyncCommunication.WebSocketComponent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import model.App;
import model.Model;
import model.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testfx.framework.junit.ApplicationTest;
import syncCommunication.HttpRequests;
import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.RESTExceptions.RegistrationFailedException;
import syncCommunication.SynchronousArmyCommunicator;
import syncCommunication.SynchronousGameCommunicator;
import syncCommunication.SynchronousUserCommunicator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Utility class to supply a test user.
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
     * @param test the test instance to log in the user
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
     * @param test     the test instance to log in the user
     * @param name     the name of the user to log in
     * @param password the password of the user to log in
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
     * @param test the test instance to log in the user
     */
    public static void loginForOfflineTest(ApplicationTest test, Model model) {
        LoginRegisterTestUtils.loginForOfflineTest(test, LoginRegisterTestUtils.TEST_USER_NAME,
                LoginRegisterTestUtils.TEST_USER_PASSWORD, model);
    }

    /**
     * Log in a user for offline tests on the given test instance.
     *
     * @param test     the test instance to log in the user
     * @param name     the name of the user to log in
     * @param password the password of the user to log in
     */
    public static void loginForOfflineTest(ApplicationTest test, String name, String password, Model model) {
        LoginRegisterTestUtils.startupOfflineTest(test, name, password, new JSONArray(), new JSONArray(), new JSONArray(), model);
    }

    /**
     * Log in the test user for offline tests on the given test instance.
     *
     * @param test the test instance to log in the user
     */
    public static void loginForOfflineTest(ApplicationTest test, JSONArray initialUsers, JSONArray initialGames, JSONArray armyList, Model model) {
        LoginRegisterTestUtils.startupOfflineTest(test, LoginRegisterTestUtils.TEST_USER_NAME,
                LoginRegisterTestUtils.TEST_USER_PASSWORD, initialUsers, initialGames, armyList, model);
    }

    /**
     * Startup a test that checks if a message was sent to the given url with the
     * given method.
     *
     * @param test          the test instance to log in the user
     * @param urlToCheck    the URL to check
     * @param methodToCheck the method to check
     * @return the message sent to the given URL with the given method or null if no such message was sent
     */
    public static JSONObject startupMessageSentTest(ApplicationTest test, String urlToCheck, String methodToCheck, Model model) {
        return LoginRegisterTestUtils.startupTest(test, LoginRegisterTestUtils.TEST_USER_NAME,
                LoginRegisterTestUtils.TEST_USER_PASSWORD, new JSONArray(), new JSONArray(), new JSONArray(), urlToCheck, methodToCheck, model);
    }

    /**
     * Create a game on an offline test instance.
     *
     * @param test                the ApplicationTest calling this method
     * @param id                  the ID of the new game
     * @param name                the name of the new game
     * @param neededPlayer        the number of players needed for the game
     * @param notReadyPlayerNames the names of the players that are not ready
     * @param readyPlayerNames    the names of the players that are ready
     */
    public static void createGameForOfflineTest(ApplicationTest test, String id, String name, int neededPlayer, List<String> notReadyPlayerNames, List<String> readyPlayerNames, Model model) {
        LoginRegisterTestUtils.createGameForMessageSentTest(test, id, name, neededPlayer, notReadyPlayerNames, readyPlayerNames, null, null, model);
    }

    /**
     * Create a game for a message sent test instance.
     *
     * @param test          the ApplicationTest calling this method
     * @param urlToCheck    the URL to check
     * @param methodToCheck the method to check
     * @return the message sent to the given URL with the given method or null if no such message was sent
     */
    public static JSONObject createGameForMessageSentTest(ApplicationTest test, String urlToCheck, String methodToCheck, Model model) {
        return LoginRegisterTestUtils.createGameForMessageSentTest(test, "my-game-id", "My Game", 2, new ArrayList<>(), new ArrayList<>(), urlToCheck, methodToCheck, model);
    }

    /**
     * Join a game on an offline test instance.
     *
     * @param test                the ApplicationTest calling this method
     * @param id                  the ID of the new game
     * @param name                the name of the new game
     * @param neededPlayer        the number of players needed for the game
     * @param notReadyPlayerNames the names of the players that are not ready
     * @param readyPlayerNames    the names of the players that are ready
     */
    public static void joinGameForOfflineTest(ApplicationTest test, String id, String name, int neededPlayer, List<String> notReadyPlayerNames, List<String> readyPlayerNames, Model model) {
        LoginRegisterTestUtils.joinGameForMessageSentTest(test, id, name, neededPlayer, notReadyPlayerNames, readyPlayerNames, null, null, model);
    }

    /**
     * Join a game for a message sent test instance.
     *
     * @param test          the ApplicationTest calling this method
     * @param urlToCheck    the URL to check
     * @param methodToCheck the method to check
     * @return the message sent to the given URL with the given method or null if no such message was sent
     */
    public static JSONObject joinGameForMessageSentTest(ApplicationTest test, String urlToCheck, String methodToCheck, Model model) {
        return LoginRegisterTestUtils.joinGameForMessageSentTest(test, "my-game-id", "My Game", 2, new ArrayList<>(), new ArrayList<>(), urlToCheck, methodToCheck, model);
    }

    /**
     * Start the game that the current player created / joined before.
     *
     * @param test        the ApplicationTest calling this method
     * @param id          the ID of the game
     * @param playerNames the names of the players
     * @param armyName    the army to use
     */
    public static void startGameForOfflineTest(ApplicationTest test, String id, List<String> playerNames, String armyName, Model model) {
        LoginRegisterTestUtils.startGameForMessageSentTest(test, id, playerNames, armyName, null, null, model);
    }

    /**
     * Start the game that the current player created / joined before on a message sent test.
     *
     * @param test          the ApplicationTest calling this method
     * @param urlToCheck    the URL to check
     * @param methodToCheck the method to check
     * @return the message sent to the given URL with the given method or null if no such message was sent
     */
    public static JSONObject startGameForMessageSentTest(ApplicationTest test, String urlToCheck,
                                                         String methodToCheck, Model model) {
        return LoginRegisterTestUtils.startGameForMessageSentTest(test, "my-game-id", new ArrayList<>(), null, urlToCheck, methodToCheck, model);
    }

    /**
     * Wait until the game field is completely loaded and displayed.
     * Return without any further action.
     */
    public static void waitUntilGameFieldLoaded(ApplicationTest test) {
        while (test.lookup("#colorCanvas").queryAllAs(Canvas.class).isEmpty()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void startupOfflineTest(ApplicationTest test, String name, String password, JSONArray initialUsers,
                                           JSONArray initialGames, JSONArray armyList, Model model) {
        LoginRegisterTestUtils.startupTest(test, name, password, initialUsers, initialGames, armyList, null, null, model);
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

    private static JSONObject startupTest(ApplicationTest test, String name, String password, JSONArray initialUsers,
                                          JSONArray initialGames, JSONArray armyList, String urlToCheck, String methodToCheck, Model model) {
        final AtomicReference<JSONObject> result = new AtomicReference<>();

        model.setApp(new App());
        model.getPlayerHttpRequestsHashMap().clear();

        Player currentPlayer = new Player().setApp(model.getApp()).setName(name);
        HttpRequests httpRequests = new HttpRequests();
        model.getApp().setCurrentPlayer(currentPlayer);
        model.getPlayerHttpRequestsHashMap().put(currentPlayer, httpRequests);
        model.setWebSocketComponent(new WebSocketComponent(name, "myuserkey", false, model));

        SynchronousUserCommunicator userCom = new SynchronousUserCommunicator(httpRequests);
        SynchronousGameCommunicator gameCom = new SynchronousGameCommunicator(httpRequests);
        SynchronousArmyCommunicator armyCom = new SynchronousArmyCommunicator(httpRequests);
        userCom.setJsonAdapter((method, url, json) -> {
            if (url.equals("/user/login")) {
                JSONObject response = new JSONObject();
                response.put("status", "success");
                JSONObject responseData = new JSONObject();
                responseData.put("userKey", "myuserkey");
                response.put("data", responseData);
                userCom.injectResponse(response);
            } else if (url.equals("/user")) {
                JSONObject response = new JSONObject();
                response.put("status", "success");
                response.put("data", initialUsers);
                userCom.injectResponse(response);
            } else if (url.equals("/game")) {
                JSONObject response = new JSONObject();
                response.put("status", "success");
                response.put("data", initialGames);
                gameCom.injectResponse(response);
            } else if (method.equals("GET") && url.equals("/army/units")) {
                JSONObject response = new JSONObject("{\"data\":["
                        + "{\"canAttack\":[\"Infantry\",\"Bazooka Trooper\",\"Jeep\",\"Light Tank\",\"Heavy Tank\"],\"mp\":3,\"hp\":10,\"id\":\"5cc051bd62083600017db3b6\",\"type\":\"Infantry\"},"
                        + "{\"canAttack\":[\"Jeep\",\"Light Tank\",\"Heavy Tank\",\"Chopper\"],\"mp\":2,\"hp\":10,\"id\":\"5cc051bd62083600017db3b7\",\"type\":\"Bazooka Trooper\"},"
                        + "{\"canAttack\":[\"Infantry\",\"Bazooka Trooper\",\"Jeep\"],\"mp\":8,\"hp\":10,\"id\":\"5cc051bd62083600017db3b8\",\"type\":\"Jeep\"},"
                        + "{\"canAttack\":[\"Infantry\",\"Bazooka Trooper\",\"Jeep\",\"Light Tank\",\"Heavy Tank\"],\"mp\":6,\"hp\":10,\"id\":\"5cc051bd62083600017db3b9\",\"type\":\"Light Tank\"},"
                        + "{\"canAttack\":[\"Infantry\",\"Bazooka Trooper\",\"Jeep\",\"Light Tank\",\"Heavy Tank\",\"Chopper\"],\"mp\":4,\"hp\":10,\"id\":\"5cc051bd62083600017db3ba\",\"type\":\"Heavy Tank\"},"
                        + "{\"canAttack\":[\"Infantry\",\"Bazooka Trooper\",\"Jeep\",\"Light Tank\",\"Heavy Tank\"],\"mp\":6,\"hp\":10,\"id\":\"5cc051bd62083600017db3bb\",\"type\":\"Chopper\"}],"
                        + "\"message\":\"\",\"status\":\"success\"}");
                armyCom.injectResponse(response);
            } else if (method.equals("GET") && url.equals("/army")) {
                JSONObject response = new JSONObject().put("status", "success").put("data", armyList);
                armyCom.injectResponse(response);
            } else if (method.equals("POST") && url.equals("/army")) {
                JSONObject response = new JSONObject();
                response.put("status", "success");
                JSONObject responseData = new JSONObject();
                responseData.put("id", "my-army-id");
                response.put("data", responseData);
                armyCom.injectResponse(response);
            } else if (method.equals("GET") && url.equals("user/temp")) {
                //TODO: if more temp users are needed, randomize names
                String tempUser = "TempUser";
                JSONObject responseData = new JSONObject();
                responseData.put("password", tempUser).put("name", tempUser);
                userCom.injectResponse(new JSONObject().put("status", "success").put("data", responseData));
            }

            // check if message sent
            if (urlToCheck != null && url.equals(urlToCheck) && method.equals(methodToCheck)) {
                result.set(json);
            }
        });

        LoginRegisterTestUtils.loginUser(test, name, password);
        return result.get();
    }

    private static JSONObject createGameForMessageSentTest(ApplicationTest test, String gameID, String name, int neededPlayer, List<String> notReadyPlayer, List<String> readyPlayer, String urlToCheck, String methodToCheck, Model model) {

        final AtomicReference<JSONObject> result = new AtomicReference<>();

        JSONArray initialGames = new JSONArray().put(
                new JSONObject()
                        .put("joinedPlayer", notReadyPlayer.size())
                        .put("name", name)
                        .put("id", gameID)
                        .put("neededPlayer", neededPlayer));

        HttpRequests requests = model.getPlayerHttpRequestsHashMap().get(model.getApp().getCurrentPlayer());
        SynchronousGameCommunicator synchronousGameCommunicator = new SynchronousGameCommunicator(requests);
        synchronousGameCommunicator.setJsonAdapter((method, url, json) -> {
            if (method.equals("POST") && url.equals("/game")) {
                JSONObject response = new JSONObject();
                response.put("status", "success");
                JSONObject responseData = new JSONObject();
                responseData.put("gameId", gameID);
                response.put("data", responseData);
                synchronousGameCommunicator.injectResponse(response);
            } else if (method.equals("GET") && url.equals("/game")) {
                JSONObject response = new JSONObject();
                response.put("status", "success");
                response.put("data", initialGames);
                synchronousGameCommunicator.injectResponse(response);
            }

            // check if message sent
            if (urlToCheck != null && url.equals(urlToCheck) && method.equals(methodToCheck)) {
                result.set(json);
            }
        });

        Button newGame = test.lookup("#newGame").queryButton();
        test.clickOn(newGame);
        TextInputControl nameField = test.lookup("#nameField").queryTextInputControl();
        TextInputControl playerNumberField = test.lookup("#playerNumberField").queryTextInputControl();
        Button createButton = test.lookup("#createButton").queryButton();
        nameField.setText(name);
        playerNumberField.setText("" + neededPlayer);
        test.clickOn(createButton);

        File gameFieldFile = null;
        try {
            gameFieldFile = new File(test.getClass().getResource("OfflineTestGameField.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        LoginRegisterTestUtils.sendGameInitMessages(gameID, notReadyPlayer, readyPlayer, gameFieldFile, model);

        return result.get();
    }

    private static JSONObject joinGameForMessageSentTest(ApplicationTest test, String gameID, String name, int neededPlayer,
                                                         List<String> notReadyPlayer, List<String> readyPlayer, String urlToCheck, String methodToCheck, Model model) {
        final AtomicReference<JSONObject> result = new AtomicReference<>();

        JSONArray initialGames = new JSONArray().put(
                new JSONObject()
                        .put("joinedPlayer", notReadyPlayer.size())
                        .put("name", name)
                        .put("id", gameID)
                        .put("neededPlayer", neededPlayer));

        HttpRequests requests = model.getPlayerHttpRequestsHashMap().get(model.getApp().getCurrentPlayer());
        SynchronousGameCommunicator synchronousGameCommunicator = new SynchronousGameCommunicator(requests);
        synchronousGameCommunicator.setJsonAdapter((method, url, json) -> {
            if (method.equals("GET") && url.equals("/game")) {
                JSONObject response = new JSONObject();
                response.put("status", "success");
                response.put("data", initialGames);
                synchronousGameCommunicator.injectResponse(response);
            } else if (method.equals("GET") && url.startsWith("/game")) {
                JSONObject response = new JSONObject();
                response.put("status", "success");
                synchronousGameCommunicator.injectResponse(response);
            }

            // check if message sent
            if (urlToCheck != null && url.equals(urlToCheck) && method.equals(methodToCheck)) {
                result.set(json);
            }
        });

        Label gameLabel = test.lookup("#gameName").queryAllAs(Label.class).stream().filter(e -> e.getText().equals(name)).findFirst().get();
        test.clickOn(gameLabel);

        File gameFieldFile = null;
        try {
            gameFieldFile = new File(LoginRegisterTestUtils.class.getResource("../gameScreen/OfflineTestGameField.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        LoginRegisterTestUtils.sendGameInitMessages(gameID, notReadyPlayer, readyPlayer, gameFieldFile, model);

        return result.get();
    }

    private static JSONObject startGameForMessageSentTest(ApplicationTest test, String gameID, List<String> playerNames, String armyName, String urlToCheck,
                                                          String methodToCheck, Model model) {

        final AtomicReference<JSONObject> result = new AtomicReference<>();

        model.getWebSocketComponent().getGameClient().setJSONAdapter((method, url, json) -> {
//			if(url.startsWith("/game?gameID=") && json.getString("action").equals("startGame")) {
//				File unitFile = null;
//				try {
//					unitFile = new File(test.getClass().getResource("../gameScreen/OfflineTestUnitPositions.txt").toURI());
//				} catch (URISyntaxException e) {
//					e.printStackTrace();
//				}
//				LoginRegisterTestUtils.sendGameStartMessages(gameID, playerNames, unitFile, model);
//			}
            if (urlToCheck != null && url.equals(urlToCheck) && method.equals(methodToCheck)) {
                result.set(json);
            }
        });

        @SuppressWarnings("unchecked")
        ChoiceBox<String> armyComboBox = test.lookup("#choice").queryAs(ChoiceBox.class);

        if (armyName == null) {
            armyComboBox.getSelectionModel().select(0);
        } else {
            String armyItem = armyComboBox.getItems().stream().filter(e -> e.equals(armyName)).findFirst().get();
            armyComboBox.getSelectionModel().select(armyItem);
        }

        ToggleButton readyButton = test.lookup("#ready").queryAs(ToggleButton.class);
        test.clickOn(readyButton);

        File unitFile = null;
        try {
            unitFile = new File(test.getClass().getResource("OfflineTestUnitPositions.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        LoginRegisterTestUtils.sendGameStartMessages(gameID, playerNames, unitFile, model);

        return result.get();
    }

    private static void sendGameInitMessages(String gameID, List<String> notReadyPlayer, List<String> readyPlayer,
                                             File gameFieldFile, Model model) {
        List<JSONObject> playerStates = LoginRegisterTestUtils.preparePlayerStateMessages(gameID, notReadyPlayer, readyPlayer);
        LoginRegisterTestUtils.sendInitGameInfo(model);
        JSONArray playerIDs = new JSONArray(playerStates.stream().map(json -> json.getJSONObject("data").getString("id")).toArray(String[]::new));
        LoginRegisterTestUtils.sendGeneralGameData(gameID, playerIDs, model);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(gameFieldFile));
            reader.lines().map(s -> s.replace("<game-id>", gameID))
                    .forEach(s -> model.getWebSocketComponent().getGameClient().onMessage(s));
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (JSONObject msg : playerStates) {
            LoginRegisterTestUtils.sendPlayerMessage(msg, model);
        }
        LoginRegisterTestUtils.sendInitFinishedMessage(model);
    }

    private static List<JSONObject> preparePlayerStateMessages(String gameID, List<String> notReadyPlayer, List<String> readyPlayer) {
        List<JSONObject> playerStates = new ArrayList<>();
        Queue<String> color = new LinkedList<>(Arrays.asList("RED", "GREEN"));
        List<String> allPlayers = new ArrayList<>();
        allPlayers.addAll(readyPlayer);
        allPlayers.addAll(notReadyPlayer);
        for (String s : allPlayers) {
            JSONObject userMsg = new JSONObject();
            userMsg.put("action", "gameInitObject");
            JSONObject userData = new JSONObject();
            userData.put("name", s);
            userData.put("id", s.toLowerCase() + "-id");
            userData.put("color", color.poll());
            userData.put("isReady", readyPlayer.contains(s));
            userData.put("currentGame", gameID);
            userMsg.put("data", userData);
            playerStates.add(userMsg);
        }
        return playerStates;
    }

    private static void sendInitGameInfo(Model model) {
        JSONObject initGameInfo = new JSONObject();
        initGameInfo.put("action", "info");
        JSONObject initGameData = new JSONObject();
        initGameData.put("message", "Initialize game, sending start situation...");
        initGameInfo.put("data", initGameData);
        LoginRegisterTestUtils.send(initGameInfo.toString(), model);
    }

    private static void sendGeneralGameData(String gameID, JSONArray allPlayerIDs, Model model) {
        JSONObject generalGameData = new JSONObject();
        generalGameData.put("action", "gameInitObject");
        generalGameData.put("id", gameID);
        generalGameData.put("allPlayer", allPlayerIDs);
        LoginRegisterTestUtils.send(generalGameData.toString(), model);
    }

    private static void sendPlayerMessage(JSONObject msg, Model model) {
        LoginRegisterTestUtils.send(msg.toString(), model);
    }

    private static void sendInitFinishedMessage(Model model) {
        JSONObject gameInitFinished = new JSONObject();
        gameInitFinished.put("action", "gameInitFinished");
        LoginRegisterTestUtils.send(gameInitFinished.toString(), model);
    }

    private static void sendGameStartMessages(String gameID, List<String> playerNames, File unitFile, Model model) {
        LoginRegisterTestUtils.sendGameStartMessage(model);

        String[] playerIDs = playerNames.stream().map(s -> s.toLowerCase() + "-id").toArray(String[]::new);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(unitFile));
            reader.lines().map(s ->
                    s.replace("<game-id>", gameID)
                            .replace("<first-player>", playerIDs[0])
                            .replace("<second-player>", playerIDs[1]))
                    .forEach(s -> model.getWebSocketComponent().getGameClient().onMessage(s));
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LoginRegisterTestUtils.sendCurrentPlayerMessage(gameID, playerIDs[0], model);
    }

    private static void sendGameStartMessage(Model model) {
        JSONObject gameStarts = new JSONObject();
        gameStarts.put("action", "gameStarts");
        LoginRegisterTestUtils.send(gameStarts.toString(), model);
    }

    private static void sendCurrentPlayerMessage(String gameID, String currentPlayerID, Model model) {
        JSONObject gameChangeObject = new JSONObject();
        gameChangeObject.put("action", "gameChangeObject");

        JSONObject gameChangeData = new JSONObject();
        gameChangeData.put("newValue", currentPlayerID);
        gameChangeData.put("fieldName", "currentPlayer");
        gameChangeData.put("id", gameID);

        gameChangeObject.put("data", gameChangeData);
        LoginRegisterTestUtils.send(gameChangeObject.toString(), model);
    }

    private static void send(String msg, Model model) {
        model.getWebSocketComponent().getGameClient().onMessage(msg);
    }
}