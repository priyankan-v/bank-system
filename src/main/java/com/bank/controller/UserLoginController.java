package com.bank.controller;

import com.bank.model.User;
import com.bank.service.AuthService;
import com.bank.util.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UserLoginController {

    @FXML
    private TextField accountNumberField;

    @FXML
    private PasswordField pinField;

    @FXML
    private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {

        String account = accountNumberField.getText().trim();
        String pin = pinField.getText().trim();

        try {

            User user = authService.userLogin(account, pin);

            SceneManager.switchScene("user/dashboard.fxml");

        } catch (Exception e) {

            messageLabel.setText(e.getMessage());
        }
    }
}