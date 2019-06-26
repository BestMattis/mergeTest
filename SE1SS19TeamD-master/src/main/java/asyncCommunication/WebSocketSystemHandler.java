package asyncCommunication;

import gameList.GameListener;
import model.Game;
import model.Model;
import model.Player;
import org.json.JSONObject;
import playerList.PlayerListener;

import java.util.ArrayList;

public class WebSocketSystemHandler implements WebSocketHandler {


    @Override
    public void handle(JSONObject msg) {
       String action = msg.getString("action");
        JSONObject data = new JSONObject(msg.get("data").toString());


        // data contains the name of the user
        if (action.equals("userJoined")) {

            String name = data.getString("name");
            System.out.println("User: " + name + " came online.");

            if (!name.equals(Model.getInstance().getApp().getCurrentPlayer().getName())) {

                Model.getInstance().getApp().withAllPlayers(new Player().setName(name));
            }

        }

        // data contains the name of the user
        else if (action.equals("userLeft")) {

            System.out.println("User: " + data.getString("name") + " went offline.");

            Model.getInstance().getApp().withoutAllPlayers(PlayerListener.getPlayerByName(data.getString("name")));
        }

        // data contains the name of the game, the gameID and how many players it still needs
        else if (action.equals("gameCreated")) {

            Model.getInstance().getApp().withAllGames(new Game().setName(data.getString("name"))
                    .setGameId(data.getString("id"))
                    .setCapacity(4 - (int) data.get("neededPlayer")));
        }

        // data contains the gameID
        else if (action.equals("gameDeleted")) {
            Model.getInstance().getApp().withoutAllGames(GameListener.getGameByID(data.getString("id")));
        }

        // data contains the number of players that joined and the gameID
        else if (action.equals("playerJoinedGame")) {

            ArrayList<Game> games = Model.getInstance().getApp().getAllGames();
            for (int i = 0; i < games.size(); i++) {
                if (games.get(i).getGameId().equals(data.getString("id"))) {
                    //games.get(i).withPlayers(new Player());
                }
            }
        }

        // data contains the number of players that left and the gameID
        else if (action.equals("playerLeftGame")) {

            ArrayList<Game> games = Model.getInstance().getApp().getAllGames();
            for (int i = 0; i < games.size(); i++) {
                if (games.get(i).getGameId().equals(data.getString("id"))) {
                    //System.out.println(msg.toString());
                }
            }
        } else {
            System.out.println("unknown action: " + action);
        }
    }
}
