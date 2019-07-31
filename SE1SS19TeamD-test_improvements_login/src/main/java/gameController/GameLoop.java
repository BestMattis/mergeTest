package gameController;

import java.util.ArrayList;

public class GameLoop {

    private GameLoopAnimationTimer gameLoop;

    private boolean currentlyRunning = false;

    /**
     * Starts the gameLoop and sets currentlyRunning to true.
     */
    public void startLoop() {
        if (gameLoop == null) {
            gameLoop = new GameLoopAnimationTimer();
        }
        gameLoop.start();
        currentlyRunning = true;
    }

    /**
     * Stops the gameLoop and sets currentlyRunning to false.
     */
    public void stopLoop() {
        if (gameLoop == null) {
            gameLoop = new GameLoopAnimationTimer();
        }
        gameLoop.stop();
        currentlyRunning = false;
    }

    /**
     * Adds an task to the list of tasks.
     * Each will be executed 60 times per second by the loop.
     *
     * @param task Instance of interface GameLoopTask.
     *             Its execute() will be called each loop iteration
     */
    public void addTask(GameLoopTask task) {
        gameLoop.tasks.add(task);
    }

    /**
     * Removes a task from the list of all task
     * that will be executed by the gameLoop.
     *
     * @param task Instance of interface GameLoopTask.
     *             Its execute() will be called each loop iteration
     * @return Returns whether it was successfully removed from the tasks list
     */
    public boolean removeTask(GameLoopTask task) {
        if (gameLoop.tasks.contains(task)) {
            return gameLoop.tasks.remove(task);
        }
        return false;
    }

    /**
     * Check whether a task will already be executed by the gameLoop.
     *
     * @param task Instance of interface GameLoopTask.
     *             Its execute() will be called each loop iteration
     * @return Returns if the tasks is already being executed by the loop
     */
    public boolean isTaskRunning(GameLoopTask task) {
        return gameLoop.tasks.contains(task);
    }

    /**
     * A method for checking whether the gameLoop is already running.
     *
     * @return Whether it's currently running or not
     */
    public boolean isCurrentlyRunning() {
        return currentlyRunning;
    }
}
