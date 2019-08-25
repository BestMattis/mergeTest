package asyncCommunication;

import model.Game;
import model.Model;
import model.Player;
import org.json.JSONObject;

public class WebSocketSystemHandler implements WebSocketHandler {

    private Model model;

    public WebSocketSystemHandler(Model model) {
        this.model = model;
    }

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
        if (model.getApp().getCurrentPlayer() == null) {
            return;
        }

        // data contains the name of the user
        if (action.equals("userJoined")) {

            String name = data.getString("name");
            System.out.println("User: " + name + " came online.");

            if (!name.equals(model.getApp().getCurrentPlayer().getName())) {
                if (!name.equals(model.getApp().getCurrentPlayer().getName())
                        && !isPlayerInDataModel(name)) {

                    model.getApp().withAllPlayers(new Player().setName(name));
                }
            }
        }

        // data contains the name of the user
        else if (action.equals("userLeft")) {
            String playerName = data.getString("name");
            Player player = model.getApp().getAllPlayers().stream().filter(x -> x.getName().equals(playerName))
                    .findFirst().orElse(null);
            if (player != null) {
                model.getApp().withoutAllPlayers(player);
            }
            System.out.println("User: " + data.getString("name") + " went offline.");
        }

        // data contains the name of the game, the gameID and the capacity
        else if (action.equals("gameCreated")) {
            String id = data.getString("id");
            Game game = model.getApp().getAllGames().stream().filter(x -> x.getGameId().equals(id))
                    .findFirst().orElse(null);

            //is the case, when the game wasn't created by the current player
            if (game == null) {
                model.getApp().withAllGames(new Game().setName(data.getString("name"))
                        .setGameId(data.getString("id"))
                        .setCapacity((data.getInt("neededPlayer")))
                        .setJoinedPlayers(0));
            }
        }

        // data contains the gameID
        else if (action.equals("gameDeleted")) {
            String id = data.getString("id");
            Game game = model.getApp().getAllGames().stream().filter(x -> x.getGameId().equals(id))
                    .findFirst().orElse(null);
            model.getApp().withoutAllGames(game);
        }

        // data contains the number of players that joined and the gameID
        else if (action.equals("playerJoinedGame")) {
            String id = data.getString("id");
            Game game = model.getApp().getAllGames().stream().filter(x -> x.getGameId().equals(id))
                    .findFirst().orElse(null);
            game.setJoinedPlayers(data.getInt("joinedPlayer"));
        }

        // data contains the number of players that left and the gameID
        else if (action.equals("playerLeftGame")) {
            String id = data.getString("id");
            Game game = model.getApp().getAllGames().stream().filter(x -> x.getGameId().equals(id))
                    .findFirst().orElse(null);
            if (game != null) {
                game.setJoinedPlayers((data.getInt("joinedPlayer")));
            }
        } else {
            System.out.println("unknown action: " + action);
        }
    }

    private boolean isPlayerInDataModel(String name) {
        for (Player p : model.getApp().getAllPlayers()) {
            if (p.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
