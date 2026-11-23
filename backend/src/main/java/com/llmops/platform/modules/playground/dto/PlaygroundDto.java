package com.llmops.platform.modules.playground.dto;

import java.math.BigDecimal;

public class PlaygroundDto {

    public static class PlaygroundRequest {
        private String model = "gpt-4o-mini";
        private String modelId;
        private String systemPrompt;
        private String userPrompt;
        private Double temperature = 0.7;
        private Integer maxTokens = 2048;
        private boolean stream = false;

        public PlaygroundRequest() {}

        public String getModel() {
            return (model != null && !model.isBlank()) ? model : (modelId != null ? modelId : "gpt-4o-mini");
        }
        public void setModel(String model) {
            this.model = model;
            if (this.modelId == null) this.modelId = model;
        }

        public String getModelId() {
            return (modelId != null && !modelId.isBlank()) ? modelId : model;
        }
        public void setModelId(String modelId) {
            this.modelId = modelId;
            if (this.model == null || "gpt-4o-mini".equals(this.model)) this.model = modelId;
        }

        public String getSystemPrompt() { return systemPrompt; }
        public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }

        public String getUserPrompt() { return userPrompt; }
        public void setUserPrompt(String userPrompt) { this.userPrompt = userPrompt; }

        public Double getTemperature() { return temperature; }
        public void setTemperature(Double temperature) { this.temperature = temperature; }

        public Integer getMaxTokens() { return maxTokens; }
        public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }

        public boolean isStream() { return stream; }
        public void setStream(boolean stream) { this.stream = stream; }
    }

    public static class PlaygroundResponse {
        private String output;
        private long latencyMs;
        private int tokens;
        private BigDecimal estimatedCost;
        private String modelUsed;

        public PlaygroundResponse() {}

        public String getOutput() { return output; }
        public void setOutput(String output) { this.output = output; }

        // Alias for frontend compatibility
        public String getContent() { return output; }
        public void setContent(String content) { this.output = content; }

        public long getLatencyMs() { return latencyMs; }
        public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }

        public int getTokens() { return tokens; }
        public void setTokens(int tokens) { this.tokens = tokens; }

        public int getPromptTokens() { return Math.max(1, tokens / 3); }
        public int getCompletionTokens() { return Math.max(1, tokens - (tokens / 3)); }

        public BigDecimal getEstimatedCost() { return estimatedCost; }
        public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }

        // Alias for frontend compatibility
        public BigDecimal getTotalCost() { return estimatedCost; }
        public void setTotalCost(BigDecimal cost) { this.estimatedCost = cost; }

        public String getModelUsed() { return modelUsed; }
        public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }
    }
}
