package asyncCommunication;

import asyncCommunication.offlineEndpoints.ChatEndpoint;
import asyncCommunication.offlineEndpoints.SystemEndpoint;
import org.junit.Test;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static asyncCommunication.Constants.SYSTEM_WS;

public class OfflineWebSocketTest {

    private WebSocketRequests chatClient;
    private WebSocketRequests systemClient;
    private WebSocketRequests chatClient2;

    /*
     *   starts a chat- and a system-server that clients can connect to.
     */

    @Test
    public void offlineTest() {

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            ExecutorService executor2 = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                SystemEndpoint systemEndpoint = new SystemEndpoint();
                systemEndpoint.runServer();
                while (true) ;
            });
            executor2.execute(() -> {
                ChatEndpoint chatEndpoint = new ChatEndpoint();
                chatEndpoint.runServer();
                while (true) ;
            });
            URI systemURI = new URI("ws://localhost:8025/websocket" + SYSTEM_WS);
            URI chatURI = new URI("ws://localhost:8090/websocket" + "/chat");
            this.systemClient = new WebSocketRequests(systemURI);
            this.chatClient = new WebSocketRequests(chatURI);
            this.chatClient2 = new WebSocketRequests(chatURI);
            chatClient2.sendChatMessage("all", "client2", "hallo");

            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
