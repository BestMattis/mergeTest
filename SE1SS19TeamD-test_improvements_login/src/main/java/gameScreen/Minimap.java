package gameScreen;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import main.AdvancedWarsApplication;
import model.*;

import java.util.ArrayList;

public class Minimap {

    @FXML
    VBox mapBox;
    @FXML
    Pane drawPane;
    @FXML
    ScrollPane gameFieldPane;
    Shape positionBox;
    private Model model;
    private Game game;
    private GameField gameField;
    private GameFieldController gameFieldController;
    private int rootSize;
    private int blockSize;
    private double positionBoxSizeX;
    private double positionBoxSizeY;
    private ArrayList<MinimapUnit> minimapUnits;
    private boolean isCreated = false;

    /**
     * Minimap Constructor for initialization
     *
     * @param game   the current game
     * @param mapBox the VBox of the mappane
     */
    public Minimap(Game game, Node mapBox, Node gameFieldPane, GameFieldController gameFieldController, Model model) {
        this.model = model;
        model.getGameFieldController().setMinimap(this);
        this.gameFieldPane = (ScrollPane) gameFieldPane;
        this.game = game;
        gameField = game.getGameField();
        this.mapBox = (VBox) mapBox;
        this.drawPane = (Pane) this.mapBox.getChildren().get(0);
        this.gameFieldController = gameFieldController;
        this.drawPane.addEventFilter(MouseEvent.MOUSE_PRESSED, me -> {
            double x = me.getX();
            double y = me.getY();
            moveGameField(x, y, this.gameFieldPane);
            movePositionBox(x, y);
        });
        this.drawPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, me -> {
            double x = me.getX();
            double y = me.getY();
            moveGameField(x, y, this.gameFieldPane);
            movePositionBox(x, y);
        });

        this.gameFieldPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, me -> {
            double hvalue = this.gameFieldPane.getHvalue();
            double vvalue = this.gameFieldPane.getVvalue();

            movePositionBox(hvalue * 258, vvalue * 258);
        });

        minimapUnits = new ArrayList<>();
    }

    public void moveGameField(double x, double y, ScrollPane gameFieldPane) {
        gameFieldPane.setHvalue((gameFieldPane.getHmax() / 258) * x);
        gameFieldPane.setVvalue((gameFieldPane.getVmax() / 258) * y);
    }

    /**
     * method to create the minimap without units
     */
    public void createMinimap() {
        if (gameField.getFields().size() == 1024) {
            rootSize = 32;
            blockSize = 8;
        } else { //if size == 64^2 == 4096
            rootSize = 64;
            blockSize = 4;
        }
        int magnification = gameFieldController.getMagnification();
        if (AdvancedWarsApplication.getInstance().getGameScreenCon().base.getScene() != null) {
            positionBoxSizeX = blockSize *
                    (AdvancedWarsApplication.getInstance().getGameScreenCon().base.getScene().getWidth() / (rootSize * magnification));

            positionBoxSizeY = blockSize *
                    (AdvancedWarsApplication.getInstance().getGameScreenCon().base.getScene().getHeight() / (rootSize * magnification));
        } else {
            //for offline test
            positionBoxSizeX = (blockSize * 1920) / (rootSize * magnification);
            positionBoxSizeY = (blockSize * 1080) / (rootSize * magnification);
        }

        for (int i = 0; i < rootSize; ++i) {
            for (int j = 0; j < rootSize; ++j) {
                Field currentField = gameField.getFields().get(i * rootSize + j);
                Rectangle rectangle = new Rectangle(blockSize, blockSize);
                if (!currentField.getType().equals("Grass")) {
                    if (currentField.getType().equals("Water")) {
                        rectangle.setFill(Color.rgb(11, 89, 139));
                    } else if (currentField.getType().equals("Forest")) {
                        rectangle.setFill(Color.rgb(38, 106, 0));
                    } else { //if currentField equals Mountain
                        rectangle.setFill(Color.rgb(95, 111, 54));
                    }
                    rectangle.relocate((i * blockSize) + 1, (j * blockSize) + 1);
                    drawPane.getChildren().add(rectangle);
                }
            }
        }
        Rectangle rect = new Rectangle(positionBoxSizeX, positionBoxSizeY);
        Rectangle clip = new Rectangle(1, 1, positionBoxSizeX - 2, positionBoxSizeY - 2);
        positionBox = Shape.subtract(rect, clip);
        positionBox.setFill(Color.WHITE);
        drawPane.getChildren().add(positionBox);
        isCreated = true;
    }

    /**
     * method which sets and moves a box, which shows the current position on the map
     */
    public void movePositionBox(double x, double y) {
        double myX = x - positionBoxSizeX / 2;
        double myY = y - positionBoxSizeY / 2;
        if (myX > drawPane.getWidth() - positionBoxSizeX) {
            myX = drawPane.getWidth() - positionBoxSizeX;
        } else if (x < (positionBoxSizeX / 2)) {
            myX = 0;
        }
        if (myY > drawPane.getHeight() - positionBoxSizeY) {
            myY = drawPane.getHeight() - positionBoxSizeY;
        } else if (myY < 0) {
            myY = 0;
        }
        positionBox.relocate(myX, myY);
    }

    /**
     * add all Units to the drawPane and minimapUnits ArrayList
     * add Listeners to position attributes of Units for movement on Minimap
     */
    public void addUnitsToMinimap() {

        for (Unit unit : model.getApp().getCurrentPlayer().getGame().getAllUnits()) {
            Player player = unit.getPlayer();
            Color color;
            if (player.getColor().equals("RED")) {
                color = Color.RED;
            } else if (player.getColor().equals("BLUE")) {
                color = Color.BLUE;
            } else if (player.getColor().equals("YELLOW")) {
                color = Color.YELLOW;
            } else { //if(player.getColor().equals("GREEN")){
                color = Color.GREEN;
            }

            MinimapUnit minimapUnit = new MinimapUnit(blockSize, blockSize);
            minimapUnit.setMyPlayer(player);
            minimapUnit.setColor(color);
            minimapUnit.setMyUnit(unit);
            minimapUnit.setPosXY((unit.getPosX() * blockSize) + 1, (unit.getPosY() * blockSize) + 1);
            minimapUnits.add(minimapUnit);
            drawPane.getChildren().add(minimapUnit);
        }
        System.out.println("allUnitsToMinimap");
    }

    /**
     * method to add a single unit to the minimap
     *
     * @param unit the unit that should be added
     */
    public void addSingleUnitToMinimap(Unit unit) {
        Player player = unit.getPlayer();
        Color color;
        if (player.getColor().equals("RED")) {
            color = Color.RED;
        } else if (player.getColor().equals("BLUE")) {
            color = Color.BLUE;
        } else if (player.getColor().equals("YELLOW")) {
            color = Color.YELLOW;
        } else { //if(player.getColor().equals("GREEN")){
            color = Color.GREEN;
        }

        MinimapUnit minimapUnit = new MinimapUnit(blockSize, blockSize);
        minimapUnit.setMyPlayer(player);
        minimapUnit.setColor(color);
        minimapUnit.setMyUnit(unit);
        minimapUnit.setPosXY((unit.getPosX() * blockSize) + 1, (unit.getPosY() * blockSize) + 1);
        minimapUnits.add(minimapUnit);
        drawPane.getChildren().add(minimapUnit);
        System.out.println("SingleUnitToMinimap");
    }

    /**
     * move unit on minimap functionality with a call instead of listener
     *
     * @param unit the unit that was moved on the gameField
     */
    public void moveUnitOnMinimap(Unit unit, Field target) {
        for (int k = 0; k < minimapUnits.size(); ++k) {
            if (minimapUnits.get(k).getMyUnit().equals(unit)) {
                minimapUnits.get(k).setPosXY((target.getPosX() * blockSize) + 1, (target.getPosY() * blockSize) + 1);
                break;
            }
        }
    }

    /**
     * delete unit from the minimap when it is destroyed in the game
     *
     * @param unit the unit that will be removed from the minimap
     */
    public void deleteUnitFromMinimap(Unit unit) {
        for (int k = 0; k < minimapUnits.size(); ++k) {
            if (minimapUnits.get(k).getMyUnit().equals(unit)) {
                drawPane.getChildren().remove(minimapUnits.get(k));
                minimapUnits.remove(minimapUnits.get(k));
                break;
            }
        }
    }

    public boolean getIsCreated() {
        return isCreated;
    }

    /**
     * method to test if units can be added to minimap (offline)
     * MIGHT CHANGE/DELETE LATER!
     */
    public void addUnitsTestMethod() {
        minimapUnits = new ArrayList<>();
        System.out.println("JOINED PLAYERS: " + game.getJoinedPlayers());
        for (int i = 0; i < game.getJoinedPlayers(); ++i) {
            Player player = game.getPlayers().get(i);
            System.out.println("PLAYER: " + player);
            Color color;
            if (i == 0) {
                color = Color.RED;
            } else if (i == 1) {
                color = Color.BLUE;
            } else if (i == 2) {
                color = Color.YELLOW;
            } else {
                color = Color.GREEN;
            }
            System.out.println("COLOR: " + color);

            for (int j = 0; j < 10; ++j) {
                MinimapUnit minimapUnit = new MinimapUnit(blockSize, blockSize);
                minimapUnit.setMyPlayer(player);
                minimapUnit.setColor(color);
                int id = i * 10 + j;
                Unit unit = new Unit().setPosX(i).setPosY(j).setId(String.valueOf(id));
                minimapUnit.setMyUnit(unit);
                minimapUnit.setPosXY((i * blockSize) + 1, (j * blockSize) + 1);
                minimapUnits.add(minimapUnit);
                drawPane.getChildren().add(minimapUnit);
                System.out.println("MINIMAP UNIT: " + minimapUnit);
            }
        }
    }
}
