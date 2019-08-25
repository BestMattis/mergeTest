package gameController.sprites;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import gameController.gameLoop.GameLoop;
import gameController.gameLoop.sprites.MovementPath;
import gameController.gameLoop.sprites.UnitSprite;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.Field;
import model.Unit;

public class SpriteMovementTest extends Application {

    private static boolean sandBoxMode = false;

    @Test
    public void testMovement() {
        launch(this.getClass());
    }

    @Test
    public void sandBoxMode() {
        sandBoxMode = true;
        launch(this.getClass());
    }

    public void start(Stage stage) {
        System.out.println("SandboxMode: " + sandBoxMode);

        Unit unit = new Unit();
        unit.setPosX(5);
        unit.setPosY(5);

        String pathToTestSpriteSheet = "./src/test/resources/SpriteAnimationTest/LTank_SpriteSheet_Test.png";

        int magnification = 3;

        Canvas canvas = new Canvas(9 * 32 * magnification, 9 * 32 * magnification);

        UnitSprite sprite = new UnitSprite(unit, pathToTestSpriteSheet, magnification, canvas);
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
        // Map WASD to unit for manual control
        if (sandBoxMode) {
            scene.setOnKeyPressed(e -> {
                MovementPath movementPath = new MovementPath();

                switch (e.getCode()) {
                    case W: {
                        this.addPathStep(unit, movementPath, 0, -1);
                        break;
                    }

                    case A: {
                	this.addPathStep(unit, movementPath, -1, 0);
                        break;
                    }

                    case S: {
                	this.addPathStep(unit, movementPath, 0, +1);
                        break;
                    }

                    case D: {
                	this.addPathStep(unit, movementPath, +1, 0);
                        break;
                    }

                    case UP: {
                        sprite.setSubPixelMovementSteps(sprite.getSubPixelMovementSteps() + 1);
                        System.out.println("Now moving at " + sprite.getSubPixelMovementSteps() + "pix");
                        movementPath = null;
                        break;
                    }

                    case DOWN: {
                        sprite.setSubPixelMovementSteps(sprite.getSubPixelMovementSteps() - 1);
                        System.out.println("Now moving at " + sprite.getSubPixelMovementSteps() + "pix");
                        movementPath = null;
                        break;
                    }

                    case RIGHT: {
                        sprite.setAnimationDurationMilliSec(sprite.getAnimationDurationMilliSec() + 100);
                        System.out.println("Animation duration: " + sprite.getAnimationDurationMilliSec() + "ms");
                        movementPath = null;
                        break;
                    }

                    case LEFT: {
                        sprite.setAnimationDurationMilliSec(sprite.getAnimationDurationMilliSec() - 100);
                        System.out.println("Animation duration: " + sprite.getAnimationDurationMilliSec() + "ms");
                        movementPath = null;
                        break;
                    }

                    default:
                        movementPath = null;
                        break;
                }
                if (movementPath != null) {
                    sprite.addMovementPath(movementPath);
                }
            });
        }

        gameLoop.startLoop();
        stage.setScene(scene);
        stage.show();

        if (!sandBoxMode) {

            Assert.assertNull(sprite.getCurrentMovementPath());
            
            List<Field> fields = new ArrayList<>();
            fields.add(this.fieldByCoords(5, 6));
            fields.add(this.fieldByCoords(5, 7));
            fields.add(this.fieldByCoords(5, 6));
            fields.add(this.fieldByCoords(5, 5));
            fields.add(this.fieldByCoords(4, 5));
            fields.add(this.fieldByCoords(5, 5));
            
            for (int i = 0; i < 2; ++i) {
                fields.add(this.fieldByCoords(6, 5));
                fields.add(this.fieldByCoords(5, 5));
                fields.add(this.fieldByCoords(4, 5));
                fields.add(this.fieldByCoords(5, 5));
            }
            
            MovementPath mPath = new MovementPath();
            mPath.setPath(fields);

            sprite.addMovementPath(mPath);

            Assert.assertNotNull(sprite.getCurrentMovementPath());

            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(() -> {
                while (!sandBoxMode && sprite.getCurrentMovementPath() != null) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Platform.exit();
            }, 0, 5, TimeUnit.SECONDS);
        }
    }
    
    private Field fieldByCoords(int x, int y) {
	return AdvancedWarsApplication.getInstance().getGameScreenCon().getGameFieldController().getFieldByCoordinates(x, y);
    }
    
    private void addPathStep(Unit unit, MovementPath movementPath, int xOffset, int yOffset) {
	movementPath.addPathStep(unit.getOccupiesField().getNeighbour().stream().filter(f -> f.getPosX() == unit.getPosX() + xOffset && f.getPosY() == unit.getPosY() + yOffset).findAny().get());
    }
}
