package asyncCommunication.offlineEndpoints;

import org.glassfish.tyrus.server.Server;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;

import static asyncCommunication.Constants.NOOP_OP;

@ServerEndpoint(value = "/chat")
public class ChatEndpoint {

    private ArrayList<Session> sessions = new ArrayList<>();

    public ChatEndpoint() {

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
        } else {

            for (int i = 0; i < sessions.size(); i++) {

                try {
                    sessions.get(i).getBasicRemote().sendText(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnError
    public void onError(Throwable e) {

        System.out.println("error");
        e.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {

        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).equals(session)) {
                sessions.remove(i);
                try {
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void runServer() {

        Server server = new Server("localhost", 8090, "/websocket",
                null, ChatEndpoint.class);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
