package com.bank.util;

import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlPath) {

        try {

            // Build full path
            String fullPath = "/ui/" + fxmlPath;

            URL resource = SceneManager.class.getResource(fullPath);

            if (resource == null) {
                throw new RuntimeException("FXML not found: " + fullPath);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load scene: " + fxmlPath, e);
        }
    }
}