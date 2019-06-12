package lobby;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import model.ChatMessage;
import model.Player;
import sendChatMessageToPlayer.ChatMessageSender;

public class ChatTab {

    protected LobbyChatMessageListController AllController;
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
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classLoader.getResource("en-US.properties").openStream();
            ResourceBundle bundle = new PropertyResourceBundle(inputStream);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LobbyChatMessageList.fxml"), bundle);
            Parent parent = fxmlLoader.load();//Laden der Scrollpane mit vbox
            AllController = fxmlLoader.getController();// Controller merken um z.B. Nachrichten anzuzeigen
            tab.setContent(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.addPropertyChangeListener("messages", this::printMessage);

        for (ChatMessage chatMessage: player.getMessages()){
            if (AllController != null && chatMessage != null) {
                AllController.addMessage(chatMessage.getMessage());
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
    public void addMessage(String text){
        new ChatMessageSender().sendMessageTo(text, player);
    }

    /** calle when a change in the model occurres
     * show the latest message
     */
    public void printMessage(PropertyChangeEvent t){
        ChatMessage message = (ChatMessage)t.getNewValue();
        AllController.addMessage(message.getMessage());
    }
}
