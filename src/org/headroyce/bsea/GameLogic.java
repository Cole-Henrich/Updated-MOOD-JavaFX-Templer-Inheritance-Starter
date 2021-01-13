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

    // CHANGE: Added ability to detect that the game is over
    private boolean gameOver;

    private Random rand;

    // The player
    private Ball player;
    private HashMap<DIRECTION, Boolean> forcesOnPlayer;

    // CHANGE: Controls Player flash on collision
    private static final int PLAYER_FLASH_TIME = 500;
    private int flashTimer = 0;

    // CHANGE: Add scoring
    private static final int PLAYER_SCORING_TIME = 5000;
    private int PLAYER_SCORING_TIMER = 5000;

    private static final int PLAYER_SCORING_POINTS = 2;
    private int playerScore = 0;

    // CHANGE: Add enemy spawn
    private static final int ENEMY_SPAWN_TIME = 150;
    private static final int ENEMY_DIRECTION_PROBABILITY = 5;
    private static final int ENEMY_SPAWN_PROBABILITY = 5;
    private static final int OBSTACLE_SPAWN_PROBABILITY = 10;
    private int ENEMY_SPAWN_TIMER = 150;


    // Enemy Elements
    private ArrayList<Ball> enemies;
    private ArrayList<Obstacle> obstacles;

    // Width and height of the canvas
    private double width, height;

    public GameLogic(double width, double height){
        rand = new Random();

        gameTimer = new GameTimer();

        this.width = Math.abs(width);
        this.height = Math.abs(height);

        player = new Ball();
        enemies = new ArrayList<>();
        obstacles = new ArrayList<>();

        forcesOnPlayer = new HashMap<>();


        // CHANGE: Use the reset method
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
            Ball enemy = enemies.get(i);

            int min = (int)enemy.getRadius();
            if( enemy.x < 0 ){
                int maxW = (int)(width-min+1);
                enemy.x = rand.nextInt(maxW-min+1)+min;
            }

            // CHANGE: Remove randomization of y
            enemy.render(canvas);
        }

        for( int i = 0; i < obstacles.size(); i++ ){
            Obstacle enemy = obstacles.get(i);

            int min = (int)enemy.getWidth();
            if( enemy.x < 0 ){
                int maxW = (int)(width-min+1);
                enemy.x = rand.nextInt(maxW-min+1)+min;
            }
            // CHANGE: Remove randomization of y
            enemy.render(canvas);
        }


        // CHANGE Add Score and Lives Text
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

    // CHANGE: Add Reset Ability
    public void reset(){
        player.x = 200;
        player.y = 400;
        player.setRadius(10);

        // CHANGE: Remove player start speed
        player.velX = player.velY = 0;
        player.setVelocityBoundX(-7, 7);
        player.setVelocityBoundY(-7,7);


        // CHANGE: Add three lives to the player
        player.addHP(3);
        enemies.clear();
        forcesOnPlayer.clear();

        gameOver = false;
        playerScore = 0;

        enemies.clear();
        obstacles.clear();
    }

    public boolean isGameOver(){
        return gameOver;
    }

    // CHANGE: Return type to boolean (true if collision)
    private boolean collideWalls(Ball player){

        boolean collided = false;

        // Keep player with the window

        // CHANGE: Only applies to the player
        if( player == this.player ) {
            if (player.y + player.getRadius() > height) {
                player.y = height - player.getRadius();
                player.bounceY();
                collided = true;
            }

            if (player.y - player.getRadius() < 0) {
                player.y = player.getRadius();
                player.bounceY();
                collided = true;
            }
        }


        if( player.x + player.getRadius() > width ){
            player.x = width - player.getRadius();
            player.bounceX();
            collided = true;
        }
        if( player.x - player.getRadius() < 0 ){
            player.x = player.getRadius();
            player.bounceX();
            collided = true;
        }

        return collided;
    }

    public void applyForce( DIRECTION direction ) {
        forcesOnPlayer.put(direction, true);
    }

    // CHANGE: Added ability to remove a force
    public void removeForce(DIRECTION direction){
        forcesOnPlayer.remove(direction);
    }

    /**
     * Checks to see if two balls collide and performs an inelastic collision
     * @param ball1 one of the balls to check
     * @param ball2 second ball to check colliion with
     * @return true if ball1 and ball2 collided, false otherwise
     */
    // CHANGE: return type to boolean
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

        // CHANGE: Added player flashing
        public void handle(long now) {

            // Covert the time_elapsed from nanoseconds to milliseconds
            long time_elapsed = (now - lastUpdate)/1000000;

            // CHANGE: Control flash timer
            flashTimer -= time_elapsed;
            if( flashTimer < 0 ){
                player.setColor(Color.BLACK);
            }

            // CHANGE: Control scoring timer
            PLAYER_SCORING_TIMER -= time_elapsed;
            if( PLAYER_SCORING_TIMER < 0 ){
                PLAYER_SCORING_TIMER = PLAYER_SCORING_TIME;
                playerScore += PLAYER_SCORING_POINTS;
            }

            // CHANGE: Control Enemy Spawning
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
                        // CHANGE: Always go down
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
                        obstacles.add(enemy);
                    }

                }

                ENEMY_SPAWN_TIMER = ENEMY_SPAWN_TIME;
            }

            if( time_elapsed > GameLogic.GAME_STEP_TIMER) {
                // Game steps go here

                if( forcesOnPlayer.containsKey(DIRECTION.LEFT) ){
                    player.velX--;
                }
                if( forcesOnPlayer.containsKey(DIRECTION.RIGHT) ){
                    player.velX++;
                }
                if( forcesOnPlayer.containsKey(DIRECTION.UP) ){
                    player.velY--;
                }
                if( forcesOnPlayer.containsKey(DIRECTION.DOWN) ){
                    player.velY++;
                }

                if( forcesOnPlayer.containsKey(DIRECTION.STOP) ){
                    player.velX -= Math.signum(player.velX);
                    player.velY -= Math.signum(player.velY);
                }

                // MOVE EVERYTHING
                player.move();
                for( int i = 0; i < enemies.size(); i++ ){
                    Ball enemy = enemies.get(i);

                    // CHANGE: Redirect Enemies with probability
                    if( rand.nextInt(100) < ENEMY_DIRECTION_PROBABILITY &&
                        enemy.getColor() == Color.RED
                    ){
                        double changeX = Math.signum(player.x - enemy.x);
                        double changeY = Math.signum(player.y - enemy.y);

                        enemy.velX = changeX * Math.abs(enemy.velX);
                        enemy.velY = changeY * Math.abs(enemy.velY);
                    }

                    enemy.move();
                }
                for( int i = 0; i < obstacles.size(); i++ ){
                    Obstacle enemy = obstacles.get(i);
                    enemy.move();
                }



                // CHECK WALLS ON EVERYTHING
                boolean playerCollided = collideWalls(player);
                for( int i = 0; i < enemies.size(); i++ ){
                    Ball enemy = enemies.get(i);
                    collideWalls(enemy);

                    // CHANGE: Remove the enemy if it goes off the screen
                    if( enemy.y > height ){
                        enemies.remove(enemy);
                        i--;
                    }
                }
                // Remove obstacles if they go past the end of the window
                for( int i = 0; i < obstacles.size(); i++ ) {
                    Obstacle enemy = obstacles.get(i);
                    if( enemy.y > height ){
                        obstacles.remove(enemy);
                        i--;
                    }
                }

                // CHECK BALL COLLISIONS ON EVERYTHING
                for( int i = 0; i < enemies.size(); i++ ) {
                    Ball enemy = enemies.get(i);
                    for( int j = i + 1; j < enemies.size(); j++ ) {
                        if(collideBalls(enemy, enemies.get(j)) ){
                            enemies.remove(j);
                            enemies.remove(enemy);
                            j -= 2;
                        }
                    }

                    // Obstacle-enemy collision -- only removes enemies
                   for( int j = 0; j < obstacles.size(); j++ ) {
                        if( obstacles.get(j).intersects(enemy) ){
                            enemies.remove(enemy);
                            i--;
                        }
                    }


                    boolean enemyRemove = collideBalls(player, enemy);
                    if( enemyRemove ){
                        enemies.remove(enemy);
                        i--;
                    }
                    playerCollided =  enemyRemove || playerCollided;
                }
                // Check player with the obstacles
                for( int i = 0; i < obstacles.size(); i++ ) {
                    Obstacle o = obstacles.get(i);
                    if( o.intersects(player)){
                        playerCollided = true;
                        obstacles.remove(o);
                        i--;
                    }
                }


                // CHANGE: Handle player collision
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
