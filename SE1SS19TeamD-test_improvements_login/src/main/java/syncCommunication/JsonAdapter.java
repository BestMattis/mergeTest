package syncCommunication;

import org.json.JSONObject;

@FunctionalInterface
public interface JsonAdapter {

    void onRequestSend(String url, JSONObject json);

}
