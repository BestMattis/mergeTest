package msgToAllPlayers;

import model.App;
import model.Model;
import model.Player;
import org.junit.Assert;
import org.junit.Test;

public class msgToAllPlayersTest {
    @SuppressWarnings("static-access")
    @Test
    public void addMsgToModel() {
        MsgToAllPlayers msgTAP = new MsgToAllPlayers();
        App app = Model.getApp();
        String msg = "testMsg";
        app.setCurrentPlayer(new Player());

        Assert.assertEquals(app.getCurrentPlayer().getReceivedMessages().size(), 0);
        msgTAP.sendToAll(msg);
        Assert.assertEquals(app.getCurrentPlayer().getReceivedMessages().get(0).getMessage(), msg);
    }

    @SuppressWarnings("static-access")
    @Test
    public void getMsgFromAllChat() {
        MsgToAllPlayers msgTAP = new MsgToAllPlayers();
        App app = Model.getApp();
        Player testPlayer = new Player().setName("TestPlayer");
        String msg = "testMsg";
        app.setCurrentPlayer(testPlayer);

        WSChatEndpoint.getInstance().setListeners();

        msgTAP.sendToAll(msg);
    }
}
