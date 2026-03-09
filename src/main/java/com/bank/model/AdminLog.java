package com.bank.model;

import java.time.LocalDateTime;

public class AdminLog {

    private Long id;
    private String performedId;
    private String targetId;
    private String event;
    private String description;
    private LocalDateTime createdAt;

    public AdminLog() {}

    public AdminLog(String performedId, String targetId, String event, String description) {
        this.performedId = performedId;
        this.targetId = targetId;
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

    public String getPerformedId() {
        return performedId;
    }

    public void setPerformedId(String performedId) {
        this.performedId = performedId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
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