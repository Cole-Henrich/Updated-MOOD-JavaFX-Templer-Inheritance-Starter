package org.headroyce.bsea;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;

/**
 * Represents the logic of our game
 */
public class GameLogic {

    // The game step in milliseconds
    public static final int GAME_STEP_TIMER = 500;
    private GameTimer gameTimer;

    // The player
    private Ball player;

    public GameLogic(){
        gameTimer = new GameTimer();

    }

    /**
     * Renders the game elements onto a canvas
     * @param canvas the canvas to render onto
     */
    public void render(Canvas canvas){
        player.render(canvas);
    }

    /**
     * Pause or unpause the game
     * @param setPaused true to pause, false otherwise
     */
    public void pause(boolean setPaused ){
        if( setPaused ){
            gameTimer.stop();
        }
        else {
            gameTimer.start();
        }
    }

    /**
     * Runs once per game tick which is set dynamically by the GAME_STEP_TIMER
     */
    private class GameTimer extends AnimationTimer {
        // The last nanosecond
        private long lastUpdate;

        public GameTimer() {
            lastUpdate = 0;
        }

        @Override
        public void handle(long now) {

            // Covert the time_elapsed from nanoseconds to milliseconds
            long time_elaped = (now - lastUpdate)/1000000;
            if( time_elaped > GameLogic.GAME_STEP_TIMER) {
                // Game steps go here


                lastUpdate = now;
            }
        }
    }
}
