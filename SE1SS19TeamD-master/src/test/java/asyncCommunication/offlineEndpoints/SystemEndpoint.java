package asyncCommunication.offlineEndpoints;


import org.glassfish.tyrus.server.Server;
import org.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;

import static asyncCommunication.Constants.SYSTEM_WS;


@ServerEndpoint(value = SYSTEM_WS)
public class SystemEndpoint {

    private ArrayList<Session> sessions = new ArrayList<>();

    public SystemEndpoint() {
        System.out.println("connecting...");
    }

    @OnOpen
    public void onOpen(Session session) {

        this.sessions.add(session);
        System.out.println("connection opened with Systemserver, Session id: " + session.getId());
        for (int i = 0; i < sessions.size(); i++) {
            if (!sessions.get(i).equals(session)) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("data", new JSONObject().put("id", session.toString()));
                    json.put("action", "userJoined");
                    sessions.get(i).getBasicRemote().sendText(json.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //system server should not respond on messages
    @OnMessage
    public void onMessage(String msg, Session session) {

    }

    @OnError
    public void onError(Throwable e) {

        System.out.println("error");
        e.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {

        for (int i = 0; i < sessions.size(); i++) {
            if (!sessions.get(i).equals(session)) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("data", new JSONObject().put("id", session.getId()).toString());
                    json.put("id", session.getId());
                    json.put("action", "userLeft");
                    sessions.get(i).getBasicRemote().sendText(json.toString());
                    sessions.remove(i);
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void runServer() {

        Server server = new Server("localhost", 8025, "/websocket",
                null, SystemEndpoint.class);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
