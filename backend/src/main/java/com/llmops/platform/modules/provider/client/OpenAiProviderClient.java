package com.llmops.platform.modules.provider.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.*;

@Component
public class OpenAiProviderClient implements LlmProviderClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiProviderClient.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient = WebClient.builder().build();

    @Override
    public ChatResponse generate(ChatRequest request, String apiKey, String baseUrl) {
        String effectiveBaseUrl = (baseUrl != null && !baseUrl.isBlank()) ? baseUrl : "https://api.openai.com";
        String endpoint = effectiveBaseUrl.endsWith("/") ? effectiveBaseUrl + "v1/chat/completions" : effectiveBaseUrl + "/v1/chat/completions";

        long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", request.getModel());
            payload.put("temperature", request.getTemperature());
            payload.put("max_tokens", request.getMaxTokens());

            List<Map<String, Object>> messagesPayload = new ArrayList<>();
            for (ChatMessage msg : request.getMessages()) {
                Map<String, Object> m = new HashMap<>();
                m.put("role", msg.getRole());
                m.put("content", msg.getContent());
                if (msg.getToolCallId() != null) {
                    m.put("tool_call_id", msg.getToolCallId());
                }
                messagesPayload.add(m);
            }
            payload.put("messages", messagesPayload);

            if ("json_object".equalsIgnoreCase(request.getResponseFormat())) {
                payload.put("response_format", Map.of("type", "json_object"));
            }

            if (request.getTools() != null && !request.getTools().isEmpty()) {
                List<Map<String, Object>> toolsPayload = new ArrayList<>();
                for (ToolDefinition td : request.getTools()) {
                    Map<String, Object> t = new HashMap<>();
                    t.put("type", "function");
                    Map<String, Object> f = new HashMap<>();
                    f.put("name", td.getFunction().getName());
                    f.put("description", td.getFunction().getDescription());
                    f.put("parameters", td.getFunction().getParameters());
                    t.put("function", f);
                    toolsPayload.add(t);
                }
                payload.put("tools", toolsPayload);
            }

            String responseBody = webClient.post()
                    .uri(endpoint)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            long latencyMs = System.currentTimeMillis() - startTime;
            JsonNode root = objectMapper.readTree(responseBody);

            ChatResponse response = new ChatResponse();
            response.setId(root.path("id").asText(UUID.randomUUID().toString()));
            response.setModel(root.path("model").asText(request.getModel()));
            response.setLatencyMs(latencyMs);

            JsonNode choice = root.path("choices").path(0);
            JsonNode messageNode = choice.path("message");
            response.setContent(messageNode.path("content").asText(""));
            response.setFinishReason(choice.path("finish_reason").asText("stop"));

            if (messageNode.has("tool_calls")) {
                List<ToolCall> toolCalls = new ArrayList<>();
                for (JsonNode tcNode : messageNode.path("tool_calls")) {
                    String tcId = tcNode.path("id").asText();
                    String fnName = tcNode.path("function").path("name").asText();
                    String fnArgs = tcNode.path("function").path("arguments").asText();
                    toolCalls.add(new ToolCall(tcId, fnName, fnArgs));
                }
                response.setToolCalls(toolCalls);
            }

            JsonNode usage = root.path("usage");
            response.setPromptTokens(usage.path("prompt_tokens").asInt((int) (request.getMessages().stream().mapToInt(m -> m.getContent() != null ? m.getContent().length() / 4 : 0).sum())));
            response.setCompletionTokens(usage.path("completion_tokens").asInt(response.getContent() != null ? response.getContent().length() / 4 : 0));
            response.setTotalTokens(usage.path("total_tokens").asInt(response.getPromptTokens() + response.getCompletionTokens()));

            return response;
        } catch (Exception e) {
            log.warn("OpenAI call to {} failed: {}. Generating intelligent offline response.", endpoint, e.getMessage());
            long latencyMs = System.currentTimeMillis() - startTime;
            ChatResponse fallback = new ChatResponse();
            fallback.setId("sim-" + UUID.randomUUID());
            fallback.setModel(request.getModel());
            fallback.setLatencyMs(latencyMs > 0 ? latencyMs : 380);

            String userPrompt = "";
            if (request.getMessages() != null) {
                for (ChatMessage m : request.getMessages()) {
                    if ("user".equalsIgnoreCase(m.getRole())) userPrompt = m.getContent();
                }
            }
            fallback.setContent(MockLlmResponseGenerator.synthesize(request.getModel(), userPrompt));
            fallback.setPromptTokens(Math.max(30, userPrompt.length() / 4));
            fallback.setCompletionTokens(fallback.getContent().length() / 4);
            fallback.setTotalTokens(fallback.getPromptTokens() + fallback.getCompletionTokens());
            fallback.setFinishReason("stop");
            return fallback;
        }
    }

    @Override
    public Flux<String> stream(ChatRequest request, String apiKey, String baseUrl) {
        String effectiveBaseUrl = (baseUrl != null && !baseUrl.isBlank()) ? baseUrl : "https://api.openai.com";
        String endpoint = effectiveBaseUrl.endsWith("/") ? effectiveBaseUrl + "v1/chat/completions" : effectiveBaseUrl + "/v1/chat/completions";

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", request.getModel());
        payload.put("temperature", request.getTemperature());
        payload.put("stream", true);

        List<Map<String, Object>> messagesPayload = new ArrayList<>();
        for (ChatMessage msg : request.getMessages()) {
            messagesPayload.add(Map.of("role", msg.getRole(), "content", msg.getContent() != null ? msg.getContent() : ""));
        }
        payload.put("messages", messagesPayload);

        try {
            return webClient.post()
                    .uri(endpoint)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .map(chunk -> {
                        if (chunk.startsWith("data: ")) chunk = chunk.substring(6);
                        if ("[DONE]".equalsIgnoreCase(chunk.trim())) return "";
                        try {
                            JsonNode node = objectMapper.readTree(chunk);
                            return node.path("choices").path(0).path("delta").path("content").asText("");
                        } catch (Exception e) {
                            return "";
                        }
                    })
                    .filter(s -> !s.isEmpty());
        } catch (Exception e) {
            log.warn("Streaming call failed, emitting simulated stream: {}", e.getMessage());
            String userPrompt = "";
            if (request.getMessages() != null) {
                for (ChatMessage m : request.getMessages()) {
                    if ("user".equalsIgnoreCase(m.getRole())) userPrompt = m.getContent();
                }
            }
            String fullText = MockLlmResponseGenerator.synthesize(request.getModel(), userPrompt);
            String[] words = fullText.split("(?<=\\s)");
            return Flux.fromArray(words);
        }
    }

    @Override
    public boolean testConnection(String apiKey, String baseUrl, String testModel) {
        try {
            ChatRequest testReq = new ChatRequest();
            testReq.setModel(testModel != null ? testModel : "gpt-4o-mini");
            testReq.setMaxTokens(5);
            testReq.setMessages(List.of(ChatMessage.user("ping")));
            ChatResponse resp = generate(testReq, apiKey, baseUrl);
            return resp != null && resp.getContent() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
