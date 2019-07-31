package msgToAllPlayers;

import gameLobby.GameLobbyController;
import gameScreen.GameChatController;
import javafx.application.Platform;
import lobby.LobbyChatController;
import lobby.LobbyChatMessageListController;
import main.AdvancedWarsApplication;
import model.*;

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
    public void setLobbyChatListeners() {

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

    public void setIngameListeners() {
       setIngameAllchatListeners();
       setIngamePrivateChatListeners();
    }

    private void setIngamePrivateChatListeners() {

        Model.getApp().getCurrentPlayer().addPropertyChangeListener(Game.PROPERTY_ingameMessages, evt -> {

            ChatMessage message = (ChatMessage) evt.getNewValue();
            if (message.getChannel() != null && message.getChannel().equals("private")) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {

                    while (message.getReceiver() == null || message.getSender() == null);
                    if ( message.getMessage() != null) {

                        Player currentPlayer = Model.getApp().getCurrentPlayer();
                        Player sender = message.getSender();

                        if (sender.getName().equals(currentPlayer.getName())) {
                            Platform.runLater(() -> Model.getWebSocketComponent().sendGameChatMessage(message));
                        } else {
                            //show private message in chat
                            System.out.println(message.toString() + "private game message");
                            GameChatController controller = AdvancedWarsApplication.getInstance()
                                    .getGameScreenCon().getGameChatController();
                            GameLobbyController controller_v2 = AdvancedWarsApplication.getInstance()
                                    .getGameScreenCon().getGameLobbyController();

                            if (controller_v2.isOpen()) {
                                controller_v2.displayMessage(message);
                            } else if (controller.isOpen()) {
                                controller.displayMessage(message);
                            }
                        }
                    }
                });
                executor.shutdown();

            }
        });
    }

    private void setIngameAllchatListeners() {

        Model.getApp().getCurrentPlayer().addPropertyChangeListener(Game.PROPERTY_ingameMessages, evt -> {

            ChatMessage message = (ChatMessage) evt.getNewValue();
            if (message.getChannel() != null && message.getChannel().equals("all")) {

                if (message.getSender() != null && message.getMessage() != null) {

                    if (message.getSender().getName().equals(Model.getApp()
                            .getCurrentPlayer().getName())) {

                        Platform.runLater(() -> Model.getWebSocketComponent().sendGameChatMessage(message));
                    } else if (!message.getSender().getName().equals(Model.getApp().getCurrentPlayer().getName())) {

                       //display allchat message in chat
                        System.out.println(message.toString() + " all chat game message");
                        GameChatController controller = AdvancedWarsApplication.getInstance()
                                .getGameScreenCon().getGameChatController();
                        GameLobbyController controller_v2 = AdvancedWarsApplication.getInstance()
                                .getGameScreenCon().getGameLobbyController();
                        if (controller_v2.isOpen()) {
                            controller_v2.displayMessage(message);
                        } else if (controller.isOpen()) {
                            controller.displayMessage(message);
                        }
                    }
                }
            }
        });
    }

    public static void removeIngameListeners() {
    }
}