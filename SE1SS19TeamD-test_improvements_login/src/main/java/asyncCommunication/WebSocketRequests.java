package asyncCommunication;

import model.ChatMessage;
import syncCommunication.JsonAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import syncCommunication.JsonAdapter;

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
    private JsonAdapter adapter;

    /**
     * Initializes this Request Component.
     *
     * @param uri The address the client wants to connect to.
     */
    public WebSocketRequests(URI uri) {
	
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
    
    /**
     * Creates a connection between the ClientEndpoint and the ServerEndpoint by doing a "handshake".
     */
    public void connect() {
	try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, uri);
        } catch (IOException | DeploymentException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called when a connection between the ServerEndpoint and the
     * ClientEndpoint is successfully established after a handshake.
     * It also starts a schedule to send a "noop" to the ServerEndpoint to hold the connection.
     *
     * @param session is the session we receive from the server.
     */
    @OnOpen
    public void onOpen(Session session) {

        this.session = session;
        this.noopTimer = new Timer();

        //send noop every minute to server
        this.noopTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (session.isOpen()) {
                    sendNOOP();
                }
            }
        }, 0, 1000 * 60);
    }

    /**
     * This method is called when the ServerEndpoint has sent a message to the ClientEndpoint.
     * The message will be handled by the specific WebSocketHandler.
     *
     * @param message is a JSONObject as a String that we receive from the ServerEndpoint.
     */
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

    /**
     * This method is called once the connection is being closed.
     * It also tries to reconnect the ClientEndpoint to the ServerEndpoint in case
     * of an abnormal close reason. The code "1000" stands for a normal closure.
     *
     * @param session is the session we received from the server.
     * @param reason contains the reason why the ServerEndpoint closed the connection.
     */
    @OnClose
    public void onClose(Session session, CloseReason reason) {

        System.out.println("close code: " + reason.getCloseCode().getCode());
        //abnormal closure
        if (reason.getCloseCode().getCode() != 1000 && reason.getCloseCode().getCode() != 503) {
            try {
                while (!this.session.isOpen()) {
                    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                    container.connectToServer(this, uri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (reason.getCloseCode().getCode() == 503) {
            System.out.println("Error 503, The service is unavailable");
        }
        //normal closure
        else {
            //Stop the timer
            this.session = null;
            this.noopTimer.cancel();
        }
    }

    /**
     * This method is called when an Exception is being thrown by any method annotated
     * with @OnOpen, @OnMessage and @OnClose.
     * Since the methods of this class won't throw an Exception, this method will only be called when
     * the methods of the ServerEndpoint throw an Exception.
     *
     * @param session is the session we received from the server.
     * @param t is the thrown Exception that we received.
     */
    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
    }
    
    /**
     * Set the JSON adapter to mock this Requests object.
     * 
     * @param adapter the JSON adapter
     */
    public void setJSONAdapter(JsonAdapter adapter) {
	this.adapter = adapter;
    }
    
    /**
     * Checks if the message is a private message or an all-chat message.
     * Uses the specific method afterwards.
     *
     * @param message is the ChatMessage that has to be sent to the ServerEndpoint.
     */
    public void sendChatMessage(ChatMessage message) {

        if (message.getChannel().equals("all")) {
            sendChatMessage(message.getChannel(), message.getSender().getName(), message.getMessage());
        } else {
            sendChatMessage(message.getChannel(), message.getSender().getName(),
                    message.getMessage(), message.getReceiver().getName());
        }
    }

    /**
     * Sends an all-chat message to the ServerEndpoint.
     *
     * @param channel is "all"
     * @param from contains the name of the player who has sent the message.
     * @param msg contains the message for the all-chat.
     */
    public void sendChatMessage(String channel, String from, String msg) {

        JSONObject jsonObject = new JSONObject().put("channel", channel)
                .put("from", from).put("message", msg);

        sendMessage(jsonObject);
    }

    /**
     * Sends a private message to the ServerEndpoint.
     *
     * @param channel is "private".
     * @param from contains the name of the player who has sent the message.
     * @param msg contains the private message.
     * @param to contains the name of the player who should receive the message.
     */
    public void sendChatMessage(String channel, String from, String msg, String to) {

        JSONObject jsonObject = new JSONObject().put("channel", channel)
                .put("from", from).put("message", msg).put("to", to);

        sendMessage(jsonObject);
    }
    
    /**
     * Closes the session with the server and stops the WebSocket.
     *
     * @throws IOException if there was a connection error closing the connection.
     */
    public void stop() throws IOException {

        if (this.session != null && this.session.isOpen()) {
            this.session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "normal close"));
            this.noopTimer.cancel();
        }
    }

    /**
     * This method checks if the connection with the ServerEndpoint is still established.
     *
     * @return returns true if the session is not closed yet and false if it is closed.
     */
    public boolean isOpen() {

        if (this.session != null) {
            if (this.session.isOpen()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Sends a JSONObject as a String to the ServerEndpoint.
     *
     * @param jsonObject contains the JSONObject that has to be sent to the ServerEndpoint.
     */
    public void sendGameMessage(JSONObject jsonObject) {
	
	this.sendMessage(jsonObject);
    }

    /**
     * Sends an all-chat message to the game.
     *
     * @param message contains all necessary information about the message and the sender/receiver.
     */
    public void sendAllChatGameMessage(ChatMessage message) {
        if (!this.uri.toString().startsWith(BS_WS_URI + GAME_WS)) {
            System.out.println("not a gameClient!");
            return;
        } else {
            JSONObject json = new JSONObject();
            json.put("messageType", "chat").put("channel", message.getChannel()).put("message", message.getMessage());
            sendGameMessage(json);
        }
    }

    /**
     * Sends a private message to a player that is in the same game as the current player.
     *
     * @param message contains all necessary information about the message and the sender/receiver.
     */
    public void sendPrivateChatGameMessage(ChatMessage message) {
        if (!this.uri.toString().startsWith(BS_WS_URI + GAME_WS)) {
            System.out.println("not a gameClient!");
            return;
        } else {
            JSONObject json = new JSONObject();
            json.put("messageType", "chat").put("channel", message.getChannel())
                    .put("from", message.getSender().getName())
                    .put("to", message.getReceiver().getName())
                    .put("message", message.getMessage());
            sendGameMessage(json);
        }
    }
    
    private void sendMessage(JSONObject jsonObject) {
	
	if(this.adapter != null) {
	    this.adapter.onRequestSend("WS", this.uri.toString(), jsonObject);
	    return;
	}
	this.session.getAsyncRemote().sendText(jsonObject.toString());
    }
    
    private void sendNOOP() {
	    if(this.adapter == null) {
    	    try {
                session.getBasicRemote().sendText(NOOP_OP);
            } catch (Exception e) {
                System.err.println("cannot send NOOP");
            }
	    }

    }
}