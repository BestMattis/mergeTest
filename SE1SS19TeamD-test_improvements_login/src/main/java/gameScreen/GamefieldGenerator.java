package gameScreen;

import model.Field;
import model.GameField;

import java.util.ArrayList;

public class GamefieldGenerator {

    /**
     * Generate a gamefield with neighborhood and size
     *
     * @param gameField the gameField where the fields shall be generated
     */
    static public void generateGameField(GameField gameField) {

        ArrayList<Field> fields = gameField.getFields();
        int x = 0;
        for (Field field : fields) {
            if (field.getPosX() > x) {
                x = field.getPosX() + 1;
            }
        }

        int y = 0;
        for (Field field : fields) {
            if (field.getPosX() > y) {
                y = field.getPosX() + 1;
            }
        }

        gameField.setSizeX(x);
        gameField.setSizeY(y);
        gameField.withFields(fields);
        for (Field field : fields) {
            int xpos = field.getPosX();
            int ypos = field.getPosY();
            for (Field field1 : fields) {
                if ((field1.getPosX() == xpos - 1) || (field1.getPosX() == xpos) || (field1.getPosX() == xpos + 1)) {
                    if ((field1.getPosY() == ypos - 1) || (field1.getPosY() == ypos) || (field1.getPosY() == ypos + 1))
                        if (field != field1) {
                            field.withNeighbour(field1);
                        }
                }
            }
        }

    }

    /**
     * Generate a random gamefield of given size
     *
     * @param x tile-count horizontally
     * @param y tile-count vertically
     * @return a gamefield
     */
    static public GameField randomField(int x, int y) {
        ArrayList<Field> fields = new ArrayList<>();
        String[] bios = {"Water", "Forest", "Grass", "Mountain"};
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                int rnd = (int) (Math.random() * ((100) + 1));
                if (rnd < 30) {
                    rnd = 2;
                } else if (rnd < 50) {
                    rnd = 1;
                } else if (rnd < 90) {
                    rnd = 0;
                } else {
                    rnd = 3;
                }
                String tmp = bios[rnd];
                Field field = new Field();
                field.setType(tmp).setPosX(i).setPosY(j);
                fields.add(field);
            }
        }
        GameField gameField = new GameField();
        gameField.withFields(fields);
        generateGameField(gameField);
        return gameField;
    }

}
