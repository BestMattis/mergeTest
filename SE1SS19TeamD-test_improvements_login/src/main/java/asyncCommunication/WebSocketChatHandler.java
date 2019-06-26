package asyncCommunication;

import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONObject;

import model.App;
import model.ChatMessage;
import model.Model;
import model.Player;


public class WebSocketChatHandler implements WebSocketHandler {

    WebSocketRequests chatClient;
    public ChatMessage message = new ChatMessage();

    public WebSocketChatHandler(WebSocketRequests chatClient) {
        this.chatClient = chatClient;
        
        PropertyChangeListener listener = (e -> {
            ChatMessage message = (ChatMessage) e.getNewValue();
            if (message != null && 
                    message.getSender() == Model.getApp().getCurrentPlayer()) {
                this.chatClient.sendChatMessage(message);
            }
        });
        
        Model.getApp().addPropertyChangeListener(App.PROPERTY_allPlayers, e -> {
            Player oldPlayer = (Player) e.getOldValue();
            Player newPlayer = (Player) e.getNewValue();
            
            if(oldPlayer != null)
            {
                oldPlayer.removePropertyChangeListener(listener);
            }
            if(newPlayer != null)
            {
                newPlayer.addPropertyChangeListener(
                        Player.PROPERTY_messages, listener);
            }
        });
    }

    @Override
    public void handle(JSONObject msg) {

        String channel = msg.getString("channel");
        String chatMessage = msg.getString("message");
        String from = msg.getString("from");
        ArrayList<Player> players = Model.getApp().getAllPlayers();
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(from)) {
                this.message.setSender(players.get(i));
            }
        }
        this.message.setChannel(channel);
        this.message.setMessage(chatMessage);
        this.message.setDate(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
    }
}
