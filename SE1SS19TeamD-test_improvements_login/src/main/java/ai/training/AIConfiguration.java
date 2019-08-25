package ai.training;

import ai.AIStrategyManager;

import java.util.List;

/**
 * The configuration for a AI training.
 */
public class AIConfiguration {

    private List<AIStrategyManager> strategyManagers;

    /**
     * Return the strategy managers to use in the training.
     *
     * @return the strategy managers
     */
    public List<AIStrategyManager> getStrategyManagers() {
        return strategyManagers;
    }

    /**
     * Set the given strategy managers.
     *
     * @param strategyManagers the strategy managers to set.
     */
    public void setStrategyManagers(List<AIStrategyManager> strategyManagers) {
        this.strategyManagers = strategyManagers;
    }
}
