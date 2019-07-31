package asyncCommunication;

import model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class WebSocketGameHandler implements WebSocketHandler {

    public ArrayList<Player> Players = new ArrayList<>();
    public ArrayList<String> allPlayers = new ArrayList<>();
    public boolean gameInitFinished = false;
    public ArrayList<Unit> allUnits = new ArrayList<>();
    //stores the the units by their addresses for every player
    Map<String, List<String>> playerWithUnitIDs = new HashMap<String, List<String>>();

    private String[] specificPlayerKeys = {"color", "isReady", "name", "id", "currentGame"};
    private String[] allUnitPlayerKeys = {"allUnits", "allPlayer", "id"};
    private String[] gameFieldKeys = {"game", "isPassable",  "x", "y", "id"};
    private String[] gameChangeKeys = {"newValue", "fieldName", "id"};


    @Override
    public void handle(JSONObject msg) {

        if (msg.has("data")) {
            JSONObject data = new JSONObject(msg.get("data").toString());
            JSONArray dataKeys = data.names();

            // check if keys are similar, add other players to the datamodel
            if (dataKeys.similar(new JSONArray(specificPlayerKeys)) &&
                    (msg.get("action").equals("gameInitObject") || msg.get("action").equals("gameNewObject"))) {

                String color = data.getString("color");
                Boolean isReady = data.getBoolean("isReady");
                String playerName = data.getString("name");
                String playerAdress = data.getString("id");
                String currentGameAdress = data.getString("currentGame");
                // TODO: add gameAddress and playerAddresses as a game attribute
                
                if (!playerName.equals(Model.getApp().getCurrentPlayer().getName())) {
                    Player newPlayer = new Player();
                    newPlayer.setName(playerName);
                    newPlayer.setId(playerAdress); //ID = playerAddress?
                    newPlayer.setColor(color);
                    newPlayer.setIsReady(isReady);
                    Players.add(newPlayer);
                    Game currentGame = Model.getApp().getCurrentPlayer().getGame();
                    currentGame.withPlayers(newPlayer);
                    System.out.println(newPlayer.toString());
                }else{
                    //if message is for currentPlayer, only add missing attributes
                    Player currentPlayer = Model.getApp().getCurrentPlayer();
                    currentPlayer.setColor(color);
                    currentPlayer.setId(playerAdress);
                }

            //Game Change Object
            } else if (dataKeys.similar(new JSONArray(gameChangeKeys)) && msg.get("action").equals("gameChangeObject")){
                String id = data.getString("id");
                String fieldName = data.getString("fieldName");
                String newValue = data.getString("newValue");

                Game currentGame = Model.getApp().getCurrentPlayer().getGame();

                for(Player player : currentGame.getPlayers()){
                    if(player.getId() != null && player.getId().equals(id)){
                        if(fieldName.equals("isReady")){
                            player.setIsReady(Boolean.valueOf(newValue));
                            break;
                        }
                    }
                }

            // Game-chat
            } else if (msg.get("action").equals("gameChat")) {
                String sender = data.getString("from");
                String channel = data.getString("channel");
                String message = data.getString("message");
                ChatMessage chatMessage = new ChatMessage().setChannel(channel).setMessage(message);
                chatMessage.setDate(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
                // set sender
                if (!sender.equals(Model.getApp().getCurrentPlayer().getName())) {
                    for (Player player : Players) {
                        if (player.getName().equals(sender)) {
                            chatMessage.setSender(player);
                        }
                    }

                    // set receiver if it is a private message
                    if (data.has("to")) {

                        String receiver = data.getString("to");
                        if (receiver.equals(Model.getApp().getCurrentPlayer().getName())) {

                            chatMessage.setReceiver(Model.getApp().getCurrentPlayer());
                            Model.getApp().getCurrentPlayer().getGame().withIngameMessages(chatMessage);
                        }
                    }
                     // add an all-chat ingameMessage
                    else {
                        System.out.println(chatMessage.toString() + "###");
                        System.out.println(Model.getApp().getCurrentPlayer().getName());
                        System.out.println(Model.getApp().getCurrentPlayer().getGame().getName());
                        Model.getApp().getCurrentPlayer().getGame().withIngameMessages(chatMessage);
                    }
                }
            } else if ((dataKeys.similar(gameFieldKeys)|| (data.has("isPassable"))) && msg.get("action").equals("gameInitObject")) {
                Game currentGame = Model.getApp().getCurrentPlayer().getGame();
                boolean isPassable = data.getBoolean("isPassable");
                int x = data.getInt("x");
                int y = data.getInt("y");
                String id = data.getString("id");

                String fieldName = id.substring(0, id.indexOf('@'));

                Field newField = new Field().setType(fieldName).setIsPassable(isPassable).setPosX(x).setPosY(y).setId(id);
                if(Model.getApp().getCurrentPlayer().getGame().getGameField() == null) {
                	Model.getApp().getCurrentPlayer().getGame().setGameField(new GameField());
                }
                Model.getApp().getCurrentPlayer().getGame().getGameField().withFields(newField);

            } else if(msg.get("action").equals("winner")) {
            	System.out.println("new winner");
            	if(data.getString("playerName") != null) {
            		for(Player p : Model.getApp().getCurrentPlayer().getGame().getPlayers()) {
            			if(p.getName().equals(data.getString("playerName"))) {
            				Model.getApp().getCurrentPlayer().getGame().setWinner(p.getName());
            			}
            		}
            	}
            	System.out.println(Model.getApp().getCurrentPlayer().getGame().getWinner()+" won the game");
            }
        } else if(msg.get("action").equals("gameInitFinished")){
            gameInitFinished = true;
            Model.getApp().getCurrentPlayer().getGame().getGameField().setGameFieldLoaded(true);
        }
    }
}
