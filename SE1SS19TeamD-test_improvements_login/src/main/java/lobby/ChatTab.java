package lobby;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import main.FXMLLoad;
import model.ChatMessage;
import model.Model;
import model.Player;
import sendChatMessageToPlayer.ChatMessageSender;

import java.beans.PropertyChangeEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatTab {

    protected LobbyChatMessageListController AllController;
    private Model model;
    private FXMLLoad messageList;
    private Tab tab;
    private Player player;

    /**
     * This method loads the Chatpanel (LobbyChatAll) into the tab
     *
     * @param tab1    the chat is loaded in
     * @param player1 the player to chat with
     */
    public ChatTab(Tab tab1, Player player1, Model model) {
        this.model = model;
        tab = tab1;
        player = player1;
        if (player.getName() != null) {
            tab.setText(player.getName());
        }
        messageList = new FXMLLoad("/lobby/LobbyChatMessageList.fxml", new LobbyChatMessageListController());
        AllController = messageList.getController(LobbyChatMessageListController.class);
        tab.setContent(messageList.getParent());

        player.addPropertyChangeListener(Player.PROPERTY_sentMessages, evt -> {
            Platform.runLater(() -> printMessage(evt));
        });

        player.addPropertyChangeListener(Player.PROPERTY_receivedMessages, evt -> {
            Platform.runLater(() -> printMessage(evt));
        });

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

        new ChatMessageSender(model).sendMessageTo(text, player);
    }

    public void printMessage(PropertyChangeEvent t) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ChatMessage message = (ChatMessage) t.getNewValue();
            while (message.getReceiver() == null || message.getSender() == null) ;
            Platform.runLater(() -> AllController.displayMessage("[" + message.getDate() + "] " + "[" + message.getSender().getName() + "] " + message.getMessage()));
        });
        executor.shutdown();
    }

    public Player getPlayer() {

        return this.player;
    }
}
