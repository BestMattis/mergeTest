package model;
import org.fulib.Fulib;
import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.ClassModel;

public class GenModel {

    public static void main(String... args) {

        ClassModelBuilder mb = Fulib.classModelBuilder("model", "src/main/java");

        // Classes
        ClassBuilder player = mb.buildClass("Player");
        ClassBuilder game = mb.buildClass("Game");
        ClassBuilder app = mb.buildClass("App");
        ClassBuilder chatMessage = mb.buildClass("ChatMessage");

        // Attributes
        player.buildAttribute("name", ClassModelBuilder.STRING);
        player.buildAttribute("password", ClassModelBuilder.STRING);
        game.buildAttribute("name", ClassModelBuilder.STRING)
                .buildAttribute("capacity", ClassModelBuilder.INT)
                .buildAttribute("gameId", ClassModelBuilder.STRING)
                .buildAttribute("joinedPlayers", ClassModelBuilder.INT);
        chatMessage.buildAttribute("message", ClassModelBuilder.STRING)
                   .buildAttribute("channel", ClassModelBuilder.STRING)
                   .buildAttribute("date", ClassModelBuilder.STRING);

        //Associations
        game.buildAssociation(player, "players", ClassModelBuilder.MANY, "game", ClassModelBuilder.ONE);
<<<<<<< HEAD
        chatMessage.buildAssociation(player, "receiver", ClassModelBuilder.ONE,"messages", ClassModelBuilder.MANY);
        chatMessage.buildAssociation(player, "sender", ClassModelBuilder.ONE,"messages", ClassModelBuilder.MANY);
=======
        chatMessage.buildAssociation(player, "receiver", ClassModelBuilder.ONE, "receivedMessages", ClassModelBuilder.MANY);
        chatMessage.buildAssociation(player, "sender", ClassModelBuilder.ONE, "sentMessages", ClassModelBuilder.MANY);
>>>>>>> master
        app.buildAssociation(player, "allPlayers", ClassModelBuilder.MANY, "app", ClassModelBuilder.ONE);
        app.buildAssociation(game,"allGames", ClassModelBuilder.MANY,"app", ClassModelBuilder.ONE);
        app.buildAssociation(player, "currentPlayer", ClassModelBuilder.ONE, "myApp", ClassModelBuilder.ONE);
        app.buildAssociation(chatMessage, "allChatMessages", ClassModelBuilder.MANY, "app", ClassModelBuilder.ONE);

        ClassModel model = mb.getClassModel();

        Fulib.generator().generate(model);
    }
}
