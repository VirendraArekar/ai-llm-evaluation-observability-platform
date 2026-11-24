package com.llmops.platform.modules.experiment.entity;

import com.llmops.platform.common.entity.BaseEntity;
import com.llmops.platform.modules.auth.entity.Project;
import com.llmops.platform.modules.dataset.entity.Dataset;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "experiments")
public class Experiment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataset_id", nullable = false)
    private Dataset dataset;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50, nullable = false)
    private String status = "PENDING"; // PENDING, RUNNING, COMPLETED, FAILED

    @Column(name = "total_runs", nullable = false)
    private int totalRuns = 0;

    @Column(name = "completed_runs", nullable = false)
    private int completedRuns = 0;

    @Column(name = "failed_runs", nullable = false)
    private int failedRuns = 0;

    @Column(name = "average_score", precision = 5, scale = 4)
    private BigDecimal averageScore = BigDecimal.ZERO;

    @Column(name = "average_latency")
    private long averageLatency = 0;

    @Column(name = "total_cost", precision = 12, scale = 6)
    private BigDecimal totalCost = BigDecimal.ZERO;

    public Experiment() {}

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public Dataset getDataset() { return dataset; }
    public void setDataset(Dataset dataset) { this.dataset = dataset; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getTotalRuns() { return totalRuns; }
    public void setTotalRuns(int totalRuns) { this.totalRuns = totalRuns; }
    public int getCompletedRuns() { return completedRuns; }
    public void setCompletedRuns(int completedRuns) { this.completedRuns = completedRuns; }
    public int getFailedRuns() { return failedRuns; }
    public void setFailedRuns(int failedRuns) { this.failedRuns = failedRuns; }
    public BigDecimal getAverageScore() { return averageScore; }
    public void setAverageScore(BigDecimal averageScore) { this.averageScore = averageScore; }
    public long getAverageLatency() { return averageLatency; }
    public void setAverageLatency(long averageLatency) { this.averageLatency = averageLatency; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
}
