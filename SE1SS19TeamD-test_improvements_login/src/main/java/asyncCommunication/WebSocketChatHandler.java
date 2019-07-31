package asyncCommunication;

import main.AdvancedWarsApplication;
import model.ChatMessage;
import model.Model;
import model.Player;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class WebSocketChatHandler implements WebSocketHandler {

    /**
     * Handles an incoming all-chat or private message.
     *
     *
     * @param msg contains the incoming chat message.
     */
    @Override
    public void handle(JSONObject msg) {

        /* the server also sends back the messages that the currentplayer has send.
         * That's why they need to be ignored.
         */
        System.out.println(msg.toString() + "");
        if (Model.getApp().getCurrentPlayer() == null || !msg.has("channel")) {

            /* if we send a message to a player that is not online anymore
             * the server answers like this: {"msg":"User seb1 is not online."}
             */
            if (msg.has("msg")) {
                //TODO: display in the GUI that the user is not online
                System.out.println(msg.toString() + "");
            }
        } else if (msg.has("channel")) {

            //all-chat message
            if (msg.getString("channel").equals("all")) {

                handleAllChatMessage(msg);
                //private-chat message
            } else if (msg.getString("channel").equals("private")) {

                handlePrivateMessage(msg);
            }
        }
    }

    private void handleAllChatMessage(JSONObject msg) {

        if (msg.has("from") && msg.has("message")) {

            if (msg.getString("from").equals(Model.getApp().getCurrentPlayer().getName())) {
                return;
            }
            ChatMessage message = new ChatMessage();
            message.setChannel("all");
            message.setMessage(msg.getString("message"));
            message.setDate(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));

            ArrayList<Player> players = Model.getApp().getAllPlayers();
            for (int i = 0; i < players.size(); i++) {

                if (players.get(i).getName().equals(msg.getString("from"))) {

                    message.setSender(players.get(i));
                    Model.getApp().withAllChatMessages(message);
                }
            }
        } else {
            System.out.println("invalid message");
        }
    }

    private void handlePrivateMessage(JSONObject msg) {

        if (msg.has("from") && msg.has("message") && msg.has("to")) {

            if (msg.getString("from").equals(Model.getApp().getCurrentPlayer().getName())) {
                return;
            }
            ChatMessage message = new ChatMessage();
            message.setChannel("private");
            message.setMessage(msg.getString("message"));
            message.setDate(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));

            if (msg.getString("to").equals(Model.getApp().getCurrentPlayer().getName())) {

                ArrayList<Player> players = Model.getApp().getAllPlayers();
                Player sender = null;
                for (int i = 0; i < players.size(); i++) {

                    if (players.get(i).getName().equals(msg.getString("from"))) {

                        sender = players.get(i);
                    }
                }
                AdvancedWarsApplication.getInstance().getLobbyCon().getChatCon().getSingleController().newTab(sender);
                message.setSender(sender);
                message.setReceiver(Model.getApp().getCurrentPlayer());
            }
        }
    }

}
