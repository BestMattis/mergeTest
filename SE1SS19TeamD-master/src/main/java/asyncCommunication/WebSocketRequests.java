package asyncCommunication;

import model.ChatMessage;
import org.json.JSONException;
import org.json.JSONObject;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import static asyncCommunication.Constants.*;

@ClientEndpoint(configurator = WebSocketConfigurator.class)
public class WebSocketRequests {

    private Session session = null;
    private Timer noopTimer;
    private URI uri;
    private WebSocketHandler handler;

    /**
     * Creates a connection from the client-endpoint to a server-endpoint.
     *
     * @param uri The address the client wants to connect to
     */
    public WebSocketRequests(URI uri) {

        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, uri);
        } catch (IOException | DeploymentException e) {
            e.printStackTrace();
        }
        this.uri = uri;
        if (this.uri.toString().startsWith(BS_WS_URI + CHAT_WS)
                || this.uri.toString().startsWith("ws://localhost:8090/websocket" + "/chat")) {
            this.handler = new WebSocketChatHandler();
        } else if (this.uri.toString().startsWith(BS_WS_URI + SYSTEM_WS)
                || this.uri.toString().startsWith("ws://localhost:8025/websocket" + SYSTEM_WS)) {
            this.handler = new WebSocketSystemHandler();
        } else if (this.uri.toString().startsWith(BS_WS_URI + GAME_WS)) {
            this.handler = new WebSocketGameHandler();
        }
    }

    @OnOpen
    public void onOpen(Session session) {

        this.session = session;
        this.noopTimer = new Timer();

        //send noop every 2min to server
        this.noopTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(NOOP_OP);
                    } catch (Exception e) {
                        System.err.println("cannot send NOOP");
                    }
                }
            }
        }, 0, 1000 * 60 * 2);
    }

    @OnMessage
    public void onMessage(String message) {

        try {
            if (message.equals("noop")) {
                return;
            }
            if (handler != null) {
                handler.handle(new JSONObject(message));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     *   restart the connection in case of abnormal closure
     *   1000 is the code for normal closure
     */

    @OnClose
    public void onClose(Session session, CloseReason reason) {

        //abnormal closure
        if (reason.getCloseCode().getCode() != 1000) {
            try {
                while (!this.session.isOpen()) {
                    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                    container.connectToServer(this, uri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //normal closure
        else {
            //Stop the timer
            this.session = null;
            this.noopTimer.cancel();
        }
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
    }

    public void sendChatMessage(ChatMessage message) {

        if (message.getChannel().equals("all")) {
            sendChatMessage(message.getChannel(), message.getSender().getName(), message.getMessage());
        } else {
            sendChatMessage(message.getChannel(), message.getReceiver().getName(),
                    message.getSender().getName(), message.getMessage());
        }
    }

    public void sendChatMessage(String channel, String from, String msg) {

        JSONObject jsonObject = new JSONObject().put("channel", channel)
                .put("from", from).put("message", msg);

        try {
            this.session.getBasicRemote().sendText(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendChatMessage(String channel, String to, String from, String msg) {

        JSONObject jsonObject = new JSONObject().put("channel", channel)
                .put("from", from).put("message", msg).put("to", to);

        try {
            this.session.getBasicRemote().sendText(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //stops the session and connection
    public void stop() throws IOException {

        if (this.session != null && this.session.isOpen()) {
            this.session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "normal close"));
            this.noopTimer.cancel();
        }
    }

    public WebSocketHandler getHandler() {
        return this.handler;
    }

    public boolean isOpen() {

        if (this.session.isOpen()) {
            return true;
        } else {
            return false;
        }
    }

    public void sendGameMessage(JSONObject jsonObject) {

        try {
            this.session.getBasicRemote().sendText("" + jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}