package com.llmops.platform.modules.evaluation.entity;

import com.llmops.platform.common.entity.BaseEntity;
import com.llmops.platform.modules.auth.entity.Project;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "evaluation_metrics")
public class EvaluationMetric extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String code;

    @Column(length = 100, nullable = false)
    private String category; // RELEVANCE, FAITHFULNESS, SAFETY, QUALITY, CUSTOM

    @Column(name = "scoring_type", length = 50, nullable = false)
    private String scoringType = "NUMERIC"; // NUMERIC, BOOLEAN, CATEGORICAL

    @Column(name = "min_score", precision = 5, scale = 2)
    private BigDecimal minScore = BigDecimal.ZERO;

    @Column(name = "max_score", precision = 5, scale = 2)
    private BigDecimal maxScore = BigDecimal.ONE;

    @Column(name = "passing_threshold", precision = 5, scale = 2)
    private BigDecimal passingThreshold = BigDecimal.valueOf(0.70);

    @Column(name = "is_system", nullable = false)
    private boolean system = false;

    public EvaluationMetric() {}

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getScoringType() { return scoringType; }
    public void setScoringType(String scoringType) { this.scoringType = scoringType; }
    public BigDecimal getMinScore() { return minScore; }
    public void setMinScore(BigDecimal minScore) { this.minScore = minScore; }
    public BigDecimal getMaxScore() { return maxScore; }
    public void setMaxScore(BigDecimal maxScore) { this.maxScore = maxScore; }
    public BigDecimal getPassingThreshold() { return passingThreshold; }
    public void setPassingThreshold(BigDecimal passingThreshold) { this.passingThreshold = passingThreshold; }
    public boolean isSystem() { return system; }
    public void setSystem(boolean system) { this.system = system; }
}
