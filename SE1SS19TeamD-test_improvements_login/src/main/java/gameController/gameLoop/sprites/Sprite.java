package gameController.gameLoop.sprites;


import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.io.File;

public abstract class Sprite {

    final int SPRITE_SIZE = 32;
    protected int yPixelShift = 0;
    protected int xPixelShift = 0;
    protected int pixelMagnification;
    Canvas canvas;
    // In units of SPRITE_SIZE ( *= 32)
    int fieldPosX;
    int fieldPosY;
    // Real Pixel distance used when moving ( < 32)
    int subX;
    int subY;
    private String spriteSheetURI;
    private Image[][] frames;
    private int spriteSheetWidth;
    private int spriteSheetHeight;

    protected Sprite(String pathToSpriteSheet, Canvas canvas, int magnification, int posX, int posY) {

        this.canvas = canvas;

        pixelMagnification = magnification;

        fieldPosX = posX;
        fieldPosY = posY;
        subX = 0;
        subY = 0;

        spriteSheetURI = new File(pathToSpriteSheet).toURI().toString();

        frames = getFramesFromSheet(resizeSpriteSheet());
    }

    public abstract void draw();

    private Image resizeSpriteSheet() {
        Image spriteSheet = new Image(spriteSheetURI);

        spriteSheetWidth = (int) spriteSheet.getWidth();
        spriteSheetHeight = (int) spriteSheet.getHeight();

        spriteSheet = new Image(spriteSheetURI,
                spriteSheetWidth * pixelMagnification,
                spriteSheetHeight * pixelMagnification,
                true, false);

        spriteSheetWidth = (int) spriteSheet.getWidth();
        spriteSheetHeight = (int) spriteSheet.getHeight();

        return spriteSheet;
    }

    private Image[][] getFramesFromSheet(Image sheet) {
        int sheetWidth = spriteSheetWidth / (SPRITE_SIZE * pixelMagnification);
        int sheetHeight = spriteSheetHeight / (SPRITE_SIZE * pixelMagnification);

        Image[][] images = new Image[sheetWidth][sheetHeight];

        System.out.println("Sprite size: "
                + (SPRITE_SIZE * pixelMagnification) + "x" + (SPRITE_SIZE * pixelMagnification));

        for (int i = 0; i < sheetWidth; ++i) {
            for (int j = 0; j < sheetHeight; ++j) {
                images[i][j] = new WritableImage(sheet.getPixelReader(),
                        i * (SPRITE_SIZE * pixelMagnification),
                        j * (SPRITE_SIZE * pixelMagnification),
                        (SPRITE_SIZE * pixelMagnification),
                        (SPRITE_SIZE * pixelMagnification));
            }
        }

        return images;
    }

    public int getyPixelShift() {
        return yPixelShift;
    }

    public void setyPixelShift(int yPixelShift) {
        this.yPixelShift = yPixelShift;
    }

    public int getxPixelShift() {
        return xPixelShift;
    }

    public void setxPixelShift(int xPixelShift) {
        this.xPixelShift = xPixelShift;
    }

    public int getCurrentPositionX() {
        return fieldPosX * SPRITE_SIZE + subX;
    }

    public int getCurrentPositionY() {
        return fieldPosY * SPRITE_SIZE + subY;
    }

    public Image[][] getFrames() {
        return frames;
    }

    public Image getFrame(int x, int y) {
        return frames[x][y];
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public int getPixelMagnification() {
        return pixelMagnification;
    }

    public void setPixelMagnification(int pixelMagnification) {
        // No need to redraw the sheet, when magnification didn't change
        if (this.pixelMagnification != pixelMagnification) {
            this.pixelMagnification = pixelMagnification;
            frames = getFramesFromSheet(resizeSpriteSheet());
        }
    }
}

