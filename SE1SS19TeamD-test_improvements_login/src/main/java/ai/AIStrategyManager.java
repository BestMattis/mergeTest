package ai;

import model.Game;

import java.util.HashSet;
import java.util.Set;

/**
 * A manager for {@link AIStrategy} objects.
 */
public class AIStrategyManager {

    private Set<AIStrategy> strategies;

    /**
     * Add the given strategy to this manager.
     *
     * @param strategy the strategy to add
     */
    public void add(AIStrategy strategy) {
        if (this.strategies == null) {
            this.strategies = new HashSet<>();
        }

        this.strategies.add(strategy);
    }

    /**
     * Apply all strategies to the given game situation and find the best move.
     *
     * @param game the game situation
     * @return the best move as an {@link AICommand}
     */
    public AICommand applyStrategies(Game game) {
        AIStrategy s = strategies.stream().max((t, o) -> Integer.compare(t.getEstimation(game), o.getEstimation(game))).get();

        AICommand result = s.findBestMove(game);
        return result;
    }
}
