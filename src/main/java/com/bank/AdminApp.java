package com.bank;

import com.bank.config.FlywayConfig;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AdminApp extends Application {

    @Override
    public void start(Stage stage) {

        FlywayConfig.migrate(false);  // true only in development

        Label label = new Label("Admin Control Panel");

        Scene scene = new Scene(new StackPane(label), 900, 650);

        stage.setTitle("Bank System - Admin App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}