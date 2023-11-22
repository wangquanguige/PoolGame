package org.example.Items;

import javafx.scene.paint.Paint;
import org.example.Drawable;
import org.example.Movable;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ball implements Movable, Drawable {
    private static final double RASIUS = 15.0;
    private final Circle shape;
    private final double mass;
    private final double start_x;
    private final double start_y;
    private boolean ever_hole;
    private double dragRelativeX = 0.0;
    private double dragRelativeY = 0.0;
    private final double[] velocity = {0.0, 0.0};
    private final double[] bounds = {600, 300};
    public Ball(double xPos, double yPos, double xVel, double yVel, double mass, String colour) {
        this.shape = new Circle(xPos, yPos, RASIUS);
        this.shape.setFill(Color.valueOf(colour));
        velocity[0] = xVel;
        velocity[1] = yVel;
        this.mass = mass;
        this.start_x = xPos;
        this.start_y = yPos;
        ever_hole = false;
    }

    public void registerMouseAction() {
        this.shape.setOnMousePressed(e -> {
            dragRelativeX = e.getSceneX() - shape.getCenterX();
            dragRelativeY = e.getSceneY() - shape.getCenterY();
        });
        this.shape.setOnMouseDragged(e -> {
            double newX = e.getSceneX() - dragRelativeX;
            if (newX < 0 || newX + shape.getRadius() > bounds[0]) return;
            //this.shape.setX(newX);
        });
    }

    @Override
    public Node getNode() {
        return this.shape;
    }

    @Override
    public void addToGroup(ObservableList<Node> group) {
        group.add(this.shape);
    }

    @Override
    public double getXPos() {
        return this.shape.getCenterX();
    }

    @Override
    public double getYPos() {
        return this.shape.getCenterY();
    }

    @Override
    public double getXVel() {
        return this.velocity[0];
    }

    @Override
    public double getYVel() {
        return this.velocity[1];
    }

    public double getMass() {
        return this.mass;
    }

    public Paint getColour() {
        return shape.getFill();
    }

    public boolean getEverhole() {
        return ever_hole;
    }

    public double getStart_x() {
        return start_x;
    }

    public double getStart_y() {
        return start_y;
    }

    public void setEverhole(boolean everHole) {
        ever_hole = everHole;
    }

    @Override
    public void setXPos(double xPos) {
        this.shape.setCenterX(xPos);
    }

    @Override
    public void setYPos(double yPos) {
        this.shape.setCenterY(yPos);
    }

    @Override
    public void setXVel(double xVel) {
        this.velocity[0] = xVel;
    }

    @Override
    public void setYVel(double yVel) {
        this.velocity[1] = yVel;
    }

    @Override
    public void move() {
        double xPos = getXPos() + getXVel();
        double yPos = getYPos() + getYVel();
        setXPos(xPos);
        setYPos(yPos);
    }
}
