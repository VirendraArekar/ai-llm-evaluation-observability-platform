package com.llmops.platform.modules.evaluation.entity;

import com.llmops.platform.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "evaluation_rubrics")
public class EvaluationRubric extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metric_id", nullable = false)
    private EvaluationMetric metric;

    @Column(nullable = false)
    private int version = 1;

    @Column(name = "prompt_template", columnDefinition = "TEXT", nullable = false)
    private String promptTemplate;

    @Column(name = "criteria_json", columnDefinition = "TEXT")
    private String criteriaJson;

    public EvaluationRubric() {}

    public EvaluationMetric getMetric() { return metric; }
    public void setMetric(EvaluationMetric metric) { this.metric = metric; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public String getPromptTemplate() { return promptTemplate; }
    public void setPromptTemplate(String promptTemplate) { this.promptTemplate = promptTemplate; }
    public String getCriteriaJson() { return criteriaJson; }
    public void setCriteriaJson(String criteriaJson) { this.criteriaJson = criteriaJson; }
}
