package gameController.gameLoop;

import gameController.gameLoop.sprites.Sprite;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;

import java.util.ArrayList;

class GameLoopAnimationTimer extends AnimationTimer {

    ArrayList<GameLoopTask> tasks = new ArrayList<>();

    Canvas spriteCanvas;
    ArrayList<Sprite> sprites = new ArrayList<>();

    @Override
    public void handle(long now) {
        updateSprites();
        for (GameLoopTask task : tasks) {
            task.execute();
        }
    }

    private void updateSprites() {
        // Updates sprite position when they are moving
        // and animates their movement at 60fps

        if (spriteCanvas != null) {
            spriteCanvas.getGraphicsContext2D().clearRect(0, 0, spriteCanvas.getWidth(), spriteCanvas.getHeight());
            for (Sprite s : sprites) {
                s.draw();
            }
        }
    }
}
