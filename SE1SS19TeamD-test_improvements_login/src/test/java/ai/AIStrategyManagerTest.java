package ai;

import ai.strategies.AITestStrategy;
import model.App;
import model.Game;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.IntStream;

public class AIStrategyManagerTest {

    /**
     * Test the {@link AIStrategyManager#add(AIStrategy)} method.
     */
    @Test
    public void testAdd() {
        AITestStrategy strategy = new AITestStrategy();
        AIStrategyManager manager = new AIStrategyManager();
        manager.add(strategy);

        AICommand command = manager.applyStrategies(new Game());
        AIStrategy resultStrategy = command.getStrategy();

        Assert.assertEquals("Given strategy was not used.\n", strategy, resultStrategy);
    }

    /**
     * Test the {@link AIStrategyManager#applyStrategies(App)} method.
     */
    @Test
    public void testApplyStrategies() {
        SortedMap<ConstantStrategy, AICommand> strategies = new TreeMap<>();
        final Random r = new Random(System.currentTimeMillis());
        AIStrategyManager manager = new AIStrategyManager();

        IntStream.range(0, 10).forEach(c -> {
            ConstantStrategy strategy = new ConstantStrategy();
            AICommand command = new AICommand();
            command.setStrategy(strategy);
            strategy.setValue(r.nextInt());
            strategy.setCommand(command);
            strategies.put(strategy, command);
            manager.add(strategy);
        });

        ConstantStrategy expectedStrategy = strategies.lastKey();
        AICommand expectedCommand = strategies.get(expectedStrategy);

        AICommand actualCommand = manager.applyStrategies(new Game());

        Assert.assertEquals("Did not return best move.\n", expectedCommand, actualCommand);
    }

    /**
     * A strategy that has a constant value and command.
     */
    private class ConstantStrategy implements AIStrategy, Comparable<ConstantStrategy> {

        private int value;
        private AICommand command;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public AICommand getCommand() {
            return command;
        }

        public void setCommand(AICommand command) {
            this.command = command;
        }

        @Override
        public int getEstimation(Game game) {
            // this strategy has a constant estimation for every situation
            return this.getValue();
        }

        @Override
        public AICommand findBestMove(Game game) {
            // this strategy has a constant best move
            return this.getCommand();
        }

        @Override
        public int compareTo(ConstantStrategy other) {
            return Integer.compare(this.getValue(), other.getValue());
        }
    }
}
