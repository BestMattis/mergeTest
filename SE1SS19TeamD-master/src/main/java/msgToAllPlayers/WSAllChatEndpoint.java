package msgToAllPlayers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import lobby.LobbyChatController;
import lobby.LobbyChatMessageListController;
import model.App;
import model.ChatMessage;
import model.Model;

public class WSAllChatEndpoint {
	private static WSAllChatEndpoint instance;

	private WSAllChatEndpoint() {
	}

	public static WSAllChatEndpoint getInstance() {
		if (instance == null) {
			instance = new WSAllChatEndpoint();
		}
		return instance;
	}

	/**
	 * This Method creats a changeListener on the arrayList Messeges in the data
	 * model and sends the new messages back
	 */

	@SuppressWarnings("static-access")
	public void getMessages() {
		LobbyChatMessageListController lobChatCont = LobbyChatController.getAllController();
		/* Change Listener on the data model Observable List chat */
		App app = Model.getInstance().getApp();
		app.addPropertyChangeListener(App.PROPERTY_allChatMessages, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ChatMessage change = (ChatMessage) evt.getNewValue();
				/* for tests till WS is finished */
				System.out.println("Sender: " +change.getSender().getName()+" Msg: "+ change.getMessage());
				if (change.getSender().getName() != Model.getApp().getCurrentPlayer().getName()) {	// check if its your own message
					lobChatCont.addMessage(change.getMessage()); // send new message to LobbyChatController
				} else {
					System.out.println("Message denied: Own Message");
				}
			}
		});
	}
}