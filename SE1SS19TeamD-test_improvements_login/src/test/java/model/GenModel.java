package model;

import java.io.File;

import org.assertj.core.util.Files;
import org.fulib.Fulib;
import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;

public class GenModel {

    public static void main(String... args) {
	
	Files.delete(new File("src/generatedSources/java"));

        ClassModelBuilder mb = Fulib.classModelBuilder("model", "src/generatedSources/java");

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
        player.buildAttribute("name", ClassModelBuilder.STRING)
        	.buildAttribute("id", ClassModelBuilder.STRING)
        	.buildAttribute("color", ClassModelBuilder.STRING)
        	.buildAttribute("isReady", ClassModelBuilder.BOOLEAN)
        	.buildAttribute("password", ClassModelBuilder.STRING);
        game.buildAttribute("name", ClassModelBuilder.STRING)
                .buildAttribute("capacity", ClassModelBuilder.INT)
                .buildAttribute("gameId", ClassModelBuilder.STRING)
                .buildAttribute("joinedPlayers", ClassModelBuilder.INT)
                .buildAttribute("winner", ClassModelBuilder.STRING);
        chatMessage.buildAttribute("message", ClassModelBuilder.STRING)
                .buildAttribute("channel", ClassModelBuilder.STRING)
                .buildAttribute("date", ClassModelBuilder.STRING);
        // ArmyManager
        armyConfiguration.buildAttribute("name", ClassModelBuilder.STRING)
                .buildAttribute("id", ClassModelBuilder.STRING);
        unit.buildAttribute("id", ClassModelBuilder.STRING)
                .buildAttribute("canAttack", "java.util.ArrayList<String>")
                .buildAttribute("type", ClassModelBuilder.STRING)
                .buildAttribute("mp", ClassModelBuilder.INT)
                .buildAttribute("hp", ClassModelBuilder.INT);
        // Field and GameField
        gameField.buildAttribute("sizeX", ClassModelBuilder.INT)
                .buildAttribute("sizeY", ClassModelBuilder.INT)
                .buildAttribute("gameFieldLoaded", ClassModelBuilder.BOOLEAN);

        field.buildAttribute("isPassable", ClassModelBuilder.BOOLEAN)
                .buildAttribute("posX", ClassModelBuilder.INT)
                .buildAttribute("posY", ClassModelBuilder.INT)
                .buildAttribute("id", ClassModelBuilder.STRING)
                .buildAttribute("type", ClassModelBuilder.STRING);

        // Associations
        game.buildAssociation(player, "players", ClassModelBuilder.MANY, "game", ClassModelBuilder.ONE);
        game.buildAssociation(chatMessage, "ingameMessages", ClassModelBuilder.MANY, "game", ClassModelBuilder.ONE);
        game.buildAssociation(player, "turnPlayer", ClassModelBuilder.ONE, "turnPlayerGame", ClassModelBuilder.ONE);
        game.buildAssociation(unit, "selectedUnit", ClassModelBuilder.ONE, "selectedBy", ClassModelBuilder.ONE);
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
        field.buildAssociation(unit, "currentUnitOnField", ClassModelBuilder.ONE, "currentField", ClassModelBuilder.ONE);

        Fulib.generator().generate(mb.getClassModel());
    }
}