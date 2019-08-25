package asyncCommunication;

import gameScreen.GameFieldController;
import gameScreen.GameScreenController;
import gameScreen.Minimap;
import javafx.application.Platform;
import main.AdvancedWarsApplication;
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
    // stores the the units by their addresses for every player
    Map<String, List<String>> playerWithUnitIDs = new HashMap<String, List<String>>();
    private Model model;
    private String[] specificPlayerKeys = {"color", "isReady", "name", "id", "currentGame"};
    private String[] allUnitPlayerKeys = {"allUnits", "allPlayer", "id"};
    private String[] gameFieldKeys = {"game", "isPassable", "x", "y", "id"};
    private String[] gameChangeKeys = {"newValue", "fieldName", "id"};
    private String[] unitInfoKeys = {"leader", "canAttack", "game", "mp", "hp", "id", "position", "type"};
    public WebSocketGameHandler(Model model) { 
        this.model = model;
    }

    @Override
    public void handle(JSONObject msg) {

        System.out.println(this.getClass().toString() + ": " + msg.toString() + "");

        if (msg.has("data")) {

            // System.out.println("try handling");
            JSONObject data = new JSONObject(msg.get("data").toString());
            JSONArray dataKeys = data.names();
            JSONArray playerData = new JSONArray(specificPlayerKeys);
            // check if keys are similar, add other players to the datamodel
            if (dataKeys.similar(playerData)
                    && (msg.get("action").equals("gameInitObject") || msg.get("action").equals("gameNewObject"))) {
                String color = data.getString("color");
                Boolean isReady = data.getBoolean("isReady");
                String playerName = data.getString("name");
                String playerAdress = data.getString("id");
                String currentGameAdress = data.getString("currentGame");
                // TODO: add gameAddress and playerAddresses as a game attribute

                if (playerName != null) {
                    // Display system Msg Player is ready in chat
                    ChatMessage message = new ChatMessage().setChannel("all")
                            .setMessage(" { " + playerName + " has joined the game }")
                            .setSender(new Player().setName("System"));
                    message.setDate(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
                    model.getApp().getCurrentPlayer().getGame().withIngameMessages(message);
                }

                System.out.println(this.getClass().toString() + ": add Player");

                if (!playerName.equals(model.getApp().getCurrentPlayer().getName())) {
                    Player newPlayer = new Player();
                    newPlayer.setName(playerName);
                    newPlayer.setId(playerAdress); // ID = playerAddress?
                    newPlayer.setColor(color);
                    newPlayer.setIsReady(isReady);
                    Players.add(newPlayer);
                    Game currentGame = model.getApp().getCurrentPlayer().getGame();
                    currentGame.withPlayers(newPlayer);
                    System.out.println(newPlayer.toString());
                } else {
                    // if message is for currentPlayer, only add missing attributes
                    Player currentPlayer = model.getApp().getCurrentPlayer();
                    currentPlayer.setColor(color);
                    currentPlayer.setId(playerAdress);
                }

                // Game Change Object
            } else if (dataKeys.similar(new JSONArray(gameChangeKeys))
                    && msg.get("action").equals("gameChangeObject")) {
                String id = data.getString("id");
                String fieldName = data.getString("fieldName");
                String newValue = data.getString("newValue");

                Game currentGame = model.getApp().getCurrentPlayer().getGame();

                switch (fieldName) {
                    case "isReady":
                        for (Player player : currentGame.getPlayers()) {
                            if (player.getId() != null && player.getId().equals(id)) {
                                if (Boolean.valueOf(newValue)) {
                                    player.setIsReady(Boolean.valueOf(newValue));

                                    // Display system Msg Player is ready in chat
                                    ChatMessage messageReady = new ChatMessage().setChannel("all")
                                            .setMessage(" { " + player.getName() + " is ready }")
                                            .setSender(new Player().setName("System"));
                                    messageReady.setDate(
                                            new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
                                    model.getApp().getCurrentPlayer().getGame().withIngameMessages(messageReady);
                                    break;
                                }
                            }
                        }
                        break;
                    case "winner":
                        for (Player player : currentGame.getPlayers()) {
                            if (player.getId() != null && player.getId().equals(newValue)) {
                                model.getApp().getCurrentPlayer().getGame().setWinner(player.getName());
                                break;
                            }
                        }
                        break;
                    case "phase":
                        model.getApp().getCurrentPlayer().getGame().setCurrentPhase(data.getString("newValue"));
                        // Display system Msg Player is ready in chat
                        ChatMessage messagePhase = new ChatMessage().setChannel("all")
                                .setMessage(" { " + model.getApp().getCurrentPlayer().getGame().getActivePlayer()
                                        + " changed into the " + model.getApp().getCurrentPlayer().getGame().getCurrentPhase() + "} ")
                                .setSender(new Player().setName("System"));
                        messagePhase.setDate(
                                new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
                        model.getApp().getCurrentPlayer().getGame().withIngameMessages(messagePhase);
                        break;
                    case "currentPlayer":
                        for (Player p : model.getApp().getCurrentPlayer().getGame().getPlayers()) {
                            if (p.getId().equals(data.get("newValue"))) {
                                model.getApp().getCurrentPlayer().getGame().setActivePlayer(p.getName());
                                model.getApp().getCurrentPlayer().getGame().setTurnPlayer(p);
                                // Display system Msg Player is ready in chat
                                ChatMessage messageCP = new ChatMessage().setChannel("all")
                                        .setMessage(" { Active Player is " + p.getName() + "}")
                                        .setSender(new Player().setName("System"));
                                messageCP.setDate(
                                        new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
                                model.getApp().getCurrentPlayer().getGame().withIngameMessages(messageCP);
                                break;
                            }
                        }
                        break;
                }
                // Game-chat
            } else if (msg.get("action").equals("gameChat")) {

                System.out.println(this.getClass().toString() + ": messaging");

                String sender = data.getString("from");
                String channel = data.getString("channel");
                String message = data.getString("message");
                ChatMessage chatMessage = new ChatMessage().setChannel(channel).setMessage(message);
                chatMessage.setDate(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
                // set sender
                if (!sender.equals(model.getApp().getCurrentPlayer().getName())) {
                    for (Player player : model.getApp().getCurrentPlayer().getGame().getPlayers()) {
                        if (player.getName().equals(sender)) {
                            chatMessage.setSender(player);
                            break;
                        }
                    }

                    // set receiver if it is a private message
                    if (data.has("to")) {

                        String receiver = data.getString("to");
                        if (receiver.equals(model.getApp().getCurrentPlayer().getName())) {

                            chatMessage.setReceiver(model.getApp().getCurrentPlayer());
                            model.getApp().getCurrentPlayer().getGame().withIngameMessages(chatMessage);
                        }
                    }
                    // add an all-chat ingameMessage
                    else {
                        model.getApp().getCurrentPlayer().getGame().withIngameMessages(chatMessage);
                    }
                }
            } else if ((dataKeys.similar(gameFieldKeys) || (data.has("isPassable")))
                    && msg.get("action").equals("gameInitObject")) {
                Game currentGame = model.getApp().getCurrentPlayer().getGame();
                boolean isPassable = data.getBoolean("isPassable");
                int x = data.getInt("x");
                int y = data.getInt("y");
                String id = data.getString("id");

                String fieldName = id.substring(0, id.indexOf('@'));

                Field newField = new Field().setType(fieldName).setIsPassable(isPassable).setPosX(x).setPosY(y)
                        .setId(id);
                model.getApp().getCurrentPlayer().getGame().getGameField().withFields(newField);
                // System.out.println(this.getClass().toString() + ": packedField");

            } else if (dataKeys.similar(new JSONArray(unitInfoKeys)) && msg.get("action").equals("gameNewObject")) {
                Player leader = null;
                for (Player player : model.getApp().getCurrentPlayer().getGame().getPlayers()) {
                    if (player.getId().equals(data.getString("leader"))) {
                        leader = player;
                        break;
                    }
                }
                String[] canAttack = data.get("canAttack").toString().replace("[", "").replace("]", "")
                        .replace("\"", "").replace(" ", "").split(",");

                Game game = model.getApp().getCurrentPlayer().getGame();
                int mp = data.getInt("mp");
                int hp = data.getInt("hp");
                String id = data.getString("id");
                Field position = null;
                for (Field field : model.getApp().getCurrentPlayer().getGame().getGameField().getFields()) {
                    if (field.getId().equals(data.getString("position"))) {
                        position = field;
                        break;
                    }
                }
                String type = data.getString("type").replace(" ", "");

                Unit unit = new Unit().setMaxHp(hp).setHp(hp).setMaxMp(mp).setMp(mp).setId(id).setType(type);
                unit.setPosX(position.getPosX()).setPosY(position.getPosY()).setHasAttacked(false);

                unit.setCanAttack(new ArrayList<>());

                for (String s : canAttack) {
                    unit.getCanAttack().add(s);
                }
                leader.withCurrentUnits(unit);
                position.setOccupiedBy(unit);
                game.withAllUnits(unit);

                Minimap minimap = AdvancedWarsApplication.getInstance().getGameScreenCon().getMinimap();
                if (minimap != null && minimap.getIsCreated()
                        && AdvancedWarsApplication.getInstance().getGameScreenCon().getMinimapWasReady()) {
                    Platform.runLater(() -> minimap.addSingleUnitToMinimap(unit));
                } else {
                    AdvancedWarsApplication.getInstance().getGameScreenCon().setMinimapWasReady(false);
                }
            } else if (msg.get("action").equals("gameRemoveObject")) {
                if (data.getString("id").startsWith("Player") && data.getString("fieldName").equals("allPlayer")) {
                    for (Player p : model.getApp().getCurrentPlayer().getGame().getPlayers()) {
                        if (p.getId().equals(data.getString("id"))) {
                            ChatMessage message = new ChatMessage().setChannel("all")
                                    .setMessage(" { " + p.getName() + " has left the game }")
                                    .setSender(new Player().setName("System"));
                            message.setDate(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
                            model.getApp().getCurrentPlayer().getGame().withIngameMessages(message);
                            model.getApp().getCurrentPlayer().getGame().withoutPlayers(p);
                            break;
                        }
                    }
                }
            }
        } else if (msg.get("action").equals("gameInitFinished")) {
            System.out.println(this.getClass().toString() + ": field("
                    + model.getApp().getCurrentPlayer().getGame().getGameField() + ") will be set true");
            gameInitFinished = true;
            model.getApp().getCurrentPlayer().getGame().getGameField().setGameFieldLoaded(true);
        } else if(msg.get("action").equals("gameStarts")) {
			model.getApp().getCurrentPlayer().getGame().setStarted(true);
		}
    }
}
