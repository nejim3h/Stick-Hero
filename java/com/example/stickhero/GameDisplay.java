package com.example.stickhero;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameDisplay extends Application {
    private int score = 0;  // Initialize score
    private int cherries_count = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Stick Hero");
        stage.setResizable(false);
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 600);

        Text instructionsText = new Text("Hold your finger on the screen to stretch out the stick");
        instructionsText.setFont(Font.font("Arial", 20));
        instructionsText.setFill(Color.BLACK);
        instructionsText.setTranslateX(250);
        instructionsText.setTranslateY(120);

        Image backgroundImage = new Image("file:src/main/assets/background.png");
        ImageView backgroundImageView = new ImageView(backgroundImage);
        root.getChildren().add(backgroundImageView);

        Image characterImage = new Image("file:src/main/assets/character.png");
        ImageView characterImageView = new ImageView(characterImage);
        characterImageView.setTranslateX(50);
        characterImageView.setTranslateY(400);
        characterImageView.setFitWidth(30);
        characterImageView.setFitHeight(30);
        root.getChildren().add(characterImageView);

        Text scoreText = new Text("" + score);
        scoreText.setX(467.5);
        scoreText.setY(65);
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        scoreText.setFill(Color.WHITE);

        Text cherriesText = new Text("" + cherries_count);
        cherriesText.setX(950);  // Adjust the X position
        cherriesText.setY(50);   // Adjust the Y position
        cherriesText.setFont(Font.font("Arial", FontWeight.BOLD, 25));
        cherriesText.setFill(Color.BLACK);

        Image cherryImage = new Image("file:src/main/assets/cherry.png");
        ImageView cherryImageView = new ImageView(cherryImage);
        cherryImageView.setTranslateX(900);
        cherryImageView.setTranslateY(20);
        cherryImageView.setFitWidth(40);
        cherryImageView.setFitHeight(40);
        root.getChildren().add(cherryImageView);

        Rectangle scoreCard = new Rectangle(100, 50);
        scoreCard.setArcWidth(15);
        scoreCard.setArcHeight(15);
        scoreCard.setX(425);
        scoreCard.setY(30);
        scoreCard.setFill(Color.rgb(0, 0, 0, 0.4));

        root.getChildren().addAll(instructionsText, cherriesText, scoreCard, scoreText);

        stage.setScene(scene);
        stage.show();
    }
}

