package org.headroyce.bsea;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Represents an rectangular obstacle
 */
public class Obstacle extends Mob {
    /**
     * Creates an obstacle with a width of ten and height of five
     */
    public Obstacle(){
        this(10, 40);
    }

    /**
     * Creates an obstacle with a custom width and height (in pixels)
     * @param width the width (in pixels) to set of the obstacle; Non-positives are reset to ten
     * @param height the height (in pixels) to set of the obstacle; Non-positives are reset to ten
     */
    public Obstacle( double width, double height ){

        setColor(Color.PURPLE);
        if( width <= 0 ){
            width = 10;
        }
        if( height <= 0 ){
            height = 10;
        }

        setWidth(width);
        setHeight(height);
    }



    /**
     * Check to see if the obstacle overlaps with a ball (circular)
     * @param other the ball to check intersection with
     * @return true is this object intersects with other, false otherwise
     */
    public boolean intersects(Ball other){
        Obstacle o = new Obstacle(other.getRadius()*2, other.getRadius()*2);
        o.x = other.x - other.getRadius();
        o.y = other.y - other.getRadius();

        return this.intersects(o);
    }

    /**
     * Check to see if the obstacle overlaps with another obstacle
     * @param other the second obstacle to check intersection with
     * @return true if this objects intersect, false otherwise
     */
    public boolean intersects(Obstacle other){
        if( this.x + this.getWidth() < other.x ){
            return false;
        }
        if( this.x > other.x + other.getWidth()){
            return false;
        }
        if( this.y + this.getHeight() < other.y ){
            return false;
        }
        if( this.y > other.y + other.getHeight()){
            return false;
        }

        return true;
    }


    public void render( Canvas canvas ){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(this.getColor());
        gc.fillRect(x, y, getWidth(), getHeight());

    }
}
