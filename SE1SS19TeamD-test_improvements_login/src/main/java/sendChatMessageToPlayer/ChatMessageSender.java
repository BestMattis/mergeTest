package sendChatMessageToPlayer;

import lobby.LobbyChatSingleTabsController;
import model.ChatMessage;
import model.Model;
import model.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChatMessageSender {

    private LobbyChatSingleTabsController chatController = new LobbyChatSingleTabsController();

    public ChatMessageSender() {
        Model.getApp().getCurrentPlayer().addPropertyChangeListener(Player.PROPERTY_messages, e -> {
            if (e.getNewValue() != null) {
                if (((ChatMessage)e.getNewValue()).getSender() != Model.getApp().getCurrentPlayer()) {
                    chatController.newTab(((ChatMessage)e.getNewValue()).getSender());
                }
            }
        });
    }

    public boolean sendMessageTo(String message, Player receivingPlayer) {
        ChatMessage chatMessage = new ChatMessage();

        Player sender = Model.getApp().getCurrentPlayer();

        if (receivingPlayer == null || sender == null || message == null) {
            System.out.println("Message was not sent.");
            return false;
        }

        chatMessage.setMessage(message);
        chatMessage.setChannel("private");
        chatMessage.setDate(new SimpleDateFormat("HH:mm:ss")
                .format(Calendar.getInstance().getTime()));
        chatMessage.setSender(sender);
        chatMessage.setReceiver(receivingPlayer);

        System.out.println("Message sent to " + receivingPlayer.getName());
        return true;
    }
}