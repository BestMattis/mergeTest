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
        // ArmyManager
        ClassBuilder armyConfiguration = mb.buildClass("ArmyConfiguration");
        ClassBuilder unit = mb.buildClass("Unit");
        // Field and GameField
        ClassBuilder gameField = mb.buildClass("GameField");
        ClassBuilder field = mb.buildClass("Field");

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
        // ArmyManager
        armyConfiguration.buildAttribute("name", ClassModelBuilder.STRING)
                .buildAttribute("id", ClassModelBuilder.STRING);
        unit.buildAttribute("id", ClassModelBuilder.STRING)
                .buildAttribute("canAttack", ClassModelBuilder.COLLECTION_ARRAY_LIST)
                .buildAttribute("type", ClassModelBuilder.STRING)
                .buildAttribute("mp", ClassModelBuilder.INT)
                .buildAttribute("hp", ClassModelBuilder.INT);
        // Field and GameField
        gameField.buildAttribute("sizeX", ClassModelBuilder.INT)
                .buildAttribute("sizeY", ClassModelBuilder.INT);

        field.buildAttribute("isPassable", ClassModelBuilder.BOOLEAN)
                .buildAttribute("posX", ClassModelBuilder.INT)
                .buildAttribute("posY", ClassModelBuilder.INT)
                .buildAttribute("id", ClassModelBuilder.STRING)
                .buildAttribute("type", ClassModelBuilder.STRING);

        // Associations
        game.buildAssociation(player, "players", ClassModelBuilder.MANY, "game", ClassModelBuilder.ONE);
        game.buildAssociation(chatMessage, "ingameMessages", ClassModelBuilder.MANY, "game", ClassModelBuilder.ONE);
        game.buildAssociation(player, "turnPlayer", ClassModelBuilder.ONE, "turnPlayerGame", ClassModelBuilder.ONE);
        chatMessage.buildAssociation(player, "receiver", ClassModelBuilder.ONE, "receivedMessages", ClassModelBuilder.MANY);
        chatMessage.buildAssociation(player, "sender", ClassModelBuilder.ONE, "sentMessages", ClassModelBuilder.MANY);
        app.buildAssociation(player, "allPlayers", ClassModelBuilder.MANY, "app", ClassModelBuilder.ONE);
        app.buildAssociation(game, "allGames", ClassModelBuilder.MANY, "app", ClassModelBuilder.ONE);
        app.buildAssociation(player, "currentPlayer", ClassModelBuilder.ONE, "myApp", ClassModelBuilder.ONE);
        app.buildAssociation(chatMessage, "allChatMessages", ClassModelBuilder.MANY, "app", ClassModelBuilder.ONE);
        // ArmyManager
        player.buildAssociation(armyConfiguration, "armyConfigurations", ClassModelBuilder.MANY, "player", ClassModelBuilder.ONE);
        player.buildAssociation(armyConfiguration, "currentArmyConfiguration", ClassModelBuilder.ONE, "player", ClassModelBuilder.ONE);
        armyConfiguration.buildAssociation(unit, "units", ClassModelBuilder.MANY, "armyConfiguration", ClassModelBuilder.ONE);
        // Field and GameField
        gameField.buildAssociation(field, "fields", ClassModelBuilder.MANY, "gameField", ClassModelBuilder.ONE);
        gameField.buildAssociation(game, "game", ClassModelBuilder.ONE, "gameField", ClassModelBuilder.ONE);
        field.buildAssociation(field, "neighbour", ClassModelBuilder.MANY, "neighbour", ClassModelBuilder.MANY);
        field.buildAssociation(unit, "occupiedBy", ClassModelBuilder.ONE, "occupiesField", ClassModelBuilder.ONE);


        ClassModel model = mb.getClassModel();

        Fulib.generator().generate(model);
    }
}