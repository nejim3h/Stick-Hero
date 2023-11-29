package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;

public class PauseController {
    int gameScore;
    int cherryScore;

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
        SceneController sc = new SceneController();
        try {
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
        SceneController sc = new SceneController();
        try {
//            System.out.println(savedGames.get(0));
            sc.setSavedGames(savedGames);
            sc.switchToMainMenu(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onSaveButtonClick(ActionEvent event){
        savedGames.add(new Pair<>(gameScore,cherryScore));
//        System.out.println(savedGames.get(0));
        System.out.println("Saved!");
    }

}
