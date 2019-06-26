package asyncCommunication.offlineEndpoints;

import org.glassfish.tyrus.server.Server;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;

import static asyncCommunication.Constants.NOOP_OP;

@ServerEndpoint(value = "/game")
public class GameEndPoint {

    private ArrayList<Session> sessions = new ArrayList<>();

    public GameEndPoint() {

        System.out.println("connecting...");
    }

    @OnOpen
    public void onOpen(Session session) {

        this.sessions.add(session);
        System.out.println("connection opened with Chatserver, Session id: " + session.getId());
    }

    @OnMessage
    public void onMessage(String msg, Session session) {

        if (msg == NOOP_OP) {
            //do nothing
        }
    }

    @OnError
    public void onError(Throwable e) {

        System.out.println("error");
        e.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
    }

    public void runServer() {

        Server server = new Server("localhost", 8080, "/websocket",
                null, ChatEndpoint.class);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
