package com.llmops.platform.modules.monitoring.entity;

import com.llmops.platform.common.entity.BaseEntity;
import com.llmops.platform.modules.auth.entity.Project;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "alerts")
public class Alert extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String name;

    @Column(name = "alert_type", length = 100, nullable = false)
    private String alertType; // LATENCY_THRESHOLD, ERROR_RATE, BUDGET_EXCEEDED, EVAL_SCORE_DROP

    @Column(name = "threshold_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal thresholdValue;

    @Column(name = "time_window_minutes", nullable = false)
    private int timeWindowMinutes = 15;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled = true;

    @Column(length = 50, nullable = false)
    private String severity = "WARNING"; // INFO, WARNING, CRITICAL

    @Column(name = "last_triggered_at")
    private Instant lastTriggeredAt;

    public Alert() {}

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
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
}
