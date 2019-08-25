package gameController.attackUnit;

import asyncCommunication.WebSocketComponent;
import gameController.units.UnitController;
import model.Field;
import model.GameField;
import model.Model;
import model.Unit;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class UnitAttackTest {

    @Test
    public void testAttacking() {
        Model model = new Model();
        model.setWebSocketComponent(new WebSocketComponent("TeamDTestUser",
                "myuserkey", false, model));

        // TODO: No damage calculation until we get the official numbers

        GameField board = new GameField();

        Field field = new Field()
                .setGameField(board);

        for (int i = 1; i < 4; ++i) {
            field = new Field()
                    .setGameField(board)
                    .withNeighbour(field);
        }

        ArrayList<String> heroCanAttack = new ArrayList<>();
        heroCanAttack.add("Archer");
        heroCanAttack.add("Commander");
        heroCanAttack.add("Dark Knight");

        ArrayList<String> darkKnightCanAttack = new ArrayList<>();
        darkKnightCanAttack.add("Hero");

        // Too far away
        Unit archer = new Unit()
                .setType("Archer")
                .setHp(1)
                .setOccupiesField(board.getFields().get(0));

        // Can't retaliate
        Unit commander = new Unit()
                .setType("Commander")
                .setCanAttack(new ArrayList<>())
                .setHp(1)
                .setOccupiesField(board.getFields().get(1));

        // The hero of our jUnit story
        Unit hero = new Unit()
                .setType("Hero")
                .setCanAttack(heroCanAttack)
                .setHp(1)
                .setOccupiesField(board.getFields().get(2));

        // Attack-able but will die
        Unit darkKnight = new Unit()
                .setType("Dark Knight")
                .setCanAttack(darkKnightCanAttack)
                .setHp(0)
                .setOccupiesField(board.getFields().get(3));

        UnitController attackController = new UnitController(true, model);

        boolean archerAttack = attackController.attackUnit(hero, archer);
        boolean commanderAttack = attackController.attackUnit(hero, commander);
        boolean darkKnightAttack = attackController.attackUnit(hero, darkKnight);
        boolean heroAttack = attackController.attackUnit(hero, hero);

        Assert.assertFalse(archerAttack);
        Assert.assertTrue(commanderAttack);
        Assert.assertTrue(darkKnightAttack);
        Assert.assertFalse(heroAttack);
    }
}
