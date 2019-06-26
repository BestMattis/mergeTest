package asyncCommunication;


import org.junit.Assert;
import org.junit.Test;
import syncCommunication.HttpRequests;
import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.SynchronousUserCommunicator;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static asyncCommunication.Constants.*;

public class WebSocketTest {

    private WebSocketRequests chatClient;
    private WebSocketRequests systemClient;

    /*
     * the user-key is necessary to put it in the header for the requests to the server.
     * also opens a websocket for chat-messages and for system-messages.
     */
    @Test
    public void testOpenWebsocket() {

        HttpRequests hr = new HttpRequests();
        SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);
        String userName = "aVeryUniqueUsername251";
        String userPassword = "1235813";
        try {
            uComm.logIn(userName, userPassword);
            String userKey = uComm.getUserKey();
            Assert.assertNotNull(userKey);
            WebSocketConfigurator.userKey = userKey;
            this.chatClient = new WebSocketRequests(new URI(BS_WS_URI + CHAT_WS + userName));
            this.systemClient = new WebSocketRequests(new URI(BS_WS_URI + SYSTEM_WS));
            Thread.sleep(1000);
            this.chatClient.stop();
            this.systemClient.stop();
        } catch (LoginFailedException | URISyntaxException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * hold the connection until the test is manually closed
     */

    @Test
    public void testListenToWebsocket() {

        HttpRequests hr = new HttpRequests();
        SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);
        String userName = "aVeryUniqueUsernameWS";
        String userPassword = "1235813";
        try {
            uComm.logIn(userName, userPassword);
            String userKey = uComm.getUserKey();
            Assert.assertNotNull(userKey);
            WebSocketConfigurator.userKey = userKey;
            this.chatClient = new WebSocketRequests(new URI(BS_WS_URI + CHAT_WS + userName));
            this.systemClient = new WebSocketRequests(new URI(BS_WS_URI + SYSTEM_WS));

            int testDurationInSeconds = 30;
            long endTime = System.currentTimeMillis() + (testDurationInSeconds * 1000);

            while (System.currentTimeMillis() < endTime) ;
        } catch (LoginFailedException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /*
     * test the WS component in an easier way
     * the component will run in another thread
     */
    @Test
    public void testWholeComponent() {

        HttpRequests hr = new HttpRequests();
        SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);
        String userName = "aVeryUniqueUsernameWS";
        String userPassword = "1235813";
        try {
            uComm.logIn(userName, userPassword);
            String userKey = uComm.getUserKey();
            WebSocketComponent wsComponent = new WebSocketComponent(userName, userKey);
            Thread.sleep(5000);
            wsComponent.stopComponent();
            Thread.sleep(2000);
        } catch (LoginFailedException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
