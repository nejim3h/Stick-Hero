package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;

public class PauseController {
    private int gameScore;
    private int cherryScore;

    private int highScore;

    MusicPlayer mp = new MusicPlayer();

    private ArrayList<Pair<Integer,Integer>> savedGames= new ArrayList<>();

    public void setSavedGames(ArrayList<Pair<Integer,Integer>> savedGames) {
        this.savedGames = savedGames;
    }

    public void setCherryScore(int cherryScore) {
        this.cherryScore = cherryScore;
    }

    public void setGameScore(int gameScore) {
        this.gameScore = gameScore;
    }

    @FXML
    private void onResumeButtonClick(ActionEvent event){
        SceneController sc = SceneController.getInstance();
        try {
            sc.setHighScore(highScore);
            sc.setCherryCount(cherryScore);
            sc.setGameScore(gameScore);
            sc.setSavedGames(savedGames);
            sc.switchToRevivedGame(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onExitButtonClick(ActionEvent event){
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
    private void onSaveButtonClick(ActionEvent event){
        mp.playSound("gamesave");
        savedGames.add(new Pair<>(gameScore,cherryScore));
        System.out.println("Saved!");
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }
}