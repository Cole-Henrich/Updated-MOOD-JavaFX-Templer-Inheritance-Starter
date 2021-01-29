package org.headroyce.bsea;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Represents the logic of our game
 */
public class GameLogic {

    public enum DIRECTION {
        LEFT,
        UP,
        RIGHT,
        DOWN,
        STOP,
        NONE
    }

    // The game step in milliseconds
    public static final int GAME_STEP_TIMER = 17;
    private GameTimer gameTimer;

    private boolean gameOver;

    private Random rand;

    // The player
    private Ball player;
    private HashMap<DIRECTION, Boolean> forcesOnPlayer;

    private static final int PLAYER_FLASH_TIME = 500;
    private int flashTimer = 0;

    private static final int PLAYER_SCORING_TIME = 5000;
    private int PLAYER_SCORING_TIMER = 5000;

    private static final int PLAYER_SCORING_POINTS = 2;
    private int playerScore = 0;

    private static final int ENEMY_SPAWN_TIME = 150;
    private static final int ENEMY_DIRECTION_PROBABILITY = 5;
    private static final int ENEMY_SPAWN_PROBABILITY = 5;
    private static final int OBSTACLE_SPAWN_PROBABILITY = 10;
    private int ENEMY_SPAWN_TIMER = 150;


    // Enemy Elements
    private ArrayList<Mob> enemies;

    // Width and height of the canvas
    private double width, height;

    public GameLogic(double width, double height){
        rand = new Random();

        gameTimer = new GameTimer();

        this.width = Math.abs(width);
        this.height = Math.abs(height);

        player = new Ball();
        enemies = new ArrayList<>();

        forcesOnPlayer = new HashMap<>();

        reset();
    }

    /**
     * Renders the game elements onto a canvas
     * @param canvas the canvas to render onto
     */
    public void render(Canvas canvas){

        // Update width and height
        width = canvas.getWidth();
        height = canvas.getHeight();

        GraphicsContext gc = canvas.getGraphicsContext2D();

        player.render(canvas);
        for( int i = 0; i < enemies.size(); i++ ){
            Mob enemy = enemies.get(i);

            int min = (int)enemy.getWidth();
            if( enemy.x < 0 ){
                int maxW = (int)(width-min+1);
                enemy.x = rand.nextInt(maxW-min+1)+min;
            }

            enemy.render(canvas);
        }

        // Draw lives and score last so that the balls go under them
        Text lives = new Text("Lives: " + Math.round(player.getHP()));

        gc.strokeText("Score: " + playerScore, 10, 30);
        gc.strokeText(lives.getText(),width - 10 - lives.getLayoutBounds().getWidth(), 20);
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

    public void reset(){
        player.x = 200;
        player.y = 400;
        player.setRadius(10);

        player.velX = player.velY = 0;
        player.setVelocityBoundX(-7, 7);
        player.setVelocityBoundY(-7,7);


        player.addHP(3);
        enemies.clear();
        forcesOnPlayer.clear();

        gameOver = false;
        playerScore = 0;

        enemies.clear();
    }

    public boolean isGameOver(){
        return gameOver;
    }

    private boolean collideWalls(Mob player){

        boolean collided = false;

        // Keep player with the window

        if( player == this.player ) {
            if (player.y + player.getHeight() > height) {
                player.y = height - player.getHeight();
                player.bounceY();
                collided = true;
            }

            if (player.y - player.getHeight() < 0) {
                player.y = player.getHeight();
                player.bounceY();
                collided = true;
            }
        }


        if( player.x + player.getWidth() > width ){
            player.x = width - player.getWidth();
            player.bounceX();
            collided = true;
        }
        if( player.x - player.getWidth() < 0 ){
            player.x = player.getWidth();
            player.bounceX();
            collided = true;
        }

        return collided;
    }

    public void applyForce( DIRECTION direction ) {
        forcesOnPlayer.put(direction, true);
    }

    public void removeForce(DIRECTION direction){
        forcesOnPlayer.remove(direction);
    }

    /**
     * Checks to see if two balls collide and performs an inelastic collision
     * @param ball1 one of the balls to check
     * @param ball2 second ball to check colliion with
     * @return true if ball1 and ball2 collided, false otherwise
     */
    private boolean collideBalls( Ball ball1, Ball ball2 ){
        // inelastic collision
        // Swap velocities on collision
        boolean collided = false;

        if( ball1.intersects(ball2) ){
            double velXp = ball1.velX;
            double velYp = ball1.velY;

            ball1.velX = ball2.velX;
            ball1.velY = ball2.velY;

            ball2.velX = velXp;
            ball2.velY = velYp;

            collided = true;
        }

        return collided;
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
            long time_elapsed = (now - lastUpdate)/1000000;

            flashTimer -= time_elapsed;
            if( flashTimer < 0 ){
                player.setColor(Color.BLACK);
            }

            PLAYER_SCORING_TIMER -= time_elapsed;
            if( PLAYER_SCORING_TIMER < 0 ){
                PLAYER_SCORING_TIMER = PLAYER_SCORING_TIME;
                playerScore += PLAYER_SCORING_POINTS;
            }

            ENEMY_SPAWN_TIMER -= time_elapsed;
            if( ENEMY_SPAWN_TIMER < 0 ){
                int chance = rand.nextInt(100);
                if( chance < OBSTACLE_SPAWN_PROBABILITY ){

                    if( chance < ENEMY_SPAWN_PROBABILITY ) {
                        Ball enemy = new Ball();
                        enemy.setRadius(10);

                        enemy.x = -1;
                        enemy.y = -enemy.getRadius();  // off screen
                        enemy.setVelocityBoundX(-5,5);
                        enemy.setVelocityBoundY(-5,5);

                        enemy.setColor(Color.RED);

                        enemy.velX = rand.nextInt(5) + 2;
                        enemy.velY = rand.nextInt(5) + 2;
                        enemies.add(enemy);
                    }
                    else{
                        Obstacle enemy = new Obstacle();
                        enemy.x = -1;
                        enemy.y = -enemy.getHeight();  // off screen
                        enemy.setVelocityBoundX(-5,5);
                        enemy.setVelocityBoundY(-5,5);

                        enemy.velY = 10;
                        enemies.add(enemy);
                    }

                }

                ENEMY_SPAWN_TIMER = ENEMY_SPAWN_TIME;
            }

            if( time_elapsed > GameLogic.GAME_STEP_TIMER) {
                // Game steps go here

                if (forcesOnPlayer.containsKey(DIRECTION.LEFT)) {
                    player.velX--;
                }
                if (forcesOnPlayer.containsKey(DIRECTION.RIGHT)) {
                    player.velX++;
                }
                if (forcesOnPlayer.containsKey(DIRECTION.UP)) {
                    player.velY--;
                }
                if (forcesOnPlayer.containsKey(DIRECTION.DOWN)) {
                    player.velY++;
                }

                if (forcesOnPlayer.containsKey(DIRECTION.STOP)) {
                    player.velX -= Math.signum(player.velX);
                    player.velY -= Math.signum(player.velY);
                }

                // MOVE EVERYTHING
                player.move();
                for (int i = 0; i < enemies.size(); i++) {
                    Mob enemy = enemies.get(i);

                    if (enemy instanceof Ball) {
                        if (rand.nextInt(100) < ENEMY_DIRECTION_PROBABILITY &&
                                enemy.getColor() == Color.RED
                        ) {
                            double changeX = Math.signum(player.x - enemy.x);
                            double changeY = Math.signum(player.y - enemy.y);

                            enemy.velX = changeX * Math.abs(enemy.velX);
                            enemy.velY = changeY * Math.abs(enemy.velY);
                        }
                    }

                    enemy.move();
                }


                // CHECK WALLS ON EVERYTHING
                boolean playerCollided = collideWalls(player);
                for (int i = 0; i < enemies.size(); i++) {
                    Mob enemy = enemies.get(i);
                    collideWalls(enemy);

                    if (enemy.y > height) {
                        enemies.remove(enemy);
                        i--;
                    }
                }

                // CHECK BALL COLLISIONS ON EVERYTHING
                for (int i = 0; i < enemies.size(); i++) {
                    Mob enemy = enemies.get(i);
                    for (int j = i + 1; j < enemies.size(); j++) {
                        if (enemy instanceof Ball && enemies.get(j) instanceof Ball) {
                            if (collideBalls((Ball) enemy, (Ball) enemies.get(j))) {
                                enemies.remove(j);
                                enemies.remove(enemy);
                                j -= 2;
                            }
                        }
                    }
                    boolean enemyRemove;
                    if (enemy instanceof Ball) {
                    enemyRemove = collideBalls(player, (Ball) enemy);

                    if (enemyRemove) {
                        enemies.remove(enemy);
                        i--;
                    }
                    playerCollided = enemyRemove || playerCollided;
                }
            }

                if( playerCollided ){
                    // Stops lives being lost if green
                    if( flashTimer <= 0 ){
                        player.addHP(-1);
                        if( player.getHP() <= 0 ) {
                            gameOver = true;
                            pause(true);
                        }
                    }

                    flashTimer = PLAYER_FLASH_TIME;
                    player.setColor(Color.GREEN);
                }

                lastUpdate = now;
            }
        }
    }
}
