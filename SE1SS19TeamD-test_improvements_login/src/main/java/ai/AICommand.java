package ai;

/**
 * A command as a result of applying an {@link AIStrategy} to a specific game situation.
 */
public class AICommand {

    private AIStrategy strategy;

    /**
     * Return the strategy that created this command.
     *
     * @return the strategy that created this command
     */
    public AIStrategy getStrategy() {
        return this.strategy;
    }

    /**
     * Set the strategy that created this command.
     *
     * @param strategy the strategy that created this command
     */
    public void setStrategy(AIStrategy strategy) {
        this.strategy = strategy;
    }
}
