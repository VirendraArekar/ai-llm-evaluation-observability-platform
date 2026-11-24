package com.llmops.platform.modules.experiment.entity;

import com.llmops.platform.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "experiment_results")
public class ExperimentResult extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment_run_id", nullable = false)
    private ExperimentRun experimentRun;

    @Column(name = "metric_name", length = 100, nullable = false)
    private String metricName;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal score;

    @Column(name = "pass_fail", nullable = false)
    private boolean passFail;

    @Column(name = "details_json", columnDefinition = "TEXT")
    private String detailsJson;

    public ExperimentResult() {}

    public ExperimentRun getExperimentRun() { return experimentRun; }
    public void setExperimentRun(ExperimentRun experimentRun) { this.experimentRun = experimentRun; }
    public String getMetricName() { return metricName; }
    public void setMetricName(String metricName) { this.metricName = metricName; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public boolean isPassFail() { return passFail; }
    public void setPassFail(boolean passFail) { this.passFail = passFail; }
    public String getDetailsJson() { return detailsJson; }
    public void setDetailsJson(String detailsJson) { this.detailsJson = detailsJson; }
}
