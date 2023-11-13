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
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

public class HelloController {
    @FXML
    private Line stick;

    private boolean extending = false;
    private boolean extended = false; // Flag to track if the stick has been extended

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
            initialLength = stick.getEndY(); // Store the initial length
            extendTimeline.play();
        }
    }

    @FXML
    private void onKeyRelease(KeyEvent event) {
        if (event.getCode().equals(javafx.scene.input.KeyCode.SPACE) && extending && !extended) {
            extending = false;
            extended = true; // Set the flag to true after the first extension
            extendTimeline.stop();
            finalLength = stick.getEndY(); // Store the final length
            rotateStick();
        }
    }

    private void updateStick() {
        if (extending) {
            stick.setEndY(stick.getEndY() - extendSpeed);
        }
    }

    private void rotateStick() {
        // Calculate the pivot point for rotation (upper end of the stick)
        double pivotX = stick.getStartX();
        double pivotY = stick.getStartY();

        // Create a Rotate transformation with a pivot set to the calculated point
        Rotate rotate = new Rotate(0, pivotX, pivotY);
        stick.getTransforms().add(rotate);

        // Animation to rotate the stick to 90 degrees (clockwise)
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

class Rectangle {
    public static final int MIN_SIZE = 50;
    public static final int MAX_SIZE = 120;
    private int width;

    public Rectangle() {
        Random rand = new Random();
        width = 0;
        while (width < Rectangle.MIN_SIZE)
            width = rand.nextInt(Rectangle.MAX_SIZE);
    }

    public int getWidth() {
        return width;
    }
}