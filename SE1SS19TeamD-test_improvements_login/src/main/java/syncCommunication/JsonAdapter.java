package syncCommunication;

import org.json.JSONObject;

public interface JsonAdapter {

    void onRequestSend(JSONObject json);

}
