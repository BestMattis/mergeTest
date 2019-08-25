package sendChatMessageToPlayer;

import lobby.LobbyChatSingleTabsController;
import model.ChatMessage;
import model.Model;
import model.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChatMessageSender {

    private Model model;
    private LobbyChatSingleTabsController chatController = new LobbyChatSingleTabsController(model);

    public ChatMessageSender(Model model) {
        this.model = model;
    }

    public boolean sendMessageTo(String message, Player receivingPlayer) {

        ChatMessage chatMessage = new ChatMessage();

        Player sender = model.getApp().getCurrentPlayer();

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
        return true;
    }
}
