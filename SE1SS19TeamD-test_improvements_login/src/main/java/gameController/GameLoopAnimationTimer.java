package gameController;

import javafx.animation.AnimationTimer;

import java.util.ArrayList;

class GameLoopAnimationTimer extends AnimationTimer {

    ArrayList<GameLoopTask> tasks = new ArrayList<>();

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

        // TODO: When sprite class is made
    }


}
