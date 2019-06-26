package lobby;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import main.AdvancedWarsApplication;
import model.Player;

import java.util.ArrayList;

public class LobbyChatSingleTabsController {

    protected ArrayList<ChatTab> chatTabs = new ArrayList<>();
    @FXML
    protected TabPane singleTabPane;

    public void initialize() {
    }

    /**
     * This methode opens a new tab to chat with the player given
     *
     * @param player the player to communicate with in the tab
     */
    public void newTab(Player player){
        boolean notopened = true;
        Tab playerTab = null;
        for (ChatTab chatTab: chatTabs){
            if (chatTab.getTab().getText().equals(player.getName())){
                notopened = false;
                playerTab = chatTab.getTab();
            }
        }
        if (notopened) {
            Tab tab = new Tab(player.getName());
            ChatTab chattab = new ChatTab(tab, player);
            tab.setOnCloseRequest(t -> chatTabs.remove(chattab));
            chatTabs.add(chattab);
            singleTabPane.getTabs().add(chattab.getTab());
            singleTabPane.getSelectionModel().select(tab);
        } else {
            singleTabPane.getSelectionModel().select(playerTab);
        }
        AdvancedWarsApplication.getInstance().getLobbyCon().getChatCon().setToPlayers();
    }
    
    /**
     * This method pushes the message to the chatpanel controller of the tab currently open
     *
     * @param text text to be displayed
     */
    public void addMessage(String text) {
        for (ChatTab chatTab : chatTabs) {
            if (singleTabPane.getSelectionModel().getSelectedItem() == chatTab.getTab()) {
                chatTab.addMessage(text);
            }
        }
    }

}
