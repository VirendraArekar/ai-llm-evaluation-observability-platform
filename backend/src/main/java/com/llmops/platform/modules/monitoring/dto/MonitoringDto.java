package com.llmops.platform.modules.monitoring.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class MonitoringDto {

    public static class AlertDto {
        private UUID id;
        private UUID projectId;
        private String name;
        private String alertType;
        private BigDecimal thresholdValue;
        private int timeWindowMinutes;
        private boolean enabled;
        private String severity;
        private Instant lastTriggeredAt;
        private Instant createdAt;

        public AlertDto() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public UUID getProjectId() { return projectId; }
        public void setProjectId(UUID projectId) { this.projectId = projectId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAlertType() { return alertType; }
        public void setAlertType(String alertType) { this.alertType = alertType; }
        public BigDecimal getThresholdValue() { return thresholdValue; }
        public void setThresholdValue(BigDecimal thresholdValue) { this.thresholdValue = thresholdValue; }
        public int getTimeWindowMinutes() { return timeWindowMinutes; }
        public void setTimeWindowMinutes(int timeWindowMinutes) { this.timeWindowMinutes = timeWindowMinutes; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public Instant getLastTriggeredAt() { return lastTriggeredAt; }
        public void setLastTriggeredAt(Instant lastTriggeredAt) { this.lastTriggeredAt = lastTriggeredAt; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }

    public static class CreateAlertRequest {
        private String name;
        private String alertType;
        private BigDecimal thresholdValue;
        private int timeWindowMinutes = 15;
        private String severity = "WARNING";

        public CreateAlertRequest() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAlertType() { return alertType; }
        public void setAlertType(String alertType) { this.alertType = alertType; }
        public BigDecimal getThresholdValue() { return thresholdValue; }
        public void setThresholdValue(BigDecimal thresholdValue) { this.thresholdValue = thresholdValue; }
        public int getTimeWindowMinutes() { return timeWindowMinutes; }
        public void setTimeWindowMinutes(int timeWindowMinutes) { this.timeWindowMinutes = timeWindowMinutes; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
    }

    public static class NotificationDto {
        private UUID id;
        private String title;
        private String message;
        private String type;
        private boolean read;
        private String linkUrl;
        private Instant createdAt;

        public NotificationDto() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }
        public String getLinkUrl() { return linkUrl; }
        public void setLinkUrl(String linkUrl) { this.linkUrl = linkUrl; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }
}
