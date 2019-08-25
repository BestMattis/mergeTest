package ai;

import model.Game;

/**
 * Defines an interface for a strategies that evaluates a given game situation
 * and generates an {@link AICommand} with the "best" move the strategy has found.
 */
public interface AIStrategy {

    /**
     * Return the estimation for the given game situation.
     * The estimation describes
     * how much the given strategy can reach in the given situation.
     *
     * @param game the current situation
     * @return the estimation
     */
    public int getEstimation(Game game);

    /**
     * Find the best move in the given situation.
     *
     * @param game the current situation
     * @return the best move in the given situation
     */
    public AICommand findBestMove(Game game);
}
