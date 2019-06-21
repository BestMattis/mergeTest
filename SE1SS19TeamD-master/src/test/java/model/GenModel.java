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

        //Classes for ArmyManager
        ClassBuilder armyConfiguration = mb.buildClass("ArmyConfiguration");
        ClassBuilder unit = mb.buildClass("Unit");
        ClassBuilder bazookatrooper = mb.buildClass("BazookaTrooper");
        ClassBuilder infantry = mb.buildClass("Infantry");
        ClassBuilder heavytank = mb.buildClass("HeavyTank");
        ClassBuilder chopper = mb.buildClass("Chopper");
        ClassBuilder lighttank = mb.buildClass("LightTank");
        ClassBuilder jeep = mb.buildClass("Jeep");

        bazookatrooper.setSuperClass(unit);
        infantry.setSuperClass(unit);
        heavytank.setSuperClass(unit);
        chopper.setSuperClass(unit);
        lighttank.setSuperClass(unit);
        jeep.setSuperClass(unit);

        // Attributes
        player.buildAttribute("name", ClassModelBuilder.STRING);
        player.buildAttribute("password", ClassModelBuilder.STRING);
        game.buildAttribute("name", ClassModelBuilder.STRING)
                .buildAttribute("capacity", ClassModelBuilder.INT)
                .buildAttribute("gameId", ClassModelBuilder.STRING);
        chatMessage.buildAttribute("message", ClassModelBuilder.STRING)
                .buildAttribute("channel", ClassModelBuilder.STRING)
                .buildAttribute("date", ClassModelBuilder.STRING);

        //Attributes for ArmyManager
        armyConfiguration.buildAttribute("name", ClassModelBuilder.STRING)
                .buildAttribute("id", ClassModelBuilder.STRING);
        unit.buildAttribute("id", ClassModelBuilder.STRING)
                .buildAttribute("canAttack", ClassModelBuilder.COLLECTION_ARRAY_LIST)
                .buildAttribute("mp", ClassModelBuilder.INT)
                .buildAttribute("hp", ClassModelBuilder.INT);

        //Associations
        game.buildAssociation(player, "players", ClassModelBuilder.MANY, "game", ClassModelBuilder.ONE);
        chatMessage.buildAssociation(player, "receiver", ClassModelBuilder.ONE, "messages", ClassModelBuilder.MANY);
        chatMessage.buildAssociation(player, "sender", ClassModelBuilder.ONE, "messages", ClassModelBuilder.MANY);
        app.buildAssociation(player, "allPlayers", ClassModelBuilder.MANY, "app", ClassModelBuilder.ONE);
        app.buildAssociation(game, "allGames", ClassModelBuilder.MANY, "app", ClassModelBuilder.ONE);
        app.buildAssociation(player, "currentPlayer", ClassModelBuilder.ONE, "myApp", ClassModelBuilder.ONE);
        app.buildAssociation(chatMessage, "allChatMessages", ClassModelBuilder.MANY, "app", ClassModelBuilder.ONE);

        //Associations for ArmyManager
        player.buildAssociation(armyConfiguration, "armyConfigurations", ClassModelBuilder.MANY, "player", ClassModelBuilder.ONE);
        armyConfiguration.buildAssociation(unit, "units", ClassModelBuilder.MANY, "armyConfiguration", ClassModelBuilder.ONE);

        ClassModel model = mb.getClassModel();

        Fulib.generator().generate(model);
    }
}
