package com.example.demo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

public class HelloController {
    @FXML
    private Pane gamepane;

    @FXML
    protected void onButtonClick(ActionEvent event) {
        Platform platform = Platform.generateRectangle(gamepane);
        gamepane.getChildren().add(platform);
        double currentY = platform.getHeight();
    }


    @FXML
    private Line stick;

    private boolean extending = false;
    private boolean extended = false;

    private double initialLength;
    private double finalLength;

    private double extendSpeed = 3;

    private Timeline extendTimeline;

    public void initialize() {

        // Initialize timeline
        extendTimeline = new Timeline(new KeyFrame(Duration.millis(16), event -> updateStick()));
        extendTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    @FXML
    private void onKeyPress(KeyEvent event) {
        if (event.getCode().equals(javafx.scene.input.KeyCode.SPACE) && !extending && !extended) {
            extending = true;
            initialLength = stick.getEndY();
            extendTimeline.play();
        }
    }

    @FXML
    private void onKeyRelease(KeyEvent event) {
        if (event.getCode().equals(javafx.scene.input.KeyCode.SPACE) && extending && !extended) {
            extending = false;
            extended = true;
            extendTimeline.stop();
            finalLength = stick.getEndY();
            rotateStick();
        }
    }

    private void updateStick() {
        if (extending) {
            stick.setEndY(stick.getEndY() - extendSpeed);
        }
    }

    private void rotateStick() {
        double pivotX = stick.getStartX();
        double pivotY = stick.getStartY();

        Rotate rotate = new Rotate(0, pivotX, pivotY);
        stick.getTransforms().add(rotate);

        Timeline rotateTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), new javafx.animation.KeyValue(rotate.angleProperty(), 90))
        );
        rotateTimeline.play();
    }

    @FXML
    protected void onStartButtonClick(ActionEvent event) {
        SceneController sc = new SceneController();
        try {
            sc.switchToGame(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class SceneController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void SwitchToLaunch(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Launch.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Game.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}


class Platform extends Rectangle {

    private static final int MIN_RECTANGLE_WIDTH = 20;
    private static final int MAX_RECTANGLE_WIDTH = 100;
    private static final int MIN_GAP = 50;
    private static final int MAX_GAP = 200;

    private static final int HEIGHT = 200;

    private static double currentY = HEIGHT - MIN_RECTANGLE_WIDTH;

    private static int previousGap = 0;
    private static final Random random = new Random();

    private static double currentX;

    public Platform() {
    }

    static void initializeStartingRectangles(Pane gamePane) {
        int rectangleWidth1 = random.nextInt(MAX_RECTANGLE_WIDTH - MIN_RECTANGLE_WIDTH) + MIN_RECTANGLE_WIDTH;
        Rectangle rectangle1 = new Rectangle(rectangleWidth1, HEIGHT);
        rectangle1.setFill(Color.BLACK);
        rectangle1.setX(123 - rectangleWidth1);
        rectangle1.setY(0);

        int rectangleWidth2 = random.nextInt(MAX_RECTANGLE_WIDTH - MIN_RECTANGLE_WIDTH) + MIN_RECTANGLE_WIDTH;
        Rectangle rectangle2 = new Rectangle(rectangleWidth2, HEIGHT);
        rectangle2.setFill(Color.BLACK);
        rectangle2.setX(123);
        rectangle2.setY(0);

        gamePane.getChildren().addAll(rectangle1, rectangle2);

        currentX = rectangle2.getX() + rectangleWidth2;
        previousGap = random.nextInt(MAX_GAP - MIN_GAP) + MIN_GAP;
    }

    static Platform generateRectangle(Pane gamePane) {
        int gap = random.nextInt(MAX_GAP - MIN_GAP) + MIN_GAP;

        double newX = currentX + previousGap;

        int rectangleWidth = random.nextInt(MAX_RECTANGLE_WIDTH - MIN_RECTANGLE_WIDTH) + MIN_RECTANGLE_WIDTH;

        Rectangle rectangle = new Rectangle(rectangleWidth, HEIGHT);
        rectangle.setFill(Color.BLACK);
        rectangle.setX(newX);
        rectangle.setY(0);

        gamePane.getChildren().add(rectangle);

        // Adjust the transition to move only by the previous gap
//        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), gamePane);
//        transition.setToX(123 - newX); // Move the pane to reach x = 123 smoothly
//        transition.play();

        currentX += previousGap + rectangleWidth;
        previousGap = gap;

        return null;
    }

public double getCurrentX() {
        return currentX;
    }
}


class Cherry {
    private double positionX;
    private double positionY;
    private int cherryCount;

    public Cherry(double positionX, double positionY, int cherryCount) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.cherryCount = cherryCount;
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public int getCherryCount() {
        return cherryCount;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public void collectCherry() {
        cherryCount++;
    }

    public void generateRandomCherry() {
        Random random = new Random();
        this.positionX = random.nextDouble() * 900;
        this.positionY = random.nextDouble() * 600;
    }
}
