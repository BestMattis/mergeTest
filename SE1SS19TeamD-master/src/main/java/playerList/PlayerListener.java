package playerList;

import model.Model;
import model.Player;

public class PlayerListener {

    /**
     * Searches Player with playerName in App from Model.
     *
     * @param playerName name of Player to be searched.
     * @return Player or null(when no Player has been found).
     */
    public static Player getPlayerByName(String playerName) {
        for (Player player : Model.getApp().getAllPlayers()) {
            if (player.getName().equals(playerName)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Creates new Player with playerName
     * and adds it to the App from Model.
     *
     * @param playerName name of Player to add.
     */
    public void addPlayer(String playerName) {
        Model.getInstance().getApp().withAllPlayers(new Player().setName(playerName));
    }

    /**
     * Deletes a Player with playerName in allPlayers from Model.
     *
     * @param playerName name of Player to delete.
     */
    public void deletePlayer(String playerName) {
        Model.getInstance().getApp().withoutAllPlayers(getPlayerByName(playerName));
    }

}
