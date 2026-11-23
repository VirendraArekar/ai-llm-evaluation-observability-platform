package com.llmops.platform.modules.evaluation.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class EvaluationDto {
    private UUID id;
    private UUID projectId;
    private String traceId;
    private String spanId;
    private UUID experimentRunId;
    private String evaluationType;
    private String metricName;
    private BigDecimal score;
    private boolean passFail;
    private String explanation;
    private String evidence;
    private String category;
    private String judgeModel;
    private int rubricVersion;
    private long executionTimeMs;
    private Instant createdAt;

    public EvaluationDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getSpanId() { return spanId; }
    public void setSpanId(String spanId) { this.spanId = spanId; }
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
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class RunJudgeRequest {
        private UUID projectId;
        private String traceId;
        private String spanId;
        private String metricCode; // RELEVANCE, FAITHFULNESS, CORRECTNESS, TOXICITY, CUSTOM
        private String inputPrompt;
        private String generatedResponse;
        private String sourceContext;
        private String expectedOutput;
        private String judgeModel = "gpt-4o-mini";
        private Double passingThreshold = 0.70;
        private String customCriteria;

        public RunJudgeRequest() {}

        public UUID getProjectId() { return projectId; }
        public void setProjectId(UUID projectId) { this.projectId = projectId; }
        public String getTraceId() { return traceId; }
        public void setTraceId(String traceId) { this.traceId = traceId; }
        public String getSpanId() { return spanId; }
        public void setSpanId(String spanId) { this.spanId = spanId; }
        public String getMetricCode() { return metricCode; }
        public void setMetricCode(String metricCode) { this.metricCode = metricCode; }
        public String getInputPrompt() { return inputPrompt; }
        public void setInputPrompt(String inputPrompt) { this.inputPrompt = inputPrompt; }
        public String getGeneratedResponse() { return generatedResponse; }
        public void setGeneratedResponse(String generatedResponse) { this.generatedResponse = generatedResponse; }
        public String getSourceContext() { return sourceContext; }
        public void setSourceContext(String sourceContext) { this.sourceContext = sourceContext; }
        public String getExpectedOutput() { return expectedOutput; }
        public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
        public String getJudgeModel() { return judgeModel; }
        public void setJudgeModel(String judgeModel) { this.judgeModel = judgeModel; }
        public Double getPassingThreshold() { return passingThreshold; }
        public void setPassingThreshold(Double passingThreshold) { this.passingThreshold = passingThreshold; }
        public String getCustomCriteria() { return customCriteria; }
        public void setCustomCriteria(String customCriteria) { this.customCriteria = customCriteria; }
    }

    public static class JudgeResultDto {
        private String metricName;
        private double score;
        private boolean passFail;
        private String explanation;
        private String evidence;
        private String judgeModel;
        private long executionTimeMs;

        public JudgeResultDto() {}

        public String getMetricName() { return metricName; }
        public void setMetricName(String metricName) { this.metricName = metricName; }
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
        public boolean isPassFail() { return passFail; }
        public void setPassFail(boolean passFail) { this.passFail = passFail; }
        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
        public String getEvidence() { return evidence; }
        public void setEvidence(String evidence) { this.evidence = evidence; }
        public String getJudgeModel() { return judgeModel; }
        public void setJudgeModel(String judgeModel) { this.judgeModel = judgeModel; }
        public long getExecutionTimeMs() { return executionTimeMs; }
        public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    }

    public static class DetectHallucinationRequest {
        private UUID projectId;
        private String traceId;
        private String inputPrompt;
        private String generatedResponse;
        private String sourceContext;
        private String judgeModel = "gpt-4o-mini";

        public DetectHallucinationRequest() {}

        public UUID getProjectId() { return projectId; }
        public void setProjectId(UUID projectId) { this.projectId = projectId; }
        public String getTraceId() { return traceId; }
        public void setTraceId(String traceId) { this.traceId = traceId; }
        public String getInputPrompt() { return inputPrompt; }
        public void setInputPrompt(String inputPrompt) { this.inputPrompt = inputPrompt; }
        public String getGeneratedResponse() { return generatedResponse; }
        public void setGeneratedResponse(String generatedResponse) { this.generatedResponse = generatedResponse; }
        public String getSourceContext() { return sourceContext; }
        public void setSourceContext(String sourceContext) { this.sourceContext = sourceContext; }
        public String getJudgeModel() { return judgeModel; }
        public void setJudgeModel(String judgeModel) { this.judgeModel = judgeModel; }
    }

    public static class HallucinationResultDto {
        private double hallucinationRiskScore; // 0.0 (safe) to 1.0 (hallucinated)
        private boolean hallucinationDetected;
        private String summary;
        private List<SentenceClaimDto> sentenceClaims;
        private String suggestedCorrection;
        private long executionTimeMs;

        public HallucinationResultDto() {}

        public double getHallucinationRiskScore() { return hallucinationRiskScore; }
        public void setHallucinationRiskScore(double hallucinationRiskScore) { this.hallucinationRiskScore = hallucinationRiskScore; }
        public boolean isHallucinationDetected() { return hallucinationDetected; }
        public void setHallucinationDetected(boolean hallucinationDetected) { this.hallucinationDetected = hallucinationDetected; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public List<SentenceClaimDto> getSentenceClaims() { return sentenceClaims; }
        public void setSentenceClaims(List<SentenceClaimDto> sentenceClaims) { this.sentenceClaims = sentenceClaims; }
        public String getSuggestedCorrection() { return suggestedCorrection; }
        public void setSuggestedCorrection(String suggestedCorrection) { this.suggestedCorrection = suggestedCorrection; }
        public long getExecutionTimeMs() { return executionTimeMs; }
        public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    }

    public static class SentenceClaimDto {
        private String sentence;
        private String claimType; // "SUPPORTED", "UNSUPPORTED", "CONTRADICTION"
        private String evidenceQuote;
        private double riskScore;
        private String explanation;

        public SentenceClaimDto() {}

        public String getSentence() { return sentence; }
        public void setSentence(String sentence) { this.sentence = sentence; }
        public String getClaimType() { return claimType; }
        public void setClaimType(String claimType) { this.claimType = claimType; }
        public String getEvidenceQuote() { return evidenceQuote; }
        public void setEvidenceQuote(String evidenceQuote) { this.evidenceQuote = evidenceQuote; }
        public double getRiskScore() { return riskScore; }
        public void setRiskScore(double riskScore) { this.riskScore = riskScore; }
        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
    }

    public static class MetricDto {
        private UUID id;
        private String name;
        private String code;
        private String category;
        private String scoringType;
        private BigDecimal minScore;
        private BigDecimal maxScore;
        private BigDecimal passingThreshold;
        private boolean system;

        public MetricDto() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
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

    public static class EvaluationStatsDto {
        private long totalEvaluations;
        private long passedEvaluations;
        private long failedEvaluations;
        private double passRate;
        private double averageScore;
        private double hallucinationRate;

        public EvaluationStatsDto() {}

        public long getTotalEvaluations() { return totalEvaluations; }
        public void setTotalEvaluations(long totalEvaluations) { this.totalEvaluations = totalEvaluations; }
        public long getPassedEvaluations() { return passedEvaluations; }
        public void setPassedEvaluations(long passedEvaluations) { this.passedEvaluations = passedEvaluations; }
        public long getFailedEvaluations() { return failedEvaluations; }
        public void setFailedEvaluations(long failedEvaluations) { this.failedEvaluations = failedEvaluations; }
        public double getPassRate() { return passRate; }
        public void setPassRate(double passRate) { this.passRate = passRate; }
        public double getAverageScore() { return averageScore; }
        public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
        public double getHallucinationRate() { return hallucinationRate; }
        public void setHallucinationRate(double hallucinationRate) { this.hallucinationRate = hallucinationRate; }
    }
}
