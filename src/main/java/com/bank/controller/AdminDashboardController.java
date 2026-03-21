package com.bank.controller;

import java.sql.Connection;
import java.sql.SQLException;

import com.bank.dao.AdminDAO;
import com.bank.dao.UserDAO;
import com.bank.model.Admin;
import com.bank.model.User;
import com.bank.service.AuthService;
import com.bank.util.DBConnection;
import com.bank.util.SceneManager;
import com.bank.util.SessionManager;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class AdminDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label userNameLabel; 
    @FXML private VBox superAdminSection;
    @FXML private Label roleBadge;
    @FXML private TextField accountNumberField;
    @FXML private TextField userNameField;

    private Admin admin;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {

        // Validate session - auto-logout if expired
        if (!SessionManager.validateAndHandleTimeout()) {
            showAlert(Alert.AlertType.WARNING, "Session Expired", 
                "Your session has expired. Please log in again.");
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> {
                SceneManager.switchScene("admin/login.fxml");
            });
            pause.play();
            return;
        }

        admin = SessionManager.getCurrentAdmin();

        if (admin == null) {
            showAlert(Alert.AlertType.ERROR, "Session Error", 
                "Invalid session. Please log in again.");
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> { 
                SceneManager.switchScene("admin/login.fxml");
            });
            pause.play();
            return;
        }

        welcomeLabel.setText("Welcome, " + admin.getName());
        userNameLabel.setText("User Name: " + admin.getUsername());

        handleRoleAccess();

        accountNumberField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,8}")) {
                return change;
            } else {
                return null;
            }
        }));

        userNameField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches(".{0,6}")) {
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
    private void checkAdmin() {

        String userName = userNameField.getText().trim();

        // --- Basic validation ---
        if (userName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid User Name", "User name is required.");
            return;
        }

        if (!userName.matches(".{6}")) {
            userNameField.clear();
            showAlert(Alert.AlertType.ERROR, "Invalid User Name", "Incorrect format.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) { 
            Admin admin = new AdminDAO().findByUsername(conn, userName);

            if (admin == null) {
                userNameField.clear();
                showAlert(Alert.AlertType.ERROR, "Admin Not Found", "No active admin found with this user name.");
                return;
            }

            SessionManager.setCurrentTargetAdmin(admin);

            SceneManager.switchScene("admin/targetAdminDashboard.fxml");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Unable to check admin. Please try again.");
        }
    }

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

    // ---------------- ACCOUNT OPS ----------------
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
        authService.adminLogout(admin.getUsername());
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