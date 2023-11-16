package com.example.demo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class StickHero extends Application {

    private static final int CANVAS_WIDTH = 375;
    private static final int CANVAS_HEIGHT = 375;
    private static final int PLATFORM_HEIGHT = 100;
    private static final int HERO_DISTANCE_FROM_EDGE = 10;

    private Canvas canvas;
    private GraphicsContext gc;
    private Button restartButton;
    private int score = 0;

    private enum Phase {
        WAITING, STRETCHING, TURNING, WALKING, TRANSITIONING, FALLING
    }

    private Phase phase = Phase.WAITING;
    private long lastTimestamp;
    private double heroX;
    private double heroY;
    private double sceneOffset;
    private Stick[] sticks;
    private Platform[] platforms;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        restartButton = new Button("RESTART");
        restartButton.setOnAction(e -> resetGame());

        StackPane root = new StackPane();
        root.getChildren().addAll(canvas, restartButton);

        Scene scene = new Scene(root, CANVAS_WIDTH, CANVAS_HEIGHT);

        primaryStage.setTitle("Stick Hero");
        primaryStage.setScene(scene);
        primaryStage.show();

        resetGame();
        setupGameLoop();
    }

    private void resetGame() {
        phase = Phase.WAITING;
        lastTimestamp = 0;
        sceneOffset = 0;
        score = 0;
        updateScore();

        platforms = new Platform[]{new Platform(50, 50)};
        generatePlatforms();
        sticks = new Stick[]{new Stick(platforms[0].getX() + platforms[0].getWidth(), 0, 0)};


        heroX = platforms[0].getX() + platforms[0].getWidth() - HERO_DISTANCE_FROM_EDGE;
        heroY = 0;

        draw();
    }

    private void generatePlatforms() {
        for (int i = 1; i <= 4; i++) {
            double gap = Math.random() * 150 + 40;
            double width = Math.random() * 80 + 20;

            double x = platforms[i - 1].getX() + platforms[i - 1].getWidth() + gap;
            platforms[i] = new Platform(x, width);
        }
    }

    private void updateScore() {
        restartButton.setText("RESTART\nScore: " + score);
    }

    private void setupGameLoop() {
        Duration duration = Duration.millis(16);
        KeyFrame keyFrame = new KeyFrame(duration, event -> gameLoop());
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void gameLoop() {
        long timestamp = System.currentTimeMillis();

        if (lastTimestamp == 0) {
            lastTimestamp = timestamp;
            return;
        }

        switch (phase) {
            case WAITING:
                break;
            case STRETCHING:
                sticks[sticks.length - 1].stretch((timestamp - lastTimestamp) / 4.0);
                break;
            case TURNING:
                sticks[sticks.length - 1].turn((timestamp - lastTimestamp) / 4.0);

                if (sticks[sticks.length - 1].getRotation() > 90) {
                    sticks[sticks.length - 1].setRotation(90);
                    boolean perfectHit = checkPerfectHit();
                    if (perfectHit) {
                        score += 2;
                        updateScore();
                    } else {
                        score += 1;
                        updateScore();
                    }

                    generatePlatforms();
                    phase = Phase.WALKING;
                }
                break;
            case WALKING:
                heroX += (timestamp - lastTimestamp) / 4.0;
                double maxHeroX = calculateMaxHeroX();

                if (heroX > maxHeroX) {
                    heroX = maxHeroX;
                    phase = Phase.TRANSITIONING;
                }
                break;
            case TRANSITIONING:
                sceneOffset += (timestamp - lastTimestamp) / 2.0;
                double maxTransitionX = calculateMaxTransitionX();

                if (sceneOffset > maxTransitionX) {
                    sticks = addStick(sticks);
                    phase = Phase.WAITING;
                }
                break;
            case FALLING:
                break;
        }

        draw();
        lastTimestamp = timestamp;
    }

    private boolean checkPerfectHit() {
        double perfectAreaX = platforms[platforms.length - 2].getX() + platforms[platforms.length - 2].getWidth() / 2 - 5;
        double stickFarX = sticks[sticks.length - 1].getX() + sticks[sticks.length - 1].getLength();

        return (perfectAreaX <= stickFarX && stickFarX <= perfectAreaX + 10);
    }

    private double calculateMaxHeroX() {
        Platform nextPlatform = platforms[platforms.length - 1];
        return nextPlatform.getX() + nextPlatform.getWidth() - HERO_DISTANCE_FROM_EDGE;
    }

    private double calculateMaxTransitionX() {
        Platform nextPlatform = platforms[platforms.length - 1];
        return nextPlatform.getX() + nextPlatform.getWidth() - 100; // Adjust this value as needed
    }

    private void draw() {
        gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        drawBackground();
        drawPlatforms();
        drawHero();
        drawSticks();
    }

    private void drawBackground() {
        // Implement background drawing logic (sky, hills, etc.)
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
    }

    private void drawPlatforms() {
        for (Platform platform : platforms) {
            double platformHeight = PLATFORM_HEIGHT + (canvas.getHeight() - CANVAS_HEIGHT) / 2;
            gc.setFill(Color.BLACK);
            gc.fillRect(platform.getX(), canvas.getHeight() - platformHeight, platform.getWidth(), platformHeight);

            // Draw perfect area only if hero did not yet reach the platform
            if (sticks[sticks.length - 1].getX() < platform.getX()) {
                double perfectAreaX = platform.getX() + platform.getWidth() / 2 - 5;
                gc.setFill(Color.RED);
                gc.fillRect(perfectAreaX, canvas.getHeight() - platformHeight, 10, 10);
            }
        }
    }

    private void drawHero() {
        gc.save();
        gc.setFill(Color.BLACK);

        double translateY = canvas.getHeight() - PLATFORM_HEIGHT - heroY - HERO_DISTANCE_FROM_EDGE;

        gc.translate(heroX - 10, translateY);

        gc.fillRect(0, 0, 20, 30); // Simple representation of the hero

        gc.restore();
    }

    private void drawSticks() {
        gc.save();

        for (Stick stick : sticks) {
            gc.translate(stick.getX(), canvas.getHeight() - PLATFORM_HEIGHT);

            double stickEndY = stick.getLength() * Math.sin(Math.toRadians(stick.getRotation()));
            gc.fillRect(0, 0, stick.getLength(), stickEndY); // Simple representation of the stick

            gc.translate(-stick.getX(), -(canvas.getHeight() - PLATFORM_HEIGHT));
        }

        gc.restore();
    }

    private Stick[] addStick(Stick[] oldSticks) {
        Stick[] newSticks = new Stick[oldSticks.length + 1];
        System.arraycopy(oldSticks, 0, newSticks, 0, oldSticks.length);

        double x = platforms[platforms.length - 1].getX() + platforms[platforms.length - 1].getWidth();
        double length = 20; // Adjust this value as needed
        double rotation = 0;

        newSticks[newSticks.length - 1] = new Stick(x, length, rotation);

        return newSticks;
    }

    private class Platform {
        private double x;
        private double width;

        public Platform(double x, double width) {
            this.x = x;
            this.width = width;
        }

        public double getX() {
            return x;
        }

        public double getWidth() {
            return width;
        }
    }

    private class Stick {
        private double x;
        private double length;
        private double rotation;

        public Stick(double x, double length, double rotation) {
            this.x = x;
            this.length = length;
            this.rotation = rotation;
        }

        public double getX() {
            return x;
        }

        public double getLength() {
            return length;
        }

        public double getRotation() {
            return rotation;
        }

        public void stretch(double deltaLength) {
            length += deltaLength;
        }

        public void turn(double deltaRotation) {
            rotation += deltaRotation;
        }

        public void setRotation(double rotation) {
            this.rotation = rotation;
        }
    }
}
