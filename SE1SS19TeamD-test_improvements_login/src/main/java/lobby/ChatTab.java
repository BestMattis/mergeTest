package lobby;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import main.FXMLLoad;
import model.ChatMessage;
import model.Player;
import sendChatMessageToPlayer.ChatMessageSender;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class ChatTab {

    protected LobbyChatMessageListController AllController;
    private FXMLLoad messageList;
    private Tab tab;
    private Player player;

    /**
     * This method loads the Chatpanel (LobbyChatAll) into the tab
     *
     * @param tab1    the chat is loaded in
     * @param player1 the player to chat with
     */
    public ChatTab(Tab tab1, Player player1) {
        tab = tab1;
        player = player1;
        if (player.getName() != null) {
            tab.setText(player.getName());
        }
        messageList = new FXMLLoad("/lobby/LobbyChatMessageList.fxml",new LobbyChatMessageListController());
        AllController = messageList.getController(LobbyChatMessageListController.class);
        tab.setContent(messageList.getParent());

        player.addPropertyChangeListener(Player.PROPERTY_sentMessages, evt -> {
            Platform.runLater(() -> printMessage(evt));
        });

        player.addPropertyChangeListener(Player.PROPERTY_receivedMessages, evt -> {
            Platform.runLater(() -> printMessage(evt));
        });

        for (ChatMessage chatMessage : player.getSentMessages()) {
            if (AllController != null && chatMessage != null) {
                AllController.displayMessage("[" + player.getName() + "] " + chatMessage.getMessage());
            }
        }

    }

    /**
     * return the tab from this class
     *
     * @return the tab this instance is managing
     */
    public Tab getTab() {
        return tab;
    }

    /**
     * Method to display a message
     *
     * @param text The text to display
     */
    public void addMessage(String text) {

        new ChatMessageSender().sendMessageTo(text, player);
    }

    public void printMessage(PropertyChangeEvent t) {
        ChatMessage message = (ChatMessage) t.getNewValue();
        AllController.displayMessage("[" + message.getSender().getName() + "] " + message.getMessage());
    }

    public Player getPlayer() {

        return this.player;
    }
}
