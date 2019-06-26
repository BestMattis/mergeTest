package gameList;

import model.App;
import model.Model;
import model.Player;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class GameListenerTest {

    GameListener gameListener = new GameListener();

    @Test
    public void createNewGameTest() {
        Model.getInstance().setApp(new App());
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player());

        gameListener.createNewGame("TestGame", 4, players);
        Assert.assertEquals("TestGame", Model.getApp().getAllGames().get(0).getName());
        Assert.assertEquals(4, Model.getApp().getAllGames().get(0).getCapacity());
        Assert.assertEquals(players.size(), Model.getApp().getAllGames().get(0).getPlayers().size());
    }

    @Test
    public void addPlayersToGameTest() {
        Model.getInstance().setApp(new App());
        ArrayList<Player> players = new ArrayList<>();

        gameListener.createNewGame("TestGame", 4, players);

        Assert.assertEquals(players.size(), Model.getApp().getAllGames().get(0).getPlayers().size());

        players.add(new Player());

        gameListener.addPlayersToGame("TestGame", players);

        Assert.assertEquals(players.size(), Model.getApp().getAllGames().get(0).getPlayers().size());
    }

    @Test
    public void deleteGameTest() {
        Model.getInstance().setApp(new App());
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player());

        gameListener.createNewGame("TestGame", 4, players);

        gameListener.deleteGame("TestGame");

        Assert.assertEquals(0, Model.getApp().getAllGames().size());
    }

    @Test
    public void findGame() {
        Model.getInstance().setApp(new App());
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player());

        Assert.assertNull(gameListener.getGameByName("Game"));
    }

}
