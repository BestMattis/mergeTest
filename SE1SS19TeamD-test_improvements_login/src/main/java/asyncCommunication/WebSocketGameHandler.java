package asyncCommunication;

import org.json.JSONObject;

public class WebSocketGameHandler implements WebSocketHandler {

    @Override
    public void handle(JSONObject msg) {

        System.out.println(msg.toString() + "");
    }
}
