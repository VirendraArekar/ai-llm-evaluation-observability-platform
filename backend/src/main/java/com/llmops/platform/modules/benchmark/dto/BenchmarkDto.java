package com.llmops.platform.modules.benchmark.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class BenchmarkDto {

    public static class RunBenchmarkRequest {
        private String name;
        private String inputPrompt;
        private String baselineModel;
        private List<String> comparisonModels;
        private Double temperature = 0.7;
        private Integer maxTokens = 1024;

        public RunBenchmarkRequest() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getInputPrompt() { return inputPrompt; }
        public void setInputPrompt(String inputPrompt) { this.inputPrompt = inputPrompt; }
        public void setPrompt(String prompt) { if (this.inputPrompt == null || this.inputPrompt.isBlank()) this.inputPrompt = prompt; }
        public String getBaselineModel() { return baselineModel; }
        public void setBaselineModel(String baselineModel) { this.baselineModel = baselineModel; }
        public List<String> getComparisonModels() { return comparisonModels; }
        public void setComparisonModels(List<String> comparisonModels) { this.comparisonModels = comparisonModels; }
        public void setModelIds(List<String> modelIds) { if (this.comparisonModels == null || this.comparisonModels.isEmpty()) this.comparisonModels = modelIds; }
        public Double getTemperature() { return temperature; }
        public void setTemperature(Double temperature) { this.temperature = temperature; }
        public Integer getMaxTokens() { return maxTokens; }
        public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
    }

    public static class ModelBenchmarkResultDto {
        private String modelIdentifier;
        private String displayName;
        private String output;
        private long latencyMs;
        private int tokens;
        private BigDecimal cost;
        private double qualityScore;
        private double hallucinationRisk;
        private int rank;

        public ModelBenchmarkResultDto() {}

        public String getModelIdentifier() { return modelIdentifier; }
        public void setModelIdentifier(String modelIdentifier) { this.modelIdentifier = modelIdentifier; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public String getOutput() { return output; }
        public void setOutput(String output) { this.output = output; }
        public long getLatencyMs() { return latencyMs; }
        public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }
        public int getTokens() { return tokens; }
        public void setTokens(int tokens) { this.tokens = tokens; }
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
        public double getQualityScore() { return qualityScore; }
        public void setQualityScore(double qualityScore) { this.qualityScore = qualityScore; }
        public double getHallucinationRisk() { return hallucinationRisk; }
        public void setHallucinationRisk(double hallucinationRisk) { this.hallucinationRisk = hallucinationRisk; }
        public int getRank() { return rank; }
        public void setRank(int rank) { this.rank = rank; }
    }

    public static class BenchmarkComparisonResponse {
        private UUID id;
        private String name;
        private String inputPrompt;
        private String baselineModel;
        private List<ModelBenchmarkResultDto> modelResults;
        private String bestOverallModel;
        private String mostCostEffectiveModel;
        private String lowestLatencyModel;
        private Instant createdAt;

        public BenchmarkComparisonResponse() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getInputPrompt() { return inputPrompt; }
        public void setInputPrompt(String inputPrompt) { this.inputPrompt = inputPrompt; }
        public String getBaselineModel() { return baselineModel; }
        public void setBaselineModel(String baselineModel) { this.baselineModel = baselineModel; }
        public List<ModelBenchmarkResultDto> getModelResults() { return modelResults; }
        public void setModelResults(List<ModelBenchmarkResultDto> modelResults) { this.modelResults = modelResults; }
        public String getBestOverallModel() { return bestOverallModel; }
        public void setBestOverallModel(String bestOverallModel) { this.bestOverallModel = bestOverallModel; }
        public String getMostCostEffectiveModel() { return mostCostEffectiveModel; }
        public void setMostCostEffectiveModel(String mostCostEffectiveModel) { this.mostCostEffectiveModel = mostCostEffectiveModel; }
        public String getLowestLatencyModel() { return lowestLatencyModel; }
        public void setLowestLatencyModel(String lowestLatencyModel) { this.lowestLatencyModel = lowestLatencyModel; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }
}
