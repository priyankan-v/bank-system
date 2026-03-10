package com.bank.controller;

import java.math.BigDecimal;

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

        welcomeLabel.setText("Welcome " + user.getName());
        accountLabel.setText("Account: " + user.getAccountNumber());

        refreshBalance();
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
    private void logout() {

        SessionManager.logout();

        SceneManager.switchScene("user/login.fxml");
    }
}