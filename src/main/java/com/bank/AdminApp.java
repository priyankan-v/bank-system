package com.bank;

import com.bank.util.SceneManager;

import javafx.application.Application;
import javafx.stage.Stage;

public class AdminApp extends Application {

    @Override
    public void start(Stage stage) {

        SceneManager.setStage(stage);

        SceneManager.switchScene("admin/login.fxml");

        stage.setTitle("Bank Admin System");
        stage.setWidth(1000);
        stage.setHeight(650);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}