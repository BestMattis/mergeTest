package gameScreen.routing;

import model.Field;
import model.Model;
import model.Unit;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Builder class for {@link GameRouting} objects.
 */
public class GameRoutingBuilder {

    /**
     * Return a {@link GameRouting} object that contains all paths starting at the given field.
     *
     * @param start the field to use as start field
     * @return a {@link GameRouting} with all possible paths
     */
    public static GameRouting createGameRouting(Field start, Model model) {
        return GameRoutingBuilder.createGameRouting(start, Integer.MAX_VALUE, model);
    }

    /**
     * Return a {@link GameRouting} object that contains all paths starting at the given field
     * with a length of at most maxSteps.
     *
     * @param start    the field to use as start field
     * @param maxSteps the maximal number of steps
     * @return a {@link GameRouting} with all possible paths of length at most maxSteps
     */
    public static GameRouting createGameRouting(Field start, int maxSteps, Model model) {
        ArrayList<Field> fields = model.getApp().getCurrentPlayer().getGame().getGameField().getFields();
        Stream<Field> fieldStream = fields.stream().filter(f -> GameRoutingBuilder.isFieldPassable(f, model));
        return GameRoutingBuilder.performDijkstraSearch(start, fieldStream.collect(Collectors.toList()), maxSteps);
    }

    private static GameRouting performDijkstraSearch(Field start, List<Field> fields, int maxSteps) {
        Map<Field, Integer> distances = new HashMap<>();
        Map<Field, Field> ancestors = new HashMap<>();

        GameRoutingBuilder.init(fields, start, distances, ancestors);

        PriorityQueue<Field> queue = new PriorityQueue<>(fields.size(), (f1, f2) -> distances.get(f1).compareTo(distances.get(f2)));
        queue.addAll(fields);
        if (!queue.contains(start)) {
            queue.add(start);
        }
        while (!queue.isEmpty()) {
            Field current = queue.poll();
            if (distances.get(current) < maxSteps) {
                for (Field f : current.getNeighbour().stream().filter(e -> Math.abs(e.getPosX() - current.getPosX()) + Math.abs(e.getPosY() - current.getPosY()) == 1).collect(Collectors.toList())) {
                    if (queue.contains(f)) {
                        GameRoutingBuilder.updateDistance(current, f, distances, ancestors, queue);
                    }
                }
            }
        }
        GameRouting result = new GameRouting(start, distances, ancestors);
        return result;
    }

    private static void init(List<Field> fields, Field start, Map<Field, Integer> distances, Map<Field, Field> ancestors) {
        for (Field f : fields) {
            distances.put(f, Integer.MAX_VALUE);
            ancestors.put(f, null);
        }
        distances.put(start, 0);
    }

    private static void updateDistance(Field current, Field f, Map<Field, Integer> distances,
                                       Map<Field, Field> ancestors, PriorityQueue q) {
        int newDistance = distances.get(current) + 1;
        int actualDistance = distances.get(f);
        if (newDistance < actualDistance) {
            distances.put(f, newDistance);
            ancestors.put(f, current);
            q.remove(f);
            q.add(f);
        }
    }

    private static boolean isFieldPassable(Field f, Model model) {
        boolean fieldPassable = f.getIsPassable();
        Unit unit = f.getOccupiedBy();
        boolean noEnemyUnits = (unit == null || unit.getPlayer() == model.getApp().getCurrentPlayer());
        return fieldPassable && noEnemyUnits;
    }
}
