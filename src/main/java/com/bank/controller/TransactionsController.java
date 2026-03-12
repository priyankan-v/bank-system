package com.bank.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.service.AccountService;
import com.bank.util.SceneManager;
import com.bank.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TransactionsController {

    @FXML
    private TableView<Transaction> table;

    @FXML
    private TableColumn<Transaction, LocalDateTime> dateCol;

    @FXML
    private TableColumn<Transaction, String> typeCol;

    @FXML
    private TableColumn<Transaction, BigDecimal> amountCol;

    @FXML
    private ComboBox<String> limitSelector;

    private final AccountService accountService = new AccountService();

    private User user;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a");

    @FXML
    public void initialize() {

        user = SessionManager.getCurrentUser();

        /* Bind columns */

        dateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        /* Format date */

        dateCol.setCellFactory(col -> new TableCell<>() {

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        /* Format amount + color */

        amountCol.setCellFactory(col -> new TableCell<>() {

            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {

                super.updateItem(amount, empty);

                if (empty || amount == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                Transaction transaction =
                        getTableView().getItems().get(getIndex());

                setText(String.format("Rs. %,.2f", amount));

                if ("DEPOSIT".equalsIgnoreCase(transaction.getType())) {
                    setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                } 
                else if ("WITHDRAW".equalsIgnoreCase(transaction.getType())) {
                    setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                } 
                else {
                    setStyle("");
                }
            }
        });

        /* ComboBox options */

        limitSelector.setItems(FXCollections.observableArrayList(
                "10",
                "20",
                "40",
                "All"
        ));

        limitSelector.setValue("10");

        loadTransactions(10);

        limitSelector.setOnAction(e -> {

            String value = limitSelector.getValue();

            if ("All".equals(value)) {
                loadTransactions(Integer.MAX_VALUE);
            } else {
                loadTransactions(Integer.parseInt(value));
            }
        });
    }

    private void loadTransactions(int limit) {

        List<Transaction> transactions =
                accountService.getTransactions(user, limit);

        table.setItems(FXCollections.observableArrayList(transactions));
    }

    @FXML
    private void back() {
        SceneManager.switchScene("user/dashboard.fxml");
    }
}