package msgToAllPlayers;

import javafx.application.Platform;
import lobby.LobbyChatController;
import lobby.LobbyChatMessageListController;
import model.App;
import model.ChatMessage;
import model.Model;
import model.Player;

public class WSChatEndpoint {
    private static WSChatEndpoint instance;

    private WSChatEndpoint() {
    }

    public static WSChatEndpoint getInstance() {
        if (instance == null) {
            instance = new WSChatEndpoint();
        }
        return instance;
    }

    /**
     * This Method creates a changeListener on the arrayList Messeges in the data
     * model and sends the new messages back
     */

    @SuppressWarnings("static-access")
    public void setListeners() {

        setAllChatListeners();
        setPrivateChatListeners();
    }



    private void setAllChatListeners() {

        Model.getApp().addPropertyChangeListener(App.PROPERTY_allChatMessages, evt -> {

            ChatMessage message = (ChatMessage) evt.getNewValue();

            if (message.getReceiver() == null && message.getChannel() != null && message.getSender() != null
                    && message.getMessage() != null) {

                if (message.getSender().getName().equals(Model.getApp()
                        .getCurrentPlayer().getName())) {

                    Platform.runLater(() -> Model.getWebSocketComponent().sendChatmessage(message));
                } else if (!message.getSender().getName().equals(Model.getApp()
                        .getCurrentPlayer().getName())) {

                    LobbyChatMessageListController lobChatCont = LobbyChatController.getAllController();
                    Platform.runLater(() -> lobChatCont.displayMessage("[" + message.getSender().getName() + "] "
                            + message.getMessage()));
                }
            }
        });
    }

    private void setPrivateChatListeners() {

        //send messages from sentMessages
        Model.getApp().getCurrentPlayer()
                .addPropertyChangeListener(Player.PROPERTY_sentMessages, evt -> {

            ChatMessage message = (ChatMessage) evt.getNewValue();
            if (message.getReceiver() != null && message.getChannel() != null && message.getSender() != null
                    && message.getMessage() != null) {

                Player currentPlayer = Model.getInstance().getApp().getCurrentPlayer();
                Player sender = message.getSender();
                if (sender.getName().equals(currentPlayer.getName()) &&
                        message.getReceiver() != null) {
                    Platform.runLater(() -> Model.getWebSocketComponent().sendChatmessage(message));
                }
            }
        });
    }
}