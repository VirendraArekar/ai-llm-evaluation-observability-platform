package com.llmops.platform.modules.prompt.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PromptDto {
    private UUID id;
    private UUID projectId;
    private String name;
    private String slug;
    private String description;
    private String tags;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private int latestVersion;
    private PromptVersionDto publishedVersion;

    public PromptDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public int getLatestVersion() { return latestVersion; }
    public void setLatestVersion(int latestVersion) { this.latestVersion = latestVersion; }
    public PromptVersionDto getPublishedVersion() { return publishedVersion; }
    public void setPublishedVersion(PromptVersionDto publishedVersion) { this.publishedVersion = publishedVersion; }

    public static class PromptVersionDto {
        private UUID id;
        private UUID promptId;
        private int versionNumber;
        private String template;
        private String systemMessage;
        private String variablesSchema;
        private UUID modelId;
        private String modelIdentifier;
        private BigDecimal temperature;
        private int maxTokens;
        private String changeSummary;
        private boolean published;
        private String createdByName;
        private Instant createdAt;

        public PromptVersionDto() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public UUID getPromptId() { return promptId; }
        public void setPromptId(UUID promptId) { this.promptId = promptId; }
        public int getVersionNumber() { return versionNumber; }
        public void setVersionNumber(int versionNumber) { this.versionNumber = versionNumber; }
        public String getTemplate() { return template; }
        public void setTemplate(String template) { this.template = template; }
        public String getSystemMessage() { return systemMessage; }
        public void setSystemMessage(String systemMessage) { this.systemMessage = systemMessage; }
        public String getVariablesSchema() { return variablesSchema; }
        public void setVariablesSchema(String variablesSchema) { this.variablesSchema = variablesSchema; }
        public UUID getModelId() { return modelId; }
        public void setModelId(UUID modelId) { this.modelId = modelId; }
        public String getModelIdentifier() { return modelIdentifier; }
        public void setModelIdentifier(String modelIdentifier) { this.modelIdentifier = modelIdentifier; }
        public BigDecimal getTemperature() { return temperature; }
        public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
        public String getChangeSummary() { return changeSummary; }
        public void setChangeSummary(String changeSummary) { this.changeSummary = changeSummary; }
        public boolean isPublished() { return published; }
        public void setPublished(boolean published) { this.published = published; }
        public String getCreatedByName() { return createdByName; }
        public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }

    public static class CreatePromptRequest {
        private String name;
        private String description;
        private String tags;
        private String template;
        private String systemMessage;
        private String modelIdentifier;
        private BigDecimal temperature;
        private Integer maxTokens;

        public CreatePromptRequest() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getTags() { return tags; }
        public void setTags(String tags) { this.tags = tags; }
        public String getTemplate() { return template; }
        public void setTemplate(String template) { this.template = template; }
        public String getSystemMessage() { return systemMessage; }
        public void setSystemMessage(String systemMessage) { this.systemMessage = systemMessage; }
        public String getModelIdentifier() { return modelIdentifier; }
        public void setModelIdentifier(String modelIdentifier) { this.modelIdentifier = modelIdentifier; }
        public BigDecimal getTemperature() { return temperature; }
        public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
        public Integer getMaxTokens() { return maxTokens; }
        public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
    }

    public static class CreateVersionRequest {
        private String template;
        private String systemMessage;
        private String modelIdentifier;
        private BigDecimal temperature;
        private Integer maxTokens;
        private String changeSummary;

        public CreateVersionRequest() {}

        public String getTemplate() { return template; }
        public void setTemplate(String template) { this.template = template; }
        public String getSystemMessage() { return systemMessage; }
        public void setSystemMessage(String systemMessage) { this.systemMessage = systemMessage; }
        public String getModelIdentifier() { return modelIdentifier; }
        public void setModelIdentifier(String modelIdentifier) { this.modelIdentifier = modelIdentifier; }
        public BigDecimal getTemperature() { return temperature; }
        public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
        public Integer getMaxTokens() { return maxTokens; }
        public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
        public String getChangeSummary() { return changeSummary; }
        public void setChangeSummary(String changeSummary) { this.changeSummary = changeSummary; }
    }

    public static class PromptExecutionRequest {
        private Map<String, String> variables;
        private String modelOverride;
        private Double temperatureOverride;

        public PromptExecutionRequest() {}

        public Map<String, String> getVariables() { return variables; }
        public void setVariables(Map<String, String> variables) { this.variables = variables; }
        public String getModelOverride() { return modelOverride; }
        public void setModelOverride(String modelOverride) { this.modelOverride = modelOverride; }
        public Double getTemperatureOverride() { return temperatureOverride; }
        public void setTemperatureOverride(Double temperatureOverride) { this.temperatureOverride = temperatureOverride; }
    }

    public static class PromptExecutionResponse {
        private String renderedPrompt;
        private String output;
        private int tokens;
        private long latencyMs;
        private BigDecimal estimatedCost;
        private String modelUsed;

        public PromptExecutionResponse() {}

        public String getRenderedPrompt() { return renderedPrompt; }
        public void setRenderedPrompt(String renderedPrompt) { this.renderedPrompt = renderedPrompt; }
        public String getOutput() { return output; }
        public void setOutput(String output) { this.output = output; }
        public int getTokens() { return tokens; }
        public void setTokens(int tokens) { this.tokens = tokens; }
        public long getLatencyMs() { return latencyMs; }
        public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }
        public BigDecimal getEstimatedCost() { return estimatedCost; }
        public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }
        public String getModelUsed() { return modelUsed; }
        public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }
    }

    public static class OptimizePromptRequest {
        private String promptTemplate;
        private String systemMessage;
        private String targetObjective; // e.g. "Reduce tokens while maintaining accuracy", "Improve JSON consistency"
        private String judgeModel;

        public OptimizePromptRequest() {}

        public String getPromptTemplate() { return promptTemplate; }
        public void setPromptTemplate(String promptTemplate) { this.promptTemplate = promptTemplate; }
        public String getSystemMessage() { return systemMessage; }
        public void setSystemMessage(String systemMessage) { this.systemMessage = systemMessage; }
        public String getTargetObjective() { return targetObjective; }
        public void setTargetObjective(String targetObjective) { this.targetObjective = targetObjective; }
        public String getJudgeModel() { return judgeModel; }
        public void setJudgeModel(String judgeModel) { this.judgeModel = judgeModel; }
    }

    public static class PromptOptimizationResult {
        private String originalPrompt;
        private List<String> ambiguityIssues;
        private List<String> missingInstructions;
        private List<PromptAlternative> alternatives;
        private int recommendedIndex;
        private int estimatedTokenSavingsPercent;
        private String overallAnalysis;

        public PromptOptimizationResult() {}

        public String getOriginalPrompt() { return originalPrompt; }
        public void setOriginalPrompt(String originalPrompt) { this.originalPrompt = originalPrompt; }
        public List<String> getAmbiguityIssues() { return ambiguityIssues; }
        public void setAmbiguityIssues(List<String> ambiguityIssues) { this.ambiguityIssues = ambiguityIssues; }
        public List<String> getMissingInstructions() { return missingInstructions; }
        public void setMissingInstructions(List<String> missingInstructions) { this.missingInstructions = missingInstructions; }
        public List<PromptAlternative> getAlternatives() { return alternatives; }
        public void setAlternatives(List<PromptAlternative> alternatives) { this.alternatives = alternatives; }
        public int getRecommendedIndex() { return recommendedIndex; }
        public void setRecommendedIndex(int recommendedIndex) { this.recommendedIndex = recommendedIndex; }
        public int getEstimatedTokenSavingsPercent() { return estimatedTokenSavingsPercent; }
        public void setEstimatedTokenSavingsPercent(int estimatedTokenSavingsPercent) { this.estimatedTokenSavingsPercent = estimatedTokenSavingsPercent; }
        public String getOverallAnalysis() { return overallAnalysis; }
        public void setOverallAnalysis(String overallAnalysis) { this.overallAnalysis = overallAnalysis; }

        public static class PromptAlternative {
            private String name; // e.g., "Concise & Token-Optimized", "Structured & Few-Shot", "Defensive & Strict"
            private String systemMessage;
            private String optimizedTemplate;
            private String rationale;
            private int tokenCount;
            private double expectedQualityScore;

            public PromptAlternative() {}

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            public String getSystemMessage() { return systemMessage; }
            public void setSystemMessage(String systemMessage) { this.systemMessage = systemMessage; }
            public String getOptimizedTemplate() { return optimizedTemplate; }
            public void setOptimizedTemplate(String optimizedTemplate) { this.optimizedTemplate = optimizedTemplate; }
            public String getRationale() { return rationale; }
            public void setRationale(String rationale) { this.rationale = rationale; }
            public int getTokenCount() { return tokenCount; }
            public void setTokenCount(int tokenCount) { this.tokenCount = tokenCount; }
            public double getExpectedQualityScore() { return expectedQualityScore; }
            public void setExpectedQualityScore(double expectedQualityScore) { this.expectedQualityScore = expectedQualityScore; }
        }
    }
}
