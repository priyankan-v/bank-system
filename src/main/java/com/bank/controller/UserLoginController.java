package com.bank.controller;

import com.bank.model.User;
import com.bank.service.AuthService;
import com.bank.service.exceptions.AuthException;
import com.bank.util.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class UserLoginController {

    @FXML
    private TextField accountNumberField;

    @FXML
    private PasswordField pinField;

    @FXML
    private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {

        // Limit account number to 8 digits only
        accountNumberField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d{0,8}")) {
                return change;
            }
            return null;
        }));

        // Limit PIN to 4 digits only
        pinField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d{0,4}")) {
                return change;
            }
            return null;
        }));
    }

    @FXML
    private void handleLogin() {

        String account = accountNumberField.getText().trim();
        String pin = pinField.getText().trim();

        // Empty account number validation
        if (account.isEmpty()) {

            messageLabel.setText("Please enter account number");

            accountNumberField.clear();
            pinField.clear();
            accountNumberField.requestFocus();

            return;
        }

        if (!account.matches("\\d{8}")) {
            messageLabel.setText("Invalid account number format");

            accountNumberField.clear();
            pinField.clear();
            accountNumberField.requestFocus();
            return;
        }

        if (pin.isEmpty()) {

            messageLabel.setText("Please enter PIN");

            pinField.clear();
            accountNumberField.requestFocus();

            return;
        }

        if (!pin.matches("\\d{4}")) {

            messageLabel.setText("Invalid PIN format");

            pinField.clear();
            accountNumberField.requestFocus();

            return;
        }

        try {

            User user = authService.userLogin(account, pin);

            SceneManager.switchScene("user/dashboard.fxml");

        } catch (AuthException e) {

            messageLabel.setText(e.getMessage());

            // If invalid account number
            if (e.getMessage().contains("Invalid account number")) {

                accountNumberField.clear();
                pinField.clear();
                accountNumberField.requestFocus();

            } else {

                pinField.clear();
                pinField.requestFocus();
            }

        } catch (Exception e) {

            messageLabel.setText("System error. Please try again.");

            pinField.clear();
            pinField.requestFocus();
        }
    }
}