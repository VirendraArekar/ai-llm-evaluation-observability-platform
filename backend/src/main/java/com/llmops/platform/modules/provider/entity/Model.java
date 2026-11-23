package com.llmops.platform.modules.provider.entity;

import com.llmops.platform.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "models")
public class Model extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private LlmProvider provider;

    @Column(name = "model_identifier", nullable = false)
    private String modelIdentifier;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "context_window", nullable = false)
    private int contextWindow = 128000;

    @Column(name = "max_output_tokens", nullable = false)
    private int maxOutputTokens = 4096;

    @Column(name = "input_cost_per_million", nullable = false, precision = 10, scale = 4)
    private BigDecimal inputCostPerMillion = BigDecimal.ZERO;

    @Column(name = "output_cost_per_million", nullable = false, precision = 10, scale = 4)
    private BigDecimal outputCostPerMillion = BigDecimal.ZERO;

    @Column(name = "supports_streaming", nullable = false)
    private boolean supportsStreaming = true;

    @Column(name = "supports_tools", nullable = false)
    private boolean supportsTools = true;

    @Column(name = "supports_vision", nullable = false)
    private boolean supportsVision = false;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public Model() {}

    public LlmProvider getProvider() { return provider; }
    public void setProvider(LlmProvider provider) { this.provider = provider; }
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
}
