package com.example.demo;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StickHeroTest {

    @Before
    public void setUp() {
        JFXPanel jfxPanel = new JFXPanel();
    }

    @After
    public void tearDown() {
        Platform.exit();
    }

    @Test
    public void testScoreIsZero() {
        Platform.runLater(() -> {
            FXMLLoader gameOverLoader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
            Parent mainMenuRoot = null;
            try {
                mainMenuRoot = gameOverLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            HelloController MainMenuController = gameOverLoader.getController();
            assertEquals(0, MainMenuController.getScore());
        });
    }

    @Test
    public void testCherryScoreIsZero() {
        Platform.runLater(() -> {
            FXMLLoader gameOverLoader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
            Parent mainMenuRoot = null;
            try {
                mainMenuRoot = gameOverLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            HelloController MainMenuController = gameOverLoader.getController();
            assertEquals(0,MainMenuController.getCherryScore());
        });
    }

    @Test
    public void testExtendTimelineIsNotNull()  {
        Platform.runLater(() -> {
            FXMLLoader gameOverLoader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
            Parent mainMenuRoot = null;
            try {
                mainMenuRoot = gameOverLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            HelloController MainMenuController = gameOverLoader.getController();
            assertNotNull(MainMenuController.getExtendTimeline());
        });
    }
}
