package playerList;

import model.App;
import model.Model;
import model.Player;
import org.junit.Assert;
import org.junit.Test;


public class PlayerListenerTest {

    PlayerListener playerListener = new PlayerListener();

    @Test
    public void getPlayerByName() {
        Model.getInstance().setApp(new App());
        Player player = new Player().setName("TestPlayer");
        Model.getApp().withAllPlayers(player);

        Assert.assertEquals(player, PlayerListener.getPlayerByName("TestPlayer"));
    }

    @Test
    public void addPlayer() {
        Model.getInstance().setApp(new App());
        playerListener.addPlayer("TestPlayer");

        Assert.assertNotNull(PlayerListener.getPlayerByName("TestPlayer"));
    }

    @Test
    public void deletePlayer() {
        Model.getInstance().setApp(new App());
        playerListener.addPlayer("TestPlayer");
        playerListener.deletePlayer("TestPlayer");

        Assert.assertNull(PlayerListener.getPlayerByName("TestPlayer"));
        Assert.assertEquals(0, Model.getApp().getAllPlayers().size());
    }

}
