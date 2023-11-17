package com.example.demo;

import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
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
    private ImageView character;

    @FXML
    protected void onButtonClick(ActionEvent event) {
        Platform.reset();
        Platform platform = Platform.generateRectangle(gamepane);
        gamepane.getChildren().add(platform);
    }

    @FXML
    private Label scoreLabel;

    @FXML
    private Line stick;

    private int score = 0;

    private double currentStickLength=0;

    private boolean extending = false;
    private boolean extended = false;

    private double initialLength;
    private double finalLength;

    private double extendSpeed = 3;

    private Timeline extendTimeline;

    public void initialize() {
        try {
            updateScoreLabel();
            extendTimeline = new Timeline(new KeyFrame(Duration.millis(16), event -> updateStick()));
            extendTimeline.setCycleCount(Timeline.INDEFINITE);
            onButtonClick(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateScoreLabel() {
        scoreLabel.setText(String.valueOf(score));
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
            double len = finalLength - initialLength;
            currentStickLength = Math.abs(len);
        }
    }

    private void resetStick() {
        stick.setStartX(270);
        stick.setEndX(270);
        stick.setStartY(400);
        stick.setEndY(400);

        stick.getTransforms().clear();
        extended = false;
    }


    public void movePaneToLeft(Pane pane, double x) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), pane);
        double initialTranslateX = pane.getTranslateX();
        transition.setToX(initialTranslateX - x);

        transition.setOnFinished(event -> {
             Platform platform = Platform.generateRectangle(gamepane);
             gamepane.getChildren().add(platform);

            resetStick();
        });

        transition.play();
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

        rotateTimeline.setOnFinished(event -> {
            Platform p = new Platform();
            if (Platform.isValidRange(currentStickLength)) {
                movePaneToLeft(gamepane, (p.getCurrentCoordinate()));
                resetStick();
                extended = false;
                if(Platform.isStickOnRedSquare(currentStickLength)) {
                    score+=2;
                    updateScoreLabel();
                }
                else{
                    score++;
                    updateScoreLabel();
                }
            }
            else {
                Platform.reset();
                moveCharacter(character, currentStickLength, 400);

            }
        });
        rotateTimeline.play();

    }

    private void moveCharacter(ImageView imageView, double x, double fallDistance) {
        TranslateTransition moveRight = new TranslateTransition(Duration.seconds(1), imageView);
        moveRight.setToX(imageView.getTranslateX() + x);

        TranslateTransition fallDown = new TranslateTransition(Duration.seconds(1), imageView);
        fallDown.setToY(fallDistance);

        SequentialTransition sequentialTransition = new SequentialTransition(moveRight, fallDown);
        sequentialTransition.play();
    }



    @FXML
    protected void onMainMenuButtonClick(ActionEvent event) {
        SceneController sc = new SceneController();
        try {
            sc.switchToMainMenu(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void switchToGameOver(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("GameOver.fxml"));
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

    public void switchToMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}


class Platform extends Rectangle {

    private static final int MIN_RECTANGLE_WIDTH = 20;
    private static final int MAX_RECTANGLE_WIDTH = 100;

    private static double currentCoordinate;
    private static final int MIN_GAP = 50;
    private static final int MAX_GAP = 200;


    private static final int HEIGHT = 200;

    private static double currentY = HEIGHT - MIN_RECTANGLE_WIDTH;

    private static int previousGap = 0;
    private static final Random random = new Random();

    private static double currentX;
    private static double RectangleWidth;

    private static double X1;
    private static double X2;

    public static double getX1() {
        return X1;
    }
    public static double getX2() {
        return X2;
    }
    private static final double dotSize = 6;

    public Platform() {
    }

    static Platform generateRectangle(Pane gamePane) {
        int gap = random.nextInt(MAX_GAP - MIN_GAP) + MIN_GAP;
        double r = random.nextInt(41) + 20;
        double newX = currentX + previousGap + r +  270;

        int rectangleWidth = random.nextInt(MAX_RECTANGLE_WIDTH - MIN_RECTANGLE_WIDTH) + MIN_RECTANGLE_WIDTH;
        setRectangleWidth(rectangleWidth);
        Rectangle rectangle = new Rectangle(rectangleWidth, HEIGHT);
        rectangle.setFill(Color.BLACK);
        rectangle.setX(newX);
        rectangle.setY(0);

        Rectangle redDot = new Rectangle(newX + rectangleWidth / 2 - dotSize / 2, 0, dotSize, dotSize);
        redDot.setFill(Color.RED);

        setCurrentCoordinate(previousGap+rectangleWidth+r);
        X1 = previousGap+r;
        X2 = previousGap+rectangleWidth+r;

        gamePane.getChildren().addAll(rectangle, redDot);

        currentX += previousGap + rectangleWidth + r;
        previousGap = gap;

        return null;
    }

    public static boolean isValidRange(double stickLength) {
        return stickLength >= X1 && stickLength <= X2;
    }

    public static boolean isStickOnRedSquare(double stickLength) {
        double redSquareStart = X1 + getRectangleWidth()/2 - dotSize/2;
        double redSquareEnd = redSquareStart + dotSize/2;
        return stickLength >= redSquareStart && stickLength <= redSquareEnd;
    }

    public static void reset() {
        currentCoordinate = 0;
        currentY = HEIGHT - MIN_RECTANGLE_WIDTH;
        previousGap = 0;
        currentX = 0;
        RectangleWidth = 0;
        X1 = 0;
        X2 = 0;
    }

    public static void setCurrentCoordinate(double currentCoordinate) {
        Platform.currentCoordinate = currentCoordinate;
    }

    public double getCurrentCoordinate(){
        return currentCoordinate;
    }

    public static void setRectangleWidth(double x) {
        RectangleWidth = x;
    }

    public static double getRectangleWidth(){
        return RectangleWidth;
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
