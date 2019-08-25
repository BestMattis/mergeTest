package gameController.gameLoop;

import gameController.gameLoop.sprites.Sprite;
import javafx.scene.canvas.Canvas;

public class GameLoop {

    private GameLoopAnimationTimer gameLoop = new GameLoopAnimationTimer();

    private boolean currentlyRunning = false;

    /**
     * Starts the gameLoop and sets currentlyRunning to true.
     */
    public void startLoop() {
        gameLoop.start();
        currentlyRunning = true;
    }

    /**
     * Stops the gameLoop and sets currentlyRunning to false.
     */
    public void stopLoop() {
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
     * @return The canvas where all sprites will be drawn on.
     */
    public Canvas getSpriteCanvas() {
        return gameLoop.spriteCanvas;
    }

    /**
     * Set the canvas where all sprites will be drawn on.
     * Clears and redraws all units at 60 fps.
     *
     * @param canvas The canvas that all sprites will be drawn upon
     */
    public void setSpriteCanvas(Canvas canvas) {
        gameLoop.spriteCanvas = canvas;
    }

    /**
     * Adds an sprite to the list of sprites.
     * Each will be drawn 60 times per second by the loop.
     *
     * @param sprite A sprite that can be shown
     */
    public void addSprite(Sprite sprite) {
        gameLoop.sprites.add(sprite);
    }

    /**
     * Removes a sprite of the list of shown sprites
     *
     * @param sprite A sprite that can be shown
     * @return Returns whether it was successfully removed from the sprites list
     */
    public boolean removeSprite(Sprite sprite) {
        if (gameLoop.sprites.contains(sprite)) {
            return gameLoop.sprites.remove(sprite);
        }
        return false;
    }

    /**
     * Check whether a sprite is currently being shown.
     *
     * @param sprite A sprite that can be shown
     * @return Returns true if the sprite is currently being shown
     */
    public boolean containsSprite(Sprite sprite) {
        return gameLoop.sprites.contains(sprite);
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
