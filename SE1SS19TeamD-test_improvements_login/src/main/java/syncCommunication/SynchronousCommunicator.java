package syncCommunication;

import org.json.JSONObject;

public class SynchronousCommunicator {
    private HttpRequests hr;

    SynchronousCommunicator(HttpRequests hReq) {
        hr = hReq;
    }


    /**
     * Inject a JSONObject into the HTTPRequests for offline tests.
     *
     * @param jsonO The injected JSONObject
     */
    public void injectResponse(JSONObject jsonO) {
        hr.injectResponse(jsonO);
    }

    /**
     * Inject a JSONObject into the HTTPRequests for offline tests.
     *
     * @param jAdapter The JsonAdapter Interface that will be set
     */
    public void setJsonAdapter(JsonAdapter jAdapter) {
        hr.setJsonAdapter(jAdapter);
    }


}
