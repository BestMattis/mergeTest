package syncCommunication;

import com.mashape.unirest.request.BaseRequest;

public interface JsonAdapter  {

    void onRequestSend(BaseRequest req);

}
