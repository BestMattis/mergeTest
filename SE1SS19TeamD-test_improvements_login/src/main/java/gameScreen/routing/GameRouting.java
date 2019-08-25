package gameScreen.routing;

import gameController.gameLoop.sprites.MovementPath;
import model.Field;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A routing that describes paths from a specific game field with at maximum size.
 */
public class GameRouting {

    private Field start;
    private Map<Field, Integer> distances;
    private Map<Field, Field> ancestors;

    /**
     * Constructor for objects of this class.
     *
     * @param start     the field to calculate the paths
     * @param distances a map describing the minimal distance from start to the given field
     * @param ancestors a map describing the ancestor of every node to get a minimal path
     */
    public GameRouting(Field start, Map<Field, Integer> distances, Map<Field, Field> ancestors) {
        this.start = start;
        this.distances = distances;
        this.ancestors = ancestors;
    }

    /**
     * Return true if the given field is reachable.
     *
     * @param target the field to check
     * @return true if target is reachable in the given number of steps
     */
    public boolean isReachable(Field target) {
        return this.getPathLength(target) >= 0;
    }

    /**
     * Return true if the given field is reachable in the given number of steps.
     *
     * @param target the field to check
     * @param steps  the maximal number of steps to take
     * @return true if target is reachable in the given number of steps
     */
    public boolean isReachableIn(Field target, int steps) {
        return this.getPathLength(target) <= steps;
    }

    /**
     * Return the length of the path to the given field.
     *
     * @param target the field to calculate the path length
     * @return the length of the path to the field
     * or {@code -1} if there is no such path
     */
    public int getPathLength(Field target) {
        return this.distances.getOrDefault(target, -1);
    }

    /**
     * Return the fields reachable in the given number of steps
     *
     * @param steps the maximal number of steps to take
     * @return the fields reachable in the given number of steps
     */
    public List<Field> getReachableFields(int steps) {
        List<Field> result = this.distances.entrySet().stream()
                .filter(e -> e.getValue() <= steps).map(e -> e.getKey())
                .collect(Collectors.toList());
        return result;
    }

    /**
     * Return a path from start to target.
     *
     * @param target the field to use as target
     * @return a path from start to target
     */
    public MovementPath getPath(Field target) {
        LinkedList<Field> path = new LinkedList<>();
        Field current = this.ancestors.get(target);

        path.add(0, target);

        while (current != this.getStartField()) {
            path.add(0, current);
            current = this.ancestors.get(current);
        }

        path.add(0, this.getStartField());

        return new MovementPath().setPath(path);
    }

    /**
     * Return the start field
     *
     * @return the start field
     */
    public Field getStartField() {
        return start;
    }
}
