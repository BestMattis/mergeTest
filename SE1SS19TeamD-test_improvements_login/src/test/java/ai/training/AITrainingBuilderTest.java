package ai.training;

import ai.AIStrategyManager;
import ai.strategies.AITestStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the {@link AITrainingBuilder} class.
 */
public class AITrainingBuilderTest {

    /**
     * Test the {@link AITrainingBuilder#createTraining(AIConfiguration)}.
     */
    @Test
    public void testCreateAITraining() {
        AIConfiguration configuration = new AIConfiguration();

        AIStrategyManager manager1 = new AIStrategyManager();
        manager1.add(new AITestStrategy());
        AIStrategyManager manager2 = new AIStrategyManager();
        manager2.add(new AITestStrategy());

        List<AIStrategyManager> strategyManagers = new ArrayList<>();
        strategyManagers.add(manager1);
        strategyManagers.add(manager2);
        configuration.setStrategyManagers(strategyManagers);

        AITraining training = AITrainingBuilder.createTraining(configuration);
        int numberOfPlayers = strategyManagers.size();

        Assert.assertNotNull("Game to train on is null. ", training.getGame());
        Assert.assertEquals("Wrong number of players. ", numberOfPlayers, training.getGame().getPlayers().size());
        Assert.assertEquals("Game contains wrong number of units. ", numberOfPlayers * 10,
                training.getGame().getPlayers().stream().mapToInt(p -> p.getCurrentUnits().size()).sum());
    }
}
