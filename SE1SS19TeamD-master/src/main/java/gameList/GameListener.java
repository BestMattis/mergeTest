package gameList;

import model.Game;
import model.Model;
import model.Player;

import java.util.ArrayList;

public class GameListener {

    /**
     * Searches a Game in Model by its gameName.
     *
     * @param gameName gameName to search.
     * @return Game or null(if gameName not found).
     */
    public static Game getGameByName(String gameName) {
        for (int i = 0; i < Model.getInstance().getApp().getAllGames().size(); i++) {
            if (Model.getInstance().getApp().getAllGames().get(i).getName().equals(gameName)) {
                return Model.getInstance().getApp().getAllGames().get(i);
            }
        }
        return null;
    }

    /**
     * Searches a Game in Model by its gameName.
     *
     * @param gameID gameID to search.
     * @return Game or null(if gameID not found).
     */
    public static Game getGameByID(String gameID) {
        for (int i = 0; i < Model.getInstance().getApp().getAllGames().size(); i++) {
            if (Model.getInstance().getApp().getAllGames().get(i).getGameId().equals(gameID)) {
                return Model.getInstance().getApp().getAllGames().get(i);
            }
        }
        return null;
    }


    /**
     * Calls getGameByName(gameName) and adds players.
     *
     * @param gameName gameName to add Players to.
     * @param players  players to add.
     */
    public void addPlayersToGame(String gameName, ArrayList<Player> players) {
        getGameByName(gameName).withPlayers(players);
    }

    /**
     * Creates new Game with gameName, capacity and players.
     * Adds Game to Model.
     *
     * @param gameName gameName to set.
     * @param capacity capacity to set.
     * @param players
     */
    public void createNewGame(String gameName, int capacity, ArrayList<Player> players) {
        Game game = new Game().setName(gameName).setCapacity(capacity).withPlayers(players);
        Model.getInstance().getApp().withAllGames(game);
    }

    /**
     * Calls getGameByName(gameName) and removes it from Model.
     *
     * @param gameName
     */
    public void deleteGame(String gameName) {
        Model.getInstance().getApp().withoutAllGames(getGameByName(gameName));
    }

}
