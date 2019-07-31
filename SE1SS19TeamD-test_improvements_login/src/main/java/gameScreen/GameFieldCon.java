package gameScreen;

import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import model.*;

import java.util.HashSet;
import java.util.Set;

public class GameFieldCon {


    Image imgfail;
    Game game;
    GameField gameField;
    Set<Field> fields;
    Canvas canvas;
    Canvas unitcanvas;
    private Canvas colorCanvas;
    GraphicsContext gc;
    ProgressBar loadingProgress;
    double progress;
    double max;
    final int YTOPDISTANCE = 80;
    final int YBOTTOMDISTANCE = 80;
    final int YTILE = 96;
    final int XTILE = 96;

    //The general functions to collect all the necessary informations to draw the tiles one by one

    /**
     * Constructor to create the gamefield, unit canvas with the size calculated and
     * calls collectTiles and loadTiles
     * @param gametmp the game to draw the gamefield for
     */
    public GameFieldCon(Game gametmp, ProgressBar loadingProgress){
        this.loadingProgress = loadingProgress;
        game = gametmp;
        gameField = game.getGameField();
        canvas = calcSize(gameField.getSizeX(), gameField.getSizeY());
        unitcanvas = calcSize(gameField.getSizeX(), gameField.getSizeY());
        colorCanvas = calcSize(gameField.getSizeX(), gameField.getSizeY());
        colorCanvas.setId("colorCanvas");
        unitcanvas.setOnMouseClicked(event -> {
            Field field = getFieldByCoordinates(event.getX(),event.getY());
            System.out.println(this.getClass().toString()+": Clicked on position ("+getTrueXPos(event.getX())+","+getTrueYPos(event.getY())+"). Found: "+field);
            if(field != null){
                if(field.getCurrentUnitOnField() != null){
                    if(field.getCurrentUnitOnField().getSelectedBy() == null) {
                        Model.getApp().getCurrentPlayer().getGame().setSelectedUnit(field.getCurrentUnitOnField());
                        System.out.println(this.getClass().toString() + ": selected unit set to: " + field.getCurrentUnitOnField().getType());
                    } else {
                        Model.getApp().getCurrentPlayer().getGame().setSelectedUnit(null);
                    }
                }
            }
        });
        gc = canvas.getGraphicsContext2D();
        collectTiles();
        loadTiles();
        for(Field field : gameField.getFields()){
            field.addPropertyChangeListener(Field.PROPERTY_currentUnitOnField, evt ->
                    addUnitToTile(field.getPosX(),field.getPosY(), field.getCurrentUnitOnField()));
        }
    }

    /**
     * get all the tiles form the game
     */
    public void collectTiles(){
        fields = new HashSet<>();
        fields.addAll(gameField.getFields());
    }

    /**
     * Generate a canvas with the correct size for the gamefield
     * @param x tile-count horizontally
     * @param y tile-count vertically
     * @return a canvas with the size for the gamefield
     */
    public Canvas calcSize(int x,int y){
        int xpx = x * XTILE;
        int ypx = y * YTILE;
        ypx += YTOPDISTANCE;
        ypx += YBOTTOMDISTANCE;
        return new Canvas(xpx, ypx);
    }

    // Iterate over the tiles of the field and call the methods to draw them

    /**
     *  Iterate over all fields to draw them one by one
     *  by calling drawTile
     */
    public void loadTiles(){
        progress = 0;
        max = fields.size();
        for (Field field: fields){
            int i = field.getPosX();
            int j = field.getPosY();
            int xpos = i * XTILE;
            int ypos = YTOPDISTANCE + (j * YTILE);
            drawTile(xpos, ypos, field);
            progress++;
            if(loadingProgress != null){
                loadingProgress.setProgress(progress/max);
            }
        }
    }

    /**
     * Loads the pictures the texture is taken from
     *  checks the type of the tile and:
     *  draws mountain and grass or calls further methods for water( testWater)
     *  and draws forest with the Image returned by testForest
     * @param x tileposition horizontally in pixels
     * @param y tileposition vertically in pixels
     * @param field the tile to draw
     */
    public void drawTile(int x, int y, Field field){
        imgfail = new Image("/armyManager/placeholderunit.jpg");
        WritableImage wimg = new WritableImage(XTILE, YTILE);

        Image grass = new Image("/textures/terrain/Grass.png", XTILE, YTILE, true, false);
        Image mount = new Image("/textures/terrain/Mountain.png", XTILE, YTILE, true, false);
        Image forest = new Image("/textures/terrain/Forest.png", 384, 288, true, false);
        Image water = new Image("/textures/terrain/Water.png", 288, 480, true, false);

        gc.setFill(Color.BLUE);
        gc.fillRect(x, y, XTILE, YTILE);
        if (field.getType().equals("bla")){
            gc.setFill(Color.RED);
            gc.drawImage((Image)wimg, x, y, XTILE, YTILE);
        } else if (field.getType().equals("blubb")){
            gc.setFill(Color.BLUE);
            gc.fillRect(x, y, XTILE, YTILE);
        } else if (field.getType().equals("Grass")){
            gc.drawImage(grass, x, y, XTILE, YTILE);
        }else if (field.getType().equals("Mountain")){
            gc.drawImage(mount, x, y, XTILE, YTILE);
        }else if (field.getType().equals("Water")){
            testWater(field, water, wimg, x, y);
        }else if (field.getType().equals("Forest")){
            gc.drawImage(testForest(field, forest, wimg), x, y, XTILE, YTILE);
        }
    }

    //The methods to draw a tile of water

    /**
     * The method to call the methods to check the surrounding tiles( readsurround) for water, determine the texture and rotation( precheck)
     *  to load the imagepart( getImageWater), and draw the texture( drawRotated)
     * @param field the field to draw
     * @param all the Image of the water-tetures
     * @param wimg a writableimage to cut the used part of all
     * @param x tileposition in pixels
     * @param y tileposition in pixels
     * @return a Image of the tile that was drawn
     */
    public Image testWater(Field field, Image all, WritableImage wimg, int x, int y){
        boolean[] surround = new boolean[8];
        surround[0] = false;
        surround[1] = false;
        surround[2] = false;
        surround[3] = false;
        surround[4] = false;
        surround[5] = false;
        surround[6] = false;
        surround[7] = false;

        surround = readsurround(surround, field, true);
        int[] tileinfo = precheck(surround);
        System.out.println("Tileinfo: " + tileinfo[0] + ":" + tileinfo[1] + " für: " + x/96 + ":" + y/96);
        Image itd = imgfail;
        if (tileinfo[0] != 0) {
            itd = getImageWater(tileinfo[0], all, wimg);
        }

        drawRotated(tileinfo[1],tileinfo[0], itd, x, y);

        return null;
    }

    /**
     * Get the Texture needed from the Water-Image
     * @param j the number of the necessary texture
     * @param all image of all textures( numbered from top left(1) to bottom right(15))
     * @param wimg writableimage to cut the needed imagepart
     * @return
     */
    public Image getImageWater(int j, Image all, WritableImage wimg){
        int i = j-1;
        int y = ((i / 3)) * YTILE;
        int x = ((i % 3)) * XTILE;
        wimg.getPixelWriter().setPixels(0,0,XTILE, YTILE, all.getPixelReader(), x, y);
        return (Image)wimg;
    }

    /**
     * Draws a given Image with the dimensions 96*96 to the given position with a texturspecific( because I messed the readsurround up) rotation
     * @param i the common angle to rotate(i * 90)
     * @param sonder the number of the texture to draw( to identify the texture)
     * @param image the texture to draw
     * @param x horizontal coordinate
     * @param y vertical coordinate
     */
    public void drawRotated(int i,int sonder, Image image, int x, int y){
        if (sonder == 2){
            i += 2;
        }
        if (sonder == 4 || sonder == 7 || sonder == 13 || sonder == 14) {
            i -= 1;
        }
        if (sonder == 15){
            i+=1;
        }

        i+=1;
        gc.save();
        gc.translate(x, y);
        gc.translate((XTILE/2), (YTILE/2));
        gc.rotate((i) * (90));
        gc.drawImage(image, -(XTILE/2), -(YTILE/2), XTILE, YTILE);
        gc.restore();
    }

    //The methods to draw a Forest-tile

    /**
     * The method to call the methods to check the surrounding tiles( readsurround) for forest, determine the texture and rotation( precheck)
     * and map the rotation and texturenumber to the corresponding texturenumber for the foresttextures
     * and load the imagepart( getImageFor)
     * @param field the field to draw
     * @param all the Image of the water-textures
     * @param wimg a writableimage to cut the used part of all
     * @return a Image of the tile to draw
     */
    public Image testForest(Field field, Image all, WritableImage wimg){

        Image ret = null;

        boolean[] surround = new boolean[8];
        surround[0] = false;
        surround[1] = false;
        surround[2] = false;
        surround[3] = false;
        surround[4] = false;
        surround[5] = false;
        surround[6] = false;
        surround[7] = false;

        surround = readsurround(surround, field, false);
        int[] tileinfo = precheck(surround);
        int tile = tileinfo[0];
        int rot = tileinfo[1];
        if (tile == 1){
            if (rot == 0){
                ret =getImageFor(3, all, wimg);
            } else if(rot == 1){
                ret =getImageFor(5, all, wimg);
            }
        } else if (tile == 2){
            if (rot == 0){
                ret =getImageFor(4, all, wimg);
            } else if (rot == 1){
                ret =getImageFor(9, all, wimg);
            } else if (rot == 2){
                ret =getImageFor(2, all, wimg);
            } else if (rot == 3){
                ret =getImageFor(1, all, wimg);
            }
        } else if (tile == 3){
            if (rot == 0){
                ret =getImageFor(6, all, wimg);
            } else if (rot == 3){
                ret =getImageFor(7, all, wimg);
            }
        } else if (tile == 5){
            ret =getImageFor(10, all, wimg);
        }

        if (ret == null){
            ret = getImageFor(8, all, wimg);
        }
        return ret;
    }

    /**
     * Get the Texture needed from the forest-Image
     * @param j the number of the necessary texture
     * @param all image of all textures(numbered from top left(1) to bottom right(10))
     * @param wimg writableimage to cut the needed imagepart
     * @return
     */
    public Image getImageFor(int j, Image all, WritableImage wimg){
        int i = j-1;
        int y = ((i / 4)) * YTILE;
        int x = ((i % 4)) * XTILE;
        wimg.getPixelWriter().setPixels(0,0,XTILE, YTILE, all.getPixelReader(), x, y);
        return (Image)wimg;
    }

    //The methods to select the water/forest tile to draw and calculate the rotation

    /**
     * This method first checks if the tile has to be a typ which hasn't to be rotated.
     * If not it checks further by calling checkTile and rotate90
     * as long as no texture and rotation is found, but max 6 times.
     * If a config is found the numbers are returned, else 0 is returned.
     * @param s the boolean-Array if the tiles surrounding the tile to draw are the same type
     * @return an integer-Array, the first is the texturenumber, the second is how often to rotate 90 degrees
     */
    public int[] precheck(boolean[] s){
        int[] ret = new int[2];
        ret[0] = 0;
        ret[1] = 0;

        if (!s[0]  && !s[2] && !s[4] && !s[6]){
            ret[0] = 5;
        } else if (s[0] && s[1] && s[2] && s[3] && s[4] && s[5] && s[6] && s[7]){
            ret[0] = 12;
        } else if (s[0] && !s[1] && s[2] && !s[3] && s[4] && !s[5] && s[6] && !s[7]){
            ret[0] = 6;
        } else {
            L:
            for (int i = 0; i < 6; i++){
                int tmp = checkTile(s);
                if (tmp != 0){
                    ret[0] = tmp;
                    ret[1] = i;
                    break L;
                } else {
                    s = rotate90(s);
                }
            }
        }

        return ret;
    }

    /**
     * Checks if one of the textures corresponds to the surrounding of the tile to draw
     * @param s the (maybe rotated) boolean value if the surrounding tiles are the same type as the one to draw
     * @return 0 if no texture is found, else the number of the texture
     */
    public int checkTile(boolean[] s){
        int t = 0;
        if(s[0] && !s[2] && s[4] && !s[6]){
            t = 1;
        } else if (s[0] && !s[2] && !s[4]  && !s[6]){
            t = 2;
        }else if (s[0] && !s[1] && s[2] && !s[3] && s[4] && !s[6]){
            t= 3;
        }else if (!s[0] && s[2] && !s[3] && s[4] && !s[6]){
            t = 4;
        }else if (!s[0] && s[2] && s[3] && s[4] && !s[6]){
            t = 7;
        }else if (s[0] && !s[2] && s[4] && s[5] && s[6] && s[7]){
            t = 8;
        }else if (s[0] && !s[1] && s[2] && !s[3] && s[4] && s[5] && s[6] && s[7]){
            t = 9;
        }else if (s[0] && s[5] && !s[2]  && s[4]  && s[6] && !s[7]){
            t = 10;
        }else if (s[0] && !s[2] && s[4] && s[6] && s[7] && !s[5]){
            t = 11;
        }else if (s[0] && s[1] && s[2] && s[3] && s[4] && s[5] && s[6] && !s[7]){
            t = 13;
        }else if (s[0] && s[1] && s[2] && !s[3] && s[4] && s[5] && s[6] && !s[7]){
            t = 14;
        }else if (s[0] && !s[1] && s[2] && !s[3] && s[4] && s[5] && s[6] && !s[7]){
            t = 15;
        }
        return t;
    }

    /**
     * This method rotates the boolean-array of the surrounding tiles every time called another 90 degrees
     *  so the tile to draw is virtually rotated -90 degrees
     * @param s the boolean values if the surrounding tiles are the same type as the tile to draw
     * @return the rotated array
     */
    public boolean[] rotate90(boolean[] s){
        boolean[] tmp = new boolean[8];
        tmp = s.clone();
        for (int i = 0; i < 8; i++){
            s[i] = tmp[(i-2+8)%8];
        }
        return s;
    }

    /**
     * This method checks the neighbour-tiles of the tile to draw( middle) and writes true into the array
     * if they are the correct type( water or forest, which is choosen by the variable "water")
     *  and otherwise false.
     *  ( this is the part I initially messed up, because the order of the checking is anitclockwise instead
     *  of clockwise)
     * @param s the boolean-array to save if the surrounding tiles are the same type as the tile to draw
     * @param middle the tile to draw
     * @param water to choose to whether check for water or forest
     * @return the surrounding boolean-array
     */
    public boolean[] readsurround(boolean[] s, Field middle, boolean water){
        for (Field field: middle.getNeighbour()){
            if ((field.getType().equals("Water")&& water)|| (field.getType().equals("Forest")&& !water)) {
                if (field.getPosX() == middle.getPosX() - 1) {
                    if (field.getPosY() == middle.getPosY() - 1) {
                        s[7] = true;
                    } else if (field.getPosY() == middle.getPosY()) {
                        s[0] = true;
                    } else if (field.getPosY() == middle.getPosY() + 1) {
                        s[1] = true;
                    }
                } else if (field.getPosX() == middle.getPosX()) {
                    if (field.getPosY() == middle.getPosY() - 1) {
                        s[6] = true;
                    } else if (field.getPosY() == middle.getPosY() + 1) {
                        s[2] = true;
                    }
                } else if (field.getPosX() == middle.getPosX() + 1) {
                    if (field.getPosY() == middle.getPosY() - 1) {
                        s[5] = true;
                    } else if (field.getPosY() == middle.getPosY()) {
                        s[4] = true;
                    } else if (field.getPosY() == middle.getPosY() + 1) {
                        s[3] = true;
                    }
                }
            }
        }

        return s;
    }

    //The unit operations

    /**
     * This method draws a unit( till now it is a placeholder) with the given coordinates onto an additional canvas for the units
     * @param x the tile-position horizontally
     * @param y the tile-position vertically
     * @param unit the unit that shall be drawn
     */
    public void addUnitToTile(int x, int y, Unit unit){
        if(unit != null){
            String type = unit.getType();
            type = type.replace(" ","");
            System.out.println(this.getClass().toString()+": New Unit("+type+") on the field");
            int xpos = x * XTILE;
            int ypos = YTOPDISTANCE + (y * YTILE);
            Image img = new Image("/textures/units/PH_"+type+".png");
            unitcanvas.getGraphicsContext2D().drawImage(img, xpos, ypos, XTILE, YTILE);
        }
    }

    /**
     * The method to clear a given tile
     * @param x the tile-position horizontally
     * @param y the tile-position vertically
     */
    public void removeUnitfromTile(int x, int y){
        int xpos = x * XTILE;
        int ypos = YTOPDISTANCE + (y * YTILE);
        unitcanvas.getGraphicsContext2D().clearRect(xpos, ypos, XTILE, YTILE);
    }

    /**
     * Clear the complete unit canvas
     */
    public void refreshUnits(){
        unitcanvas.getGraphicsContext2D().clearRect(0, 0, unitcanvas.getWidth(), unitcanvas.getHeight());
    }
    
    public void addColorToField(int x, int y, Color color) {
	GraphicsContext ctx = colorCanvas.getGraphicsContext2D();
	ctx.setFill(color);
	ctx.fillRect(x, y, XTILE, YTILE);
    }
    
    public void removeColorFromField(int x, int y) {
	colorCanvas.getGraphicsContext2D().clearRect(x, y, XTILE, YTILE);
    }

    //Getter

    /**
     * Get only the gamefield
     * @return the canvas of the gamefield
     */
    public Canvas getCanvas(){
        return canvas;
    }

    /**
     * Get only the canvas which contains the units
     * @return the unitcanvas
     */
    public Canvas getUnitcanvas(){
        return unitcanvas;
    }

    /**
     * Get an anchorpane which combines the gamefieldcanvas and the unitcanvas
     * @return an anchorpane
     */
    public AnchorPane getGamefield(){
        AnchorPane base = new AnchorPane();
        base.getChildren().addAll(canvas, unitcanvas, colorCanvas);
        return base;
    }

    /**
     * Get the Field corresponding to the x and y coordinates.
     * @param x actual x position
     * @param y actual y position
     * @return Filed that has been clicked
     */
    public Field getFieldByCoordinates(double x, double y){
        int xPos = getTrueXPos(x);
        int yPos = getTrueYPos(y);
        for(Field field : Model.getApp().getCurrentPlayer().getGame().getGameField().getFields()){
            if(field.getPosX() == xPos && field.getPosY() == yPos){
                return field;
            }
        }
        return null;
    }

    /**
     * Get the true x position for the field
     * @param x actual x position
     * @return x position to locate the field
     */
    public int getTrueXPos(double x){
        int xPos = (int)(x/((double)XTILE));
        return xPos;
    }

    /**
     * Get the true y position for the field
     * @param y actual y position
     * @return y position to locate the field
     */
    public int getTrueYPos(double y){
        int yPos = (int)((y-YTOPDISTANCE)/((double)YTILE));
        return yPos;
    }

}
