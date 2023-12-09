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
import javafx.util.Pair;

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

    private Platform currentPlatform;

    public boolean platformInit;

    MusicPlayer mp = new MusicPlayer();

    private ArrayList<Pair<Integer,Integer>> savedGames= new ArrayList<>();

    public void setSavedGames(ArrayList<Pair<Integer,Integer>>savedGames) {
        this.savedGames = savedGames;
    }

    @FXML
    protected void onButtonClick(ActionEvent event) {
        if (currentPlatform != null) {
            gamepane.getChildren().remove(currentPlatform);
        }

        Platform.reset();
        currentPlatform = Platform.generateRectangle(gamepane);

        gamepane.getChildren().add(currentPlatform);
        platformInit = true;
    }

    @FXML
    Label scoreLabel;

    @FXML
    Label highestScore;

    @FXML
    private Label cherryScoreLabel;

    @FXML
    private Line stick;

    private int cherryCount = 0;
    private int score = 0;

    private int highScore = 0;

    public void setHighScore(int highScore) {
        this.highScore = highScore;
        updateHighScoreLabel();
    }

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
            updateHighScoreLabel();
            updateCherryScoreLabel();
            extendTimeline = new Timeline(new KeyFrame(Duration.millis(16), event -> updateStick()));
            try {
                extendTimeline.setCycleCount(Timeline.INDEFINITE);
                onButtonClick(null);
            }catch (NullPointerException n){}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Timeline getExtendTimeline() {
        return extendTimeline;
    }

    void updateScoreLabel() {
        try {
            scoreLabel.setText(String.valueOf(score));
        }
        catch (NullPointerException n) {

        }
    }

    void updateHighScoreLabel() {
        try {
            highestScore.setText(String.valueOf(highScore));
        }
        catch (NullPointerException n) {

        }
    }

    private void updateCherryScoreLabel() {
        try {
            cherryScoreLabel.setText(String.valueOf(cherryCount));
        }
        catch (NullPointerException n) {

        }
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
        mp.playSound("levelUp");
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), pane);
        double initialTranslateX = pane.getTranslateX();
        transition.setToX(initialTranslateX - x);
        if(isFlipped) {
            characterFall(character, 400);
        }

        transition.setOnFinished(event -> {
            Platform p = new Platform();
            Platform platform = Platform.generateRectangle(gamepane);
            try {
                gamepane.getChildren().add(platform);
            }catch (NullPointerException n) {}

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
        mp.playSound("stick");

        Rotate rotate = new Rotate(0, pivotX, pivotY);
        stick.getTransforms().add(rotate);

        Timeline rotateTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.75), new javafx.animation.KeyValue(rotate.angleProperty(), 90))
        );

        rotateTimeline.setOnFinished(event -> {

            if (Platform.isValidRange(currentStickLength)) {
                Platform p = new Platform();
                moveCharacter(character,p.getCurrentCoordinate(),null);
                if(Platform.isStickOnRedSquare(currentStickLength)) {
                    score+=2;
                    updateScoreLabel();
                    if(highScore<score) {
                        setHighScore(score);
                        updateHighScoreLabel();
                    }

                }
                else{
                    score++;
                    updateScoreLabel();
                    if(highScore<score) {
                        setHighScore(score);
                        updateHighScoreLabel();
                    }
                }
            }
            else {
                Platform.reset();
                ISRUNNING = false;
                moveCharacter(character, currentStickLength , () -> characterFall(character, 400));
            }

        });
        rotateTimeline.play();
    }


    public int getCherryScore() {
        return cherryCount;
    }

    private boolean isCharacterMoving = false;

    private void moveCharacter(ImageView imageView, double x, Runnable callback) {
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
                        boolean intersection = false;
                        try {
                            intersection = character.getBoundsInParent().intersects(Cherry.getImage().getBoundsInParent());
                        }catch (NullPointerException n ){}

                        if (!collisionDetected[0] && intersection) {
                            updateCherryScoreLabel(cherryScoreLabel);
                            Cherry.disappearCherry(Cherry.getImage());
                            collisionDetected[0] = true;
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

                    if (callback != null) {
                        callback.run();
                    }
                }
        );
        timeline.getKeyFrames().add(finalKeyFrame);

        isCharacterMoving = true;
        timeline.play();

        TranslateTransition moveRight = new TranslateTransition(Duration.seconds(duration), imageView);
        moveRight.setToX(currentX + x);
        moveRight.play();
    }



    private boolean isColliding(ImageView character, ImageView cherry){
        return character.getBoundsInParent().intersects(cherry.getBoundsInParent());
    }


    public void setCherryScore(int count) {
        cherryCount= count;
        updateCherryScoreLabel();
    }

    public void setGameScore(int count) {
        score= count;
        updateScoreLabel();
    }


    public void updateCherryScoreLabel(Label cherryScoreLabel) {
        cherryCount++;
        cherryScoreLabel.setText(String.valueOf(cherryCount));
    }

    private void characterFall(ImageView imageView, double fallDistance) {
        mp.playSound("fall");
        TranslateTransition fallDown = new TranslateTransition(Duration.seconds(2), imageView);
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
        SceneController sc = SceneController.getInstance();
        try {
            sc.setHighScore(highScore);
            sc.setSavedGames(savedGames);
            sc.switchToMainMenu(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void onGameOverButtonClick(ActionEvent event) {
        SceneController sc = SceneController.getInstance();
        try {
            sc.setHighScore(highScore);
            sc.setGameScore(getScore());
            sc.setCherryCount(getCherryScore());
            sc.setSavedGames(savedGames);
            try {
                sc.switchToGameOver(event);
            }catch (NullPointerException n){}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onStartButtonClick(ActionEvent event) {
        SceneController sc = SceneController.getInstance();
        try {
            sc.setHighScore(highScore);
            sc.setSavedGames(savedGames);
            sc.switchToGame(event);
            ISRUNNING = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onLoadButtonClick(ActionEvent event) {
        SceneController sc = SceneController.getInstance();
        try {
            sc.setHighScore(highScore);
            sc.setSavedGames(savedGames);
            sc.switchToSavedMenu(event);
            ISRUNNING = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onReviveButtonClick(ActionEvent event) {
        if(getCherryScore()>=2) {
            SceneController sc = SceneController.getInstance();
            try {
                sc.setHighScore(highScore);
                sc.setGameScore(getScore());
                sc.setSavedGames(savedGames);
                sc.setCherryCount(getCherryScore() - 2);
                sc.switchToRevivedGame(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void onPauseButtonClick(ActionEvent event) {
        SceneController sc = SceneController.getInstance();
        try {
            sc.setHighScore(highScore);
            sc.setGameScore(getScore());
            sc.setCherryCount(getCherryScore());
            sc.setSavedGames(savedGames);
            sc.switchToPauseMenu(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Singleton Design Pattern used for SceneController
class SceneController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private int highScore;
    private int gameScore;
    private int cherryCount;

    private static SceneController mySc = null;

    private SceneController (){}

    public static SceneController getInstance(){
        if(mySc==null){
            mySc= new SceneController();
        }
        return mySc;
    }

    private ArrayList<Pair<Integer,Integer>> savedGames = new ArrayList<>();

    public void setSavedGames(ArrayList<Pair<Integer,Integer>>savedGames) {
        this.savedGames = savedGames;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public void setGameScore(int count){
        gameScore=count;
    }

    public void setCherryCount(int count){
        cherryCount=count;
    }

    public void switchToGameOver(ActionEvent event) throws IOException {
        FXMLLoader gameOverLoader = new FXMLLoader(getClass().getResource("GameOver.fxml"));
        Parent gameOverRoot = gameOverLoader.load();
        HelloController gameOverController = gameOverLoader.getController();
        System.out.println(highScore);
        gameOverController.setHighScore(highScore);
        gameOverController.setGameScore(gameScore);
        gameOverController.setCherryScore(cherryCount);
        gameOverController.setSavedGames(savedGames);
        Scene gameOverScene = new Scene(gameOverRoot);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            primaryStage.setScene(gameOverScene);
        }catch (NullPointerException n){}
        primaryStage.show();
    }

    public void switchToRevivedGame(ActionEvent event) throws IOException {
        FXMLLoader gameOverLoader = new FXMLLoader(getClass().getResource("Game.fxml"));
        Parent gameOverRoot = gameOverLoader.load();
        HelloController gameOverController = gameOverLoader.getController();
        gameOverController.setHighScore(highScore);
        gameOverController.setGameScore(gameScore);
        gameOverController.setCherryScore(cherryCount);
        gameOverController.setSavedGames(savedGames);
        Scene gameOverScene = new Scene(gameOverRoot);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(gameOverScene);
        primaryStage.show();
    }


    public void switchToGame(ActionEvent event) throws IOException {
        FXMLLoader gameOverLoader = new FXMLLoader(getClass().getResource("Game.fxml"));
        Parent gameOverRoot = gameOverLoader.load();
        HelloController gameOverController = gameOverLoader.getController();
        gameOverController.setSavedGames(savedGames);
        gameOverController.setHighScore(highScore);
        Scene gameOverScene = new Scene(gameOverRoot);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(gameOverScene);
        primaryStage.show();
    }

    public void switchToMainMenu(ActionEvent event) throws IOException {
        FXMLLoader gameOverLoader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
        Parent gameOverRoot = gameOverLoader.load();
        HelloController gameOverController = gameOverLoader.getController();
        gameOverController.setSavedGames(savedGames);
        gameOverController.setHighScore(highScore);
        Scene gameOverScene = new Scene(gameOverRoot);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(gameOverScene);
        primaryStage.show();
    }

    public void switchToSavedMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Save.fxml"));
        Parent root = loader.load();

        Save newController = loader.getController();

        newController.setSavedGames(savedGames);
        newController.setHighScore(highScore);
        Scene newScene = new Scene(root);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(newScene);
        primaryStage.show();
    }

    public void switchToPauseMenu(ActionEvent event) throws IOException {
        FXMLLoader gameOverLoader = new FXMLLoader(getClass().getResource("Pause.fxml"));
        Parent gameOverRoot = gameOverLoader.load();
        PauseController gameOverController = gameOverLoader.getController();
        gameOverController.setSavedGames(savedGames);
        gameOverController.setCherryScore(cherryCount);
        gameOverController.setGameScore(gameScore);
        gameOverController.setHighScore(highScore);
        Scene gameOverScene = new Scene(gameOverRoot);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(gameOverScene);
        primaryStage.show();
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
        try {
            gamePane.getChildren().addAll(rectangle, redDot);
        }catch (NullPointerException n) {

        }

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

        double cherryX = Math.random() * (x2 - x1 - 25) + x1;
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