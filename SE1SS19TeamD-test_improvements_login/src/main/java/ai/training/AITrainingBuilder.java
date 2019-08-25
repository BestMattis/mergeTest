package ai.training;

import ai.AIStrategyManager;
import model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class AITrainingBuilder {

    private static final String[] FIELD_TYPES = new String[]{"Grass", "Mountain", "Water", "Forest"};
    private static final String[] UNIT_TYPES = new String[]{"Infantry", "Bazooka Trooper", "Jeep", "Light Tank", "Heavy Tank", "Chopper"};
    private static final Map<String, Integer> UNIT_MP = new HashMap<String, Integer>();
    private static final Map<String, ArrayList<String>> UNIT_CAN_ATTACK = new HashMap<>();

    private static Random random;

    static {
        random = new Random();
        AITrainingBuilder.UNIT_MP.put("Infantry", 3);
        AITrainingBuilder.UNIT_MP.put("Bazooka Trooper", 2);
        AITrainingBuilder.UNIT_MP.put("Jeep", 8);
        AITrainingBuilder.UNIT_MP.put("Light Tank", 6);
        AITrainingBuilder.UNIT_MP.put("Heavy Tank", 4);
        AITrainingBuilder.UNIT_MP.put("Chopper", 6);

        String[] canAttack = new String[]{"Infantry", "Bazooka Trooper", "Jeep", "Light Tank", "Heavy Tank"};
        AITrainingBuilder.UNIT_CAN_ATTACK.put("Infantry", new ArrayList<>(Arrays.asList(canAttack)));
        canAttack = new String[]{"Jeep", "Light Tank", "Heavy Tank", "Chopper"};
        AITrainingBuilder.UNIT_CAN_ATTACK.put("Bazooka Trooper", new ArrayList<>(Arrays.asList(canAttack)));
        canAttack = new String[]{"Infantry", "Bazooka Trooper", "Jeep"};
        AITrainingBuilder.UNIT_CAN_ATTACK.put("Jeep", new ArrayList<>(Arrays.asList(canAttack)));
        canAttack = new String[]{"Infantry", "Bazooka Trooper", "Jeep", "Light Tank", "Heavy Tank"};
        AITrainingBuilder.UNIT_CAN_ATTACK.put("Light Tank", new ArrayList<>(Arrays.asList(canAttack)));
        canAttack = new String[]{"Infantry", "Bazooka Trooper", "Jeep", "Light Tank", "Heavy Tank", "Chopper"};
        AITrainingBuilder.UNIT_CAN_ATTACK.put("Heavy Tank", new ArrayList<>(Arrays.asList(canAttack)));
        canAttack = new String[]{"Infantry", "Bazooka Trooper", "Jeep", "Light Tank", "Heavy Tank"};
        AITrainingBuilder.UNIT_CAN_ATTACK.put("Chopper", new ArrayList<>(Arrays.asList(canAttack)));
    }

    /**
     * Create an AI training with the given configuration.
     * Generate a random game field with random unit positions.
     *
     * @param configuration the configuration to use
     * @return the created AI training
     */
    public static AITraining createTraining(AIConfiguration configuration) {

        Game game = AITrainingBuilder.createGame(configuration);

        AITraining result = new AITraining();
        result.setConfiguration(configuration);
        result.setGame(game);

        return result;
    }

    private static Game createGame(AIConfiguration configuration) {
        Game game = new Game();
        int tilesPerAxis = 16 * configuration.getStrategyManagers().size();

        AITrainingBuilder.createPlayers(configuration, game);
        AITrainingBuilder.createGameField(configuration, game, tilesPerAxis);
        AITrainingBuilder.createUnits(configuration, game, tilesPerAxis);

        return game;
    }

    private static void createPlayers(AIConfiguration configuration, Game game) {
        for (AIStrategyManager manager : configuration.getStrategyManagers()) {
            Player p = new Player();
            p.setName(String.format("Player for manager %s", manager.toString()));
            p.setGame(game);
        }
    }

    private static void createGameField(AIConfiguration configuration, Game game, int tilesPerAxis) {
        GameField gameField = new GameField();

        AITrainingBuilder.createFields(gameField, tilesPerAxis);
        AITrainingBuilder.linkFields(configuration, gameField, tilesPerAxis);

        game.setGameField(gameField);
    }

    private static void createUnits(AIConfiguration configuration, Game game, int tilesPerAxis) {
        for (Player p : game.getPlayers()) {
            IntStream.range(0, 10).forEach(n -> {
                Unit u = new Unit();
                AITrainingBuilder.setUnitAttributes(u, game, p, n);
                AITrainingBuilder.addToGameField(game, tilesPerAxis, u);
            });
        }
    }

    private static void createFields(GameField gameField, int tilesPerAxis) {
        final Field[] upperFields = new Field[tilesPerAxis];
        final AtomicReference<Field> leftField = new AtomicReference<>();

        IntStream.range(0, tilesPerAxis).forEachOrdered(x -> IntStream.range(0, tilesPerAxis).forEachOrdered(y -> {
            Field f = new Field();
            f.setGameField(gameField);
            f.setId(String.format("Field@%d|%d", x, y));
            f.setPosX(x);
            f.setPosY(y);

            String type = AITrainingBuilder.randomType(upperFields[x], leftField.get());
            f.setType(type);
            f.setIsPassable(type.equals("Water"));

            upperFields[x] = f;
            leftField.set(f);
        }));
    }

    private static String randomType(Field upperField, Field leftField) {
        int noOfUnitTypes = AITrainingBuilder.FIELD_TYPES.length;
        int i = random.nextInt(noOfUnitTypes + (upperField != null ? 1 : 0) + (leftField != null ? 1 : 0));
        String result;

        if (i == noOfUnitTypes && upperField != null) {
            result = upperField.getType();
        } else if (i >= noOfUnitTypes && leftField != null) {
            result = leftField.getType();
        } else {
            result = AITrainingBuilder.FIELD_TYPES[i];
        }

        return result;
    }

    private static void linkFields(AIConfiguration configuration, GameField gameField, int tilesPerAxis) {
        gameField.getFields().forEach(f -> {
            int x = f.getPosX();
            int y = f.getPosY();
            IntStream.range(-1, 2).forEach(dx -> IntStream.range(-1, 2).forEach(dy -> {
                if (x + dx > 0 && x + dx < tilesPerAxis - 1 && y + dy > 0 && y + dy < tilesPerAxis - 1) {
                    Field neighbor = gameField.getFields().stream()
                            .filter(ff -> ff.getPosX() == x + dx && ff.getPosY() == y + dy).findAny().get();
                    f.withNeighbour(neighbor);
                }
            }));
        });
    }

    private static void setUnitAttributes(Unit u, Game game, Player p, int n) {
        u.setId(String.format("Unit %d from player %s", n, p.toString()));
        u.setPlayer(p);
        u.setGame(game);

        int r = Math.abs(random.nextInt(AITrainingBuilder.UNIT_TYPES.length));
        String type = AITrainingBuilder.UNIT_TYPES[r];
        u.setType(type);
        u.setCanAttack(AITrainingBuilder.UNIT_CAN_ATTACK.get(type));
        u.setMp(AITrainingBuilder.UNIT_MP.get(type));
        u.setHp(10);
    }

    private static void addToGameField(Game game, int tilesPerAxis, Unit u) {
        int x = random.nextInt(tilesPerAxis);
        int y = random.nextInt(tilesPerAxis);
        u.setPosX(x);
        u.setPosY(y);
        u.setOccupiesField(game.getGameField().getFields().stream()
                .filter(f -> f.getPosX() == x && f.getPosY() == y)
                .findAny().get());
    }
}
