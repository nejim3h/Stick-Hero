package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

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