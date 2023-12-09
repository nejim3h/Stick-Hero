package com.example.demo;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

abstract class Sound {

    MediaPlayer myPlayer;

    abstract public void playMusic();

    public void stopMusic() {
        if (myPlayer!= null) {
            myPlayer.stop();
        }
    }
}

class FallSound extends Sound {
    @Override
    public void playMusic() {
        myPlayer = new MediaPlayer(new Media(new File("src/main/resources/music/fall.mp3").toURI().toString()));
        myPlayer.play();
    }

}

class gameMusic extends Sound {
    @Override
    public void playMusic() {
        myPlayer = new MediaPlayer(new Media(new File("src/main/resources/music/gameMusic.mp3").toURI().toString()));
        myPlayer.play();
    }

}


class gameSaveSound extends Sound {
    @Override
    public void playMusic() {
        myPlayer = new MediaPlayer(new Media(new File("src/main/resources/music/gameSaved.mp3").toURI().toString()));
        myPlayer.play();
    }

}

class levelUpSound extends Sound {
    @Override
    public void playMusic() {
        myPlayer = new MediaPlayer(new Media(new File("src/main/resources/music/new_platform.mp3").toURI().toString()));
        myPlayer.play();
    }

}

class StickSound extends Sound {
    @Override
    public void playMusic() {
        myPlayer = new MediaPlayer(new Media(new File("src/main/resources/music/stick_fall.mp3").toURI().toString()));
        myPlayer.play();
    }
}


//Facade design pattern used for MusicPlayer

public class MusicPlayer {
    Sound stick;
    Sound fall;
    Sound gameMusic;
    Sound gameSave;
    Sound levelUp;

    public MusicPlayer(){
        stick = new StickSound();
        gameMusic = new gameMusic();
        gameSave = new gameSaveSound();
        levelUp = new levelUpSound();
        fall  = new FallSound();
    }


    public void playSound(String type){
        if(type.equalsIgnoreCase("Fall")){
            fall.playMusic();
        }
        else if(type.equalsIgnoreCase("Stick")){
            stick.playMusic();
        }
        else if(type.equalsIgnoreCase("Gamesave")){
            gameSave.playMusic();
        }
        else if(type.equalsIgnoreCase("GameMusic")){
            gameMusic.playMusic();
        }
        else if(type.equalsIgnoreCase("LevelUp")){
            levelUp.playMusic();
        }
    }

    public void stopSound(String type){
        if(type.equalsIgnoreCase("Fall")){
            fall.stopMusic();
        }
        else if(type.equalsIgnoreCase("Stick")){
            stick.stopMusic();
        }
        else if(type.equalsIgnoreCase("Gamesave")){
            gameSave.stopMusic();
        }
        else if(type.equalsIgnoreCase("GameMusic")){
            gameMusic.stopMusic();
        }
        else if(type.equalsIgnoreCase("LevelUp")){
            levelUp.stopMusic();
        }
    }

}