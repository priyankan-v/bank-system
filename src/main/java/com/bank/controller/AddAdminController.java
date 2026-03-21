package com.bank.controller;

import com.bank.model.Admin;
import com.bank.service.AdminService;
import com.bank.util.AdminPasswordGenerator;
import com.bank.util.SceneManager;
import com.bank.util.SessionManager;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class AddAdminController {

    @FXML private TextField adminName;
    @FXML private Label successLabel;
    @FXML private Label errorLabel;
    @FXML private Label nameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label roleLabel;
    @FXML private Label statusLabel;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private VBox resultBox;
    @FXML private VBox errorBox;
    @FXML private VBox inputSection;
    @FXML private Button createAnotherBtn;

    private boolean isPasswordVisible = false;

    private AdminService adminService = new AdminService();

    @FXML
    private void createAdmin() {

        // Validating session
        if (!SessionManager.validateAndHandleTimeout()) {
            showError("Session expired. Please log in again.");
            // Auto-redirect to login after 2 seconds
            // javafx.application.Platform.runLater(() -> {
            //     try {
            //         Thread.sleep(2000);
            //         SceneManager.switchScene("admin/login.fxml");
            //     } catch (InterruptedException e) {
            //         e.printStackTrace();
            //     }
            // });
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> {
                SceneManager.switchScene("admin/login.fxml");
            });
            pause.play();
            return;
        }

        String name = adminName.getText().trim();

        // Hide previous messages
        resultBox.setVisible(false);
        resultBox.setManaged(false);
        errorBox.setVisible(false);
        errorBox.setManaged(false);

        // --- VALIDATION ---

        // Check if empty
        if (name.isEmpty()) {
            showError("Admin name is required.");
            return;
        }

        // Check length (3-50 characters)
        if (name.length() < 3 || name.length() > 50) {
            showError("Admin name must be between 3 and 50 characters.");
            adminName.clear();
            return;
        }

        // Check for valid characters
        if (!name.matches("^[A-Z][a-z]*(\\s[A-Z][a-z]*)*$")) {
            showError("Each word must start with a capital letter and contain only letters (e.g., John Doe).");
            adminName.clear();
            return;
        }

        try {
            // Check if admin is logged in
            Admin creator = SessionManager.getCurrentAdmin();

            if (creator == null) {
                showError("Session expired. Please log in again.");
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event -> {
                    SceneManager.switchScene("admin/login.fxml");
                });
                pause.play();
                return;
            }

            // Check if creator is super admin
            if (!creator.isSuperAdmin()) {
                showError("Only Super Admin can create new admins.");
                return;
            }

            // Generate password and create admin
            String password = AdminPasswordGenerator.generate();
            String username = adminService.createAdmin(creator, name, password, "ADMIN");

            // Hide input section
            inputSection.setVisible(false);
            inputSection.setManaged(false);

            // Display success result
            resultBox.setVisible(true);
            resultBox.setManaged(true);

            // Set values
            successLabel.setText("Admin Created Successfully!");
            nameLabel.setText(name);
            usernameLabel.setText(username);
            roleLabel.setText("ADMIN");
            statusLabel.setText("Active");

            passwordField.setText(password);
            passwordVisibleField.setText(password);

            // Reset password visibility state
            isPasswordVisible = false;
            passwordField.setVisible(true);
            passwordVisibleField.setVisible(false);

            // Show "Create Another" button
            createAnotherBtn.setVisible(true);
            createAnotherBtn.setManaged(true);

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            
            // Handle specific error cases
            if (errorMessage != null && errorMessage.contains("super admin")) {
                showError("Only Super Admin can create new admins.");
            } else if (errorMessage != null && errorMessage.contains("Invalid role")) {
                showError("Invalid role specified.");
            } else if (errorMessage != null && errorMessage.contains("Admin creation failed")) {
                showError("Failed to create admin. Please try again.");
            } else {
                showError(errorMessage != null ? errorMessage : "An error occurred while creating admin.");
            }

        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void togglePasswordVisibility() {

        if (isPasswordVisible) {
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
        } else {
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
        }

        isPasswordVisible = !isPasswordVisible;
    }

    @FXML
    private void resetForm() {
        // Hide result and error sections
        resultBox.setVisible(false);
        resultBox.setManaged(false);
        errorBox.setVisible(false);
        errorBox.setManaged(false);

        // Show input section
        inputSection.setVisible(true);
        inputSection.setManaged(true);

        // Hide "Create Another" button
        createAnotherBtn.setVisible(false);
        createAnotherBtn.setManaged(false);

        // Clear input
        adminName.clear();
        adminName.requestFocus();

        // Reset password visibility
        isPasswordVisible = false;
        passwordField.setVisible(true);
        passwordVisibleField.setVisible(false);
    }

    @FXML
    private void goBack() {
        SceneManager.switchScene("admin/dashboard.fxml");
    }

    private void showError(String message) {
        errorBox.setVisible(true);
        errorBox.setManaged(true);
        errorLabel.setText(message);
    }
}