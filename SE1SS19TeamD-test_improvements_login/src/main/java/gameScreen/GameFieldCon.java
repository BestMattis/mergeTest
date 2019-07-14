package gameScreen;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import model.Field;
import model.Game;
import model.GameField;

import java.util.HashSet;
import java.util.Set;

public class GameFieldCon {


    Image imgfail;
    Game game;
    GameField gameField;
    Set<Field> fields;
    Canvas canvas;
    Canvas unitcanvas;
    GraphicsContext gc;
    final int YTOPDISTANCE = 80;
    final int YBOTTOMDISTANCE = 80;
    final int YTILE = 96;
    final int XTILE = 96;

    public GameFieldCon(Game gametmp){
        game = gametmp;
        gameField = game.getGameField();
        canvas = calcSize(gameField.getSizeX(), gameField.getSizeY());
        unitcanvas = calcSize(gameField.getSizeX(), gameField.getSizeY());
        gc = canvas.getGraphicsContext2D();
        collectTiles();
        loadTiles(gameField.getSizeX(), gameField.getSizeY());
    }

    public void collectTiles(){
        fields = new HashSet<>();
        fields.addAll(gameField.getFields());
    }

    public Canvas calcSize(int x,int y){
        int xpx = x * XTILE;
        int ypx = y * YTILE;
        ypx += YTOPDISTANCE;
        ypx += YBOTTOMDISTANCE;
        return new Canvas(xpx, ypx);
    }

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

        surround = readsurround(surround, field);
        int[] tileinfo = precheck(surround);
        System.out.println("Tileinfo: " + tileinfo[0] + ":" + tileinfo[1] + " für: " + x/96 + ":" + y/96);
        Image itd = imgfail;
        if (tileinfo[0] != 0) {
            itd = getImageWater(tileinfo[0], all, wimg);
        }

        drawRotated(tileinfo[1],tileinfo[0], itd, x, y);

        return null;
    }

    public Image getImageWater(int j, Image all, WritableImage wimg){
        int i = j-1;
        int y = ((i / 3)) * YTILE;
        int x = ((i % 3)) * XTILE;
        wimg.getPixelWriter().setPixels(0,0,XTILE, YTILE, all.getPixelReader(), x, y);
        return (Image)wimg;
    }


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

    public boolean[] readsurround(boolean[] s, Field middle){
        for (Field field: middle.getNeighbour()){
            if (field.getType().equals("Water")) {
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

    public boolean[] rotate90(boolean[] s){
        boolean[] tmp = new boolean[8];
        tmp = s.clone();
        for (int i = 0; i < 8; i++){
            s[i] = tmp[(i-2+8)%8];
        }
        return s;
    }

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

        surround = readsurroundFor(surround, field);
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

    public boolean[] readsurroundFor(boolean[] s, Field middle){
        for (Field field: middle.getNeighbour()){
            if (field.getType().equals("Forest")) {
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

    public Image getImageFor(int j, Image all, WritableImage wimg){
        int i = j-1;
        int y = ((i / 4)) * YTILE;
        int x = ((i % 4)) * XTILE;
        wimg.getPixelWriter().setPixels(0,0,XTILE, YTILE, all.getPixelReader(), x, y);
        return (Image)wimg;
    }






    public void loadTiles(int x, int y){

        for (Field field: fields){
            int i = field.getPosX();
            int j = field.getPosY();
            int xpos = i * XTILE;
            int ypos = YTOPDISTANCE + (j * YTILE);
            drawTile(xpos, ypos, field);

        }
    }

    public void addUnitToTile(int x, int y){
        int xpos = x * XTILE;
        int ypos = YTOPDISTANCE + (y * YTILE);
        Image img = new Image("/armyManager/trashbin.png");
        unitcanvas.getGraphicsContext2D().drawImage(img, xpos, ypos, XTILE, YTILE);

    }

    public void removeUnitfromTile(int x, int y){
        int xpos = x * XTILE;
        int ypos = YTOPDISTANCE + (y * YTILE);
        unitcanvas.getGraphicsContext2D().clearRect(xpos, ypos, XTILE, YTILE);
    }

    public void refreshUnits(){
        unitcanvas.getGraphicsContext2D().clearRect(0, 0, unitcanvas.getWidth(), unitcanvas.getHeight());
    }

    public Canvas getCanvas(){
        return canvas;
    }

    public Canvas getUnitcanvas(){
        return unitcanvas;
    }

    public AnchorPane getGamefield(){
        AnchorPane base = new AnchorPane();
        base.getChildren().addAll(canvas, unitcanvas);
        return base;
    }

}
