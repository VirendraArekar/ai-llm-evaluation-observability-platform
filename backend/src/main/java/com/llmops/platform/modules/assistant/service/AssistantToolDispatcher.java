package com.llmops.platform.modules.assistant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.llmops.platform.modules.evaluation.repository.EvaluationRepository;
import com.llmops.platform.modules.prompt.repository.PromptRepository;
import com.llmops.platform.modules.prompt.repository.PromptVersionRepository;
import com.llmops.platform.modules.provider.client.ToolDefinition;
import com.llmops.platform.modules.recommendation.repository.RecommendationRepository;
import com.llmops.platform.modules.tracing.entity.Span;
import com.llmops.platform.modules.tracing.entity.Trace;
import com.llmops.platform.modules.tracing.repository.SpanRepository;
import com.llmops.platform.modules.tracing.repository.TraceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AssistantToolDispatcher {

    private final TraceRepository traceRepository;
    private final SpanRepository spanRepository;
    private final EvaluationRepository evaluationRepository;
    private final PromptRepository promptRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final RecommendationRepository recommendationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AssistantToolDispatcher(TraceRepository traceRepository,
                                   SpanRepository spanRepository,
                                   EvaluationRepository evaluationRepository,
                                   PromptRepository promptRepository,
                                   PromptVersionRepository promptVersionRepository,
                                   RecommendationRepository recommendationRepository) {
        this.traceRepository = traceRepository;
        this.spanRepository = spanRepository;
        this.evaluationRepository = evaluationRepository;
        this.promptRepository = promptRepository;
        this.promptVersionRepository = promptVersionRepository;
        this.recommendationRepository = recommendationRepository;
    }

    public List<ToolDefinition> getAvailableTools() {
        return List.of(
                new ToolDefinition("search_traces", "Search recent traces by status, query, or latency",
                        Map.of("type", "object", "properties", Map.of(
                                "status", Map.of("type", "string", "description", "Optional status: SUCCESS, ERROR"),
                                "query", Map.of("type", "string", "description", "Keyword in trace name or tags")
                        ))),
                new ToolDefinition("get_trace_details", "Get full span waterfall and error stacktrace for a specific trace ID",
                        Map.of("type", "object", "properties", Map.of(
                                "traceId", Map.of("type", "string", "description", "Trace ID to inspect")
                        ), "required", List.of("traceId"))),
                new ToolDefinition("get_cost_analytics", "Get total tokens, average latency, and estimated cost for the current project",
                        Map.of("type", "object", "properties", Map.of())),
                new ToolDefinition("get_recommendations", "Get AI recommendations for cost savings, latency optimization, and prompt improvements",
                        Map.of("type", "object", "properties", Map.of())),
                new ToolDefinition("search_evaluations", "List recent evaluations, low-scoring responses, and hallucination checks",
                        Map.of("type", "object", "properties", Map.of()))
        );
    }

    public String executeTool(UUID projectId, String toolName, String argumentsJson) {
        try {
            Map<String, Object> args = new HashMap<>();
            if (argumentsJson != null && !argumentsJson.isBlank()) {
                try {
                    args = objectMapper.readValue(argumentsJson, Map.class);
                } catch (Exception ignored) {}
            }

            return switch (toolName) {
                case "search_traces" -> {
                    String status = (String) args.get("status");
                    String query = (String) args.get("query");
                    List<Trace> traces;
                    if (query != null && !query.isBlank() && status != null && !status.isBlank()) {
                        traces = traceRepository.findByProjectIdAndStatusAndNameContainingIgnoreCaseOrderByStartTimeDesc(projectId, status, query, PageRequest.of(0, 5)).getContent();
                    } else if (query != null && !query.isBlank()) {
                        traces = traceRepository.findByProjectIdAndNameContainingIgnoreCaseOrderByStartTimeDesc(projectId, query, PageRequest.of(0, 5)).getContent();
                    } else if (status != null && !status.isBlank()) {
                        traces = traceRepository.findByProjectIdAndStatusOrderByStartTimeDesc(projectId, status, PageRequest.of(0, 5)).getContent();
                    } else {
                        traces = traceRepository.findByProjectIdOrderByStartTimeDesc(projectId, PageRequest.of(0, 5)).getContent();
                    }
                    List<Map<String, Object>> list = new ArrayList<>();
                    for (Trace t : traces) {
                        list.add(Map.of(
                                "id", t.getId(),
                                "name", t.getName(),
                                "status", t.getStatus(),
                                "latencyMs", t.getLatencyMs(),
                                "tokens", t.getTotalTokens(),
                                "cost", t.getEstimatedCost()
                        ));
                    }
                    yield objectMapper.writeValueAsString(list);
                }
                case "get_trace_details" -> {
                    String traceId = (String) args.get("traceId");
                    if (traceId == null) yield "{\"error\": \"traceId required\"}";
                    Optional<Trace> tOpt = traceRepository.findById(traceId);
                    if (tOpt.isEmpty() || !tOpt.get().getProject().getId().equals(projectId)) {
                        yield "{\"error\": \"Trace not found or unauthorized\"}";
                    }
                    Trace t = tOpt.get();
                    List<Span> spans = spanRepository.findByTraceIdOrderByStartTimeAsc(traceId);
                    List<Map<String, Object>> spanList = new ArrayList<>();
                    for (Span s : spans) {
                        Map<String, Object> sm = new HashMap<>();
                        sm.put("id", s.getId());
                        sm.put("name", s.getName());
                        sm.put("spanType", s.getSpanType());
                        sm.put("status", s.getStatus());
                        sm.put("latencyMs", s.getLatencyMs());
                        sm.put("model", s.getModelName());
                        if (s.getErrorMessage() != null) sm.put("errorMessage", s.getErrorMessage());
                        spanList.add(sm);
                    }
                    yield objectMapper.writeValueAsString(Map.of("traceId", t.getId(), "name", t.getName(), "status", t.getStatus(), "spans", spanList));
                }
                case "get_cost_analytics" -> {
                    long total = traceRepository.countByProjectId(projectId);
                    Double avgLat = traceRepository.getAverageLatency(projectId);
                    Long totalTokens = traceRepository.getTotalTokens(projectId);
                    yield objectMapper.writeValueAsString(Map.of(
                            "totalRequests", total,
                            "averageLatencyMs", avgLat != null ? avgLat : 0.0,
                            "totalTokens", totalTokens != null ? totalTokens : 0
                    ));
                }
                case "get_recommendations" -> {
                    var recs = recommendationRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
                    List<Map<String, Object>> recList = new ArrayList<>();
                    for (var r : recs) {
                        recList.add(Map.of("title", r.getTitle(), "category", r.getCategory(), "impact", r.getExpectedImpact()));
                    }
                    yield objectMapper.writeValueAsString(recList);
                }
                case "search_evaluations" -> {
                    var evals = evaluationRepository.findByProjectIdOrderByCreatedAtDesc(projectId, PageRequest.of(0, 5)).getContent();
                    List<Map<String, Object>> evalList = new ArrayList<>();
                    for (var e : evals) {
                        evalList.add(Map.of(
                                "metric", e.getMetricName(),
                                "score", e.getScore(),
                                "passFail", e.isPassFail(),
                                "explanation", e.getExplanation() != null ? e.getExplanation() : ""
                        ));
                    }
                    yield objectMapper.writeValueAsString(evalList);
                }
                default -> "{\"error\": \"Unknown tool " + toolName + "\"}";
            };
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
}
