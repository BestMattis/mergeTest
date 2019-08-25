package asyncCommunication;

import javax.websocket.ClientEndpointConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebSocketConfigurator extends ClientEndpointConfig.Configurator {

    public static String userKeytmp = "";
    public String userKey = "";

    @Override
    public void beforeRequest(Map<String, List<String>> headers) {

        super.beforeRequest(headers);
        ArrayList<String> key = new ArrayList<>();
        key.add(userKeytmp);
        headers.put("userKey", key);
    }


    public void setToMine() {
        userKeytmp = userKey;
    }
}

