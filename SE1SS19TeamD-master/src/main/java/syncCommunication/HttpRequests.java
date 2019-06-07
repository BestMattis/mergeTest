package syncCommunication;

import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.RESTExceptions.TooManyRequestsPerSecondException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.BaseRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.*;

public class HttpRequests {

    private static final String BASE_URL = "https://rbsg.uniks.de/api";

    private long lastRequestTimeMilliseconds;
    private int taskCountInTheLastSecond = 0;

    private String userKey;

    private JSONObject injection;

    private JsonAdapter jAdapter;

    public HttpRequests() {
        this(null);
    }

    public HttpRequests(String userKey) {
        this.userKey = userKey;

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {

            if (!checkLastRequest() && userKey != null) {
                try {
                    getAsUser(userKey, "/user");

                } catch (JSONException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            taskCountInTheLastSecond = 0;

        }, 0, 1, TimeUnit.SECONDS);
    }

    // gives false if too much time has passed since the last request
    // or if somehow the last request was in the future
    private boolean checkLastRequest() {
        long currentTime = System.currentTimeMillis();

        // If last request was more than 10 or negative  many minutes ago
        // time that has to elapse (in minutes) before the userKey is refreshed by exec
        // Should always be lower than 15, since a userKey is only lost after 15 minutes
        int REQUEST_REFRESH_THRESHLD = 10;
        return !(lastRequestTimeMilliseconds - currentTime > REQUEST_REFRESH_THRESHLD * 60000
                || lastRequestTimeMilliseconds - currentTime < 0);
    }

    // Counts the requests and dates the last one.
    private void updateLastRequestTime() {
        lastRequestTimeMilliseconds = System.currentTimeMillis();
        ++taskCountInTheLastSecond;
    }

    // Delays a requests by one second if there were too many.
    private void limitRequestsPerSecond() {
        try {
            checkIfOverRequestLimit();
        } catch (TooManyRequestsPerSecondException e) {
            e.printStackTrace();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                System.out.println("Delaying by one second");
                ex.printStackTrace();
            }
        }
    }

    // Throws TooManyRequestsPerSecondException if something went
    // over the limit just so limitRequestsPerSecond() can catch it.
    private void checkIfOverRequestLimit() throws TooManyRequestsPerSecondException {
        int MAXIMUM_NUMBER_OF_TASKS_PER_SECOND = 5;
        if (taskCountInTheLastSecond >= MAXIMUM_NUMBER_OF_TASKS_PER_SECOND) {
            throw new TooManyRequestsPerSecondException("This was the " + taskCountInTheLastSecond
                    + ". request. Maximum is " + MAXIMUM_NUMBER_OF_TASKS_PER_SECOND);
        }
    }

    /*
     * POST HTTP request, transmitting a json to a specific URL
     * Throws ExecutionException, InterruptedException and JSONException.
     */
    JSONObject postJSON(JSONObject jsonObject, String postToURL) throws ExecutionException, InterruptedException, JSONException {

        BaseRequest req = Unirest.post(BASE_URL + postToURL).body(jsonObject.toString());

        if (jAdapter != null) {
            jAdapter.onRequestSend(req);
        }

        if (injection != null) {
            return injection;
        }

        updateLastRequestTime();
        limitRequestsPerSecond();

        Future<HttpResponse<JsonNode>> future = req.asJsonAsync();
        HttpResponse<JsonNode> response = future.get();

        return new JSONObject(response.getBody().toString());
    }

    /*
     * POST HTTP request, transmitting a json to a specific URL as a user via userKey.
     * Throws ExecutionException, InterruptedException and JSONException.
     */
    JSONObject postJSONAs(String asUser, JSONObject jsonObject, String postToURL)
            throws ExecutionException, InterruptedException, JSONException {

        BaseRequest req = Unirest.post(BASE_URL + postToURL)
                .header("userKey", asUser).body(jsonObject.toString());

        if (jAdapter != null) {
            jAdapter.onRequestSend(req);
        }

        if (injection != null) {
            return injection;
        }

        updateLastRequestTime();
        limitRequestsPerSecond();


        Future<HttpResponse<JsonNode>> future = req.asJsonAsync();
        HttpResponse<JsonNode> response = future.get();

        return new JSONObject(response.getBody().toString());
    }


    /*
     * GET HTTP request from a URL as a user via userKey.
     * Throws ExecutionException, InterruptedException and JSONException.
     */
    JSONObject getAsUser(String asUser, String getFromURL)
            throws ExecutionException, InterruptedException, JSONException {

        BaseRequest req = Unirest.get(BASE_URL + getFromURL).header("userKey", asUser);

        if (jAdapter != null) {
            jAdapter.onRequestSend(req);
        }

        if (injection != null) {
            return injection;
        }

        updateLastRequestTime();
        limitRequestsPerSecond();

        Future<HttpResponse<JsonNode>> future = req.asJsonAsync();
        HttpResponse<JsonNode> response = future.get();

        return new JSONObject(response.getBody().toString());
    }

    /*
     * DELETE HTTP request from a URL as a user via userKey.
     * Throws ExecutionException, InterruptedException and JSONException.
     */
    JSONObject deleteAsUser(String asUser, String deleteAtURL)
            throws ExecutionException, InterruptedException, JSONException {

        BaseRequest req = Unirest.delete(BASE_URL + deleteAtURL).header("userKey", asUser);

        if (jAdapter != null) {
            jAdapter.onRequestSend(req);
        }

        if (injection != null) {
            return injection;
        }

        updateLastRequestTime();
        limitRequestsPerSecond();

        Future<HttpResponse<JsonNode>> future = req.asJsonAsync();
        HttpResponse<JsonNode> response = future.get();

        return new JSONObject(response.getBody().toString());
    }

    void loginAs(String username, String password) throws JSONException, LoginFailedException {

        updateLastRequestTime();
        limitRequestsPerSecond();

        JSONObject userData = new JSONObject();
        userData.put("name", username);
        userData.put("password", password);

        try {
            JSONObject response = postJSON(userData, "/user/login");
            if (response.getString("status").equals("success")) {
                userKey = response.getJSONObject("data").getString("userKey");
            } else {
                throw new LoginFailedException(response.getString("message"));
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void logOut() throws LoginFailedException {
        updateLastRequestTime();
        limitRequestsPerSecond();

        if (userKey == null) {
            throw new LoginFailedException("Log in first");
        }
        try {
            JSONObject response = getAsUser(userKey, "/user/logout");

            if (response.getString("status").equals("success")) {
                userKey = null;
            } else {
                throw new LoginFailedException(response.getString("message"));
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void injectResponse(JSONObject jsonO) {
        injection = jsonO;
    }

    void setJsonAdapter(JsonAdapter jAdapter) {
        this.jAdapter = jAdapter;
    }



    String getUserKey() {
        return userKey;
    }
}
