package com.llmops.platform.modules.dataset.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class DatasetDto {
    private UUID id;
    private UUID projectId;
    private String name;
    private String description;
    private String tags;
    private int currentVersion;
    private long totalItems;
    private Instant createdAt;

    public DatasetDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public int getCurrentVersion() { return currentVersion; }
    public void setCurrentVersion(int currentVersion) { this.currentVersion = currentVersion; }
    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long totalItems) { this.totalItems = totalItems; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class CreateDatasetRequest {
        private String name;
        private String description;
        private String tags;
        private List<CreateDatasetItemRequest> items;

        public CreateDatasetRequest() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getTags() { return tags; }
        public void setTags(String tags) { this.tags = tags; }
        public List<CreateDatasetItemRequest> getItems() { return items; }
        public void setItems(List<CreateDatasetItemRequest> items) { this.items = items; }
    }

    public static class CreateDatasetItemRequest {
        private String inputVariables; // JSON string e.g. {"question": "What is Aether?"}
        private String expectedOutput;
        private String expectedContext;
        private String metadataJson;

        public CreateDatasetItemRequest() {}
        public CreateDatasetItemRequest(String inputVariables, String expectedOutput, String expectedContext) {
            this.inputVariables = inputVariables;
            this.expectedOutput = expectedOutput;
            this.expectedContext = expectedContext;
        }

        public String getInputVariables() { return inputVariables; }
        public void setInputVariables(String inputVariables) { this.inputVariables = inputVariables; }
        public String getExpectedOutput() { return expectedOutput; }
        public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
        public String getExpectedContext() { return expectedContext; }
        public void setExpectedContext(String expectedContext) { this.expectedContext = expectedContext; }
        public String getMetadataJson() { return metadataJson; }
        public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }
    }

    public static class DatasetItemDto {
        private UUID id;
        private UUID datasetVersionId;
        private String inputVariables;
        private String expectedOutput;
        private String expectedContext;
        private String metadataJson;
        private Instant createdAt;

        public DatasetItemDto() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public UUID getDatasetVersionId() { return datasetVersionId; }
        public void setDatasetVersionId(UUID datasetVersionId) { this.datasetVersionId = datasetVersionId; }
        public String getInputVariables() { return inputVariables; }
        public void setInputVariables(String inputVariables) { this.inputVariables = inputVariables; }
        public String getExpectedOutput() { return expectedOutput; }
        public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
        public String getExpectedContext() { return expectedContext; }
        public void setExpectedContext(String expectedContext) { this.expectedContext = expectedContext; }
        public String getMetadataJson() { return metadataJson; }
        public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }
}
