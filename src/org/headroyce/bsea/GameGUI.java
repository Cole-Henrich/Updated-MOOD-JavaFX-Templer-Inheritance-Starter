package org.headroyce.bsea;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

import java.security.Key;

/**
 * Represents the view element of the game
 */
public class GameGUI extends StackPane {

    private GameLogic logic;

    private Canvas gameArea;
    private AnimationTimer animTimer;

    private Button reset;

    public GameGUI() {
        gameArea = new Canvas();
        gameArea.heightProperty().bind(this.heightProperty());
        gameArea.widthProperty().bind(this.widthProperty());

        animTimer = new AnimTimer();
        logic = new GameLogic(gameArea.getWidth(), gameArea.getHeight());

        reset = new Button("Reset");
        reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                reset.setVisible(false);
                logic.reset();
                logic.pause(false);
            }
        });
        reset.setVisible(false);

        this.getChildren().add(reset);
        this.getChildren().add(gameArea);
    }

    /**
     * Pause/Unpause the animation without touching the game timer
     * @param setAnimPause true to pause, false otherwise
     */
    public void pause(boolean setAnimPause) {
        if (setAnimPause) {
            animTimer.stop();
        } else {
            animTimer.start();
        }
    }

    /**
     * Pause/unpause teh animation and game timer
     * @param setAnimPause true to pause the animation timer
     * @param setGamePause true to pause the game timer
     */
    public void pause(boolean setAnimPause, boolean setGamePause ){
        this.pause(setAnimPause);
        logic.pause(setGamePause);
    }

    /**
     * Deal with key presses
     * @param event the event to handle
     */
    public void handleKeyPress(KeyEvent event){
        if( event.getCode() == KeyCode.A){
            logic.applyForce(GameLogic.DIRECTION.LEFT);
        }
        if( event.getCode() == KeyCode.D ) {
            logic.applyForce(GameLogic.DIRECTION.RIGHT);
        }
        if( event.getCode() == KeyCode.W ) {
            logic.applyForce(GameLogic.DIRECTION.UP);
        }
        if( event.getCode() == KeyCode.S ) {
            logic.applyForce(GameLogic.DIRECTION.DOWN);
        }

        if( event.getCode() == KeyCode.SPACE ){
            logic.applyForce(GameLogic.DIRECTION.STOP);
        }
    }

    /**
     * Deal with key releases
     * @param event the event to handle
     */
    public void handleKeyRelease(KeyEvent event){
        if( event.getCode() == KeyCode.A){
            logic.removeForce(GameLogic.DIRECTION.LEFT);
        }
        if( event.getCode() == KeyCode.D ) {
            logic.removeForce(GameLogic.DIRECTION.RIGHT);
        }
        if( event.getCode() == KeyCode.W ) {
            logic.removeForce(GameLogic.DIRECTION.UP);
        }
        if( event.getCode() == KeyCode.S ) {
            logic.removeForce(GameLogic.DIRECTION.DOWN);
        }

        if( event.getCode() == KeyCode.SPACE ){
            logic.removeForce(GameLogic.DIRECTION.STOP);
        }
    }

    /**
     * Runs once per frame and handles all the drawing of each frame
     */
    private class AnimTimer extends AnimationTimer {

        @Override
        public void handle(long now) {
            GraphicsContext gc = gameArea.getGraphicsContext2D();


            gc.clearRect(0,0, gameArea.getWidth(), gameArea.getHeight());

            if(logic.isGameOver()){
                reset.setVisible(true);
                reset.toFront();
            }
            else {
                logic.render(gameArea);
            }

        }
    }



}
