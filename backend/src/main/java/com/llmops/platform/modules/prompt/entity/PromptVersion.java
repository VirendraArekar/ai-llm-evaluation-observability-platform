package com.llmops.platform.modules.prompt.entity;

import com.llmops.platform.common.entity.BaseEntity;
import com.llmops.platform.modules.auth.entity.User;
import com.llmops.platform.modules.provider.entity.Model;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "prompt_versions")
public class PromptVersion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_id", nullable = false)
    private Prompt prompt;

    @Column(name = "version_number", nullable = false)
    private int versionNumber;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String template;

    @Column(name = "system_message", columnDefinition = "TEXT")
    private String systemMessage;

    @Column(name = "variables_schema", columnDefinition = "TEXT")
    private String variablesSchema;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private Model model;

    @Column(precision = 3, scale = 2)
    private BigDecimal temperature = BigDecimal.valueOf(0.70);

    @Column(name = "max_tokens")
    private int maxTokens = 2048;

    @Column(name = "change_summary", length = 500)
    private String changeSummary;

    @Column(name = "is_published", nullable = false)
    private boolean published = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    public PromptVersion() {}

    public Prompt getPrompt() { return prompt; }
    public void setPrompt(Prompt prompt) { this.prompt = prompt; }
    public int getVersionNumber() { return versionNumber; }
    public void setVersionNumber(int versionNumber) { this.versionNumber = versionNumber; }
    public String getTemplate() { return template; }
    public void setTemplate(String template) { this.template = template; }
    public String getSystemMessage() { return systemMessage; }
    public void setSystemMessage(String systemMessage) { this.systemMessage = systemMessage; }
    public String getVariablesSchema() { return variablesSchema; }
    public void setVariablesSchema(String variablesSchema) { this.variablesSchema = variablesSchema; }
    public Model getModel() { return model; }
    public void setModel(Model model) { this.model = model; }
    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    public String getChangeSummary() { return changeSummary; }
    public void setChangeSummary(String changeSummary) { this.changeSummary = changeSummary; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}
