package model;


import asyncCommunication.WebSocketComponent;
import asyncCommunication.WebSocketConfigurator;
import gameScreen.GameFieldController;
import syncCommunication.HttpRequests;

import java.util.HashMap;

public class Model {

    private  App app;
    private  HashMap<Player, HttpRequests> playerHttpRequestsHashMap;
    private  WebSocketComponent wSC;
    private  WebSocketConfigurator webSocketConfigurator;
    private GameFieldController gameFieldController;


    public GameFieldController getGameFieldController() {
        return gameFieldController;
    }

    public void setGameFieldController(GameFieldController gameFieldController) {
        this.gameFieldController = gameFieldController;
    }


    public App getApp() {
        if (app == null) {
            app = new App();
        }
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public WebSocketComponent getWebSocketComponent() {
        return wSC;
    }

    public void setWebSocketComponent(WebSocketComponent component) {
        this.wSC = component;
    }

    public HashMap<Player, HttpRequests> getPlayerHttpRequestsHashMap() {
        if (playerHttpRequestsHashMap == null) {
            playerHttpRequestsHashMap = new HashMap<>();
        }
        return playerHttpRequestsHashMap;
    }

    public WebSocketConfigurator getWebSocketConfigurator() {
        if (webSocketConfigurator == null){
            webSocketConfigurator = new WebSocketConfigurator();
        }
        return webSocketConfigurator;
    }
}
