package com.llmops.platform.modules.provider.service;

import com.llmops.platform.modules.provider.client.*;
import com.llmops.platform.modules.provider.entity.LlmProvider;
import com.llmops.platform.modules.provider.entity.Model;
import com.llmops.platform.modules.provider.entity.ProviderCredential;
import com.llmops.platform.modules.provider.repository.ModelRepository;
import com.llmops.platform.modules.provider.repository.ProviderCredentialRepository;
import com.llmops.platform.modules.provider.util.EncryptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class LlmExecutionService {

    private static final Logger log = LoggerFactory.getLogger(LlmExecutionService.class);

    private final LlmClientRegistry clientRegistry;
    private final ModelRepository modelRepository;
    private final ProviderCredentialRepository credentialRepository;
    private final EncryptionUtils encryptionUtils;

    public LlmExecutionService(LlmClientRegistry clientRegistry,
                               ModelRepository modelRepository,
                               ProviderCredentialRepository credentialRepository,
                               EncryptionUtils encryptionUtils) {
        this.clientRegistry = clientRegistry;
        this.modelRepository = modelRepository;
        this.credentialRepository = credentialRepository;
        this.encryptionUtils = encryptionUtils;
    }

    public ChatResponse execute(ChatRequest request, String fallbackModelIdentifier) {
        String primaryModelName = request.getModel() != null ? request.getModel() : "gpt-4o-mini";
        Optional<Model> modelOpt = modelRepository.findByModelIdentifier(primaryModelName);

        Model model = modelOpt.orElse(null);
        LlmProvider provider = model != null ? model.getProvider() : null;

        String apiKey = "";
        String baseUrl = "";

        if (provider != null) {
            baseUrl = provider.getBaseUrl();
            Optional<ProviderCredential> credOpt = credentialRepository.findByProviderId(provider.getId());
            if (credOpt.isPresent()) {
                try {
                    apiKey = encryptionUtils.decrypt(credOpt.get().getEncryptedApiKey(), credOpt.get().getIvSalt());
                } catch (Exception e) {
                    log.error("Failed to decrypt credentials for provider {}: {}", provider.getName(), e.getMessage());
                }
            }
        }

        LlmProviderClient client = clientRegistry.getClient(provider != null ? provider.getProviderType() : com.llmops.platform.modules.provider.entity.ProviderType.OPENAI);

        try {
            ChatResponse response = client.generate(request, apiKey, baseUrl);
            calculateCost(response, model);
            return response;
        } catch (Exception e) {
            log.warn("Primary model {} execution failed: {}. Attempting fallback...", primaryModelName, e.getMessage());
            if (fallbackModelIdentifier != null && !fallbackModelIdentifier.equalsIgnoreCase(primaryModelName)) {
                request.setModel(fallbackModelIdentifier);
                return execute(request, null);
            }
            throw new RuntimeException("LLM execution error: " + e.getMessage(), e);
        }
    }

    public Flux<String> stream(ChatRequest request) {
        String modelName = request.getModel() != null ? request.getModel() : "gpt-4o-mini";
        Optional<Model> modelOpt = modelRepository.findByModelIdentifier(modelName);
        LlmProvider provider = modelOpt.map(Model::getProvider).orElse(null);

        String apiKey = "";
        String baseUrl = "";
        if (provider != null) {
            baseUrl = provider.getBaseUrl();
            Optional<ProviderCredential> credOpt = credentialRepository.findByProviderId(provider.getId());
            if (credOpt.isPresent()) {
                apiKey = encryptionUtils.decrypt(credOpt.get().getEncryptedApiKey(), credOpt.get().getIvSalt());
            }
        }

        LlmProviderClient client = clientRegistry.getClient(provider != null ? provider.getProviderType() : com.llmops.platform.modules.provider.entity.ProviderType.OPENAI);
        return client.stream(request, apiKey, baseUrl);
    }

    public void calculateCost(ChatResponse response, Model model) {
        if (model == null || response == null) return;
        BigDecimal inputPrice = model.getInputCostPerMillion();
        BigDecimal outputPrice = model.getOutputCostPerMillion();

        BigDecimal inputCost = BigDecimal.valueOf(response.getPromptTokens())
                .multiply(inputPrice)
                .divide(BigDecimal.valueOf(1_000_000), 6, RoundingMode.HALF_UP);

        BigDecimal outputCost = BigDecimal.valueOf(response.getCompletionTokens())
                .multiply(outputPrice)
                .divide(BigDecimal.valueOf(1_000_000), 6, RoundingMode.HALF_UP);

        response.setEstimatedCost(inputCost.add(outputCost));
    }
}
