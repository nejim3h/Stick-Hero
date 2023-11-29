package com.example.demo;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;

public class Save {

    @FXML
    private ListView<String> myListView;

    private ArrayList<Pair<Integer, Integer>> savedGames = new ArrayList<>();

    @FXML
    private Label myLabel;

    public void setSavedGames(ArrayList<Pair<Integer, Integer>> savedGames) {
        this.savedGames = savedGames;
        updateListView();
    }

    @FXML
    protected void onMainMenuButtonClick(ActionEvent event) {
        SceneController sc = new SceneController();
        try {
//            System.out.println(savedGames.get(0));
            sc.setSavedGames(savedGames);
            sc.switchToMainMenu(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateListView() {
        // Update the ListView content
        ArrayList<String> stringSavedGames = new ArrayList<>();
        for (int i = 0; i < savedGames.size(); i++) {
            stringSavedGames.add(i + ". Score = " + savedGames.get(i).getKey() +
                    " Cherries collected = " + savedGames.get(i).getValue());
        }
        ObservableList<String> items = FXCollections.observableArrayList(stringSavedGames);
        myListView.setItems(items);

        myListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String selectedItem = myListView.getSelectionModel().getSelectedItem();
                for(int i=0;i<savedGames.size();i++){
                    if(i==Character.getNumericValue(selectedItem.charAt(0))) {
                        FXMLLoader gameOverLoader = new FXMLLoader(getClass().getResource("Game.fxml"));
                        Parent gameOverRoot = null;
                        try {
                            gameOverRoot = gameOverLoader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        HelloController gameOverController = gameOverLoader.getController();
                        gameOverController.setSavedGames(savedGames);
                        gameOverController.setGameScore(savedGames.get(i).getKey());
                        gameOverController.setCherryScore(savedGames.get(i).getValue());
                        Scene gameOverScene = new Scene(gameOverRoot);
                        Stage primaryStage = (Stage) myListView.getScene().getWindow();
                        primaryStage.setScene(gameOverScene);
                        primaryStage.show();
                        break;
                    }
                }
            }
        });
    }


//    @Override
//    public void initialize(URL arg0, ResourceBundle arg1) {
//
//    }
}
