package msgToAllPlayers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.json.JSONObject;

import javafx.collections.ListChangeListener;
import syncCommunication.HttpRequests;
import syncCommunication.SynchronousUserCommunicator;

public class MsgToAllPlayers {

	/**
	 * This methode creates new Chat Object with the message and puts this chat
	 * object into the data model It pushes the message to the chatpanel controller
	 * 
	 * @param text the String to be send
	 */

	public void sendToAll(String text) {
		ChatMessage chatToSend = new ChatMessage().setFrom(Model.getInstance()
				.getApp().getCurrentPlayer().getUserName())
				.setMessage(text).setChannel("(all)");
		Model.getInstance().getApp.getChat().withMessage(chatToSend); // add message to data model

		allController.addMessage(text); // show your own message in chatbox
	}

	public void addNewMsg(String gottenMsg) {
		allController.addMessage(gottenMsg); // show new messages in chatbox
	}
}

 
