package asyncCommunication;

import org.json.JSONObject;

public interface WebSocketHandler {

    public void handle(JSONObject msg);
}
