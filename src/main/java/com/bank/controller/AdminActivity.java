package com.bank.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.bank.model.Admin;
import com.bank.model.AdminLog;
import com.bank.model.AuditLog;
import com.bank.service.AdminService;
import com.bank.util.SceneManager;
import com.bank.util.SessionManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class AdminActivity {

    @FXML private Label errorLabel;
    @FXML private VBox errorBox;
    @FXML private TabPane activityTabPane;
    @FXML private TableColumn<AdminLog, LocalDateTime> adminDateColumn;
    @FXML private TableView<AdminLog> adminActivityTable;
    @FXML private TableColumn<AuditLog, LocalDateTime> userDateColumn;
    @FXML private TableView<AuditLog> userActivityTable;
    @FXML private Label adminActivityCountLabel;
    @FXML private Label userActivityCountLabel;
    @FXML private ComboBox<String> adminLimitCombo;
    @FXML private ComboBox<String> userLimitCombo;
    @FXML private DatePicker adminDatePicker;
    @FXML private DatePicker userDatePicker;

    private Admin currentAdmin;
    private AdminService adminService = new AdminService();

    // Store full data
    private List<AdminLog> allAdminLogs;
    private List<AuditLog> allUserLogs;

    // Load limit for records - increased to ensure enough data for filtering
    private static final int RECORD_LIMIT = 5000;

    @FXML
    public void initialize() {

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

        // Populate ComboBox items
        adminLimitCombo.getItems().addAll("Recent 50", "All Records", "Select by Date");
        userLimitCombo.getItems().addAll("Recent 50", "All Records", "Select by Date");

        // Set default combo box selection
        adminLimitCombo.getSelectionModel().select(0); // "Recent 50"
        userLimitCombo.getSelectionModel().select(0); // "Recent 50"

        // Keep columns stretched to table width and avoid trailing empty space.
        adminActivityTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        userActivityTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        // Set up date formatters for table columns
        setupDateFormatting();

        // Load activity data
        loadActivityData();
    }

    /**
     * Setup date formatting for table columns
     */
    private void setupDateFormatting() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");

        // Format admin date column
        adminDateColumn.setCellFactory(new Callback<TableColumn<AdminLog, LocalDateTime>, TableCell<AdminLog, LocalDateTime>>() {
            @Override
            public TableCell<AdminLog, LocalDateTime> call(TableColumn<AdminLog, LocalDateTime> column) {
                return new TableCell<AdminLog, LocalDateTime>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(formatter.format(item));
                        }
                    }
                };
            }
        });

        // Format user date column
        userDateColumn.setCellFactory(new Callback<TableColumn<AuditLog, LocalDateTime>, TableCell<AuditLog, LocalDateTime>>() {
            @Override
            public TableCell<AuditLog, LocalDateTime> call(TableColumn<AuditLog, LocalDateTime> column) {
                return new TableCell<AuditLog, LocalDateTime>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(formatter.format(item));
                        }
                    }
                };
            }
        });
    }

    /**
     * Load both admin and user activity logs
     */
    private void loadActivityData() {
        new Thread(() -> {
            try {
                if (!SessionManager.validateAndHandleTimeout()) {
                    Platform.runLater(() -> {
                        showError("Session expired during load. Please log in again.");
                        redirectToLogin();
                    });
                    return;
                }

                // Load admin logs
                List<AdminLog> adminLogs = adminService.getAdminAuditLogs(
                        currentAdmin.getUsername(), RECORD_LIMIT);
                
                // Sort all admin logs in descending order (newest first) immediately after loading
                allAdminLogs = adminLogs.stream()
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .collect(Collectors.toList());

                // Load user activity logs
                List<AuditLog> userLogs = adminService.getUserAuditbyAdminLogs(
                        currentAdmin.getUsername(), RECORD_LIMIT);
                
                // Sort all user logs in descending order (newest first) immediately after loading
                allUserLogs = userLogs.stream()
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .collect(Collectors.toList());

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    // Apply default filter (Recent 50)
                    applyAdminFilter("Recent 50");
                    applyUserFilter("Recent 50");

                    // Hide error box if there was any
                    errorBox.setVisible(false);
                    errorBox.setManaged(false);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Failed to load activity logs: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    /**
     * Handle admin activity limit combo box change
     */
    @FXML
    private void onAdminLimitChanged() {
        String selectedOption = adminLimitCombo.getValue();
        
        if (selectedOption != null) {
            if ("Select by Date".equals(selectedOption)) {
                adminDatePicker.setVisible(true);
                adminDatePicker.setManaged(true);
            } else {
                adminDatePicker.setVisible(false);
                adminDatePicker.setManaged(false);
                applyAdminFilter(selectedOption);
            }
        }
    }

    /**
     * Handle admin activity date picker selection
     */
    @FXML
    private void onAdminDateSelected() {
        LocalDate selectedDate = adminDatePicker.getValue();
        if (selectedDate != null) {
            // Filter records by the selected date (maintain newest first order)
            List<AdminLog> filteredLogs = allAdminLogs.stream()
                .filter(log -> log.getCreatedAt() != null && 
                              log.getCreatedAt().toLocalDate().equals(selectedDate))
                .collect(Collectors.toList());

            adminActivityTable.getItems().setAll(filteredLogs);
            adminActivityCountLabel.setText("Total Records (Filtered): " + filteredLogs.size());
        }
    }

    /**
     * Handle user activity limit combo box change
     */
    @FXML
    private void onUserLimitChanged() {
        String selectedOption = userLimitCombo.getValue();
        
        if (selectedOption != null) {
            if ("Select by Date".equals(selectedOption)) {
                userDatePicker.setVisible(true);
                userDatePicker.setManaged(true);
            } else {
                userDatePicker.setVisible(false);
                userDatePicker.setManaged(false);
                applyUserFilter(selectedOption);
            }
        }
    }

    /**
     * Handle user activity date picker selection
     */
    @FXML
    private void onUserDateSelected() {
        LocalDate selectedDate = userDatePicker.getValue();
        if (selectedDate != null) {
            // Filter records by the selected date (maintain newest first order)
            List<AuditLog> filteredLogs = allUserLogs.stream()
                .filter(log -> log.getCreatedAt() != null && 
                              log.getCreatedAt().toLocalDate().equals(selectedDate))
                .collect(Collectors.toList());

            userActivityTable.getItems().setAll(filteredLogs);
            userActivityCountLabel.setText("Total Records (Filtered): " + filteredLogs.size());
        }
    }

    /**
     * Apply filter to admin activity logs
     */
    private void applyAdminFilter(String filterOption) {
        if (allAdminLogs == null || allAdminLogs.isEmpty()) {
            adminActivityTable.getItems().clear();
            adminActivityCountLabel.setText("Total Records: 0");
            return;
        }

        List<AdminLog> filteredLogs;

        if ("Recent 50".equals(filterOption)) {
            // Show only first 50 records (already sorted newest first)
            filteredLogs = allAdminLogs.stream()
                .limit(50)
                .collect(Collectors.toList());
            adminActivityCountLabel.setText("Total Records (Recent 50): " + filteredLogs.size());
        } else if ("All Records".equals(filterOption)) {
            // Show all records (already sorted newest first from loading)
            filteredLogs = allAdminLogs;
            adminActivityCountLabel.setText("Total Records: " + allAdminLogs.size());
        } else {
            filteredLogs = allAdminLogs;
            adminActivityCountLabel.setText("Total Records: " + allAdminLogs.size());
        }

        adminActivityTable.getItems().setAll(filteredLogs);
    }

    /**
     * Apply filter to user activity logs
     */
    private void applyUserFilter(String filterOption) {
        if (allUserLogs == null || allUserLogs.isEmpty()) {
            userActivityTable.getItems().clear();
            userActivityCountLabel.setText("Total Records: 0");
            return;
        }

        List<AuditLog> filteredLogs;

        if ("Recent 50".equals(filterOption)) {
            // Show only first 50 records (already sorted newest first)
            filteredLogs = allUserLogs.stream()
                .limit(50)
                .collect(Collectors.toList());
            userActivityCountLabel.setText("Total Records (Recent 50): " + filteredLogs.size());
        } else if ("All Records".equals(filterOption)) {
            // Show all records (already sorted newest first from loading)
            filteredLogs = allUserLogs;
            userActivityCountLabel.setText("Total Records: " + allUserLogs.size());
        } else {
            filteredLogs = allUserLogs;
            userActivityCountLabel.setText("Total Records: " + allUserLogs.size());
        }

        userActivityTable.getItems().setAll(filteredLogs);
    }

    @FXML
    private void refreshActivity() {
        // Validate session before refresh
        if (!SessionManager.validateAndHandleTimeout()) {
            showError("Session expired. Please log in again.");
            redirectToLogin();
            return;
        }

        // Clear current data
        adminActivityTable.getItems().clear();
        userActivityTable.getItems().clear();
        adminDatePicker.setValue(null);
        userDatePicker.setValue(null);
        adminDatePicker.setVisible(false);
        adminDatePicker.setManaged(false);
        userDatePicker.setVisible(false);
        userDatePicker.setManaged(false);

        // Reset combo boxes to default
        adminLimitCombo.getSelectionModel().select(0); // "Recent 50"
        userLimitCombo.getSelectionModel().select(0); // "Recent 50"

        // Reload data
        loadActivityData();
    }

    @FXML
    private void goBack() {
        SceneManager.switchScene("admin/dashboard.fxml");
    }

    /**
     * Display error message in UI
     */
    private void showError(String message) {
        errorBox.setVisible(true);
        errorBox.setManaged(true);
        errorLabel.setText(message);
    }

    /**
     * Redirect to login page after session expiration
     */
    private void redirectToLogin() {
        SessionManager.logout();
        Platform.runLater(() -> {
            try {
                Thread.sleep(2000);
                SceneManager.switchScene("admin/login.fxml");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
