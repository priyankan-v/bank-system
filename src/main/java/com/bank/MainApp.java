package com.bank;

import com.bank.config.FlywayConfig;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        FlywayConfig.migrate(false); // true only in development

        Label label = new Label("Bank System Started Successfully");

        StackPane root = new StackPane(label);

        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("Bank Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}