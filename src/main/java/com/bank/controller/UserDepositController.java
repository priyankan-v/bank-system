package com.bank.controller;

import java.math.BigDecimal;

import com.bank.model.User;
import com.bank.service.AccountService;
import com.bank.util.SceneManager;
import com.bank.util.SessionManager;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class UserDepositController {
   @FXML
    private TextField amountField;

    @FXML
    private Label messageLabel;

    private final AccountService accountService = new AccountService();

    @FXML
    private void deposit() {

        try {

            BigDecimal amount = new BigDecimal(amountField.getText());

            User user = SessionManager.getCurrentUser();

            accountService.deposit(user, amount);

            messageLabel.setText("Deposit successful");

            // Wait 1 second, then go back to dashboard
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> SceneManager.switchScene("user/dashboard.fxml"));
            pause.play();

        } catch (Exception e) {

            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void back() {
        SceneManager.switchScene("user/dashboard.fxml");
    }
}
