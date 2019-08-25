package gameController.moveUnit;

import asyncCommunication.WebSocketComponent;
import gameController.gameLoop.GameLoop;
import gameController.gameLoop.sprites.UnitSprite;
import gameController.units.UnitController;
import javafx.scene.canvas.Canvas;
import main.AdvancedWarsApplication;
import model.*;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

public class MoveUnitTest extends ApplicationTest {

    @Test
    public void testMoveUnit() {
        Model model = new Model();
        model.setWebSocketComponent(new WebSocketComponent("TeamDTestUser", "myuserkey", false, model));

        int fieldCount = 9;

        Player testPlayer = new Player();
        GameField board = new GameField();

        model.getApp().setCurrentPlayer(testPlayer);
        testPlayer.setGame(new Game());
        testPlayer.getGame().setGameField(board);

        Field field = new Field()
                .setGameField(board)
                .setPosX(0)
                .setPosY(0)
                .setIsPassable(true)
                .setId("ID_" + 0);

        for (int i = 0; i < fieldCount; ++i) {
            System.out.println("New field at: " + field.getPosX() + "|" + field.getPosY());
            field = new Field()
                    .setGameField(board)
                    .withNeighbour(field)
                    .setPosX(field.getPosX() + 1)
                    .setPosY(field.getPosY())
                    .setIsPassable(true)
                    .setId("ID_" + (i + 1));
        }
        Assert.assertEquals(board.getFields().size(), fieldCount + 1);

        Unit unit = new Unit()
                .setPosX(1)
                .setPosY(0)
                .setId("UnitID")
                .setMp(3)
                .setOccupiesField(board.getFields().get(1));

        ArmyConfiguration army = new ArmyConfiguration();
        army.withUnits(unit);

        String pathToTestSheet = "./src/test/resources/SpriteAnimationTest/LTank_SpriteSheet_Test.png";
        Canvas canvas = new Canvas();

        UnitSprite sprite = new UnitSprite(unit, pathToTestSheet, 1, canvas);
        sprite.setSubPixelMovementSteps(32);

        GameLoop gameLoop = new GameLoop();
        gameLoop.addSprite(sprite);
        gameLoop.setSpriteCanvas(canvas);

        UnitController mover = new UnitController(true, model);
        gameLoop.startLoop();

        Assert.assertEquals(unit.getPosX(), 1);

        System.out.println("\nFirst move");
        boolean firstMove = mover.moveUnit(unit, sprite, this.fieldByCoords(unit.getPosX() + 1, unit.getPosY()));
        Assert.assertEquals(unit.getPosX(), 2);

        System.out.println("\nSecond move");
        boolean secondMove = mover.moveUnit(unit, sprite, this.fieldByCoords(unit.getPosX() - 1, unit.getPosY()));
        Assert.assertEquals(unit.getPosX(), 1);

        System.out.println("\nThird move");
        boolean thirdMove = mover.moveUnit(unit, sprite, this.fieldByCoords(unit.getPosX() - 1, unit.getPosY()));
        Assert.assertEquals(unit.getPosX(), 0);

        System.out.println("\nFourth move");
        boolean failedFourthMove = mover.moveUnit(unit, sprite, this.fieldByCoords(unit.getPosX() - 1, unit.getPosY()));
        Assert.assertEquals(unit.getPosX(), 0);

        Assert.assertTrue(firstMove);
        Assert.assertTrue(secondMove);
        Assert.assertTrue(thirdMove);
        Assert.assertFalse(failedFourthMove);
    }

    private Field fieldByCoords(int x, int y) {
        return AdvancedWarsApplication.getInstance().getGameScreenCon().getGameFieldController().getFieldByCoordinates(x, y);
    }
}
