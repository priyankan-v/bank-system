package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private Long id;
    private String account_number;
    private String type;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    public Transaction() {
    }

    public Transaction(Long id, String account_number,
                       String type, BigDecimal amount,
                       LocalDateTime createdAt) {
        this.id = id;
        this.account_number = account_number;
        this.type = type;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return account_number;
    }

    public void setAccountNumber(String account_number) {
        this.account_number = account_number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}