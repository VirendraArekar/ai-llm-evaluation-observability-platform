package com.llmops.platform.modules.benchmark.entity;

import com.llmops.platform.common.entity.BaseEntity;
import com.llmops.platform.modules.auth.entity.Project;
import jakarta.persistence.*;

@Entity
@Table(name = "model_comparisons")
public class ModelComparison extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String name;

    @Column(name = "input_prompt", columnDefinition = "TEXT", nullable = false)
    private String inputPrompt;

    @Column(name = "baseline_model", length = 100, nullable = false)
    private String baselineModel;

    @Column(name = "comparison_models_json", columnDefinition = "TEXT", nullable = false)
    private String comparisonModelsJson;

    @Column(name = "results_json", columnDefinition = "TEXT")
    private String resultsJson;

    public ModelComparison() {}

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getInputPrompt() { return inputPrompt; }
    public void setInputPrompt(String inputPrompt) { this.inputPrompt = inputPrompt; }
    public String getBaselineModel() { return baselineModel; }
    public void setBaselineModel(String baselineModel) { this.baselineModel = baselineModel; }
    public String getComparisonModelsJson() { return comparisonModelsJson; }
    public void setComparisonModelsJson(String comparisonModelsJson) { this.comparisonModelsJson = comparisonModelsJson; }
    public String getResultsJson() { return resultsJson; }
    public void setResultsJson(String resultsJson) { this.resultsJson = resultsJson; }
}
