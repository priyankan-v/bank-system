package com.bank.controller;

import java.time.format.DateTimeFormatter;

import com.bank.model.Admin;
import com.bank.service.AdminService;
import com.bank.util.SceneManager;
import com.bank.util.SessionManager;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class TargetAdminDashboardController {

    @FXML private Label adminNameLabel;
    @FXML private Label adminUsernameLabel;
    @FXML private Label adminRoleLabel;
    @FXML private Label adminStatusLabel;
    @FXML private Label adminCreatedAtLabel;
    @FXML private Label errorLabel;
    @FXML private VBox detailsBox;
    @FXML private VBox errorBox;
    @FXML private Button deleteBtn;
    @FXML private Button promoteBtn;
    @FXML private Button demoteBtn;

    private Admin targetAdmin;
    private Admin currentAdmin;
    private AdminService adminService = new AdminService();

    @FXML
    public void initialize() {

        // Validate session - auto-logout if expired
        if (!SessionManager.validateAndHandleTimeout()) {
            showError("Session expired. Please log in again.");
            redirectToLogin();
            return;
        }

        // Get current admin
        currentAdmin = SessionManager.getCurrentAdmin();
        if (currentAdmin == null) {
            showError("Invalid session. Please log in again.");
            redirectToLogin();
            return;
        }

        // Get target admin from session
        targetAdmin = SessionManager.getCurrentTargetAdmin();

        if (targetAdmin == null) {
            showError("No admin selected. Please go back and select an admin.");
            errorBox.setVisible(true);
            errorBox.setManaged(true);
            detailsBox.setVisible(false);
            detailsBox.setManaged(false);
            return;
        }

        // Display admin details
        displayAdminDetails();

        // Configure buttons based on role
        configureButtons();
    }

    private void displayAdminDetails() {
        detailsBox.setVisible(true);
        detailsBox.setManaged(true);
        errorBox.setVisible(false);
        errorBox.setManaged(false);

        adminNameLabel.setText(targetAdmin.getName());
        adminUsernameLabel.setText(targetAdmin.getUsername());
        adminRoleLabel.setText(targetAdmin.getRole());
        adminStatusLabel.setText(targetAdmin.isActive() ? "Active" : "Inactive");
        adminCreatedAtLabel.setText(targetAdmin.getCreatedAt() != null ? 
            targetAdmin.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a")) : "N/A");
    }

    private void configureButtons() {
        // Delete button is always visible

        // Show Promote button if target admin is regular ADMIN
        if ("ADMIN".equals(targetAdmin.getRole())) {
            promoteBtn.setVisible(true);
            promoteBtn.setManaged(true);
            demoteBtn.setVisible(false);
            demoteBtn.setManaged(false);
        }
        // Show Demote button if target admin is SUPER_ADMIN
        else if ("SUPER_ADMIN".equals(targetAdmin.getRole())) {
            promoteBtn.setVisible(false);
            promoteBtn.setManaged(false);
            demoteBtn.setVisible(true);
            demoteBtn.setManaged(true);
        } else {
            promoteBtn.setVisible(false);
            promoteBtn.setManaged(false);
            demoteBtn.setVisible(false);
            demoteBtn.setManaged(false);
        }
    }

    @FXML
    private void deleteAdmin() {
        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Admin");
        confirmAlert.setContentText("Are you sure you want to delete admin: " + 
            targetAdmin.getUsername() + "?\n\nThis action cannot be undone.");

        if (confirmAlert.showAndWait().isEmpty() || 
            confirmAlert.getResult() == javafx.scene.control.ButtonType.CANCEL) {
            return;
        }

        try {
            // Validate session before operation
            if (!SessionManager.validateAndHandleTimeout()) {
                showAlert(Alert.AlertType.WARNING, "Session Expired", 
                    "Your session has expired. Please log in again.");
                redirectToLogin();
                return;
            }

            // Cannot delete yourself
            if (currentAdmin.getUsername().equals(targetAdmin.getUsername())) {
                showAlert(Alert.AlertType.ERROR, "Cannot Delete Self", 
                    "You cannot delete your own admin account.");
                return;
            }

            // Delete the admin
            adminService.deleteAdmin(currentAdmin, targetAdmin.getUsername());

            // Deletion successful
            showAlert(Alert.AlertType.INFORMATION, "Admin Deleted", 
                "Admin '" + targetAdmin.getUsername() + "' has been successfully deleted.");

            // Redirect back to dashboard
            Platform.runLater(() -> {
                SceneManager.switchScene("admin/dashboard.fxml");
            });

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();

            if (errorMessage != null && errorMessage.contains("Only super admin")) {
                showAlert(Alert.AlertType.ERROR, "Permission Denied", 
                    "Only Super Admin can delete admins.");
            } else if (errorMessage != null && errorMessage.contains("cannot delete themselves")) {
                showAlert(Alert.AlertType.ERROR, "Cannot Delete Self", 
                    "You cannot delete your own admin account.");
            } else if (errorMessage != null && errorMessage.contains("not found")) {
                showAlert(Alert.AlertType.ERROR, "Admin Not Found", 
                    "The selected admin could not be found.");
            } else if (errorMessage != null && errorMessage.contains("last super admin")) {
                showAlert(Alert.AlertType.ERROR, "Cannot Delete", 
                    "Cannot delete the last Super Admin in the system.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", 
                    errorMessage != null ? errorMessage : "Failed to delete admin.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "System Error", 
                "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void promoteAdmin() {
        // Confirm promotion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Promotion");
        confirmAlert.setHeaderText("Promote Admin");
        confirmAlert.setContentText("Promote '" + targetAdmin.getUsername() + 
            "' to Super Admin?\n\nThis will grant full system administration privileges.");

        if (confirmAlert.showAndWait().isEmpty() || 
            confirmAlert.getResult() == javafx.scene.control.ButtonType.CANCEL) {
            return;
        }

        try {
            // Validate session before operation
            if (!SessionManager.validateAndHandleTimeout()) {
                showAlert(Alert.AlertType.WARNING, "Session Expired", 
                    "Your session has expired. Please log in again.");
                redirectToLogin();
                return;
            }

            // Promote the admin
            adminService.promoteToSuperAdmin(currentAdmin, targetAdmin.getUsername());

            // Update target admin's role in memory
            targetAdmin.setRole("SUPER_ADMIN");

            // Promotion successful
            showAlert(Alert.AlertType.INFORMATION, "Admin Promoted", 
                "Admin '" + targetAdmin.getUsername() + "' has been promoted to Super Admin.");

            // Update button visibility
            configureButtons();

            // Refresh display
            displayAdminDetails();

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();

            if (errorMessage != null && errorMessage.contains("Only super admin")) {
                showAlert(Alert.AlertType.ERROR, "Permission Denied", 
                    "Only Super Admin can promote admins.");
            } else if (errorMessage != null && errorMessage.contains("not found")) {
                showAlert(Alert.AlertType.ERROR, "Admin Not Found", 
                    "The selected admin could not be found.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Promotion Failed", 
                    errorMessage != null ? errorMessage : "Failed to promote admin.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "System Error", 
                "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void demoteAdmin() {
        // Confirm demotion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Demotion");
        confirmAlert.setHeaderText("Demote Admin");
        confirmAlert.setContentText("Demote '" + targetAdmin.getUsername() + 
            "' from Super Admin to regular Admin?\n\nThis will restrict their privileges.");

        if (confirmAlert.showAndWait().isEmpty() || 
            confirmAlert.getResult() == javafx.scene.control.ButtonType.CANCEL) {
            return;
        }

        try {
            // Validate session before operation
            if (!SessionManager.validateAndHandleTimeout()) {
                showAlert(Alert.AlertType.WARNING, "Session Expired", 
                    "Your session has expired. Please log in again.");
                redirectToLogin();
                return;
            }

            // Cannot demote yourself
            if (currentAdmin.getUsername().equals(targetAdmin.getUsername())) {
                showAlert(Alert.AlertType.ERROR, "Cannot Demote Self", 
                    "You cannot demote your own admin account.");
                return;
            }

            // Demote the admin
            adminService.demoteSuperAdmin(currentAdmin, targetAdmin.getUsername());

            // Update target admin's role in memory
            targetAdmin.setRole("ADMIN");

            // Demotion successful
            showAlert(Alert.AlertType.INFORMATION, "Admin Demoted", 
                "Admin '" + targetAdmin.getUsername() + "' has been demoted to regular Admin.");

            // Update button visibility
            configureButtons();

            // Refresh display
            displayAdminDetails();

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();

            if (errorMessage != null && errorMessage.contains("Only super admin")) {
                showAlert(Alert.AlertType.ERROR, "Permission Denied", 
                    "Only Super Admin can demote admins.");
            } else if (errorMessage != null && errorMessage.contains("cannot demote themselves")) {
                showAlert(Alert.AlertType.ERROR, "Cannot Demote Self", 
                    "You cannot demote your own admin account.");
            } else if (errorMessage != null && errorMessage.contains("not found")) {
                showAlert(Alert.AlertType.ERROR, "Admin Not Found", 
                    "The selected admin could not be found.");
            } else if (errorMessage != null && errorMessage.contains("last super admin")) {
                showAlert(Alert.AlertType.ERROR, "Cannot Demote", 
                    "Cannot demote the last Super Admin in the system.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Demotion Failed", 
                    errorMessage != null ? errorMessage : "Failed to demote admin.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "System Error", 
                "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
        // Clear target admin from session
        SessionManager.setCurrentTargetAdmin(null);
        SceneManager.switchScene("admin/dashboard.fxml");
    }

    private void showError(String message) {
        errorBox.setVisible(true);
        errorBox.setManaged(true);
        errorLabel.setText(message);
        detailsBox.setVisible(false);
        detailsBox.setManaged(false);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void redirectToLogin() {
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            SceneManager.switchScene("admin/login.fxml");
        });
        pause.play();
    }
}
