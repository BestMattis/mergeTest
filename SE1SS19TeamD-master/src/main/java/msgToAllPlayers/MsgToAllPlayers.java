package msgToAllPlayers;

import lobby.LobbyChatController;
import lobby.LobbyChatMessageListController;
import model.App;
import model.ChatMessage;
import model.Model;

public class MsgToAllPlayers {

    /**
     * This method creates new Chat Object with the message and puts this chat
     * object into the data model.
     * It pushes the message to the chatpanel controller
     *
     * @param text the String to be send
     */

    @SuppressWarnings("static-access")
    public void sendToAll(String text) {

        App app = Model.getInstance().getApp();
        ChatMessage chatToSend = new ChatMessage().setChannel("all").setMessage(text)
                .setSender(app.getCurrentPlayer());
        app.withAllChatMessages(chatToSend); // add message to data model
        LobbyChatMessageListController allController = LobbyChatController.getAllController();
        allController.displayMessage(text); // show your own message in chatbox
    }

    public void addNewMsg(String gottenMsg) {
        LobbyChatMessageListController allController = LobbyChatController.getAllController();
        allController.displayMessage(gottenMsg); // show new messages in chatbox
    }
}
