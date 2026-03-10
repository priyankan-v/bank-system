package com.bank.controller;

import com.bank.model.User;
import com.bank.service.AccountService;
import com.bank.util.PasswordUtil;
import com.bank.util.SceneManager;
import com.bank.util.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class ChangePinController {

    @FXML
    private PasswordField newPinField;

    @FXML
    private Label messageLabel;

    private final AccountService accountService = new AccountService();

    @FXML
    private void changePin() {

        try {

            User user = SessionManager.getCurrentUser();

            String hashed = PasswordUtil.hash(newPinField.getText());

            accountService.changePin(user, hashed);

            messageLabel.setText("PIN changed successfully");

        } catch (Exception e) {

            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void back() {
        SceneManager.switchScene("user/dashboard.fxml");
    }
}