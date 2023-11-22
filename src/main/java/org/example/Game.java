package org.example;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Pair;
import org.example.Items.Ball;
import org.example.Items.Board;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;

import java.util.Arrays;
import java.util.Vector;

public class Game {
    private static final double GRAVITY = 10.0 / 60;
    private static final double EPSILON = 0.0001;
    private static final double RADIUS = 15.0;
    private static final double hole_RADIUS = 20.0;
    private static final double dxy = 5;
    private boolean isHit = false;
    private final double FRICTION;
    private final Vector<Ball> balls;
    private final Circle[] holes;
    private int inholeNum;
    private final Circle circle;
    private final Line line;
    private final Canvas canvas;
    private final double[] canvasDim = {0.0, 0.0};

    public Game(double canvasDimX, double canvasDimY) {
        canvasDim[0] = canvasDimX;
        canvasDim[1] = canvasDimY;
        this.FRICTION = ConfigReader.getFriction();
        this.balls = ConfigReader.getBalls();
        this.canvas = ConfigReader.getCanvas();

        double table_x = this.canvas.getWidth();
        double table_y = this.canvas.getHeight();

        this.circle = new Circle(0, 0, RADIUS);
        circle.setStrokeWidth(2.0f); // 设置边框宽度
        circle.setStroke(Color.BLACK); // 设置边框颜色
        circle.setFill(Color.TRANSPARENT);
        circle.setVisible(false);

        this.line = new Line();
        line.setVisible(false);

        holes = new Circle[6];
        holes[0] = new Circle(dxy, dxy, hole_RADIUS, Color.BLACK);
        holes[1] = new Circle(table_x / 2, dxy, hole_RADIUS, Color.BLACK);
        holes[2] = new Circle(table_x - dxy, dxy, hole_RADIUS, Color.BLACK);
        holes[3] = new Circle(dxy, table_y - dxy, hole_RADIUS, Color.BLACK);
        holes[4] = new Circle(table_x / 2, table_y - dxy, hole_RADIUS, Color.BLACK);
        holes[5] = new Circle(table_x - dxy, table_y - dxy, hole_RADIUS, Color.BLACK);

        inholeNum = 0;
    }

    public void addDrawables(Group root) {
        ObservableList<Node> groupChildren = root.getChildren();
        groupChildren.add(canvas);
        for (Ball ball : balls) {
            ball.addToGroup(groupChildren);
        }
        groupChildren.add(circle);
        groupChildren.add(line);
        groupChildren.addAll(Arrays.asList(holes));
        registerMouseAction(root);
    }

    public double cal_power(double x) {
        return x * x;
    }

    public double cal_abs(double x) {
        if (x < 0) {
            return -x;
        }
        return x;
    }

    public boolean ball_intersect(int i, int j) {
        double d = cal_power(balls.get(i).getXPos() - balls.get(j).getXPos()) +
                cal_power(balls.get(i).getYPos() - balls.get(j).getYPos());
        return d <= cal_power(2 * RADIUS);
    }

    // 监听鼠标击打白球动作
    public void registerMouseAction(Group root) {
        root.setOnMousePressed(e -> {
            // 击打白球以白球中心点为击打点
            if (cal_abs(balls.get(0).getXVel()) <= EPSILON && cal_abs(balls.get(0).getYVel()) <= EPSILON) {
                double fix_x = balls.get(0).getXPos();
                double fix_y = balls.get(0).getYPos();
                double move_x = e.getSceneX();
                double move_y = e.getSceneY();
                double d = cal_power(move_x - fix_x) + cal_power(move_y - fix_y);
                circle.setCenterX(move_x);
                circle.setCenterY(move_y);
                circle.setVisible(true);
                if (d <= cal_power(RADIUS)) {
                    line.setStartX(fix_x);
                    line.setStartY(fix_y);
                    isHit = true;
                } else {
                    isHit = false;
                }
            }

        });
        // 拖拽鼠标时显示响应图案
        root.setOnMouseDragged(e -> {
            if (cal_abs(balls.get(0).getXVel()) <= EPSILON && cal_abs(balls.get(0).getYVel()) <= EPSILON) {
                if (isHit) {
                    double move_x = e.getSceneX();
                    double move_y = e.getSceneY();
                    line.setEndX(move_x);
                    line.setEndY(move_y);
                    line.setVisible(true);
                    circle.setCenterX(move_x);
                    circle.setCenterY(move_y);
                    circle.setVisible(true);
                }
            }
        });
        // 释放鼠标时赋予白球初速度
        root.setOnMouseReleased(e -> {
            if (cal_abs(balls.get(0).getXVel()) <= EPSILON && cal_abs(balls.get(0).getYVel()) <= EPSILON) {
                circle.setVisible(false);
                line.setVisible(false);
                if (isHit) {
                    double new_x = line.getStartX() - line.getEndX() ;
                    double new_y = line.getStartY() - line.getEndY();
                    balls.get(0).setXVel(new_x / 20);
                    balls.get(0).setYVel(new_y / 20);
                }
                isHit = false;
            }
        });
    }

    // tick() is called every frame, handle main game logic here
    public void tick() {
        for (Ball ball : balls) {
            double vel_x = ball.getXVel();
            double vel_y = ball.getYVel();
            // 处理球运动受摩檫力影响
            if (cal_abs(vel_x) > EPSILON || cal_abs(vel_y) > EPSILON) {
                if (vel_x > 0) {
                    ball.setXVel(Math.max(0, vel_x - FRICTION * Main.FRAMETIME));
                } else {
                    ball.setXVel(Math.min(0, vel_x + FRICTION * Main.FRAMETIME));
                }

                if (vel_y > 0) {
                    ball.setYVel(Math.max(0, vel_y - FRICTION * Main.FRAMETIME));
                } else {
                    ball.setYVel(Math.min(0, vel_y + FRICTION * Main.FRAMETIME));
                }
            }

        }

        // 处理碰撞
        handleCollision();
    }

    public void handleCollision() {
        Bounds canvasBounds = canvas.getBoundsInLocal();

        for (int i = 0; i < balls.size(); i++) {
            balls.get(i).move();

            // ball与桌壁碰撞
            Bounds ballBounds = balls.get(i).getNode().getBoundsInLocal();
            if (ballBounds.getMinX() <= canvasBounds.getMinX() ||
                    ballBounds.getMaxX() >= canvasBounds.getMaxX()) {
                balls.get(i).setXVel(-balls.get(i).getXVel());
            }
            if (ballBounds.getMinY() <= canvasBounds.getMinY() ||
                    ballBounds.getMaxY() >= canvasBounds.getMaxY()) {
                balls.get(i).setYVel(-balls.get(i).getYVel());
            }

            // ball进洞
            for (int j = 0; j < 6; j++) {
                double d = cal_power(balls.get(i).getXPos() - holes[j].getCenterX()) +
                        cal_power(balls.get(i).getYPos() - holes[j].getCenterY());
                // 球中心点在洞范围内视为入洞
                if (d <= cal_power(hole_RADIUS)) {
                    if (balls.get(i).getColour().equals(Color.RED) ||
                            (balls.get(i).getColour().equals(Color.BLUE) && balls.get(i).getEverhole())) {
                        balls.get(i).setXPos(-RADIUS);
                        balls.get(i).setYPos(-RADIUS);
                        inholeNum = inholeNum + 1;
                        if (inholeNum == balls.size() - 1) {
                            reset();
                            inholeNum = 0;
                        }
                    } else {
                        balls.get(i).setEverhole(true);
                        balls.get(i).setXPos(balls.get(i).getStart_x());
                        balls.get(i).setYPos(balls.get(i).getStart_y());
                    }
                    balls.get(i).setXVel(0);
                    balls.get(i).setYVel(0);
                }
            }

            // ball之间碰撞
            for (int j = 0; j < balls.size(); j ++ ) {
                if (i != j) {
                    if (ball_intersect(i , j)) {
                        Point2D positionA = new Point2D(balls.get(i).getXPos(), balls.get(i).getYPos());
                        Point2D positionB = new Point2D(balls.get(j).getXPos(), balls.get(j).getYPos());
                        Point2D velocityA = new Point2D(balls.get(i).getXVel(), balls.get(i).getYVel());
                        Point2D velocityB = new Point2D(balls.get(j).getXVel(), balls.get(j).getYVel());

                        Pair<Point2D, Point2D> cal = calculateCollision(positionA, velocityA, balls.get(i).getMass(),
                                positionB, velocityB, balls.get(j).getMass());
                        balls.get(i).setXVel(cal.getKey().getX());
                        balls.get(i).setYVel(cal.getKey().getY());
                        balls.get(j).setXVel(cal.getValue().getX());
                        balls.get(j).setYVel(cal.getValue().getY());
                    }
                }
            }
        }
    }

    // 球之间碰撞处理函数
    public static Pair<Point2D, Point2D> calculateCollision(Point2D positionA, Point2D velocityA, double massA, Point2D positionB, Point2D velocityB, double massB) {
        Point2D collisionVector = positionA.subtract(positionB);
        collisionVector = collisionVector.normalize();
        double vA = collisionVector.dotProduct(velocityA);
        double vB = collisionVector.dotProduct(velocityB);

        if (vB <= 0 && vA >= 0) {
            return new Pair<>(velocityA, velocityB);
        }

        double optimizedP = (2.0 * (vA - vB)) / (massA + massB);
        Point2D velAPrime = velocityA.subtract(collisionVector.multiply(optimizedP).multiply(massB));
        Point2D velBPrime = velocityB.add(collisionVector.multiply(optimizedP).multiply(massA));
        return new Pair<>(velAPrime, velBPrime);
    }

    // 除白球外全入洞后重置
    public void reset() {
        for (Ball ball : balls) {
            ball.setXPos(ball.getStart_x());
            ball.setYPos(ball.getStart_y());
            ball.setXVel(0);
            ball.setYVel(0);
        }
    }
}

