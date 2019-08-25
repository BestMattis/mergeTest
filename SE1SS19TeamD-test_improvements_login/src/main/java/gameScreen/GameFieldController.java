package gameScreen;

import gameController.gameLoop.GameLoop;
import gameController.gameLoop.sprites.UnitSprite;
import gameController.units.UnitController;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import main.AdvancedWarsApplication;
import model.*;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class GameFieldController {

    enum State {
        DEFAULT, FIELD_SELECTED, UNIT_SELECTED, ATTACK_SELECTED
    }


    private Game game;
    private GameField gameField;
    private Set<Field> fields;
    private Field lastClickedField;
    private Canvas canvas;
    private Canvas unitcanvas;
    private Canvas colorCanvas;
    private GraphicsContext gc;
    private ProgressBar loadingProgress;
    private GameLoop gameLoop;
    private ScrollPane gameFieldPane;
    private AnchorPane base;
    private Minimap minimap;
    private double progress;
    private double max;
    private final int YTOPDISTANCE = 80;
    private final int YBOTTOMDISTANCE = 80;
    private final int YTILE = 96;
    private final int XTILE = 96;
    private final int magnification = 3;
    private boolean toggle = false;
    private UnitController unitController;
    private HashMap<Unit, UnitSprite> unitToSprite;
    private GameUnitDisplayController unitDisplayController;
    private final AtomicReference<State> state;
    private Unit selectedAttacker;
    private Unit selectedDefender;

    private Image imgfail = new Image("/armyManager/placeholderunit.jpg");
    private Image grass = new Image("/textures/terrain/Grass.png", 32 * magnification, 32 * magnification, true, false);
    private Image mount = new Image("/textures/terrain/Mountain.png", 32 * magnification, 32 * magnification, true, false);
    private Image forest = new Image("/textures/terrain/Forest.png", 128 * magnification, 96 * magnification, true, false);
    private Image water = new Image("/textures/terrain/Water.png", 96 * magnification, 160 * magnification, true, false);

    private Model model;

    //The general functions to collect all the necessary informations to draw the tiles one by one

    /**
     * Constructor to create the gamefield, unit canvas with the size calculated and
     * calls collectTiles and loadTiles
     *
     * @param gametmp               the game to draw the gamefield for
     * @param unitDisplayController the unit display controller containing the unit cards
     */
    public GameFieldController(Game gametmp, ProgressBar loadingProgress, GameLoop gameLoop, GameUnitDisplayController unitDisplayController, ScrollPane gameFieldPane, Model model) {
        this.gameFieldPane = gameFieldPane;
        this.model = model;
        model.setGameFieldController(this);
        unitToSprite = new HashMap<>();
        this.loadingProgress = loadingProgress;
        game = gametmp;
        gameField = game.getGameField();
        canvas = calcSize(gameField.getSizeX(), gameField.getSizeY());
        unitcanvas = calcSize(gameField.getSizeX(), gameField.getSizeY());
        this.gameLoop = gameLoop;
        gameLoop.setSpriteCanvas(unitcanvas);
        colorCanvas = calcSize(gameField.getSizeX(), gameField.getSizeY());
        colorCanvas.setId("colorCanvas");
        gc = canvas.getGraphicsContext2D();
        this.unitDisplayController = unitDisplayController;
        state = new AtomicReference<>();
        state.set(State.DEFAULT);
        unitController = new UnitController(this.model);

        unitcanvas.setOnMouseClicked(event -> clickOnUnitCanvas(event.getX(), event.getY()));

        AdvancedWarsApplication.getInstance().addPropertyChangeListener("aiActive", t -> {
                    if (AdvancedWarsApplication.getInstance().isAiActive()) {
                        if (lastClickedField != null) {
                            reverseHighlightField(lastClickedField);
                            state.set(State.DEFAULT);
                        }
                    }
                });

        PropertyChangeListener listener = (evt -> {
            Unit oldValue = (Unit) evt.getOldValue();
            if (oldValue != null) {
                removeUnitfromTile(oldValue.getPosX(), oldValue.getPosY());
            }
            Unit newValue = (Unit) evt.getNewValue();
            if (newValue != null) {
                addUnitToTile(newValue);
            }
        });

        for (Field field : gameField.getFields()) {
            field.addPropertyChangeListener(Field.PROPERTY_occupiedBy, listener);
        }

        gameField.addPropertyChangeListener(GameField.PROPERTY_fields, evt -> {
            Field oldValue = (Field) evt.getOldValue();
            if (oldValue != null) {
                oldValue.removePropertyChangeListener(Field.PROPERTY_occupiedBy, listener);
            }
            Field newValue = (Field) evt.getNewValue();
            if (newValue != null) {
                newValue.addPropertyChangeListener(Field.PROPERTY_occupiedBy, listener);
            }
        });

        System.out.println(this.getClass().toString() + ": Listener on Fields set.");

        collectTiles();
        loadTiles();
    }

    /**
     * Handles a click on unitCanvas event.
     *
     * @param x coordinate
     * @param y coordinate
     */
    public void clickOnUnitCanvas(double x, double y) {
        if (!AdvancedWarsApplication.getInstance().isAiActive()) {
            Field field = getFieldByCoordinates(x, y);
            boolean isTurnPlayer = model.getApp().getCurrentPlayer() == model.getApp().getCurrentPlayer().getGame().getTurnPlayer();
            System.out.println(this.getClass().toString() + ": Clicked on position (" + getTrueXPos(x) + "," + getTrueYPos(y) + "). Found: " + field);
            if (field != null) {
                toggle();
                this.unitController.reverseHighlightLastReachableFields();
                if (lastClickedField != null) {
                    removeColorFromField(lastClickedField.getPosX() * XTILE, YTOPDISTANCE + (lastClickedField.getPosY() * YTILE));
                }
                /*
                 * DFA to evaluate click actions
                 */
                Unit unitOnField = field.getOccupiedBy();
                State currentState = state.get();
                if (currentState == State.DEFAULT) {
                    if (unitOnField != null) {
                        if (unitOnField.getPlayer() == model.getApp().getCurrentPlayer()) {
                            selectMyUnit(unitOnField);
                            state.set(State.UNIT_SELECTED);
                        } else {
                            unitController.reverseHighlightLastTarget();
                            highlightFieldwithColor(field, new Color(1, 0, 0, 0.5));
                            state.set(State.FIELD_SELECTED);
                        }
                    } else {
                        // Nothing selected, clicked on field -> highlight field
                        unitController.reverseHighlightLastTarget();
                        highlightField(field);
                        state.set(State.FIELD_SELECTED);

                    }
                } else if (currentState == State.FIELD_SELECTED) {
                    reverseHighlightField(lastClickedField);
                    if (field == lastClickedField) {
                        state.set(State.DEFAULT);
                        lastClickedField = null;
                        return;
                    }
                    if (unitOnField != null) {
                        if (unitOnField.getPlayer() == model.getApp().getCurrentPlayer()) {
                            selectMyUnit(unitOnField);
                            state.set(State.UNIT_SELECTED);
                        } else {
                            reverseHighlightField(lastClickedField);
                            highlightFieldwithColor(field, new Color(1, 0, 0, 0.5));
                            state.set(State.FIELD_SELECTED);
                        }
                    } else if (field == lastClickedField) {
                        // Field selected, clicked on same field -> unselect field
                        reverseHighlightField(lastClickedField);
                        state.set(State.DEFAULT);
                    } else {
                        // Field selected, clicked on other field -> unselect old and select new field
                        reverseHighlightField(lastClickedField);
                        highlightField(field);
                        state.set(State.FIELD_SELECTED);
                    }
                } else if (currentState == State.UNIT_SELECTED) {
                    if (lastClickedField.getOccupiedBy().getPlayer() == model.getApp().getCurrentPlayer()) {
                        //this.unitDisplayController.getUnitCardControllers().stream().filter(c -> c.getUnit() == lastClickedField.getOccupiedBy()).findAny().get().reverseHighlight();
                        this.unitDisplayController.reverseHighlight();
                        if (unitOnField != null && unitOnField.getPlayer() == model.getApp().getCurrentPlayer()) {
                            if (lastClickedField == field) {
                                this.unitController.reverseHighlightLastReachableFields();
                                this.unitController.reverseHighlightLastTarget();
                                state.set(State.DEFAULT);
                                lastClickedField = null;
                                return;
                            } else {
                                this.unitController.reverseHighlightLastReachableFields();
                                selectMyUnit(unitOnField);
                                state.set(State.UNIT_SELECTED);
                            }
                        } else if (unitOnField != null && unitOnField.getPlayer() != model.getApp().getCurrentPlayer()) {
                            // Unit selected, clicked on unit -> attack new unit
                            if (isTurnPlayer && !lastClickedField.getOccupiedBy().getHasAttacked()) {
                                selectedAttacker = lastClickedField.getOccupiedBy();
                                selectedDefender = unitOnField;
                                if (isRechable(selectedAttacker, selectedDefender)) {
                                    if (isAttackable(selectedAttacker, selectedDefender)) {
                                        highlightFieldwithColor(field, new Color(0, 1, 0, 0.5));
                                        Platform.runLater(() -> {
                                            showMessageToField(getTestAttackResult(selectedAttacker, selectedDefender), field.getPosX(), field.getPosY(), 0);
                                        });
                                        if (selectedAttacker.getOccupiesField().getNeighbour().contains(selectedDefender.getOccupiesField())) {
                                            state.set(State.ATTACK_SELECTED);
                                            lastClickedField = field;
                                            return;
                                        }
                                    } else {
                                        highlightFieldwithColor(field, new Color(1, 0, 0, 0.5));
                                        showMessageToField(selectedAttacker.getType() + " can't attack " + selectedDefender.getType() + "!", field.getPosX(), field.getPosY(), 5000);
                                    }
                                } else {
                                    highlightFieldwithColor(field, new Color(1, 0, 0, 0.5));
                                    showMessageToField("Unreachable!", field.getPosX(), field.getPosY(), 5000);
                                }
                            } else {
                                reverseHighlightField(lastClickedField);
                                highlightFieldwithColor(field, new Color(1, 0, 0, 0.5));
                                state.set(State.FIELD_SELECTED);
                            }
                        } else if (field != lastClickedField && field.getOccupiedBy() == null) {
                            // Unit selected, clicked on field -> move unit to field
                            if (isTurnPlayer) {
                                Unit unitToMove = lastClickedField.getOccupiedBy();
                                this.unitController.moveUnit(
                                        unitToMove, unitToSprite.get(unitToMove), field);
                            } else {
                                // Field selected, clicked on other field -> unselect old and select new field
                                reverseHighlightField(lastClickedField);
                                highlightField(field);
                                state.set(State.FIELD_SELECTED);
                            }
                        }
                    }
                    state.set(State.DEFAULT);
                } else if (currentState == State.ATTACK_SELECTED) {
                    this.unitDisplayController.reverseHighlight();
                    if (lastClickedField == field) {
                        unitController.attackUnit(selectedAttacker, selectedDefender);
                        selectedAttacker.setHasAttacked(true);
                        lastClickedField = null;
                    }
                    state.set(State.DEFAULT);
                }
                lastClickedField = field;
            }
        }
    }

    /**
     * reverses the highlight of prev. unit and sets highlight to the current selected unit.
     *
     * @param unitOnField the unit that should be selected.
     */
    private void selectMyUnit(Unit unitOnField) {
        this.unitDisplayController.reverseHighlight();
        // Field selected, clicked on unit -> highlight reachable fields
        //this.unitDisplayController.getUnitCardControllers().stream().filter(c -> c.getUnit() == unitOnField).findAny().get().highlight();
        for (int i = 0; i < this.unitDisplayController.getUnitCardControllers().size(); ++i) {
            if (this.unitDisplayController.getUnitCardControllers().get(i).getUnit() == unitOnField) {
                this.unitDisplayController.getUnitCardControllers().get(i).highlight();
                break;
            }
        }
        this.unitController.reverseHighlightLastTarget();
        this.unitController.highlightReachableFields(unitOnField);
    }

    /**
     * Shows a message at the given position for a given amount of time
     *
     * @param s        String to show
     * @param x        coordinate
     * @param y        coordinate
     * @param duration time in ms
     */
    private void showMessageToField(String s, int x, int y, long duration) {
        int xpos = x * XTILE;
        int ypos = YTOPDISTANCE + (y * YTILE);
        Label message = new Label();
        message.setText(s);

        message.setStyle("-fx-background-color: rgba(56,53,58,0.75)");
        message.setLayoutX(xpos);
        message.setLayoutY(ypos);

        base.getChildren().addAll(message);
        removeLastMessage(message, duration);
    }

    /**
     * Removes the message in the given time
     *
     * @param lastMessage to remove
     * @param duration    time to wait in ms
     */
    private void removeLastMessage(Label lastMessage, long duration) {
        if (lastMessage != null) {
            Runnable removeMessageInTime = () -> {
                boolean myToggle = toggle();
                try {
                    if (duration == 0) {
                        while (myToggle == toggle) {
                            Thread.sleep(10);
                        }
                    } else {
                        for (int i = 0; i < duration / 10; i++) {
                            if (myToggle != toggle) {
                                break;
                            }
                            Thread.sleep(10);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Platform.runLater(() -> base.getChildren().remove(lastMessage));
                }
            };
            Executors.newSingleThreadExecutor().execute(removeMessageInTime);
        }
    }

    public void addAllUnits() {
        for (Unit unit : model.getApp().getCurrentPlayer().getGame().getAllUnits()) {
            addUnitToTile(unit);
        }
    }

    /**
     * used to set toggle to !toggle
     *
     * @return the new toggle value
     */
    private boolean toggle() {
        if (toggle) {
            toggle = false;
        } else {
            toggle = true;
        }
        return toggle;
    }

    /**
     * get all the tiles form the game
     */
    public void collectTiles() {
        fields = new HashSet<>();
        fields.addAll(gameField.getFields());
    }

    /**
     * Generate a canvas with the correct size for the gamefield
     *
     * @param x tile-count horizontally
     * @param y tile-count vertically
     * @return a canvas with the size for the gamefield
     */
    public Canvas calcSize(int x, int y) {
        int xpx = x * XTILE;
        int ypx = y * YTILE;
        ypx += YTOPDISTANCE;
        ypx += YBOTTOMDISTANCE;
        return new Canvas(xpx, ypx);
    }

    // Iterate over the tiles of the field and call the methods to draw them

    /**
     * Iterate over all fields to draw them one by one
     * by calling drawTile
     */
    public void loadTiles() {
        progress = 0;
        max = fields.size();
        for (Field field : fields) {
            int i = field.getPosX();
            int j = field.getPosY();
            int xpos = i * XTILE;
            int ypos = YTOPDISTANCE + (j * YTILE);
            drawTile(xpos, ypos, field);
            progress++;
            if (loadingProgress != null) {
                loadingProgress.setProgress(progress / max);
            }
        }
    }

    /**
     * Loads the pictures the texture is taken from
     * checks the type of the tile and:
     * draws mountain and grass or calls further methods for water( testWater)
     * and draws forest with the Image returned by testForest
     *
     * @param x     tileposition horizontally in pixels
     * @param y     tileposition vertically in pixels
     * @param field the tile to draw
     */
    public void drawTile(int x, int y, Field field) {
        WritableImage wimg = new WritableImage(XTILE, YTILE);

        gc.setFill(Color.BLUE);
        gc.fillRect(x, y, XTILE, YTILE);
        if (field.getType().equals("bla")) {
            gc.setFill(Color.RED);
            gc.drawImage((Image) wimg, x, y, XTILE, YTILE);
        } else if (field.getType().equals("blubb")) {
            gc.setFill(Color.BLUE);
            gc.fillRect(x, y, XTILE, YTILE);
        } else if (field.getType().equals("Grass")) {
            gc.drawImage(grass, x, y, XTILE, YTILE);
        } else if (field.getType().equals("Mountain")) {
            gc.drawImage(mount, x, y, XTILE, YTILE);
        } else if (field.getType().equals("Water")) {
            testWater(field, water, wimg, x, y);
        } else if (field.getType().equals("Forest")) {
            gc.drawImage(testForest(field, forest, wimg), x, y, XTILE, YTILE);
        }
    }

    //The methods to draw a tile of water

    /**
     * The method to call the methods to check the surrounding tiles( readsurround) for water, determine the texture and rotation( precheck)
     * to load the imagepart( getImageWater), and draw the texture( drawRotated)
     *
     * @param field the field to draw
     * @param all   the Image of the water-tetures
     * @param wimg  a writableimage to cut the used part of all
     * @param x     tileposition in pixels
     * @param y     tileposition in pixels
     * @return a Image of the tile that was drawn
     */
    public Image testWater(Field field, Image all, WritableImage wimg, int x, int y) {
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
        System.out.println("Tileinfo: " + tileinfo[0] + ":" + tileinfo[1] + " for: " + x / 96 + ":" + y / 96);
        Image itd = imgfail;
        if (tileinfo[0] != 0) {
            itd = getImageWater(tileinfo[0], all, wimg);
        }

        drawRotated(tileinfo[1], tileinfo[0], itd, x, y);

        return null;
    }

    /**
     * Get the Texture needed from the Water-Image
     *
     * @param j    the number of the necessary texture
     * @param all  image of all textures( numbered from top left(1) to bottom right(15))
     * @param wimg writableimage to cut the needed imagepart
     * @return
     */
    public Image getImageWater(int j, Image all, WritableImage wimg) {
        int i = j - 1;
        int y = ((i / 3)) * YTILE;
        int x = ((i % 3)) * XTILE;
        wimg.getPixelWriter().setPixels(0, 0, XTILE, YTILE, all.getPixelReader(), x, y);
        return (Image) wimg;
    }

    /**
     * Draws a given Image with the dimensions 96*96 to the given position with a texturspecific( because I messed the readsurround up) rotation
     *
     * @param i      the common angle to rotate(i * 90)
     * @param sonder the number of the texture to draw( to identify the texture)
     * @param image  the texture to draw
     * @param x      horizontal coordinate
     * @param y      vertical coordinate
     */
    public void drawRotated(int i, int sonder, Image image, int x, int y) {
        if (sonder == 2) {
            i += 2;
        }
        if (sonder == 4 || sonder == 7 || sonder == 13 || sonder == 14) {
            i -= 1;
        }
        if (sonder == 15) {
            i += 1;
        }

        i += 1;
        gc.save();
        gc.translate(x, y);
        gc.translate((XTILE / 2), (YTILE / 2));
        gc.rotate((i) * (90));
        gc.drawImage(image, -(XTILE / 2), -(YTILE / 2), XTILE, YTILE);
        gc.restore();
    }

    //The methods to draw a Forest-tile

    /**
     * The method to call the methods to check the surrounding tiles( readsurround) for forest, determine the texture and rotation( precheck)
     * and map the rotation and texturenumber to the corresponding texturenumber for the foresttextures
     * and load the imagepart( getImageFor)
     *
     * @param field the field to draw
     * @param all   the Image of the water-textures
     * @param wimg  a writableimage to cut the used part of all
     * @return a Image of the tile to draw
     */
    public Image testForest(Field field, Image all, WritableImage wimg) {

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
        if (tile == 1) {
            if (rot == 0) {
                ret = getImageFor(3, all, wimg);
            } else if (rot == 1) {
                ret = getImageFor(5, all, wimg);
            }
        } else if (tile == 2) {
            if (rot == 0) {
                ret = getImageFor(4, all, wimg);
            } else if (rot == 1) {
                ret = getImageFor(9, all, wimg);
            } else if (rot == 2) {
                ret = getImageFor(2, all, wimg);
            } else if (rot == 3) {
                ret = getImageFor(1, all, wimg);
            }
        } else if (tile == 3) {
            if (rot == 0) {
                ret = getImageFor(6, all, wimg);
            } else if (rot == 3) {
                ret = getImageFor(7, all, wimg);
            }
        } else if (tile == 5) {
            ret = getImageFor(10, all, wimg);
        }

        if (ret == null) {
            ret = getImageFor(8, all, wimg);
        }
        return ret;
    }

    /**
     * Get the Texture needed from the forest-Image
     *
     * @param j    the number of the necessary texture
     * @param all  image of all textures(numbered from top left(1) to bottom right(10))
     * @param wimg writableimage to cut the needed imagepart
     * @return
     */
    public Image getImageFor(int j, Image all, WritableImage wimg) {
        int i = j - 1;
        int y = ((i / 4)) * YTILE;
        int x = ((i % 4)) * XTILE;
        wimg.getPixelWriter().setPixels(0, 0, XTILE, YTILE, all.getPixelReader(), x, y);
        return (Image) wimg;
    }

    //The methods to select the water/forest tile to draw and calculate the rotation

    /**
     * This method first checks if the tile has to be a typ which hasn't to be rotated.
     * If not it checks further by calling checkTile and rotate90
     * as long as no texture and rotation is found, but max 6 times.
     * If a config is found the numbers are returned, else 0 is returned.
     *
     * @param s the boolean-Array if the tiles surrounding the tile to draw are the same type
     * @return an integer-Array, the first is the texturenumber, the second is how often to rotate 90 degrees
     */
    public int[] precheck(boolean[] s) {
        int[] ret = new int[2];
        ret[0] = 0;
        ret[1] = 0;

        if (!s[0] && !s[2] && !s[4] && !s[6]) {
            ret[0] = 5;
        } else if (s[0] && s[1] && s[2] && s[3] && s[4] && s[5] && s[6] && s[7]) {
            ret[0] = 12;
        } else if (s[0] && !s[1] && s[2] && !s[3] && s[4] && !s[5] && s[6] && !s[7]) {
            ret[0] = 6;
        } else {
            L:
            for (int i = 0; i < 6; i++) {
                int tmp = checkTile(s);
                if (tmp != 0) {
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
     *
     * @param s the (maybe rotated) boolean value if the surrounding tiles are the same type as the one to draw
     * @return 0 if no texture is found, else the number of the texture
     */
    public int checkTile(boolean[] s) {
        int t = 0;
        if (s[0] && !s[2] && s[4] && !s[6]) {
            t = 1;
        } else if (s[0] && !s[2] && !s[4] && !s[6]) {
            t = 2;
        } else if (s[0] && !s[1] && s[2] && !s[3] && s[4] && !s[6]) {
            t = 3;
        } else if (!s[0] && s[2] && !s[3] && s[4] && !s[6]) {
            t = 4;
        } else if (!s[0] && s[2] && s[3] && s[4] && !s[6]) {
            t = 7;
        } else if (s[0] && !s[2] && s[4] && s[5] && s[6] && s[7]) {
            t = 8;
        } else if (s[0] && !s[1] && s[2] && !s[3] && s[4] && s[5] && s[6] && s[7]) {
            t = 9;
        } else if (s[0] && s[5] && !s[2] && s[4] && s[6] && !s[7]) {
            t = 10;
        } else if (s[0] && !s[2] && s[4] && s[6] && s[7] && !s[5]) {
            t = 11;
        } else if (s[0] && s[1] && s[2] && s[3] && s[4] && s[5] && s[6] && !s[7]) {
            t = 13;
        } else if (s[0] && s[1] && s[2] && !s[3] && s[4] && s[5] && s[6] && !s[7]) {
            t = 14;
        } else if (s[0] && !s[1] && s[2] && !s[3] && s[4] && s[5] && s[6] && !s[7]) {
            t = 15;
        }
        return t;
    }

    /**
     * This method rotates the boolean-array of the surrounding tiles every time called another 90 degrees
     * so the tile to draw is virtually rotated -90 degrees
     *
     * @param s the boolean values if the surrounding tiles are the same type as the tile to draw
     * @return the rotated array
     */
    public boolean[] rotate90(boolean[] s) {
        boolean[] tmp = new boolean[8];
        tmp = s.clone();
        for (int i = 0; i < 8; i++) {
            s[i] = tmp[(i - 2 + 8) % 8];
        }
        return s;
    }

    /**
     * This method checks the neighbour-tiles of the tile to draw( middle) and writes true into the array
     * if they are the correct type( water or forest, which is choosen by the variable "water")
     * and otherwise false.
     * ( this is the part I initially messed up, because the order of the checking is anitclockwise instead
     * of clockwise)
     *
     * @param s      the boolean-array to save if the surrounding tiles are the same type as the tile to draw
     * @param middle the tile to draw
     * @param water  to choose to whether check for water or forest
     * @return the surrounding boolean-array
     */
    public boolean[] readsurround(boolean[] s, Field middle, boolean water) {
        for (Field field : middle.getNeighbour()) {
            if ((field.getType().equals("Water") && water) || (field.getType().equals("Forest") && !water)) {
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
     *
     * @param unit the unit that shall be drawn
     */
    public void addUnitToTile(Unit unit) {
        if (!unitToSprite.containsKey(unit)) {
            String type = unit.getType();
            String color = unit.getPlayer().getColor();
            String colorPath = color.toLowerCase().substring(1);
            colorPath = color.charAt(0) + colorPath;
            String path = "./src/main/resources/textures/units/" + colorPath + "/" + color + "_" + type + ".png";

            UnitSprite unitSprite = new UnitSprite(unit, path, magnification, unitcanvas);
            unitSprite.setyPixelShift(YTOPDISTANCE);

            unitToSprite.put(unit, unitSprite);
            unit.addPropertyChangeListener(Unit.PROPERTY_hp, evt -> removeUnitWhenDead(unitSprite, unit));

            gameLoop.addSprite(unitSprite);
        }
    }

    /**
     * Removes a UnitSprite and the according Unit from the gameLoop and the model.
     *
     * @param unitSprite to remove
     * @param unit       to remove
     */
    private void removeUnitWhenDead(UnitSprite unitSprite, Unit unit) {
        if (unit.getHp() < 1) {
            gameLoop.removeSprite(unitSprite);
            if (minimap != null) {
                System.out.println("Delete Unit from Minimap!");
                minimap.deleteUnitFromMinimap(unit);
            }
            System.out.println("Delete Unit from Game!");
            unit.removeYou();
        }
    }

    /**
     * The method to clear a given tile
     *
     * @param x the tile-position horizontally
     * @param y the tile-position vertically
     */
    public void removeUnitfromTile(int x, int y) {
        int xpos = x * XTILE;
        int ypos = YTOPDISTANCE + (y * YTILE);
        unitcanvas.getGraphicsContext2D().clearRect(xpos, ypos, XTILE, YTILE);
    }

    /**
     * Highlights a given field.
     *
     * @param field field to highlight
     */
    public void highlightField(Field field) {
        addColorToField(field.getPosX() * XTILE, YTOPDISTANCE + (field.getPosY() * YTILE), new Color(1, 1, 1, 0.5));
    }

    /**
     * Highlights a given field.
     *
     * @param field field to highlight
     */
    public void highlightFieldwithColor(Field field, Color color) {
        addColorToField(field.getPosX() * XTILE, YTOPDISTANCE + (field.getPosY() * YTILE), color);
    }

    /**
     * Removes the highlight from a given field
     *
     * @param field field to remove highlight
     */
    public void reverseHighlightField(Field field) {
        removeColorFromField(field.getPosX() * XTILE, YTOPDISTANCE + (field.getPosY() * YTILE));
    }

    /**
     * Simulates a attack and gives back the results
     *
     * @param myLastSelectedUnit that attacks
     * @param enemy              that will defend
     * @return String that shows the old hp and the resulting hp
     */
    private String getTestAttackResult(Unit myLastSelectedUnit, Unit enemy) {
        String result = enemy.getHp() + "HP -> ";

        UnitController unitController = new UnitController(true, model);
        Field fieldAttack = new Field().setType(myLastSelectedUnit.getOccupiesField().getType());
        Field fieldDefender = new Field().setType(enemy.getOccupiesField().getType());
        fieldAttack.withNeighbour(fieldDefender);

        Unit attacker = new Unit().setType(myLastSelectedUnit.getType()).setCanAttack(myLastSelectedUnit.getCanAttack()).setHp(myLastSelectedUnit.getHp());
        Unit defender = new Unit().setType(enemy.getType()).setCanAttack(enemy.getCanAttack()).setHp(enemy.getHp());

        attacker.setOccupiesField(fieldAttack);
        defender.setOccupiesField(fieldDefender);

        unitController.attackUnit(attacker, defender);

        return result + defender.getHp() + "HP";
    }

    /**
     * Checks whether a unit is in striking distance or not
     *
     * @param attacker attacking unit
     * @param defender unit that will be attacked
     * @return true if unit is in striking distance. false if not
     */
    public boolean isRechable(Unit attacker, Unit defender) {
        UnitController unitController = new UnitController(true, model);
        for (Field field : unitController.getAttackableFields(attacker)) {
            if (field.getOccupiedBy() == defender) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a unit can attack the other unit.
     *
     * @param attacker attacking unit
     * @param defender unit that will be attacked
     * @return true if attacker is able to attack the defender.
     */
    public boolean isAttackable(Unit attacker, Unit defender) {
        if (attacker.getCanAttack().contains(defender.getType())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Clear the complete unit canvas
     */
    public void refreshUnits() {
        unitcanvas.getGraphicsContext2D().clearRect(0, 0, unitcanvas.getWidth(), unitcanvas.getHeight());
    }

    /**
     * Draws a given color to the given coordinates on the colorCanvas.
     *
     * @param x     coordinate
     * @param y     coordinate
     * @param color color to set
     */
    public void addColorToField(int x, int y, Color color) {
        GraphicsContext ctx = colorCanvas.getGraphicsContext2D();
        ctx.setFill(color);
        ctx.fillRect(x, y, XTILE, YTILE);
    }

    /**
     * Removes a color on the given coordinates from the colorCanvas
     *
     * @param x coordinate
     * @param y coordinate
     */
    public void removeColorFromField(int x, int y) {
        colorCanvas.getGraphicsContext2D().clearRect(x, y, XTILE, YTILE);
    }

    //Getter

    /**
     * Get only the gamefield
     *
     * @return the canvas of the gamefield
     */
    public Canvas getCanvas() {
        return canvas;
    }

    /**
     * Get only the canvas which contains the units
     *
     * @return the unitcanvas
     */
    public Canvas getUnitcanvas() {
        return unitcanvas;
    }

    /**
     * Get an anchorpane which combines the gamefieldcanvas and the unitcanvas
     *
     * @return an anchorpane
     */
    public AnchorPane getGamefield() {
        if (base == null) {
            base = new AnchorPane();
            base.getChildren().addAll(canvas, colorCanvas, unitcanvas);
        }
        return base;
    }

    /**
     * Get the Field corresponding to the x and y coordinates
     *
     * @param x actual x position
     * @param y actual y position
     * @return Filed that has been clicked
     */
    public Field getFieldByCoordinates(double x, double y) {
        int xPos = getTrueXPos(x);
        int yPos = getTrueYPos(y);
        for (Field field : model.getApp().getCurrentPlayer().getGame().getGameField().getFields()) {
            if (field.getPosX() == xPos && field.getPosY() == yPos) {
                return field;
            }
        }
        return null;
    }

    /**
     * Get the true x position for the field
     *
     * @param x actual x position
     * @return x position to locate the field
     */
    public int getTrueXPos(double x) {
        int xPos = (int) (x / ((double) XTILE));
        return xPos;
    }

    /**
     * Get the true y position for the field
     *
     * @param y actual y position
     * @return y position to locate the field
     */
    public int getTrueYPos(double y) {
        int yPos = (int) ((y - YTOPDISTANCE) / ((double) YTILE));
        return yPos;
    }

    public int getYTILE() {
        return YTILE;
    }

    public int getXTILE() {
        return XTILE;
    }

    public int getMagnification() {
        return magnification;
    }

    public ScrollPane getGameFieldPane() {
        return gameFieldPane;
    }

    public void setMinimap(Minimap minimap) {
        this.minimap = minimap;
    }

    public Field getLastClickedField() {
        return lastClickedField;
    }
}

