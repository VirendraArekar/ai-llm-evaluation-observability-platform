package com.llmops.platform.modules.rootcause.dto;

import java.util.List;

public class RootCauseDto {

    public static class RootCauseRequest {
        private String traceId;
        private String failedPrompt;
        private String errorMessage;
        private String contextRetrieved;
        private Long latencyMs;
        private String modelUsed;

        public RootCauseRequest() {}

        public String getTraceId() { return traceId; }
        public void setTraceId(String traceId) { this.traceId = traceId; }
        public String getFailedPrompt() { return failedPrompt; }
        public void setFailedPrompt(String failedPrompt) { this.failedPrompt = failedPrompt; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public String getContextRetrieved() { return contextRetrieved; }
        public void setContextRetrieved(String contextRetrieved) { this.contextRetrieved = contextRetrieved; }
        public Long getLatencyMs() { return latencyMs; }
        public void setLatencyMs(Long latencyMs) { this.latencyMs = latencyMs; }
        public String getModelUsed() { return modelUsed; }
        public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }
    }

    public static class RootCauseResultDto {
        private String rootCauseHypothesis;
        private String severity; // CRITICAL, HIGH, MEDIUM, LOW
        private double confidenceScore;
        private List<String> supportingTraceEvidence;
        private List<String> suggestedFixes;
        private List<String> recommendedDebuggingSteps;
        private String diagnosticCategory; // PROVIDER_TIMEOUT, CONTEXT_OVERFLOW, SCHEMA_MISMATCH, PROMPT_AMBIGUITY, RETRIEVAL_FAILURE

        public RootCauseResultDto() {}

        public String getRootCauseHypothesis() { return rootCauseHypothesis; }
        public void setRootCauseHypothesis(String rootCauseHypothesis) { this.rootCauseHypothesis = rootCauseHypothesis; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public double getConfidenceScore() { return confidenceScore; }
        public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }
        public List<String> getSupportingTraceEvidence() { return supportingTraceEvidence; }
        public void setSupportingTraceEvidence(List<String> supportingTraceEvidence) { this.supportingTraceEvidence = supportingTraceEvidence; }
        public List<String> getSuggestedFixes() { return suggestedFixes; }
        public void setSuggestedFixes(List<String> suggestedFixes) { this.suggestedFixes = suggestedFixes; }
        public List<String> getRecommendedDebuggingSteps() { return recommendedDebuggingSteps; }
        public void setRecommendedDebuggingSteps(List<String> recommendedDebuggingSteps) { this.recommendedDebuggingSteps = recommendedDebuggingSteps; }
        public String getDiagnosticCategory() { return diagnosticCategory; }
        public void setDiagnosticCategory(String diagnosticCategory) { this.diagnosticCategory = diagnosticCategory; }
    }
}
