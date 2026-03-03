package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class User {

    private Long id;
    private String name;
    private String accountNumber;
    private String pinHash;
    private BigDecimal balance;
    private int failedAttempts;
    private boolean locked;
    private LocalDateTime createdAt;

    public User() {
    }

    public User(Long id, String name, String accountNumber, String pinHash,
                BigDecimal balance, int failedAttempts,
                boolean locked, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.accountNumber = accountNumber;
        this.pinHash = pinHash;
        this.balance = balance;
        this.failedAttempts = failedAttempts;
        this.locked = locked;
        this.createdAt = createdAt;
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

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPinHash() {
        return pinHash;
    }

    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}