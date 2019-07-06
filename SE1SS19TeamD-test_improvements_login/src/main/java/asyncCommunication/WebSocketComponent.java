package asyncCommunication;

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

    public WebSocketComponent(String userName, String userKey) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            WebSocketConfigurator.userKey = userKey;
            startChatSocket(userName);
            startSystemSocket();
        });
    }

    public void stopComponent() {
        stopChatSocket();
        stopSystemSocket();
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

    public void startGameSocket(String gameID) {

        if (this.gameClient == null || !this.gameClient.isOpen()) {
            try {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    while (this.gameClient.isOpen()) {
                    }
                });
                this.gameClient = new WebSocketRequests(new URI(BS_WS_URI + GAME_WS + gameID));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopGameSocket() {

        try {
            gameClient.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
