package com.llmops.platform.modules.dataset.entity;

import com.llmops.platform.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "dataset_items")
public class DatasetItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataset_version_id", nullable = false)
    private DatasetVersion datasetVersion;

    @Column(name = "input_variables", columnDefinition = "TEXT", nullable = false)
    private String inputVariables;

    @Column(name = "expected_output", columnDefinition = "TEXT")
    private String expectedOutput;

    @Column(name = "expected_context", columnDefinition = "TEXT")
    private String expectedContext;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    public DatasetItem() {}

    public DatasetVersion getDatasetVersion() { return datasetVersion; }
    public void setDatasetVersion(DatasetVersion datasetVersion) { this.datasetVersion = datasetVersion; }
    public String getInputVariables() { return inputVariables; }
    public void setInputVariables(String inputVariables) { this.inputVariables = inputVariables; }
    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
    public String getExpectedContext() { return expectedContext; }
    public void setExpectedContext(String expectedContext) { this.expectedContext = expectedContext; }
    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }
}
