package gameScreen;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.Player;
import model.Unit;

public class MinimapUnit extends Rectangle {

    private Player myPlayer;
    private Unit myUnit;
    private Color color;
    private int posX;
    private int posY;

    /**
     * MinimapUnit constructor to set rectangle size on creation
     *
     * @param sizeX the rectangles size in X axis
     * @param sizeY the rectangles size in Y axis
     */
    public MinimapUnit(int sizeX, int sizeY) {
        super(sizeX, sizeY);
    }

    public Player getMyPlayer() {
        return myPlayer;
    }

    public void setMyPlayer(Player myPlayer) {
        this.myPlayer = myPlayer;
    }

    public Unit getMyUnit() {
        return myUnit;
    }

    public void setMyUnit(Unit myUnit) {
        this.myUnit = myUnit;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        super.setFill(color);
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    /**
     * set the pos variables and the rectangles location to the parameter values
     *
     * @param posX the rectangles position in X axis
     * @param posY the rectangles position in Y axis
     */
    public void setPosXY(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        super.relocate(posX, posY);
    }

}
