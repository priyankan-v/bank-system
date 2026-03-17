package com.bank;

import com.bank.util.SceneManager;

import javafx.application.Application;
import javafx.stage.Stage;

public class UserApp extends Application {

    @Override
    public void start(Stage stage) {

        SceneManager.setStage(stage);
        SceneManager.switchScene("user/login.fxml");

        stage.setTitle("Bank User System");
        stage.setWidth(800);
        stage.setHeight(600);

        stage.centerOnScreen();   // centers the window

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}