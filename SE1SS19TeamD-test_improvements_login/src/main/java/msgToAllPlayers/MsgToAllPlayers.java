package msgToAllPlayers;

import model.App;
import model.ChatMessage;
import model.Model;

public class MsgToAllPlayers {

	/**
	 * This methode creates new Chat Object with the message and puts this chat
	 * object into the data model. 
	 * It pushes the message to the chatpanel controller
	 * 
	 * @param text the String to be send
	 */

	@SuppressWarnings("static-access")
	public void sendToAll(String text) {
		App app = Model.getInstance().getApp();
		ChatMessage chatToSend = new ChatMessage().setChannel("(all)").setMessage(text)
				.setSender(app.getCurrentPlayer());
		app.withAllChatMessages(chatToSend); // add message to data model

//		allController.addMessage(text); // show your own message in chatbox
	}

	public void addNewMsg(String gottenMsg) {
//		allController.addMessage(gottenMsg); // show new messages in chatbox
	}
}
