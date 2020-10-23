package org.headroyce.bsea;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Represents an oval ball
 */
public class Ball {
    private double radius;
    private Color color;

    public double x, y;
    public double velX, velY;

    /**
     * Creates a ball with a radius of one
     */
    public Ball(){
        this(1);
    }

    /**
     * Creates a ball with a custom radius (in pixels)
     * @param radius the radius (in pixels) to set the ball; Negatives are reset to one
     */
    public Ball( double radius ){

    }

    /**
     * Set the radius of this object.  A ball's radius cannot be negative.
     * @param radius the new, positive, radius of this object
     * @return true if the radius is set, false if radius is not changed
     */
    public boolean setRadius(double radius){

    }

    /**
     * Get the current radius of this object
     * @return a positive radius
     */
    public double getRadius(){

    }

    /**
     * Sets the color of the ball
     * @param c the new color of the ball (cannot be null)
     * @return true if the color has changed, false otherwise
     */
    public boolean setColor( Color c ){

    }

    /**
     * Get the current color of the ball
     * @return the current color of the ball
     */
    public Color getColor(){

    }

    /**
     * Bounce the ball in the X direction
     */
    public void bounceX(){

    }

    /**
     * Bounce the ball in the Y direction
     */
    public void bounceY() {

    }

    /**
     * Move the ball along its trajectory vector
     */
    public void move(){

    }

    /**
     * Check to the see if a point in within the ball
     * @param point the 2D points to check
     * @return true if point is within this, false otherwise
     */
    public boolean contains(Point2D point){
        return false;
    }

    /**
     * Check to see if two balls overlap each other
     * @param other the other ball
     * @return true is this object intersects with other, false otherwise
     */
    public boolean intersects(Ball other){
        return false;
    }

    public void render( Canvas canvas ){
        GraphicsContext gc = canvas.getGraphicsContext2D();
    }
}
