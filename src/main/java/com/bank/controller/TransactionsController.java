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
    private TableColumn<Transaction, BigDecimal> balanceCol;

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
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balanceAfter"));

        /* Format Date column */
        dateCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : formatter.format(item));
            }
        });

        /* Format Type column (center) */
        typeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                setText(empty ? null : type);
                setStyle("-fx-alignment: CENTER;");
            }
        });

        /* Format Amount column (right, color-coded) */
        amountCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                Transaction transaction = getTableView().getItems().get(getIndex());
                setText(String.format("Rs. %,.2f", amount));
                String color = "";
                if ("DEPOSIT".equalsIgnoreCase(transaction.getType())) {
                    color = "green";
                } else if ("WITHDRAW".equalsIgnoreCase(transaction.getType())) {
                    color = "red";
                }
                setStyle(String.format("-fx-text-fill: %s; -fx-font-weight: bold; -fx-alignment: CENTER-RIGHT;", color));
            }
        });

        /* Format Balance column (right) */
        balanceCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal balance, boolean empty) {
                super.updateItem(balance, empty);
                if (empty || balance == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("Rs. %,.2f", balance));
                    setStyle("-fx-alignment: CENTER-RIGHT;");
                }
            }
        });

        /* Transaction limit ComboBox */
        limitSelector.setItems(FXCollections.observableArrayList("10", "20", "40", "All"));
        limitSelector.setValue("10");

        loadTransactions(10);

        limitSelector.setOnAction(e -> {
            String value = limitSelector.getValue();
            loadTransactions("All".equals(value) ? Integer.MAX_VALUE : Integer.parseInt(value));
        });
    }

    private void loadTransactions(int limit) {
        List<Transaction> transactions = accountService.getTransactions(user, limit);
        table.setItems(FXCollections.observableArrayList(transactions));
    }

    @FXML
    private void back() {
        SceneManager.switchScene("user/dashboard.fxml");
    }
}