package com.bank.model;

import java.time.LocalDateTime;

public class Admin {

    private Long id;
    private String name;
    private String username;
    private String passwordHash;
    private boolean active;
    private String role;
    private LocalDateTime createdAt;

    public Admin() {
    }

    public Admin(Long id, String name, String username,
                 String passwordHash, boolean active, String role,
                 LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.passwordHash = passwordHash;
        this.active = active;
        this.role = role;
        this.createdAt = createdAt;
    }

    public boolean isSuperAdmin() {
        return "SUPER_ADMIN".equals(role);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}