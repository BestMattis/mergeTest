package model;


import java.util.HashMap;

import asyncCommunication.WebSocketComponent;
import syncCommunication.HttpRequests;

public class Model {

    private static Model instance;
    private static App app;
    private static HashMap<Player, HttpRequests> playerHttpRequestsHashMap;
    private static WebSocketComponent wSC;


    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    public static App getApp() {
        if (app == null) {
            app = new App();
        }
        return app;
    }
    
    public static WebSocketComponent getWebSocketComponent() {
    	return wSC;
    }
    
    public static void setWebSocketComponent(WebSocketComponent component) {
        wSC = component;
    }

    public void setApp(App app) {
        Model.app = app;
    }

    public static HashMap<Player, HttpRequests> getPlayerHttpRequestsHashMap() {
        if (playerHttpRequestsHashMap == null) {
            playerHttpRequestsHashMap = new HashMap<>();
        }
        return playerHttpRequestsHashMap;
    }

}
