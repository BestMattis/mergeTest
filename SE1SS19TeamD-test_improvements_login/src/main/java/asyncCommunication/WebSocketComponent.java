package asyncCommunication;

import model.ChatMessage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static asyncCommunication.Constants.*;

public class WebSocketComponent {

    private WebSocketRequests chatClient;
    private WebSocketRequests systemClient;
    private WebSocketRequests gameClient;

    /**
     * creates a WebSocket component that starts a chat- and a system-client
     *
     * @param userName the username of the user
     * @param userKey  the user-key of the user
     */
    public WebSocketComponent(String userName, String userKey) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            WebSocketConfigurator.userKey = userKey;
            startChatSocket(userName);
            startSystemSocket();
            while (this.chatClient.isOpen() && this.systemClient.isOpen()) {
            }
        });
    }

    /**
     * creates a WebSocket-component for test purposes with only a chat-client
     *
     * @param userName the username of the user
     */
    public WebSocketComponent(String userName, URI uri) {

        this.chatClient = new WebSocketRequests(uri);
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
                this.chatClient = new WebSocketRequests(new URI(BS_WS_URI + CHAT_WS + userName));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopChatSocket() {

        try {
            this.chatClient.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startSystemSocket() {

        if (this.systemClient == null || !this.systemClient.isOpen()) {
            try {
                this.systemClient = new WebSocketRequests(new URI(BS_WS_URI + SYSTEM_WS));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopSystemSocket() {

        try {
            this.systemClient.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * starts a connection with an endpoint of a game that the user wants to join.
     *
     * @param gameID is the id of the game
     * @param armyID is the id of the army
     */
    public void joinGame(String gameID, String armyID) {

        if (this.gameClient == null || !this.gameClient.isOpen()) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {

                try {
                    this.gameClient = new WebSocketRequests(new URI(BS_WS_URI + GAME_WS + gameID + "&" + "" + "armyId=" +  armyID));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (this.gameClient.isOpen()) {
                }
            });
        }
    }

    /**
     * sends a command to the server to let it know that the user wants to leave the game.
     * closes the game-client and the connection.
     */
    public void leaveGame() {

        if (this.gameClient != null && this.gameClient.isOpen()) {

            JSONObject jsonObject = new JSONObject().put("messageType", "command").put("action", "leaveGame");
            gameClient.sendGameMessage(jsonObject);
            try {
                this.gameClient.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopGameSocket() {

        if (this.gameClient != null) {

            try {
                gameClient.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * tests first if the message has the necessary attributes and sends it afterwards.
     *
     * @param message is the message that has to be sent
     */
    public void sendChatmessage(ChatMessage message) {

        if (this.chatClient != null && this.chatClient.isOpen()) {
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
    public void sendGameChatMessage (ChatMessage message) {

        if (this.gameClient != null && this.gameClient.isOpen()) {
            if (testChatMessage(message)) {
                if(message.getChannel().equals("all")) {
                    this.gameClient.sendAllChatGameMessage(message);
                } else if (message.getChannel().equals("private")) {
                    this.gameClient.sendPrivateChatGameMessage(message);
                }
            }
        } else {
            System.out.println("join a game first");
        }
    }
}
