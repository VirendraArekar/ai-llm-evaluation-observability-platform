package com.llmops.platform.modules.provider.entity;

import com.llmops.platform.common.entity.BaseEntity;
import com.llmops.platform.modules.auth.entity.Project;
import jakarta.persistence.*;

@Entity
@Table(name = "llm_providers")
public class LlmProvider extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false)
    private ProviderType providerType;

    @Column(nullable = false)
    private String name;

    @Column(name = "base_url")
    private String baseUrl;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "is_default", nullable = false)
    private boolean defaultProvider = false;

    @Column(name = "config_json", columnDefinition = "TEXT")
    private String configJson;

    public LlmProvider() {}

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
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
    public String getConfigJson() { return configJson; }
    public void setConfigJson(String configJson) { this.configJson = configJson; }
}
