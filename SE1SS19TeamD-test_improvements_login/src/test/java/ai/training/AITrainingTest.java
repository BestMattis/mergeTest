package ai.training;

import ai.AICommand;
import ai.AIStrategy;
import ai.AIStrategyManager;
import ai.strategies.AITestStrategy;
import model.Game;
import model.Player;
import model.Unit;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AITrainingTest {

    /**
     * Test the {@code AITraining#configuration} property. Methods: {@link AITraining#getConfiguration()} and
     * {@link AITraining#setConfiguration(AIConfiguration)}.
     */
    @Test
    public void testConfigurationAccess() {
        AITraining training = new AITraining();
        AIConfiguration configuration = new AIConfiguration();
        training.setConfiguration(configuration);

        Assert.assertEquals("Wrong configuration used", configuration, training.getConfiguration());
    }

    /**
     * Test the {@link AITraining#start()} method. Part 1: First player wins.
     */
    @Test
    public void testStart_firstPlayerWins() {
        AIConfiguration configuration = new AIConfiguration();
        AIStrategyManager manager1 = new AIStrategyManager();
        manager1.add(new AIWinnerStrategy());
        AIStrategyManager manager2 = new AIStrategyManager();
        manager2.add(new AITestStrategy());
        List<AIStrategyManager> strategyManagers = new ArrayList<>();
        strategyManagers.add(manager1);
        strategyManagers.add(manager2);
        configuration.setStrategyManagers(strategyManagers);

        this.testStart(configuration, manager1, manager2, strategyManagers);
    }

    /**
     * Test the {@link AITraining#start()} method. Part 2: Second player wins.
     */
    @Test
    public void testStart_secondPlayerWins() {
        AIConfiguration configuration = new AIConfiguration();
        AIStrategyManager manager1 = new AIStrategyManager();
        manager1.add(new AITestStrategy());
        AIStrategyManager manager2 = new AIStrategyManager();
        manager2.add(new AIWinnerStrategy());
        List<AIStrategyManager> strategyManagers = new ArrayList<>();
        strategyManagers.add(manager1);
        strategyManagers.add(manager2);
        configuration.setStrategyManagers(strategyManagers);

        List<AIStrategyManager> result = new ArrayList<>();
        result.add(manager2);
        result.add(manager1);

        this.testStart(configuration, manager1, manager2, result);
    }

    private void testStart(AIConfiguration configuration, AIStrategyManager manager1, AIStrategyManager manager2,
                           List<AIStrategyManager> strategyManagers) {
        Map<Player, AIStrategyManager> playerStrategies = new HashMap<>();
        Player player1 = new Player();
        player1.withCurrentUnits(new Unit());
        Player player2 = new Player();
        player2.withCurrentUnits(new Unit());
        Game game = new Game().setTurnPlayer(player1).withPlayers(player1, player2);

        playerStrategies.put(player1, manager1);
        playerStrategies.put(player2, manager2);

        AITraining training = new AITraining();
        training.setConfiguration(configuration);
        training.setGame(game);
        training.setPlayerStrategies(playerStrategies);

        Future<List<AIStrategyManager>> future = training.start();
        try {
            List<AIStrategyManager> actual = future.get();
            Assert.assertEquals("Wrong order of strategy managers", strategyManagers, actual);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * A strategy that wins every game.
     */
    private class AIWinnerStrategy implements AIStrategy {

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
            // remove all units of other player

            Player turnPlayer = game.getTurnPlayer();
            Player otherPlayer = game.getPlayers().stream().filter(p -> p != turnPlayer).findAny().get();

            otherPlayer.getCurrentUnits().clear();

            return new AICommand();
        }
    }
}
