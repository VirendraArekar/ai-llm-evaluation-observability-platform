package com.llmops.platform.modules.evaluation.entity;

import com.llmops.platform.common.entity.BaseEntity;
import com.llmops.platform.modules.auth.entity.Project;
import com.llmops.platform.modules.tracing.entity.Span;
import com.llmops.platform.modules.tracing.entity.Trace;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "evaluations")
public class Evaluation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trace_id")
    private Trace trace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "span_id")
    private Span span;

    @Column(name = "experiment_run_id")
    private UUID experimentRunId;

    @Column(name = "evaluation_type", length = 50, nullable = false)
    private String evaluationType; // LLM_AS_A_JUDGE, DETERMINISTIC, HUMAN

    @Column(name = "metric_name", length = 100, nullable = false)
    private String metricName;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal score;

    @Column(name = "pass_fail", nullable = false)
    private boolean passFail;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(columnDefinition = "TEXT")
    private String evidence;

    @Column(length = 100)
    private String category;

    @Column(name = "judge_model", length = 100)
    private String judgeModel;

    @Column(name = "rubric_version")
    private int rubricVersion = 1;

    @Column(name = "execution_time_ms")
    private long executionTimeMs = 0;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    public Evaluation() {}

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public Trace getTrace() { return trace; }
    public void setTrace(Trace trace) { this.trace = trace; }
    public Span getSpan() { return span; }
    public void setSpan(Span span) { this.span = span; }
    public UUID getExperimentRunId() { return experimentRunId; }
    public void setExperimentRunId(UUID experimentRunId) { this.experimentRunId = experimentRunId; }
    public String getEvaluationType() { return evaluationType; }
    public void setEvaluationType(String evaluationType) { this.evaluationType = evaluationType; }
    public String getMetricName() { return metricName; }
    public void setMetricName(String metricName) { this.metricName = metricName; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public boolean isPassFail() { return passFail; }
    public void setPassFail(boolean passFail) { this.passFail = passFail; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public String getEvidence() { return evidence; }
    public void setEvidence(String evidence) { this.evidence = evidence; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getJudgeModel() { return judgeModel; }
    public void setJudgeModel(String judgeModel) { this.judgeModel = judgeModel; }
    public int getRubricVersion() { return rubricVersion; }
    public void setRubricVersion(int rubricVersion) { this.rubricVersion = rubricVersion; }
    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }
}
