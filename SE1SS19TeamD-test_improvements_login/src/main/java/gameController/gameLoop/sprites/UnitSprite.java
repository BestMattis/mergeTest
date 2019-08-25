package gameController.gameLoop.sprites;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import main.AdvancedWarsApplication;
import model.Unit;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class UnitSprite extends Sprite {

    private int subPixelMovementSteps = 8;
    private int animationDurationMilliSec = 500;

    private Unit unit;
    private ArrayList<MovementPath> movementPaths = new ArrayList<>();

    private boolean isMoving = false;
    private String lookingDirection = "R";

    public UnitSprite(Unit unit, String pathToSpriteSheet, int magnification, Canvas canvas) {

        super(pathToSpriteSheet, canvas, magnification, unit.getPosX(), unit.getPosY());
        this.unit = unit;

        if (!AdvancedWarsApplication.offlineTest && getFrames().length != 2 && getFrames()[0].length != 4) {
            System.out.println("Incorrect unit spriteSheet. It needs to be 2 wide and 4 high.");
        }
        subPixelMovementSteps = AdvancedWarsApplication.getInstance().getMovementSpeed();
        AdvancedWarsApplication.getInstance().addPropertyChangeListener("mSpeed", evt -> subPixelMovementSteps = AdvancedWarsApplication.getInstance().getMovementSpeed());
    }

    @Override
    public void draw() {
        int x = (int) (System.currentTimeMillis() % animationDurationMilliSec)
                < (animationDurationMilliSec / 2) ? 0 : 1;
        int y = 0;

        if (isMoving) {
            switch (lookingDirection) {
                case "R":
                    y = 2;
                    break;

                case "L":
                    y = 3;
                    break;

                default:
                    System.out.println("Incorrect LookingDirection");
            }
        } else {
            switch (lookingDirection) {
                case "R":
                    y = 0;
                    break;

                case "L":
                    y = 1;
                    break;

                default:
                    System.out.println("Incorrect LookingDirection");
            }
        }

        Image frame = getFrame(x, y);
        int posX = fieldPosX * SPRITE_SIZE * pixelMagnification + subX * pixelMagnification + xPixelShift;
        int posY = fieldPosY * SPRITE_SIZE * pixelMagnification + subY * pixelMagnification + yPixelShift;

        if (canvas != null) {
            canvas.getGraphicsContext2D().drawImage(frame, posX, posY);
        }

        if (movementPaths != null && movementPaths.size() > 0) {
            try {
                moveSprite(fieldPosX, fieldPosY, movementPaths.get(0).getNextField().getPosX(), movementPaths.get(0).getNextField().getPosY());
            } catch (NoSuchElementException e) {
                // Sometimes the first path is empty but not removed on its own
                // So far only rarely in Sandbox Mode
                movementPaths.remove(0);
            }
        }
    }

    private void moveSprite(int fromX, int fromY, int toX, int toY) {
        if (movementPaths.get(0).hasStepsLeft()) {
            isMoving = true;

            // Only non-diagonal movement allowed
            if (fromX != toX && fromY != toY) {
                System.out.println("You can't move diagonally");
                movementPaths.remove(0);
                return;
            }

            if (fromX != toX) {
                if (fromX < toX) {
                    // Move to the right
                    lookingDirection = "R";
                    subX += subPixelMovementSteps;
                    if (subX >= SPRITE_SIZE) {
                        subX = 0;
                        ++fieldPosX;
                        movementPaths.get(0).stepUp();
                        if (!movementPaths.get(0).hasStepsLeft()) {
                            movementPaths.remove(0);
                            isMoving = false;
                        }
                        System.out.println("Went right");
                    }
                } else {
                    // Move to the left
                    lookingDirection = "L";
                    subX -= subPixelMovementSteps;
                    if (subX <= -SPRITE_SIZE) {
                        subX = 0;
                        --fieldPosX;
                        movementPaths.get(0).stepUp();
                        if (!movementPaths.get(0).hasStepsLeft()) {
                            movementPaths.remove(0);
                            isMoving = false;
                        }
                        System.out.println("Went left from");
                    }
                }
            } else {
                if (fromY != toY) {
                    if (fromY < toY) {
                        // Move up
                        subY += subPixelMovementSteps;
                        if (subY >= SPRITE_SIZE) {
                            subY = 0;
                            ++fieldPosY;
                            movementPaths.get(0).stepUp();
                            if (!movementPaths.get(0).hasStepsLeft()) {
                                movementPaths.remove(0);
                                isMoving = false;
                            }
                            System.out.println("Went up");
                        }
                    } else {
                        // Move down
                        subY -= subPixelMovementSteps;
                        if (subY <= -SPRITE_SIZE) {
                            subY = 0;
                            --fieldPosY;
                            movementPaths.get(0).stepUp();
                            if (!movementPaths.get(0).hasStepsLeft()) {
                                movementPaths.remove(0);
                                isMoving = false;
                            }
                            System.out.println("Went down");
                        }
                    }
                } else {
                    System.out.println("You have to move somewhere." +
                            " Failed with: " + fromX + "|" + fromY + " to " + toX + "|" + toY);
                    movementPaths.get(0).stepUp();
                }
            }
        } else {
            if (movementPaths != null && movementPaths.size() > 0) {
                movementPaths.remove(0);
                isMoving = false;
            }
        }
    }

    // Sets movementPaths and returns true only if no path is being worked on currently
    public void addMovementPath(MovementPath path) {
        movementPaths.add(path);
    }

    public MovementPath getCurrentMovementPath() {
        if (movementPaths != null && movementPaths.size() > 0) {
            return movementPaths.get(0);
        }
        return null;
    }

    public int getSubPixelMovementSteps() {
        return subPixelMovementSteps;
    }

    //Useful for speeding up or slowing down the movement (bigger than 32 is instant, 0 freezes)
    public void setSubPixelMovementSteps(int subPixelMovementSteps) {
        if (subPixelMovementSteps >= 0) {
            this.subPixelMovementSteps = subPixelMovementSteps;
        }
    }

    public int getAnimationDurationMilliSec() {
        return animationDurationMilliSec;
    }

    public void setAnimationDurationMilliSec(int animationDurationMilliSec) {
        if (animationDurationMilliSec > 0) {
            this.animationDurationMilliSec = animationDurationMilliSec;
        }
    }

    public boolean isMoving() {
        return isMoving;
    }

    public Unit getUnit() {
        return unit;
    }
}
