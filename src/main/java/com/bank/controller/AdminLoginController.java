package com.bank.controller;

import com.bank.model.Admin;
import com.bank.service.AuthService;
import com.bank.service.exceptions.AuthException;
import com.bank.util.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class AdminLoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {

        // Username: A followed by up to 5 digits
        usernameField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("A\\d{0,5}") || newText.isEmpty()) {
                return change;
            }
            return null;
        }));

        // Password: maximum 8 characters
        passwordField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().length() <= 8) {
                return change;
            }
            return null;
        }));
    }

    @FXML
    private void handleLogin() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Empty username validation
        if (username.isEmpty()) {

            messageLabel.setText("Please enter username");

            usernameField.clear();
            passwordField.clear();
            usernameField.requestFocus();

            return;
        }

        if (!username.matches("A\\d{5}")) {

            messageLabel.setText("Invalid username format");

            usernameField.clear();
            passwordField.clear();
            usernameField.requestFocus();

            return;
        }

        if (password.isEmpty()) {

            messageLabel.setText("Please enter password");

            passwordField.clear();
            usernameField.requestFocus();

            return;
        }

        try {

            Admin admin = authService.adminLogin(username, password);

            SceneManager.switchScene("admin/dashboard.fxml");

        } catch (AuthException e) {

            messageLabel.setText(e.getMessage());

            // If invalid username
            if (e.getMessage().contains("Invalid username")) {

                usernameField.clear();
                passwordField.clear();
                usernameField.requestFocus();

            } else {

                passwordField.clear();
                passwordField.requestFocus();
            }

        } catch (Exception e) {

            messageLabel.setText("System error. Please try again.");

            passwordField.clear();
            passwordField.requestFocus();
        }
    }

    @FXML
    private void login() {

        try {

            Admin admin = authService.adminLogin(
                    usernameField.getText(),
                    passwordField.getText()
            );

            SceneManager.switchScene("admin/dashboard.fxml");

        } catch (Exception e) {

            messageLabel.setText(e.getMessage());
        }
    }
}