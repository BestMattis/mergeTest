package gameController.moveUnit;

import asyncCommunication.WebSocketComponent;
import gameController.gameLoop.GameLoop;
import gameController.gameLoop.sprites.UnitSprite;
import gameController.units.UnitController;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.*;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;

public class FeedbackUnitCannotMove extends ApplicationTest {

    private Model model;
    private Unit unit;
    private UnitSprite sprite;

    @Override
    public void start(Stage stage) {
        AdvancedWarsApplication awa = new AdvancedWarsApplication();
        this.model = new Model();
        int magnification = 3;

        unit = new Unit();
        unit.setPosX(2);
        unit.setPosY(0);
        unit.setMp(5);

        GameField board = new GameField();

        board.withFields(new Field().setPosX(0).setPosY(0).setIsPassable(false));
        board.withFields(new Field().setPosX(1).setPosY(0).setIsPassable(true));
        board.withFields(new Field().setPosX(2).setPosY(0).setIsPassable(true));
        board.withFields(new Field().setPosX(3).setPosY(0).setIsPassable(true));
        board.withFields(new Field().setPosX(4).setPosY(0).setIsPassable(false));

        for (int i = 1; i < board.getFields().size(); ++i) {
            board.getFields().get(i - 1).withNeighbour(board.getFields().get(i));
        }

        Player player = new Player();
        ArmyConfiguration army = new ArmyConfiguration();
        Game game = new Game();
        army.withUnits(unit);
        army.setPlayer(player);
        player.setGame(game);
        game.setGameField(board);
        model.getApp().setCurrentPlayer(player);

        model.setWebSocketComponent(new WebSocketComponent("TeamDTestUser",
                "myuserkey", false, model));


        Canvas canvas = new Canvas(5 * 32 * magnification, 32 * magnification);

        String pathToTestSpriteSheet = "./src/test/resources/SpriteAnimationTest/LTank_SpriteSheet_Test.png";
        sprite = new UnitSprite(unit, pathToTestSpriteSheet, magnification, canvas);
        sprite.setSubPixelMovementSteps(2);

        GameLoop gameLoop = new GameLoop();
        gameLoop.setSpriteCanvas(canvas);
        gameLoop.addSprite(sprite);

        StackPane stackPane = new StackPane(canvas);

        // Background
        Image image = new Image(new File("./src/test/resources/SpriteAnimationTest/Grass.png")
                .toURI().toString(), 32 * magnification, 32 * magnification,
                true, false);

        Background bG = new Background(new BackgroundImage(image,
                null, null, null, null));

        stackPane.setBackground(bG);
        Scene scene = new Scene(stackPane);

        gameLoop.startLoop();
        stage.setScene(scene);
        stage.show();

        Assert.assertNull(sprite.getCurrentMovementPath());
    }

    @Test
    public void testFeedbackUnitCannotMove() {
        UnitController move = new UnitController(true ,this.model);
        Assert.assertEquals(unit.getPosX(), 2);

        move.moveUnit(unit, sprite, this.fieldByCoords(3, unit.getPosY()));
        Assert.assertEquals(unit.getPosX(), 3);

        move.moveUnit(unit, sprite, this.fieldByCoords(4, unit.getPosY()));
        Assert.assertEquals(unit.getPosX(), 3);

        move.moveUnit(unit, sprite, this.fieldByCoords(4, unit.getPosY()));
        Assert.assertEquals(unit.getPosX(), 3);

        move.moveUnit(unit, sprite, this.fieldByCoords(2, unit.getPosY()));
        Assert.assertEquals(unit.getPosX(), 2);
    }

    private Field fieldByCoords(int x, int y) {
        return AdvancedWarsApplication.getInstance().getGameScreenCon().getGameFieldController().getFieldByCoordinates(x, y);
    }

}
