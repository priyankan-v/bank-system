package com.bank.controller;

import com.bank.model.User;
import com.bank.service.AccountService;
import com.bank.util.SceneManager;
import com.bank.util.SessionManager;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextFormatter;
import javafx.util.Duration;

public class ChangePinController {

    @FXML
    private PasswordField oldPinField;

    @FXML
    private PasswordField newPinField;

    @FXML
    private PasswordField confirmPinField;

    @FXML
    private Label messageLabel;

    private final AccountService accountService = new AccountService();

    @FXML
    public void initialize() {

        // Limit account number to 8 digits only
        oldPinField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d{0,4}")) {
                return change;
            }
            return null;
        }));

        // Limit PIN to 4 digits only
        newPinField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d{0,4}")) {
                return change;
            }
            return null;
        }));

        // Limit confirm PIN to 4 digits only
        confirmPinField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d{0,4}")) {
                return change;
            }
            return null;
        }));
    }

    @FXML
    private void changePin() {

        try {

            User user = SessionManager.getCurrentUser();

            String oldPin = oldPinField.getText().trim();
            String newPin = newPinField.getText().trim();
            String confirmPin = confirmPinField.getText().trim();

            // Validate new PIN format
            if (!newPin.matches("\\d{4}")) {
                messageLabel.setText("Enter a new valid pin");
                return;
            }

            // Confirm PIN match
            if (!newPin.equals(confirmPin)) {
                messageLabel.setText("New PINs do not match");
                return;
            }

            // Prevent same PIN
            if (oldPin.equals(newPin)) {
                messageLabel.setText("New PIN cannot be the old PIN");
                return;
            }

            accountService.changePin(user, oldPin, newPin);

            messageLabel.setText("PIN changed successfully. Please login again.");

            resetFields();

            // logout after 2 seconds
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> {

                SessionManager.logout();   // clear session
                SceneManager.switchScene("user/login.fxml");

            });

            pause.play();

        } catch (RuntimeException e) {

            if (e.getMessage().contains("Incorrect current PIN")) {
                messageLabel.setText("Incorrect current PIN");
            } else {
                e.printStackTrace();
                messageLabel.setText("PIN change failed");
            }

            resetFields();

        } catch (Exception e) {

            messageLabel.setText("Unexpected error occurred");

            resetFields();
        }
    }

    @FXML
    private void back() {
        SceneManager.switchScene("user/dashboard.fxml");
    }

    private void resetFields() {
        oldPinField.clear();
        newPinField.clear();
        confirmPinField.clear();
    }
}