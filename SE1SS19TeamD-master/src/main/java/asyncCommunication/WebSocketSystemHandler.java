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

            Model.getApp().withAllPlayers(new Player().setName(data.getString("name").toString()));
        }

        // data contains the name of the user
        else if (action.equals("userLeft")) {

            Model.getApp().withoutAllPlayers(PlayerListener.getPlayerByName(data.getString("name")));
        }

        // data contains the name of the game, the gameID and how many players it still needs
        else if (action.equals("gameCreated")) {

            Model.getApp().withAllGames(new Game().setName(data.getString("name"))
                    .setGameId(data.getString("id"))
                    .setCapacity(4 - (int) data.get("neededPlayer")));
        }

        // data contains the gameID
        else if (action.equals("gameDeleted")) {
            Model.getApp().withoutAllGames(GameListener.getGameByID(data.getString("id")));
        }

        // data contains the number of players that joined and the gameID
        else if (action.equals("playerJoinedGame")) {

            int joinedPlayer = (int) data.get("joinedPlayer");
            ArrayList<Game> games = Model.getApp().getAllGames();
            for (int i = 0; i < games.size(); i++) {
                if (games.get(i).getGameId().equals(data.getString("id"))) {
                    System.out.printf("Game: %s, joinedPlayer: %d\n", games.get(i), joinedPlayer);
                    games.get(i).setCapacity(games.get(i).getCapacity() + 1);
                }
            }
        }

        // data contains the number of players that left and the gameID
        else if (action.equals("playerLeftGame")) {

            ArrayList<Game> games = Model.getApp().getAllGames();
            for (int i = 0; i < games.size(); i++) {
                if (games.get(i).getGameId().equals(data.getString("id"))) {
                    games.get(i).setCapacity(games.get(i).getCapacity() - 1);
                }
            }
        }
    }
}
