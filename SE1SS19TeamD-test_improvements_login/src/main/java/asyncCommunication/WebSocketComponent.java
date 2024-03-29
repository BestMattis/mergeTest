package asyncCommunication;

import main.AdvancedWarsApplication;
import model.ChatMessage;
import model.Model;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static asyncCommunication.Constants.*;

public class WebSocketComponent {
    private WebSocketRequests chatClient;
    private WebSocketRequests systemClient;
    private WebSocketRequests gameClient;
    private boolean gameLobby = false;
    private boolean inGame = false;
    private boolean gameObserver = false;
    private boolean online;
    private Model model;

    /**
     * creates a WebSocket component that starts a chat- and a system-client
     *
     * @param userName the username of the user
     * @param userKey  the user-key of the user
     */
    public WebSocketComponent(String userName, String userKey, Model model) {
        this(userName, userKey, true, model);
    }

    /**
     * creates a WebSocket component that starts a chat- and a system-client
     *
     * @param userName the username of the user
     * @param userKey  the user-key of the user
     * @param online   whether this Requests component is used online
     */
    public WebSocketComponent(String userName, String userKey, boolean online, Model model) {
        this.model = model;
        this.online = online;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            model.getWebSocketConfigurator().userKey = userKey;
            startChatSocket(userName);
            startSystemSocket();
        });
    }

    /**
     * creates a WebSocket-component for test purposes with only a chat-client
     *
     * @param userName the username of the user
     */
    public WebSocketComponent(String userName, URI uri, Model model) {

        this.chatClient = new WebSocketRequests(uri, model);
    }

    /**
     * Return the chat client.
     *
     * @return the chat client (a websocket requests object)
     */
    public WebSocketRequests getChatClient() {
        return chatClient;
    }

    /**
     * Return the system client.
     *
     * @return the system client (a websocket requests object)
     */ 
    public WebSocketRequests getSystemClient() {
        return systemClient;
    }

    /**
     * Return the game client.
     *
     * @return the game client (a websocket requests object)
     */
    public WebSocketRequests getGameClient() {
        return gameClient;
    }

    /**
     * stops the whole websocket components.
     */
    public void stopComponent() {
        stopChatSocket();
        stopSystemSocket();
        stopGameSocket();
    }

    private void startChatSocket(String userName) {

        if (this.chatClient == null || !this.chatClient.isOpen()) {
            try {
                this.chatClient = new WebSocketRequests(new URI(BS_WS_URI + CHAT_WS + userName), model);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if (online) {
                this.chatClient.connect();
            }
        }
    }

    private void stopChatSocket() {

        if (this.chatClient != null) { 
            try { 
                this.chatClient.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startSystemSocket() {

        if (this.systemClient == null || !this.systemClient.isOpen()) {
            try {
                this.systemClient = new WebSocketRequests(new URI(BS_WS_URI + SYSTEM_WS), model);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if (online) {
                this.systemClient.connect();
            }
        }
    }

    private void stopSystemSocket() {

        if (this.systemClient != null) {
            try {
                this.systemClient.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stops the game lobby socket and starts the ingame socket.
     *
     * @param gameID is the id of the game
     * @param armyID is the id of the army
     */
    public void joinGame(String gameID, String armyID) {
        if (this.gameClient != null || this.gameClient.isOpen()) {
            if (!this.inGame && this.gameLobby && !this.gameObserver) {
                this.gameLobby = false;
                this.inGame = true;
                AdvancedWarsApplication.getInstance().getGameScreenCon().getGameLobbyController().removeGameLobbyListeners();
                AdvancedWarsApplication.getInstance().getGameScreenCon().setIngameListeners();
            }
        }
    }

    /**
     * Closes the main lobby chat client and opens a game-Socket for the game lobby.
     *
     * @param gameID             is the id of the game.
     * @param gameClientCallable the callable is called after gameClient is created
     */
    public void joinGameLobby(String gameID, Callable<Void> gameClientCallable) {

        if ((this.gameClient == null || !this.gameClient.isOpen()) && !this.inGame
                && !this.gameObserver && !this.gameLobby) {

            this.gameLobby = true;
            AdvancedWarsApplication.getInstance().getGameScreenCon().getGameLobbyController().setGameLobbyListeners();

            if (this.chatClient.isOpen()) {

                try {
                    this.chatClient.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(this.systemClient.isOpen()) {
            	try {
                    this.systemClient.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {

                try {
                    this.gameClient = new WebSocketRequests(new URI(BS_WS_URI + GAME_WS + gameID), model);
                    if (online) {
                        this.gameClient.connect();
                    }
                    gameClientCallable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    executor.awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (this.gameClient.isOpen()) {
                }
            });
        } else {
            System.out.println(this.getClass().getName() + ": you can't join game lobby");
        }
    }

    /**
     * Opens a game-Socket to join as an observer of the game.
     *
     * @param gameID is the id
     */
    public void joinGameSocketAsObserver(String gameID) {

        String observerKey = "&spectator=true";

        if ((this.gameClient == null || !this.gameClient.isOpen()) && !this.inGame
                && !this.gameObserver && !this.gameLobby) {

            this.gameObserver = true;

            if (this.chatClient.isOpen()) {

                try {
                    this.chatClient.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {

                try {
                    this.gameClient = new WebSocketRequests(new URI(BS_WS_URI + GAME_WS + gameID + observerKey), model);
                    if (online) {
                        this.gameClient.connect();
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                try {
                    executor.awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (this.gameClient.isOpen()) {
                }
            });
        }

    }

    /**
     * Changes the selected army.
     *
     * @param armyID is the id of the army.
     */
    public void selectArmyInGameLobby(String armyID) {

        // TODO: check if gameClient is not null, otherwise retry until then

        if (this.gameClient != null && this.gameClient.isOpen() && this.gameLobby) {
            JSONObject jsonObject = new JSONObject().put("messageType", "command").put("action", "changeArmy").put("data", armyID);
            gameClient.sendGameMessage(jsonObject);
            System.out.println("selectArmyInGameLobby message sent");
        }
    }

    /**
     * Tells the game-Socket that the player is ready to play.
     */
    public void playerIsReadyForGame() {
        if (this.gameClient != null && this.gameClient.isOpen() && this.gameLobby) {
            JSONObject jsonObject = new JSONObject().put("messageType", "command").put("action", "readyToPlay");
            gameClient.sendGameMessage(jsonObject);
            System.out.println("playerIsReadyForGame message sent");
        }
    }

    /**
     * Starts the game from the game lobby.
     */
    public void startGameFromLobby() {

        if (this.gameClient != null && this.gameClient.isOpen() && this.gameLobby) {

            JSONObject jsonObject = new JSONObject().put("messageType", "command").put("action", "startGame");
            gameClient.sendGameMessage(jsonObject);
            
            System.out.println("startGameFromLobby message sent");
        }
    }

    /**
     * Sends the game-Socket a message to move a Unit from one field to another field.
     *
     * @param unitID the id of the unit
     * @param path   contains a list of field-ids that build a path.
     */
    public void moveUnitIngame(String unitID, String[] path) {
        if (this.gameClient != null && (this.gameClient.isOpen() && this.inGame)) {

            JSONObject jsonObject = new JSONObject().put("messageType", "command").put("action", "moveUnit");
            JSONArray pathArray = new JSONArray(path);
            JSONObject data = new JSONObject().put("unitid", unitID).put("path", pathArray);
            jsonObject.put("data", data);
            gameClient.sendGameMessage(jsonObject);

        }
    }

    /**
     * Sends the game-Socket a mesage to attack an enemy unit.
     *
     * @param unitID     the id of the unit of the current player
     * @param toAttackID the id of the unit that the player wants to attack.
     */
    public void attackUnitIngame(String unitID, String toAttackID) {

        if (this.gameClient != null && (this.gameClient.isOpen() && this.inGame)) {

            JSONObject jsonObject = new JSONObject().put("messageType", "command").put("action", "attackUnit");
            JSONArray idArray = new JSONArray().put(unitID).put(toAttackID);
            jsonObject.put("data", idArray);
        }
    }

    /**
     * Sends the game-Socket a message to start the next phase
     */
    public void nextPhase() {
       if(this.gameClient.isOpen() && this.inGame) {

            JSONObject jsonObject = new JSONObject().put("messageType", "command").put("action", "nextPhase");
            gameClient.sendGameMessage(jsonObject);
       }
    }

    public void joinGame(String gameID) {

        if (this.gameClient == null || !this.gameClient.isOpen()) {
            try {
                joinGame(new URI(BS_WS_URI + GAME_WS + gameID));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void joinGame(URI uri) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            this.gameClient = new WebSocketRequests(uri, model);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (this.gameClient.isOpen()) {
            }
        });
    }

    /**
     * sends a command to the server to let it know that the user wants to leave the game.
     * closes the game-client and the connection.
     */
    public void leaveGame() {

        if (this.gameClient != null && (!online || this.gameClient.isOpen())) {

            JSONObject jsonObject = new JSONObject().put("messageType", "command").put("action", "leaveGame");
            gameClient.sendGameMessage(jsonObject);
            stopGameSocket();
            startChatSocket(model.getApp().getCurrentPlayer().getName());
        }
    }

    private void stopGameSocket() {

        if (this.gameClient != null) {

            try {
                gameClient.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.gameClient = null;

            this.gameLobby = false;
            this.inGame = false;
            this.gameObserver = false;
        }

    }

    /**
     * tests first if the message has the necessary attributes and sends it afterwards.
     *
     * @param message is the message that has to be sent
     */
    public void sendChatmessage(ChatMessage message) {

        if (this.chatClient != null && (!online || this.chatClient.isOpen())) {
            if (testChatMessage(message)) {
                this.chatClient.sendChatMessage(message);
            }
        }
    }

    private boolean testChatMessage(ChatMessage msg) {

        if (msg.getMessage() != null && msg.getChannel() != null && msg.getSender() != null) {
            if (msg.getChannel().equals("all")) {
                return true;
            } else if (msg.getChannel().equals("private") && msg.getReceiver() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method sends all-chat or private messages to players that are in the same game as the current player.
     *
     * @param message contains all necessary information about the message and the sender/receiver.
     */
    public void sendGameChatMessage(ChatMessage message) {

        if (this.gameClient != null && (!online || this.gameClient.isOpen())) {
            if (testChatMessage(message)) {
                if (message.getChannel().equals("all")) {
                    this.gameClient.sendAllChatGameMessage(message);
                } else if (message.getChannel().equals("private")) {
                    this.gameClient.sendPrivateChatGameMessage(message);
                }
            }
        } else {
            System.out.println(this.getClass().getName() + ": join a game first");
        }
    }

    public boolean isIngame() {

        return this.inGame;
    }

    public void setInGameForTest() {

        if (AdvancedWarsApplication.offtesting) {
            this.inGame = true;
        }
    }

    public boolean isInGameLobby() {
        return this.gameLobby;
    }

    public void setInGameLobbyForTest() {
        if (AdvancedWarsApplication.offtesting) {
            this.gameLobby = true;
        }
    }
}
