package msgToAllPlayers;

import javafx.application.Platform;
import lobby.LobbyChatController;
import lobby.LobbyChatMessageListController;
import model.App;
import model.ChatMessage;
import model.Model;
import model.Player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
     * This Method creates listeners for incoming/outgoing allchat-messages and for outgoing private messages.
     * The listeners for incoming private messages are already set in the class "ChatTab"
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
                    Platform.runLater(() -> lobChatCont.displayMessage("[" + message.getDate() + "] " + "["
                            + message.getSender().getName() + "] "
                            + message.getMessage()));
                }
            }
        });
    }

    private void setPrivateChatListeners() {

        //send messages from sentMessages
        Model.getApp().getCurrentPlayer().addPropertyChangeListener(Player.PROPERTY_sentMessages, evt -> {

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                ChatMessage message = (ChatMessage) evt.getNewValue();
                while (message.getReceiver() == null || message.getSender() == null);
                if ( message.getChannel() != null && message.getMessage() != null) {

                    Player currentPlayer = Model.getApp().getCurrentPlayer();
                    Player sender = message.getSender();

                    if (sender.getName().equals(currentPlayer.getName())) {
                        Platform.runLater(() -> Model.getWebSocketComponent().sendChatmessage(message));
                    }
                }
            });
            executor.shutdown();
        });
    }
}