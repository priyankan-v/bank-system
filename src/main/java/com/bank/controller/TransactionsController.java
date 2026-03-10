package com.bank.controller;

import java.util.List;

import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.service.AccountService;
import com.bank.util.SceneManager;
import com.bank.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TransactionsController {

    @FXML
    private TableView<Transaction> table;

    @FXML
    private TableColumn<Transaction, String> typeCol;

    @FXML
    private TableColumn<Transaction, String> amountCol;

    private final AccountService accountService = new AccountService();

    @FXML
    public void initialize() {

        User user = SessionManager.getCurrentUser();

        List<Transaction> transactions =
                accountService.getTransactions(user, 20);

        table.setItems(FXCollections.observableArrayList(transactions));
    }

    @FXML
    private void back() {
        SceneManager.switchScene("user/dashboard.fxml");
    }
}