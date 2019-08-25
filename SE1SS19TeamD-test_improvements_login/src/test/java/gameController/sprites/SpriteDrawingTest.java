package gameController.sprites;

import gameController.gameLoop.sprites.Sprite;
import gameController.gameLoop.sprites.UnitSprite;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import model.Unit;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SpriteDrawingTest extends ApplicationTest {

    /**
     * Test to check if a spriteSheet is properly cut into all single sprites.
     * Saves the sprites as images to test/resources/.
     */
    @Test
    public void test() {
        Unit unit = new Unit();

        unit.setPosX(3);
        unit.setPosY(4);

        String pathToTestSpriteSheet = "./src/test/resources/SpriteCutTest/test.png";

        File cutSpriteFolder = new File("./src/test/resources/SpriteCutTest/singleSprites");
        if (!cutSpriteFolder.exists()) {
            if (!cutSpriteFolder.mkdir()) {
                System.out.println("Directory wasn't created");
            }
        }

        Sprite sprite = new UnitSprite(unit, pathToTestSpriteSheet, 1, null);

        Image[][] frames = sprite.getFrames();


        for (int x = 0; x < frames.length; x++) {
            for (int y = 0; y < frames[x].length; ++y) {

                sprite.setPixelMagnification(((y * frames.length + x) % 3) + 1);

                File outputFile = new File("./src/test/resources/spriteCutTest/singleSprites/Sprite_"
                        + (y * frames.length + x) + "_x" + sprite.getPixelMagnification() + ".png");

                if (outputFile.exists()) {
                    if (outputFile.delete()) {
                        System.out.println(outputFile.getName() + " was deleted");
                    }
                }

                if (frames[x][y] == null) {
                    System.out.println(x + "|" + y + " is null");
                } else {
                    BufferedImage bImage = SwingFXUtils.fromFXImage(sprite.getFrame(x, y), null);
                    try {
                        ImageIO.write(bImage, "png", outputFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(outputFile.getName() + " created at: " + outputFile.getPath());
                }
            }
        }
    }
}
