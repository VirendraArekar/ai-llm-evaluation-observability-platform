package com.llmops.platform.modules.experiment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ExperimentDto {
    private UUID id;
    private UUID projectId;
    private UUID datasetId;
    private String datasetName;
    private String name;
    private String description;
    private String status;
    private int totalRuns;
    private int completedRuns;
    private int failedRuns;
    private BigDecimal averageScore;
    private long averageLatency;
    private BigDecimal totalCost;
    private Instant createdAt;

    public ExperimentDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public UUID getDatasetId() { return datasetId; }
    public void setDatasetId(UUID datasetId) { this.datasetId = datasetId; }
    public String getDatasetName() { return datasetName; }
    public void setDatasetName(String datasetName) { this.datasetName = datasetName; }
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
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class CreateExperimentRequest {
        private String name;
        private String description;
        private UUID datasetId;
        private UUID promptVersionId;
        private String modelIdentifier;
        private List<String> metrics;

        public CreateExperimentRequest() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public UUID getDatasetId() { return datasetId; }
        public void setDatasetId(UUID datasetId) { this.datasetId = datasetId; }
        public UUID getPromptVersionId() { return promptVersionId; }
        public void setPromptVersionId(UUID promptVersionId) { this.promptVersionId = promptVersionId; }
        public String getModelIdentifier() { return modelIdentifier; }
        public void setModelIdentifier(String modelIdentifier) { this.modelIdentifier = modelIdentifier; }
        public List<String> getMetrics() { return metrics; }
        public void setMetrics(List<String> metrics) { this.metrics = metrics; }
    }

    public static class ExperimentRunDto {
        private UUID id;
        private UUID experimentId;
        private UUID datasetItemId;
        private String status;
        private String inputText;
        private String generatedOutput;
        private long latencyMs;
        private int tokens;
        private BigDecimal cost;
        private String errorMessage;
        private List<RunResultDto> results;

        public ExperimentRunDto() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public UUID getExperimentId() { return experimentId; }
        public void setExperimentId(UUID experimentId) { this.experimentId = experimentId; }
        public UUID getDatasetItemId() { return datasetItemId; }
        public void setDatasetItemId(UUID datasetItemId) { this.datasetItemId = datasetItemId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getInputText() { return inputText; }
        public void setInputText(String inputText) { this.inputText = inputText; }
        public String getGeneratedOutput() { return generatedOutput; }
        public void setGeneratedOutput(String generatedOutput) { this.generatedOutput = generatedOutput; }
        public long getLatencyMs() { return latencyMs; }
        public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }
        public int getTokens() { return tokens; }
        public void setTokens(int tokens) { this.tokens = tokens; }
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public List<RunResultDto> getResults() { return results; }
        public void setResults(List<RunResultDto> results) { this.results = results; }
    }

    public static class RunResultDto {
        private String metricName;
        private BigDecimal score;
        private boolean passFail;
        private String detailsJson;

        public RunResultDto() {}

        public String getMetricName() { return metricName; }
        public void setMetricName(String metricName) { this.metricName = metricName; }
        public BigDecimal getScore() { return score; }
        public void setScore(BigDecimal score) { this.score = score; }
        public boolean isPassFail() { return passFail; }
        public void setPassFail(boolean passFail) { this.passFail = passFail; }
        public String getDetailsJson() { return detailsJson; }
        public void setDetailsJson(String detailsJson) { this.detailsJson = detailsJson; }
    }

    public static class ExperimentComparisonDto {
        private ExperimentDto baselineExperiment;
        private ExperimentDto candidateExperiment;
        private double scoreDelta;
        private double latencyDeltaMs;
        private double costDelta;
        private boolean regressionDetected;
        private String regressionSummary;

        public ExperimentComparisonDto() {}

        public ExperimentDto getBaselineExperiment() { return baselineExperiment; }
        public void setBaselineExperiment(ExperimentDto baselineExperiment) { this.baselineExperiment = baselineExperiment; }
        public ExperimentDto getCandidateExperiment() { return candidateExperiment; }
        public void setCandidateExperiment(ExperimentDto candidateExperiment) { this.candidateExperiment = candidateExperiment; }
        public double getScoreDelta() { return scoreDelta; }
        public void setScoreDelta(double scoreDelta) { this.scoreDelta = scoreDelta; }
        public double getLatencyDeltaMs() { return latencyDeltaMs; }
        public void setLatencyDeltaMs(double latencyDeltaMs) { this.latencyDeltaMs = latencyDeltaMs; }
        public double getCostDelta() { return costDelta; }
        public void setCostDelta(double costDelta) { this.costDelta = costDelta; }
        public boolean isRegressionDetected() { return regressionDetected; }
        public void setRegressionDetected(boolean regressionDetected) { this.regressionDetected = regressionDetected; }
        public String getRegressionSummary() { return regressionSummary; }
        public void setRegressionSummary(String regressionSummary) { this.regressionSummary = regressionSummary; }
    }
}
