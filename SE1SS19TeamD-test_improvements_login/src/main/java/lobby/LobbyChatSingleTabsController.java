package lobby;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import main.AdvancedWarsApplication;
import model.Model;
import model.Player;

import java.util.ArrayList;

public class LobbyChatSingleTabsController {

    protected ArrayList<ChatTab> chatTabs = new ArrayList<>();
    @FXML
    protected TabPane singleTabPane;
    private Model model;

    public LobbyChatSingleTabsController(Model model) {
        this.model = model;
    }

    public void initialize() {
    }

    /**
     * This methode opens a new tab to chat with the player given
     *
     * @param player the player to communicate with in the tab
     */
    public void newTab(Player player) {
        boolean notopened = true;
        Tab playerTab = null;
        for (ChatTab chatTab : chatTabs) {
            if (chatTab.getTab().getText().equals(player.getName())) {
                notopened = false;
                playerTab = chatTab.getTab();
            }
        }
        if (player != null && notopened) {
            Tab tab = new Tab(player.getName());
            ChatTab chattab = new ChatTab(tab, player, model);
            tab.setOnCloseRequest(t -> chatTabs.remove(chattab));
            chatTabs.add(chattab);
            Platform.runLater(() -> singleTabPane.getTabs().add(chattab.getTab()));
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

    public ArrayList getChatTabs() {

        return this.chatTabs;
    }

}
