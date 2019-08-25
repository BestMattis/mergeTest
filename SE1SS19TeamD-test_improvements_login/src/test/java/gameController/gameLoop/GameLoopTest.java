package gameController.gameLoop;

import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

public class GameLoopTest extends ApplicationTest {

    @Test
    public void testGameLoop() {
        GameLoop gameLoop = new GameLoop();
        int timeToWait = 1; // In seconds

        // Check if gameLoop is starting correctly
        Assert.assertFalse(gameLoop.isCurrentlyRunning());
        gameLoop.startLoop();
        Assert.assertTrue(gameLoop.isCurrentlyRunning());

        // Check if taskList is empty
        Assert.assertFalse(gameLoop.isTaskRunning(() -> System.out.println("Do jack shit")));

        // Test task
        GameLoopTask doingNothing = () -> System.out.println("Doing nothing right now");

        // Test adding a task
        Assert.assertFalse(gameLoop.isTaskRunning(doingNothing));
        gameLoop.addTask(doingNothing);
        Assert.assertTrue(gameLoop.isTaskRunning(doingNothing));

        // Listen to the gameLoop
        // gameLoop doesn't do anything if no javaFX Thread is running
        long startTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - startTime > timeToWait * 1000) {
                break;
            }
        }

        // Test removing tasks, one successfully,
        // one redundant remove that won't work
        boolean removedSuccessfully = gameLoop.removeTask(doingNothing);
        boolean removedUnsuccessfully = gameLoop.removeTask(doingNothing);
        Assert.assertFalse(gameLoop.isTaskRunning(doingNothing));
        Assert.assertTrue(removedSuccessfully);
        Assert.assertFalse(removedUnsuccessfully);

        // Listen again, just for a second
        System.out.println("---");
        startTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - startTime > 1000) {
                break;
            }
        }

        // Test stopping the gameLoop
        gameLoop.stopLoop();
        Assert.assertFalse(gameLoop.isCurrentlyRunning());

    }
}
