package org.example;

import org.example.Items.Ball;

public class Builder {
    public Ball build(double xPos, double yPos, double xVel, double yVel, double mass, String colour) {
        return new Ball(xPos, yPos, xVel, yVel, mass, colour);
    }
}
