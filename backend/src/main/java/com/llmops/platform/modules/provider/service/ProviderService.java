package com.llmops.platform.modules.provider.service;

import com.llmops.platform.common.exception.ResourceNotFoundException;
import com.llmops.platform.modules.auth.entity.Project;
import com.llmops.platform.modules.auth.repository.ProjectRepository;
import com.llmops.platform.modules.provider.client.LlmClientRegistry;
import com.llmops.platform.modules.provider.client.LlmProviderClient;
import com.llmops.platform.modules.provider.dto.ProviderDto;
import com.llmops.platform.modules.provider.dto.ProviderDto.CreateProviderRequest;
import com.llmops.platform.modules.provider.dto.ProviderDto.ModelDto;
import com.llmops.platform.modules.provider.entity.LlmProvider;
import com.llmops.platform.modules.provider.entity.Model;
import com.llmops.platform.modules.provider.entity.ProviderCredential;
import com.llmops.platform.modules.provider.entity.ProviderType;
import com.llmops.platform.modules.provider.repository.LlmProviderRepository;
import com.llmops.platform.modules.provider.repository.ModelRepository;
import com.llmops.platform.modules.provider.repository.ProviderCredentialRepository;
import com.llmops.platform.modules.provider.util.EncryptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProviderService {

    private final LlmProviderRepository providerRepository;
    private final ProviderCredentialRepository credentialRepository;
    private final ModelRepository modelRepository;
    private final ProjectRepository projectRepository;
    private final EncryptionUtils encryptionUtils;
    private final LlmClientRegistry clientRegistry;

    public ProviderService(LlmProviderRepository providerRepository,
                           ProviderCredentialRepository credentialRepository,
                           ModelRepository modelRepository,
                           ProjectRepository projectRepository,
                           EncryptionUtils encryptionUtils,
                           LlmClientRegistry clientRegistry) {
        this.providerRepository = providerRepository;
        this.credentialRepository = credentialRepository;
        this.modelRepository = modelRepository;
        this.projectRepository = projectRepository;
        this.encryptionUtils = encryptionUtils;
        this.clientRegistry = clientRegistry;
    }

    @Transactional(readOnly = true)
    public List<ProviderDto> getProvidersByProject(UUID projectId) {
        List<LlmProvider> providers = projectId != null ? providerRepository.findByProjectId(projectId) : List.of();
        if (providers == null || providers.isEmpty()) {
            providers = providerRepository.findAll();
        }
        return providers.stream()
                .map(this::mapToProviderDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ModelDto> getModelsByProject(UUID projectId) {
        List<Model> models = projectId != null ? modelRepository.findByProviderProjectId(projectId) : List.of();
        if (models == null || models.isEmpty()) {
            models = modelRepository.findAll();
        }
        return models.stream()
                .map(this::mapToModelDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProviderDto createProvider(UUID projectId, CreateProviderRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        LlmProvider provider = new LlmProvider();
        provider.setProject(project);
        provider.setName(request.getName());
        provider.setProviderType(request.getProviderType());
        provider.setBaseUrl(request.getBaseUrl());
        provider.setDefaultProvider(request.isDefaultProvider());
        provider.setEnabled(true);
        provider = providerRepository.save(provider);

        if (request.getApiKey() != null && !request.getApiKey().isBlank()) {
            EncryptionUtils.EncryptedPayload payload = encryptionUtils.encrypt(request.getApiKey());
            ProviderCredential cred = new ProviderCredential();
            cred.setProvider(provider);
            cred.setEncryptedApiKey(payload.getCipherText());
            cred.setIvSalt(payload.getIv());
            cred.setKeyMasked(EncryptionUtils.maskApiKey(request.getApiKey()));
            credentialRepository.save(cred);
        }

        seedDefaultModelsForProvider(provider);
        return mapToProviderDto(provider);
    }

    @Transactional(readOnly = true)
    public boolean testProvider(UUID providerId) {
        LlmProvider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found: " + providerId));

        String apiKey = "";
        Optional<ProviderCredential> credOpt = credentialRepository.findByProviderId(provider.getId());
        if (credOpt.isPresent()) {
            apiKey = encryptionUtils.decrypt(credOpt.get().getEncryptedApiKey(), credOpt.get().getIvSalt());
        }

        LlmProviderClient client = clientRegistry.getClient(provider.getProviderType());
        return client.testConnection(apiKey, provider.getBaseUrl(), null);
    }

    private void seedDefaultModelsForProvider(LlmProvider provider) {
        if (provider.getProviderType() == ProviderType.OPENAI) {
            createModel(provider, "gpt-4o", "OpenAI GPT-4o", 128000, 4096, BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0), true, true, true);
            createModel(provider, "gpt-4o-mini", "OpenAI GPT-4o Mini", 128000, 4096, BigDecimal.valueOf(0.15), BigDecimal.valueOf(0.60), true, true, true);
        } else if (provider.getProviderType() == ProviderType.GEMINI) {
            createModel(provider, "gemini-1.5-pro", "Google Gemini 1.5 Pro", 1000000, 8192, BigDecimal.valueOf(3.5), BigDecimal.valueOf(10.5), true, true, true);
            createModel(provider, "gemini-1.5-flash", "Google Gemini 1.5 Flash", 1000000, 8192, BigDecimal.valueOf(0.075), BigDecimal.valueOf(0.30), true, true, true);
        } else if (provider.getProviderType() == ProviderType.CLAUDE) {
            createModel(provider, "claude-3-5-sonnet", "Anthropic Claude 3.5 Sonnet", 200000, 8192, BigDecimal.valueOf(3.0), BigDecimal.valueOf(15.0), true, true, true);
            createModel(provider, "claude-3-haiku", "Anthropic Claude 3 Haiku", 200000, 4096, BigDecimal.valueOf(0.25), BigDecimal.valueOf(1.25), true, true, false);
        } else if (provider.getProviderType() == ProviderType.OLLAMA) {
            createModel(provider, "llama3.1", "Meta Llama 3.1 8B (Local)", 128000, 4096, BigDecimal.ZERO, BigDecimal.ZERO, true, true, false);
            createModel(provider, "mistral", "Mistral 7B (Local)", 32000, 4096, BigDecimal.ZERO, BigDecimal.ZERO, true, false, false);
        }
    }

    private void createModel(LlmProvider provider, String identifier, String displayName, int contextWindow, int maxOutputTokens, BigDecimal inputCost, BigDecimal outputCost, boolean streaming, boolean tools, boolean vision) {
        Model model = new Model();
        model.setProvider(provider);
        model.setModelIdentifier(identifier);
        model.setDisplayName(displayName);
        model.setContextWindow(contextWindow);
        model.setMaxOutputTokens(maxOutputTokens);
        model.setInputCostPerMillion(inputCost);
        model.setOutputCostPerMillion(outputCost);
        model.setSupportsStreaming(streaming);
        model.setSupportsTools(tools);
        model.setSupportsVision(vision);
        model.setActive(true);
        modelRepository.save(model);
    }

    private ProviderDto mapToProviderDto(LlmProvider p) {
        ProviderDto dto = new ProviderDto();
        dto.setId(p.getId());
        dto.setProjectId(p.getProject().getId());
        dto.setName(p.getName());
        dto.setProviderType(p.getProviderType());
        dto.setBaseUrl(p.getBaseUrl());
        dto.setEnabled(p.isEnabled());
        dto.setDefaultProvider(p.isDefaultProvider());
        dto.setCreatedAt(p.getCreatedAt());

        credentialRepository.findByProviderId(p.getId()).ifPresent(cred -> {
            dto.setMaskedApiKey(cred.getKeyMasked());
        });

        List<ModelDto> models = modelRepository.findByProviderId(p.getId()).stream()
                .map(this::mapToModelDto)
                .collect(Collectors.toList());
        dto.setModels(models);
        return dto;
    }

    private ModelDto mapToModelDto(Model m) {
        ModelDto dto = new ModelDto();
        dto.setId(m.getId());
        dto.setProviderId(m.getProvider().getId());
        dto.setModelIdentifier(m.getModelIdentifier());
        dto.setDisplayName(m.getDisplayName());
        dto.setContextWindow(m.getContextWindow());
        dto.setMaxOutputTokens(m.getMaxOutputTokens());
        dto.setInputCostPerMillion(m.getInputCostPerMillion());
        dto.setOutputCostPerMillion(m.getOutputCostPerMillion());
        dto.setSupportsStreaming(m.isSupportsStreaming());
        dto.setSupportsTools(m.isSupportsTools());
        dto.setSupportsVision(m.isSupportsVision());
        dto.setActive(m.isActive());
        return dto;
    }
}
