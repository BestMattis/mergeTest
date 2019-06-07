package model;


import syncCommunication.HttpRequests;

import java.util.HashMap;

public class Model {

    private static Model instance;
    private static App app;
    private static HashMap<Player, HttpRequests> playerHttpRequestsHashMap;


    public static Model getInstance(){
        if(instance == null){
            instance = new Model();
        }
        return instance;
    }

    public static App getApp(){
        if(app == null){
            app = new App();
        }
        return app;
    }

    public void setApp(App app){
        Model.app = app;
    }

    public static HashMap<Player, HttpRequests> getPlayerHttpRequestsHashMap() {
        if(playerHttpRequestsHashMap == null){
            playerHttpRequestsHashMap = new HashMap<>();
        }
        return playerHttpRequestsHashMap;
    }

}
