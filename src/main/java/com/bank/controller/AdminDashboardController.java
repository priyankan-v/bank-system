package com.bank.controller;

import java.sql.Connection;
import java.sql.SQLException;

import com.bank.dao.UserDAO;
import com.bank.model.Admin;
import com.bank.model.User;
import com.bank.util.DBConnection;
import com.bank.util.SceneManager;
import com.bank.util.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;

public class AdminDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private VBox superAdminSection;
    @FXML private Label roleBadge;

    @FXML private TextField accountNumberField;

    private Admin admin;

    @FXML
    public void initialize() {

        admin = SessionManager.getCurrentAdmin();

        welcomeLabel.setText("Welcome, " + admin.getUsername());

        handleRoleAccess();

        accountNumberField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,8}")) {
                return change;
            } else {
                return null;
            }
        }));
    }

    private void handleRoleAccess() {

        if ("SUPER_ADMIN".equals(admin.getRole())) {

            roleBadge.setText("SUPER ADMIN");
            roleBadge.setVisible(true);

        } else {
            superAdminSection.setVisible(false);
            superAdminSection.setManaged(false);

            roleBadge.setVisible(false);
        }
    }
    // ---------------- ADMIN OPS ----------------

    @FXML
    private void changePassword() {
        SceneManager.switchScene("admin/change-password.fxml");
    }

    @FXML
    private void viewActivity() {
        SceneManager.switchScene("admin/activity.fxml");
    }

    @FXML
    private void addAdmin() {
        SceneManager.switchScene("admin/add-admin.fxml");
    }

    @FXML
    private void deleteAdmin() {
        SceneManager.switchScene("admin/delete-admin.fxml");
    }

    @FXML
    private void promoteAdmin() {
        SceneManager.switchScene("admin/promote.fxml");
    }

    @FXML
    private void demoteAdmin() {
        SceneManager.switchScene("admin/demote.fxml");
    }

    // ---------------- ACCOUNT OPS ----------------
    // @FXML
    // private void deleteAccount() {
    //     SceneManager.switchScene("account/delete.fxml");
    // }

    // @FXML
    // private void deposit() {
    //     SceneManager.switchScene("account/deposit.fxml");
    // }

    // @FXML
    // private void withdraw() {
    //     SceneManager.switchScene("account/withdraw.fxml");
    // }

    // @FXML
    // private void unlockAccount() {
    //     SceneManager.switchScene("account/unlock.fxml");
    // }

    // @FXML
    // private void changePin() {
    //     SceneManager.switchScene("account/change-pin.fxml");
    // }

    @FXML
    private void checkAccount() {

        String accNum = accountNumberField.getText().trim();

        // --- Basic validation ---
        if (accNum.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Account Number", "Account number is required.");
            return;
        }

        if (!accNum.matches("\\d{8}")) {
            accountNumberField.clear();
            showAlert(Alert.AlertType.ERROR, "Invalid Account Number", "Incorrect format.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) { 
            User user = new UserDAO().findByAccountNumber(conn, accNum);

            if (user == null) {
                accountNumberField.clear();
                showAlert(Alert.AlertType.ERROR, "Account Not Found", "No active account found with this number.");
                return;
            }

            SessionManager.setCurrentUser(user);

            SceneManager.switchScene("account/dashboard.fxml");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Unable to check account. Please try again.");
        }
    }

    @FXML
    private void addAccount() {
        SceneManager.switchScene("account/add.fxml");
    }

    // ---------------- COMMON ----------------

    @FXML
    private void logout() {
        SessionManager.logout();
        SceneManager.switchScene("admin/login.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}