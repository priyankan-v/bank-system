package com.bank.controller;

import java.math.BigDecimal;

import com.bank.model.User;
import com.bank.service.AccountService;
import com.bank.util.SceneManager;
import com.bank.util.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class WithdrawController {

    @FXML
    private TextField amountField;

    @FXML
    private Label messageLabel;

    private final AccountService accountService = new AccountService();

    @FXML
    private void withdraw() {

        try {

            BigDecimal amount = new BigDecimal(amountField.getText());

            User user = SessionManager.getCurrentUser();

            accountService.withdraw(user, amount);

            messageLabel.setText("Withdrawal successful");

        } catch (Exception e) {

            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void back() {
        SceneManager.switchScene("user/dashboard.fxml");
    }
}