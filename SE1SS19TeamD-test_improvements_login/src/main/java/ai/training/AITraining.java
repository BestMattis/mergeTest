package ai.training;

import ai.AICommand;
import ai.AIStrategyManager;
import model.Game;
import model.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * A training for several AI Strategy Managers.
 * It contains an {@link Game} object to train on.
 * The result of the training can be used to improve the strategy managers
 * and their used strategies.
 */
public class AITraining {

    private AIConfiguration configuration;
    private Game game;
    private Map<Player, AIStrategyManager> playerStrategies;

    /**
     * Return the test configuration.
     *
     * @return the configuration
     */
    public AIConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Set the given test configuration.
     *
     * @param configuration the configuration to set
     */
    public void setConfiguration(AIConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Return the game for the training.
     *
     * @return the game
     */
    public Game getGame() {
        return game;
    }

    /**
     * Set the given game for the training.
     *
     * @param app the game
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Return the mapping from the players to the strategies.
     *
     * @return the mapping from the players to the strategies
     */
    public Map<Player, AIStrategyManager> getPlayerStrategies() {
        return playerStrategies;
    }

    /**
     * Set the mapping from the players to the strategies.
     *
     * @param playerStrategies the mapping from the players to the strategies
     */
    public void setPlayerStrategies(Map<Player, AIStrategyManager> playerStrategies) {
        this.playerStrategies = playerStrategies;
    }

    /**
     * Start the training asynchronously.
     * This method returns a {@link Future} object
     * that can be used to retrieve a ranking of the strategy managers.
     *
     * @return a {@link Future} object that can be used to retrieve the test results.
     */
    public Future<List<AIStrategyManager>> start() {
        FutureTask<List<AIStrategyManager>> result = new FutureTask<>(() -> this.runTraining());
        result.run();
        return result;
    }

    private List<AIStrategyManager> runTraining() {

        Map<Player, AIStrategyManager> alivePlayers = new HashMap<>();
        Map<Player, Player> nextPlayer = new HashMap<>();

        this.preparePlayers(alivePlayers, nextPlayer);

        List<AIStrategyManager> gameResult = train(alivePlayers, nextPlayer);

        return gameResult;
    }

    private void preparePlayers(Map<Player, AIStrategyManager> alivePlayers,
                                Map<Player, Player> nextPlayer) {

        Player turnPlayer = this.getGame().getTurnPlayer();
        alivePlayers.putAll(this.getPlayerStrategies());

        Player lastPlayer = null;
        for (Player p : this.getGame().getPlayers()) {
            nextPlayer.put(lastPlayer, p);
            lastPlayer = p;
        }
        nextPlayer.put(lastPlayer, turnPlayer);
    }

    private List<AIStrategyManager> train(Map<Player, AIStrategyManager> alivePlayers, Map<Player, Player> nextPlayer) {

        Game game = this.getGame();
        Player turnPlayer = game.getTurnPlayer();
        List<AIStrategyManager> gameResult = new ArrayList<>();

        while (alivePlayers.size() > 1) {

            game.setTurnPlayer(turnPlayer);

            AIStrategyManager manager = alivePlayers.get(turnPlayer);
            AICommand command = manager.applyStrategies(game);

            System.out.println(command.toString());

            this.removeKilledPlayers(alivePlayers, gameResult);

            turnPlayer = nextPlayer.get(turnPlayer);
        }

        return gameResult;
    }

    private void removeKilledPlayers(Map<Player, AIStrategyManager> alivePlayers, List<AIStrategyManager> gameResult) {
        List<Player> killedPlayers = new ArrayList<>();
        alivePlayers.forEach((p, m) -> {
            if (p.getCurrentUnits().isEmpty()) {
                // Player is dead -> insert on top of ranking and remove from active players list
                gameResult.add(0, m);
                killedPlayers.add(p);
            }
        });

        killedPlayers.forEach(p -> {
            alivePlayers.remove(p);
            game.getPlayers().remove(p);
        });

        // alive players <= 1: game finished

        if (alivePlayers.size() <= 1) {
            alivePlayers.forEach((p, m) -> gameResult.add(0, m));
        }
    }
}
