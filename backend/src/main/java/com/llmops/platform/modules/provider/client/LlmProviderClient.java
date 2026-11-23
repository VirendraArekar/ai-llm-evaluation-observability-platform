package com.llmops.platform.modules.provider.client;

import reactor.core.publisher.Flux;

public interface LlmProviderClient {
    ChatResponse generate(ChatRequest request, String apiKey, String baseUrl);
    Flux<String> stream(ChatRequest request, String apiKey, String baseUrl);
    boolean testConnection(String apiKey, String baseUrl, String testModel);
}
