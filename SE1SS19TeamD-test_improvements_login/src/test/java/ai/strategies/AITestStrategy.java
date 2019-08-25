package ai.strategies;

import ai.AICommand;
import ai.AIStrategy;
import model.Game;

/**
 * A test strategy that returns an empty AI Command.
 */
public class AITestStrategy implements AIStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public int getEstimation(Game game) {
        /*
         * this is the only strategy in the test, so we can return any value here.
         */
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AICommand findBestMove(Game game) {
        /*
         * this is the only strategy in the test, so we can return any value here.
         */
        AICommand command = new AICommand();
        command.setStrategy(this);
        return command;
    }
}
