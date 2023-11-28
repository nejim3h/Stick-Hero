package com.example.demo;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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
import java.util.ArrayList;
import java.util.Random;

public class HelloController {
    @FXML
    private Pane gamepane;

    @FXML
    private ImageView character;

    @FXML
    private Button gameOverButton;

    @FXML
    protected void onButtonClick(ActionEvent event) {
        Platform.reset();
        Platform platform = Platform.generateRectangle(gamepane);
        gamepane.getChildren().add(platform);
    }

    @FXML
    private Label scoreLabel;
    @FXML
    private Label cherryScoreLabel;

    @FXML
    private Line stick;

    private int cherryCount = 0;
    private int score = 0;

    private int gameOverScore;

    private static ArrayList<Integer> gameScore = new ArrayList<>();

    public ArrayList<Integer> getArrayList() {
        return gameScore;
    }

    private int gameOverCherryScore;

    private double currentStickLength=0;

    private boolean extending = false;
    private boolean extended = false;

    private double initialLength;
    private double finalLength;

    private double extendSpeed = 3;

    private boolean isFlipped = false;

    private boolean ISRUNNING = false;

    private Timeline extendTimeline;

    public void initialize() {
        try {
            updateScoreLabel();
            updateCherryScoreLabel();
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

    private void updateCherryScoreLabel() {
        scoreLabel.setText(String.valueOf(cherryCount));
    }

    private boolean flip = false;
    @FXML
    private void onKeyPress(KeyEvent event) {
        if (event.getCode().equals(javafx.scene.input.KeyCode.SPACE) && !extending && !extended) {
            extending = true;
            initialLength = stick.getEndY();
            extendTimeline.play();
        }
        else if (event.getCode().equals(javafx.scene.input.KeyCode.SPACE)) {
            if (isCharacterMoving) {
                character.setScaleY(character.getScaleY() * -1);
                if (!isFlipped) {
                    character.setY(35);
                    isFlipped = true;
                    flip = true;
                } else {
                    character.setY(0);
                    isFlipped = false;
                }
            }
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
            Platform p = new Platform();
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

            if (Platform.isValidRange(currentStickLength)) {
                Platform p = new Platform();
                moveCharacter(character,p.getCurrentCoordinate());
                if(Platform.isStickOnRedSquare(currentStickLength)) {
                    score+=2;
                    updateScoreLabel();
                }
                else{
                    score++;
                    updateScoreLabel();
                }
            }
            else if(!isCharacterMoving && isFlipped){
                characterFall(character,400);
            }
            else {
                Platform.reset();
                ISRUNNING = false;
                gameScore.add(gameScore.size(),score);
                System.out.println(gameScore);
                moveCharacterAndFall(character, currentStickLength, 400);

            }

        });
        rotateTimeline.play();

    }


    private void moveCharacterAndFall(ImageView imageView, double x, double fallDistance) {
        TranslateTransition moveRight = new TranslateTransition(Duration.seconds(1), imageView);
        moveRight.setToX(imageView.getTranslateX() + x);

        TranslateTransition fallDown = new TranslateTransition(Duration.seconds(1), imageView);
        fallDown.setToY(fallDistance);

        SequentialTransition sequentialTransition = new SequentialTransition(moveRight, fallDown);

        sequentialTransition.setOnFinished(event -> {
            gameOverScore = getScore();
            gameOverCherryScore = getCherryScore();
            gameOverButton.fire();
        });

        sequentialTransition.play();
    }

    private int getCherryScore() {
        return cherryCount;
    }

    private boolean isCharacterMoving = false;

    private void moveCharacter(ImageView imageView, double x) {
        double currentX = imageView.getTranslateX();
        double distance = Math.abs(x);
        double speed = 125.0;
        double duration = distance / speed;
        int numFrames = (int) (duration * 60);

        javafx.animation.Timeline timeline = new javafx.animation.Timeline();
        final boolean[] collisionDetected = {false};

        for (int i = 0; i < numFrames; i++) {
            double currentTime = i * duration / numFrames;
            KeyFrame keyFrame = new KeyFrame(
                    javafx.util.Duration.seconds(currentTime),
                    event -> {
                        if (!collisionDetected[0] && isColliding(imageView, Cherry.getImage())) {
                            System.out.println("Collision detected!");
                            updateCherryScoreLabel(cherryScoreLabel);
                            Cherry.disappearCherry(Cherry.getImage());
                            collisionDetected[0] = true; // Set the flag to true after executing the method
                        }
                    }
            );
            timeline.getKeyFrames().add(keyFrame);
        }

        KeyFrame finalKeyFrame = new KeyFrame(
                javafx.util.Duration.seconds(duration),
                event -> {
                    isCharacterMoving = false;
                    Platform p = new Platform();
                    movePaneToLeft(gamepane, (p.getCurrentCoordinate()));
                    resetStick();
                    extended = false;
                }
        );
        timeline.getKeyFrames().add(finalKeyFrame);

        isCharacterMoving = true;
        timeline.play();

        TranslateTransition moveRight = new TranslateTransition(Duration.seconds(duration), imageView);
        moveRight.setToX(currentX + x);
        moveRight.play();
    }


    private boolean isColliding(ImageView character, ImageView cherry) {
        return character.getBoundsInParent().intersects(cherry.getBoundsInParent());
    }


    private void updateCherryScoreLabel(Label cherryScoreLabel) {
        cherryCount++;
        cherryScoreLabel.setText(String.valueOf(cherryCount));
    }


    private void characterFall(ImageView imageView, double fallDistance) {

        TranslateTransition fallDown = new TranslateTransition(Duration.seconds(1), imageView);
        fallDown.setToY(fallDistance);


        fallDown.setOnFinished(event -> {
            gameOverButton.fire();
        });

        fallDown.play();
    }


    public int getScore() {
        return score;
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
    protected void onGameOverButtonClick(ActionEvent event) {
        SceneController sc = new SceneController();
        try {
            sc.switchToGameOver(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onStartButtonClick(ActionEvent event) {
        SceneController sc = new SceneController();
        try {
            sc.switchToGame(event);
            ISRUNNING = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onLoadButtonClick(ActionEvent event) {
        SceneController sc = new SceneController();
        try {
            sc.switchToSavedMenu(event);
            ISRUNNING = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setScore(int x) {
        score = x;
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

    public void switchToSavedMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Save.fxml"));
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
    private static double GAP;

    public static double getX1() {
        return X1;
    }
    public static double getX2() {
        return X2;
    }

    public static double getGap() {
        return GAP;
    }
    private static final double dotSize = 10;

    public Platform() {
    }

    private static boolean isFirstPlatform = true;

    static Platform generateRectangle(Pane gamePane) {
        int gap = random.nextInt(MAX_GAP - MIN_GAP) + MIN_GAP;
        GAP = gap;
        double r = random.nextInt(41) + 20;
        double newX = currentX + previousGap + r + 270;

        int rectangleWidth = random.nextInt(MAX_RECTANGLE_WIDTH - MIN_RECTANGLE_WIDTH) + MIN_RECTANGLE_WIDTH;
        setRectangleWidth(rectangleWidth);
        Rectangle rectangle = new Rectangle(rectangleWidth, HEIGHT);
        rectangle.setFill(Color.BLACK);
        rectangle.setX(newX);
        rectangle.setY(50);

        Rectangle redDot = new Rectangle(newX + rectangleWidth / 2 - dotSize / 2, 50, dotSize, dotSize);
        redDot.setFill(Color.RED);

        setCurrentCoordinate(previousGap + rectangleWidth + r);
        X1 = previousGap + r;
        X2 = previousGap + rectangleWidth + r;

        gamePane.getChildren().addAll(rectangle, redDot);

        if (!isFirstPlatform ) {
//            && Cherry.generateRandomBoolean()
            Cherry.generateCherry(gamePane, newX-previousGap, newX);
        } else {
            isFirstPlatform = false;
        }

        currentX += previousGap + rectangleWidth + r;
        previousGap = gap;

        return null;
    }




    public static boolean isValidRange(double stickLength) {
        return stickLength >= X1 && stickLength <= X2;
    }

    public static boolean isStickOnRedSquare(double stickLength) {
        double redSquareStart = X1 + getRectangleWidth()/2 - dotSize/2;
        double redSquareEnd = redSquareStart + dotSize;
        return stickLength >= redSquareStart && stickLength <= redSquareEnd;
    }

    public static void reset() {
        currentCoordinate = 0;
        currentY = HEIGHT - MIN_RECTANGLE_WIDTH;
        previousGap = 0;
        currentX = 0;
        RectangleWidth = 0;
        isFirstPlatform = true;
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
    private static double positionX;
    private static double positionY;
    private static int cherryCount;

    private static ImageView cherryImageView;



    public Cherry() {
    }

    public static double getPositionX() {
        return positionX;
    }

    public static double getPositionY() {
        return positionY;
    }

    public int getCherryCount() {
        return cherryCount;
    }

    public static void setPositionX(double positionX) {
        Cherry.positionX = positionX;
    }

    public static void setPositionY(double positionY) {
        Cherry.positionY = positionY;
    }

    public static void collectCherry() {
        cherryCount++;
    }

    public static boolean generateRandomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }



    public static void generateCherry(Pane pane, double x1, double x2) {
        Image image = new Image(Cherry.class.getResourceAsStream("cherry.png"));
        cherryImageView = new ImageView(image);

        cherryImageView.setFitWidth(25);
        cherryImageView.setFitHeight(25);

        // Generate a random x-coordinate within the specified range
        double cherryX = Math.random() * (x2 - x1 - 25) + x1; // Adjusted for the cherry width (50)
        double cherryY = 55;
        setPositionX(cherryX);
        setPositionY(cherryY);
        cherryImageView.setX(cherryX);
        cherryImageView.setY(cherryY);

        pane.getChildren().add(cherryImageView);
    }

    public static ImageView getImage() {
        return cherryImageView;
    }

    public static void disappearCherry(ImageView imageView) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), imageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.play();
    }

}
