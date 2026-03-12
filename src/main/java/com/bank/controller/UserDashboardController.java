package com.bank.controller;

import java.math.BigDecimal;
import java.time.LocalTime;

import com.bank.model.User;
import com.bank.service.AccountService;
import com.bank.util.SceneManager;
import com.bank.util.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label accountLabel;

    @FXML
    private Label balanceLabel;

    private final AccountService accountService = new AccountService();

    @FXML
    public void initialize() {

        User user = SessionManager.getCurrentUser();

        String greeting = getTimeBasedGreeting();
        welcomeLabel.setText(greeting + ", " + user.getName());

        accountLabel.setText("Account: " + user.getAccountNumber());

        refreshBalance();
    }

    private String getTimeBasedGreeting() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();

        if (hour >= 5 && hour < 12) {
            return "Good Morning";
        } else if (hour >= 12 && hour < 17) {
            return "Good Afternoon";
        } else if (hour >= 17 && hour < 21) {
            return "Good Evening";
        } else {
            return "Welcome";
        }
    }

    @FXML
    private void refreshBalance() {

        User user = SessionManager.getCurrentUser();

        BigDecimal balance = accountService.getBalance(user);

        balanceLabel.setText("Balance: " + balance);
    }

    @FXML
    private void withdrawMoney() {
        SceneManager.switchScene("user/withdraw.fxml");
    }

    @FXML
    private void changePin() {
        SceneManager.switchScene("user/change-pin.fxml");
    }

    @FXML
    private void viewTransactions() {
        SceneManager.switchScene("user/transactions.fxml");
    }

    @FXML
    public void depositMoney() {
        SceneManager.switchScene("user/deposit.fxml");
    }

    @FXML
    private void logout() {

        SessionManager.logout();

        SceneManager.switchScene("user/login.fxml");
    }
}