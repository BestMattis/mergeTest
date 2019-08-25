package gameController.gameLoop.sprites;

import model.Field;

import java.util.LinkedList;
import java.util.List;

public class MovementPath {

    private LinkedList<Field> fields;

    public MovementPath() {
        this.fields = new LinkedList<>();
    }

    public MovementPath setPath(List<Field> fields) {
        this.getFields().clear();
        this.getFields().addAll(fields);
        return this;
    }

    public MovementPath addPathStep(Field f) {
        this.getFields().add(f);
        return this;
    }

    public void stepUp() {
        getFields().removeFirst();
    }

    public Field getNextField() {
        return getFields().getFirst();
    }

    public Field getFieldAt(int i) {
        return getFields().get(i);
    }

    public boolean hasStepsLeft() {
        return getFields().size() > 0;
    }

    public LinkedList<Field> getFields() {
        return this.fields;
    }
}
