package com.example.demo;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Save implements Initializable{

    @FXML
    private ListView<String> myListView;

    @FXML
    private Label myLabel;


    @FXML
    protected void onMainMenuButtonClick(ActionEvent event) {
        SceneController sc = new SceneController();
        try {
            sc.switchToMainMenu(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    HelloController hc = new HelloController();
    Integer[] games = hc.getArrayList().toArray(new Integer[0]);

    String currentGame;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        // Add each element individually to the ListView
        for (Integer game : games) {
            myListView.getItems().add(String.valueOf(game));
        }

        myListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {

                currentGame = myListView.getSelectionModel().getSelectedItem();

                myLabel.setText(currentGame);

            }
        });
    }
}
