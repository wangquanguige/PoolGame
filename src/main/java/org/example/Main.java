package org.example;



import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
public class Main extends Application {
    private static final String TITLE = "PoolGame";
    private static final double DIM_X = 612.0;
    private static final double DIM_Y = 335.0;
    public static final double FRAMETIME = 1.0/60.0;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);

        primaryStage.setTitle(TITLE);
        primaryStage.show();
        primaryStage.setWidth(DIM_X);
        primaryStage.setHeight(DIM_Y);

        ConfigReader.main();
        Game game = new Game(DIM_X, DIM_Y);
        game.addDrawables(root);

        // setup frames
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame frame = new KeyFrame(Duration.seconds(FRAMETIME), (actionEvent) -> game.tick());
        timeline.getKeyFrames().add(frame);
        timeline.play();
    }
}