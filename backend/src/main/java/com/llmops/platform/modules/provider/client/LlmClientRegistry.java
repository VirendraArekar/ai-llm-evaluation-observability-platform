package com.llmops.platform.modules.provider.client;

import com.llmops.platform.modules.provider.entity.ProviderType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class LlmClientRegistry {

    private final Map<ProviderType, LlmProviderClient> clients = new EnumMap<>(ProviderType.class);

    public LlmClientRegistry(OpenAiProviderClient openAiClient,
                             GeminiProviderClient geminiClient,
                             ClaudeProviderClient claudeClient,
                             OllamaProviderClient ollamaClient) {
        clients.put(ProviderType.OPENAI, openAiClient);
        clients.put(ProviderType.CUSTOM_OPENAI, openAiClient);
        clients.put(ProviderType.GEMINI, geminiClient);
        clients.put(ProviderType.CLAUDE, claudeClient);
        clients.put(ProviderType.OLLAMA, ollamaClient);
    }

    public LlmProviderClient getClient(ProviderType providerType) {
        LlmProviderClient client = clients.get(providerType);
        if (client == null) {
            // Default to OpenAI-compatible client
            return clients.get(ProviderType.OPENAI);
        }
        return client;
    }
}
