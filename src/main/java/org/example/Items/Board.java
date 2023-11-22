package org.example.Items;

import org.example.Drawable;
import org.example.Movable;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Board implements Drawable, Movable {
    private static final double LENGTH = 200.0;
    private static final double HEIGHT = 30.0;

    private final double[] bounds = {0.0, 0.0};

    private double dragRelativeX = 0.0;
    private final Rectangle shape;

    public Board(double canvasDimX, double canvasDimY, String colour) {
        this.shape = new Rectangle(0, canvasDimY - 3 * HEIGHT, LENGTH, HEIGHT);
        this.shape.setFill(Color.valueOf(colour));
        bounds[0] = canvasDimX;
        bounds[1] = canvasDimY;
    }

    @Override
    public Node getNode() {
        return this.shape;
    }

    @Override
    public void addToGroup(ObservableList<Node> group) {
        group.add(this.shape);
    }

    public void registerMouseAction() {
        this.shape.setOnMousePressed(e -> {
            dragRelativeX = e.getSceneX() - shape.getX();
        });
        this.shape.setOnMouseDragged(e -> {
            double newX = e.getSceneX() - dragRelativeX;
            if (newX < 0 || newX + LENGTH > bounds[0]) return;
            this.shape.setX(newX);
        });
    }

    @Override
    public double getXPos() {
        return shape.getX();
    }

    @Override
    public double getYPos() {
        return shape.getY();
    }

    @Override
    public double getXVel() {
        return 0;
    }

    @Override
    public double getYVel() {
        return 0;
    }

    @Override
    public void setXPos(double xPos) {
        shape.setX(xPos);
    }

    @Override
    public void setYPos(double yPos) {
        shape.setY(yPos);
    }

    @Override
    public void setXVel(double xVel) {

    }

    @Override
    public void setYVel(double yVel) {

    }

    @Override
    public void move() {

    }
}
