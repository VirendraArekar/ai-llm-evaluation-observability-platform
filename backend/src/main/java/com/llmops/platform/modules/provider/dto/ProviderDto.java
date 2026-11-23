package com.llmops.platform.modules.provider.dto;

import com.llmops.platform.modules.provider.entity.ProviderType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ProviderDto {
    private UUID id;
    private UUID projectId;
    private ProviderType providerType;
    private String name;
    private String baseUrl;
    private boolean enabled;
    private boolean defaultProvider;
    private String maskedApiKey;
    private Instant createdAt;
    private List<ModelDto> models;

    public ProviderDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public ProviderType getProviderType() { return providerType; }
    public void setProviderType(ProviderType providerType) { this.providerType = providerType; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isDefaultProvider() { return defaultProvider; }
    public void setDefaultProvider(boolean defaultProvider) { this.defaultProvider = defaultProvider; }
    public String getMaskedApiKey() { return maskedApiKey; }
    public void setMaskedApiKey(String maskedApiKey) { this.maskedApiKey = maskedApiKey; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<ModelDto> getModels() { return models; }
    public void setModels(List<ModelDto> models) { this.models = models; }

    public static class ModelDto {
        private UUID id;
        private UUID providerId;
        private String modelIdentifier;
        private String displayName;
        private int contextWindow;
        private int maxOutputTokens;
        private BigDecimal inputCostPerMillion;
        private BigDecimal outputCostPerMillion;
        private boolean supportsStreaming;
        private boolean supportsTools;
        private boolean supportsVision;
        private boolean active;

        public ModelDto() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public UUID getProviderId() { return providerId; }
        public void setProviderId(UUID providerId) { this.providerId = providerId; }
        public String getModelIdentifier() { return modelIdentifier; }
        public void setModelIdentifier(String modelIdentifier) { this.modelIdentifier = modelIdentifier; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public int getContextWindow() { return contextWindow; }
        public void setContextWindow(int contextWindow) { this.contextWindow = contextWindow; }
        public int getMaxOutputTokens() { return maxOutputTokens; }
        public void setMaxOutputTokens(int maxOutputTokens) { this.maxOutputTokens = maxOutputTokens; }
        public BigDecimal getInputCostPerMillion() { return inputCostPerMillion; }
        public void setInputCostPerMillion(BigDecimal inputCostPerMillion) { this.inputCostPerMillion = inputCostPerMillion; }
        public BigDecimal getOutputCostPerMillion() { return outputCostPerMillion; }
        public void setOutputCostPerMillion(BigDecimal outputCostPerMillion) { this.outputCostPerMillion = outputCostPerMillion; }
        public boolean isSupportsStreaming() { return supportsStreaming; }
        public void setSupportsStreaming(boolean supportsStreaming) { this.supportsStreaming = supportsStreaming; }
        public boolean isSupportsTools() { return supportsTools; }
        public void setSupportsTools(boolean supportsTools) { this.supportsTools = supportsTools; }
        public boolean isSupportsVision() { return supportsVision; }
        public void setSupportsVision(boolean supportsVision) { this.supportsVision = supportsVision; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }

        public String getName() {
            return (displayName != null && !displayName.isBlank()) ? displayName : (modelIdentifier != null ? modelIdentifier : "Unknown Model");
        }

        public String getModelId() {
            return modelIdentifier != null ? modelIdentifier : (displayName != null ? displayName : "");
        }

        public BigDecimal getCostPer1kInput() {
            return inputCostPerMillion != null ? inputCostPerMillion.divide(BigDecimal.valueOf(1000), 6, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
        }

        public BigDecimal getCostPer1kOutput() {
            return outputCostPerMillion != null ? outputCostPerMillion.divide(BigDecimal.valueOf(1000), 6, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
        }
    }

    public static class CreateProviderRequest {
        private ProviderType providerType;
        private String name;
        private String baseUrl;
        private String apiKey;
        private boolean defaultProvider;

        public CreateProviderRequest() {}

        public ProviderType getProviderType() { return providerType; }
        public void setProviderType(ProviderType providerType) { this.providerType = providerType; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public boolean isDefaultProvider() { return defaultProvider; }
        public void setDefaultProvider(boolean defaultProvider) { this.defaultProvider = defaultProvider; }
    }
}
