package asyncCommunication;

import gameList.GameListener;
import model.Game;
import model.Model;
import model.Player;
import org.json.JSONObject;
import playerList.PlayerListener;

public class WebSocketSystemHandler implements WebSocketHandler {


    /**
     * Handles an incoming system message with different actions from the server.
     *
     * @param msg is a JSONObject that contains the key "action" and the key "data" which also contains a JSONObject.
     */
    @Override
    public void handle(JSONObject msg) {
        String action = msg.getString("action");
        JSONObject data = new JSONObject(msg.get("data").toString());

        //so that the tests can still run
        if (Model.getApp().getCurrentPlayer() == null) {
            return;
        }

        // data contains the name of the user
        if (action.equals("userJoined")) {

            String name = data.getString("name");
            System.out.println("User: " + name + " came online.");

            if (!name.equals(Model.getApp().getCurrentPlayer().getName())) {
                if (!name.equals(Model.getApp().getCurrentPlayer().getName())
                        && !isPlayerInDataModel(name)) {

                    Model.getApp().withAllPlayers(new Player().setName(name));
                }

            }
        }

        // data contains the name of the user
        else if (action.equals("userLeft")) {

            Model.getApp().withoutAllPlayers(PlayerListener.getPlayerByName(data.getString("name")));
            System.out.println("User: " + data.getString("name") + " went offline.");
        }

        // data contains the name of the game, the gameID and the capacity
        else if (action.equals("gameCreated")) {

            Game game = GameListener.getGameByID(data.getString("id"));

            //is the case, when the game wasn't created by the current player
            if (game == null) {
                Model.getApp().withAllGames(new Game().setName(data.getString("name"))
                        .setGameId(data.getString("id"))
                        .setCapacity((data.getInt("neededPlayer")))
                        .setJoinedPlayers(0));
            }
        }

        // data contains the gameID
        else if (action.equals("gameDeleted")) {
            Model.getApp().withoutAllGames(GameListener.getGameByID(data.getString("id")));
        }

        // data contains the number of players that joined and the gameID
        else if (action.equals("playerJoinedGame")) {

            Game game = GameListener.getGameByID(data.getString("id"));
            game.setJoinedPlayers(data.getInt("joinedPlayer"));
        }

        // data contains the number of players that left and the gameID
        else if (action.equals("playerLeftGame")) {

            if (GameListener.getGameByName(data.getString("id")) != null) {
                Game game = GameListener.getGameByID(data.getString("id"));
                game.setJoinedPlayers((data.getInt("joinedPlayer")));
            }
        } else {
            System.out.println("unknown action: " + action);
        }
    }

    private boolean isPlayerInDataModel(String name) {
        for (Player p : Model.getApp().getAllPlayers()) {
            if (p.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
