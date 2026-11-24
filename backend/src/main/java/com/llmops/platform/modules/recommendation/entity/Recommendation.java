package com.llmops.platform.modules.recommendation.entity;

import com.llmops.platform.common.entity.BaseEntity;
import com.llmops.platform.modules.auth.entity.Project;
import jakarta.persistence.*;

@Entity
@Table(name = "recommendations")
public class Recommendation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(length = 100, nullable = false)
    private String category; // COST_SAVING, LATENCY_OPTIMIZATION, PROMPT_IMPROVEMENT, MODEL_SWITCH

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "evidence_json", columnDefinition = "TEXT")
    private String evidenceJson;

    @Column(name = "expected_impact")
    private String expectedImpact;

    @Column(length = 50, nullable = false)
    private String status = "OPEN"; // OPEN, APPLIED, DISMISSED

    public Recommendation() {}

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEvidenceJson() { return evidenceJson; }
    public void setEvidenceJson(String evidenceJson) { this.evidenceJson = evidenceJson; }
    public String getExpectedImpact() { return expectedImpact; }
    public void setExpectedImpact(String expectedImpact) { this.expectedImpact = expectedImpact; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
