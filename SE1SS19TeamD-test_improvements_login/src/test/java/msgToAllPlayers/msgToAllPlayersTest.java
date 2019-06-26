package msgToAllPlayers;

import org.junit.Assert;
import org.junit.Test;

import model.App;
import model.Model;
import model.Player;

public class msgToAllPlayersTest {
	@SuppressWarnings("static-access")
	@Test
	public void addMsgToModel() {
		MsgToAllPlayers msgTAP = new MsgToAllPlayers();
		App app = Model.getInstance().getApp();
		String msg = "testMsg";
		app.setCurrentPlayer(new Player());
		Assert.assertEquals(app.getCurrentPlayer().getMessages().size(), 0);
		msgTAP.sendToAll(msg);
		Assert.assertEquals(app.getCurrentPlayer().getMessages().get(0).getMessage(), msg);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void getMsgFromAllChat() {
		MsgToAllPlayers msgTAP = new MsgToAllPlayers();
		App app = Model.getInstance().getApp();
		Player testPlayer = new Player().setName("TestPlayer");
		String msg = "testMsg";
		app.setCurrentPlayer(testPlayer);
		
		WSAllChatEndpoint.getInstance().getMessages();
		
		msgTAP.sendToAll(msg);
	}
}
