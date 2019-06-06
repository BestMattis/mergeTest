package msgToAllPlayers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import de.uniks.liverisk.model.Game;
import javafx.collections.ListChangeListener;

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
	 * This Method creats a changeListener on the observableList chat in the data
	 * model and sends the new messages back
	 */

	public void getMessages() {
		//RandomCode für merge
		ChatMessage change;
		/* Change Listener on the data model Observable List chat */
		Model.getInstance().getApp.addPropertyChangeListener(App.PROPERTY_chat, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				change = evt.getNewValue();
				if (change.getChannel() == "(all)"
						&& change.getFrom() != Model.getApp().getCurrentPlayer().getUserName()) {
					MsgToAllPlayers.addNewMsg(change.getMessage()); // send new message
				}
			}
		});
	}
}