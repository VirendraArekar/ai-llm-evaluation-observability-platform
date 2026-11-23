package com.llmops.platform.modules.tracing.entity;

import com.llmops.platform.modules.auth.entity.Project;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "traces")
public class Trace {

    @Id
    @Column(length = 64, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "user_id", length = 100)
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String status = "SUCCESS"; // SUCCESS, ERROR, RUNNING

    @Column(name = "latency_ms", nullable = false)
    private long latencyMs = 0;

    @Column(name = "total_tokens", nullable = false)
    private int totalTokens = 0;

    @Column(name = "input_tokens", nullable = false)
    private int inputTokens = 0;

    @Column(name = "output_tokens", nullable = false)
    private int outputTokens = 0;

    @Column(name = "estimated_cost", nullable = false, precision = 12, scale = 6)
    private BigDecimal estimatedCost = BigDecimal.ZERO;

    @Column(length = 500)
    private String tags;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "start_time", nullable = false)
    private Instant startTime = Instant.now();

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Trace() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getLatencyMs() { return latencyMs; }
    public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }
    public int getTotalTokens() { return totalTokens; }
    public void setTotalTokens(int totalTokens) { this.totalTokens = totalTokens; }
    public int getInputTokens() { return inputTokens; }
    public void setInputTokens(int inputTokens) { this.inputTokens = inputTokens; }
    public int getOutputTokens() { return outputTokens; }
    public void setOutputTokens(int outputTokens) { this.outputTokens = outputTokens; }
    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }
    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
