package com.bank.model;

import java.time.LocalDateTime;

public class AuditLog {

    private Long id;
    private String accountNumber;
    private String adminId;
    private String event;
    private String description;
    private LocalDateTime createdAt;

    public AuditLog() {}

    public AuditLog(String accountNumber, String adminId, String event, String description) {
        this.accountNumber = accountNumber;
        this.adminId = adminId;
        this.event = event;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}