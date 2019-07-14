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

    private String[] specificPlayerKeys = {"army", "color", "name", "id", "currentGame"};
    private String[] allUnitPlayerKeys = {"allUnits", "allPlayer", "id"};
    private String[] gameFieldKeys = {"game", "isPassable",  "x", "y", "id"};



    @Override
    public void handle(JSONObject msg) {

        System.out.println(msg.toString() + "");

        if (msg.has("data")) {

            System.out.println("try handling");

            JSONObject data = new JSONObject(msg.get("data").toString());
            JSONArray dataKeys = data.names();

            // check if keys are similar, add other players to the datamodel
            if (dataKeys.similar(new JSONArray(specificPlayerKeys)) &&
                    (msg.get("action").equals("gameInitObject") || msg.get("action").equals("gameNewObject"))) {

                JSONArray armyIDs = data.getJSONArray("army");
                String color = data.getString("color");
                String playerName = data.getString("name");
                String playerAdress = data.getString("id");
                String currentGameAdress = data.getString("currentGame");
                // maybe add gameAdress and playerAdresses as a game attribute

                System.out.println("add Player");

                if (!playerName.equals(Model.getApp().getCurrentPlayer().getName())) {

                    Player newPlayer = new Player().setName(playerName);
                    Players.add(newPlayer);
                    Game currentGame = Model.getApp().getCurrentPlayer().getGame();
                    currentGame.withPlayers(newPlayer);
                }

            // Game-chat
            } else if (msg.get("action").equals("gameChat")) {

                System.out.println("messaging");

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

                Field newField = new Field().setType(fieldName).setIsPassable(isPassable).setPosX(x).setPosY(y);
                if (Model.getInstance().getApp().getCurrentPlayer().getGame().getGameField() == null){
                    Model.getInstance().getApp().getCurrentPlayer().getGame().setGameField(new GameField());
                }
                Model.getInstance().getApp().getCurrentPlayer().getGame().getGameField().withFields(newField);
                System.out.println("packedField");

            }
        }
    }
}
