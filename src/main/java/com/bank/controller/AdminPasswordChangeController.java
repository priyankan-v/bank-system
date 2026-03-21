package com.bank.controller;

import com.bank.model.Admin;
import com.bank.service.AdminService;
import com.bank.util.SceneManager;
import com.bank.util.SessionManager;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextFormatter;
import javafx.util.Duration;

public class AdminPasswordChangeController {

    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private final AdminService adminService = new AdminService();

    @FXML
    public void initialize() {

        // Limit password fields to 8 characters only
        oldPasswordField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches(".{0,8}")) {
                return change;
            }
            return null;
        }));

        // Limit new password to 8 characters only
        newPasswordField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches(".{0,8}")) {
                return change;
            }
            return null;
        }));

        // Limit confirm password to 8 characters only
        confirmPasswordField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches(".{0,8}")) {
                return change;
            }
            return null;
        }));
    }

    @FXML
    private void changePassword() {

        try {

            Admin admin = SessionManager.getCurrentAdmin();

            String oldPassword = oldPasswordField.getText().trim();
            String newPassword = newPasswordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            // Validate new password format
            if (!newPassword.matches(".{0,8}")) {
                messageLabel.setText("Enter a new valid password (up to 8 characters)");
                return;
            }

            // Confirm password match
            if (!newPassword.equals(confirmPassword)) {
                messageLabel.setText("New passwords do not match");
                return;
            }

            // Prevent same password
            if (oldPassword.equals(newPassword)) {
                messageLabel.setText("New password cannot be the old password");
                return;
            }

            adminService.changeAdminPassword(admin, oldPassword, newPassword);

            messageLabel.setText("Password changed successfully. Please login again.");

            resetFields();

            // logout after 1 second
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {

                SessionManager.logout();   // clear session
                SceneManager.switchScene("admin/login.fxml");

            });

            pause.play();

        } catch (RuntimeException e) {

            if (e.getMessage().contains("Incorrect current password")) {
                messageLabel.setText("Incorrect current password");
            } else {
                e.printStackTrace();
                messageLabel.setText("Password change failed");
            }

            resetFields();

        } catch (Exception e) {

            messageLabel.setText("Unexpected error occurred");

            resetFields();
        }
    }

    @FXML
    private void back() {
        SceneManager.switchScene("admin/dashboard.fxml");
    }

    private void resetFields() {
        oldPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
}