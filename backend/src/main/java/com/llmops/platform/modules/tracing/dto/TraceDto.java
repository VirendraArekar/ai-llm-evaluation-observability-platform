package com.llmops.platform.modules.tracing.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TraceDto {
    private String id;
    private UUID projectId;
    private String sessionId;
    private String userId;
    private String name;
    private String status;
    private long latencyMs;
    private int totalTokens;
    private int inputTokens;
    private int outputTokens;
    private BigDecimal estimatedCost;
    private String tags;
    private Instant startTime;
    private Instant endTime;
    private int spanCount;

    public TraceDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getLatencyMs() { return latencyMs; }
    public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }
    public int getTotalTokens() { return totalTokens; }
    public void setTotalTokens(int totalTokens) { this.totalTokens = totalTokens; }
    public int getInputTokens() { return inputTokens; }
    public void setInputTokens(int inputTokens) { this.inputTokens = inputTokens; }
    public int getOutputTokens() { return outputTokens; }
    public void setOutputTokens(int outputTokens) { this.outputTokens = outputTokens; }
    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }
    public int getSpanCount() { return spanCount; }
    public void setSpanCount(int spanCount) { this.spanCount = spanCount; }

    public static class SpanDto {
        private String id;
        private String traceId;
        private String parentSpanId;
        private String name;
        private String spanType; // LLM, TOOL, RETRIEVAL, AGENT, CHAIN
        private String status;
        private Instant startTime;
        private Instant endTime;
        private long latencyMs;
        private String inputJson;
        private String outputJson;
        private String modelName;
        private String providerName;
        private int inputTokens;
        private int outputTokens;
        private int totalTokens;
        private BigDecimal cost;
        private String errorMessage;
        private String stackTrace;
        private String attributesJson;

        public SpanDto() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTraceId() { return traceId; }
        public void setTraceId(String traceId) { this.traceId = traceId; }
        public String getParentSpanId() { return parentSpanId; }
        public void setParentSpanId(String parentSpanId) { this.parentSpanId = parentSpanId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSpanType() { return spanType; }
        public void setSpanType(String spanType) { this.spanType = spanType; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Instant getStartTime() { return startTime; }
        public void setStartTime(Instant startTime) { this.startTime = startTime; }
        public Instant getEndTime() { return endTime; }
        public void setEndTime(Instant endTime) { this.endTime = endTime; }
        public long getLatencyMs() { return latencyMs; }
        public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }
        public String getInputJson() { return inputJson; }
        public void setInputJson(String inputJson) { this.inputJson = inputJson; }
        public String getOutputJson() { return outputJson; }
        public void setOutputJson(String outputJson) { this.outputJson = outputJson; }
        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }
        public String getProviderName() { return providerName; }
        public void setProviderName(String providerName) { this.providerName = providerName; }
        public int getInputTokens() { return inputTokens; }
        public void setInputTokens(int inputTokens) { this.inputTokens = inputTokens; }
        public int getOutputTokens() { return outputTokens; }
        public void setOutputTokens(int outputTokens) { this.outputTokens = outputTokens; }
        public int getTotalTokens() { return totalTokens; }
        public void setTotalTokens(int totalTokens) { this.totalTokens = totalTokens; }
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public String getStackTrace() { return stackTrace; }
        public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }
        public String getAttributesJson() { return attributesJson; }
        public void setAttributesJson(String attributesJson) { this.attributesJson = attributesJson; }
    }

    public static class SpanNodeDto {
        private SpanDto span;
        private double relativeStartPercent;
        private double relativeDurationPercent;
        private List<SpanNodeDto> children = new ArrayList<>();

        public SpanNodeDto() {}

        public SpanDto getSpan() { return span; }
        public void setSpan(SpanDto span) { this.span = span; }
        public double getRelativeStartPercent() { return relativeStartPercent; }
        public void setRelativeStartPercent(double relativeStartPercent) { this.relativeStartPercent = relativeStartPercent; }
        public double getRelativeDurationPercent() { return relativeDurationPercent; }
        public void setRelativeDurationPercent(double relativeDurationPercent) { this.relativeDurationPercent = relativeDurationPercent; }
        public List<SpanNodeDto> getChildren() { return children; }
        public void setChildren(List<SpanNodeDto> children) { this.children = children; }
    }

    public static class TraceDetailDto {
        private TraceDto trace;
        private List<SpanNodeDto> waterfallTree = new ArrayList<>();
        private List<SpanDto> allSpans = new ArrayList<>();

        public TraceDetailDto() {}

        public TraceDto getTrace() { return trace; }
        public void setTrace(TraceDto trace) { this.trace = trace; }
        public List<SpanNodeDto> getWaterfallTree() { return waterfallTree; }
        public void setWaterfallTree(List<SpanNodeDto> waterfallTree) { this.waterfallTree = waterfallTree; }
        public List<SpanDto> getAllSpans() { return allSpans; }
        public void setAllSpans(List<SpanDto> allSpans) { this.allSpans = allSpans; }
    }

    public static class TraceIngestRequest {
        private String id;
        private UUID projectId;
        private String sessionId;
        private String userId;
        private String name;
        private String status = "SUCCESS";
        private String tags;
        private String metadataJson;
        private Instant startTime;
        private Instant endTime;
        private List<SpanIngestRequest> spans;

        public TraceIngestRequest() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public UUID getProjectId() { return projectId; }
        public void setProjectId(UUID projectId) { this.projectId = projectId; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getTags() { return tags; }
        public void setTags(String tags) { this.tags = tags; }
        public String getMetadataJson() { return metadataJson; }
        public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }
        public Instant getStartTime() { return startTime; }
        public void setStartTime(Instant startTime) { this.startTime = startTime; }
        public Instant getEndTime() { return endTime; }
        public void setEndTime(Instant endTime) { this.endTime = endTime; }
        public List<SpanIngestRequest> getSpans() { return spans; }
        public void setSpans(List<SpanIngestRequest> spans) { this.spans = spans; }
    }

    public static class SpanIngestRequest {
        private String id;
        private String parentSpanId;
        private String name;
        private String spanType; // LLM, TOOL, RETRIEVAL, AGENT, CHAIN
        private String status = "SUCCESS";
        private Instant startTime;
        private Instant endTime;
        private String inputJson;
        private String outputJson;
        private String modelName;
        private String providerName;
        private Integer inputTokens;
        private Integer outputTokens;
        private BigDecimal cost;
        private String errorMessage;
        private String stackTrace;
        private String attributesJson;

        public SpanIngestRequest() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getParentSpanId() { return parentSpanId; }
        public void setParentSpanId(String parentSpanId) { this.parentSpanId = parentSpanId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSpanType() { return spanType; }
        public void setSpanType(String spanType) { this.spanType = spanType; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Instant getStartTime() { return startTime; }
        public void setStartTime(Instant startTime) { this.startTime = startTime; }
        public Instant getEndTime() { return endTime; }
        public void setEndTime(Instant endTime) { this.endTime = endTime; }
        public String getInputJson() { return inputJson; }
        public void setInputJson(String inputJson) { this.inputJson = inputJson; }
        public String getOutputJson() { return outputJson; }
        public void setOutputJson(String outputJson) { this.outputJson = outputJson; }
        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }
        public String getProviderName() { return providerName; }
        public void setProviderName(String providerName) { this.providerName = providerName; }
        public Integer getInputTokens() { return inputTokens; }
        public void setInputTokens(Integer inputTokens) { this.inputTokens = inputTokens; }
        public Integer getOutputTokens() { return outputTokens; }
        public void setOutputTokens(Integer outputTokens) { this.outputTokens = outputTokens; }
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public String getStackTrace() { return stackTrace; }
        public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }
        public String getAttributesJson() { return attributesJson; }
        public void setAttributesJson(String attributesJson) { this.attributesJson = attributesJson; }
    }

    public static class TraceStatsDto {
        private long totalTraces;
        private long successfulTraces;
        private long errorTraces;
        private double successRate;
        private double averageLatencyMs;
        private long totalTokens;
        private BigDecimal totalCost;

        public TraceStatsDto() {}

        public long getTotalTraces() { return totalTraces; }
        public void setTotalTraces(long totalTraces) { this.totalTraces = totalTraces; }
        public long getSuccessfulTraces() { return successfulTraces; }
        public void setSuccessfulTraces(long successfulTraces) { this.successfulTraces = successfulTraces; }
        public long getErrorTraces() { return errorTraces; }
        public void setErrorTraces(long errorTraces) { this.errorTraces = errorTraces; }
        public double getSuccessRate() { return successRate; }
        public void setSuccessRate(double successRate) { this.successRate = successRate; }
        public double getAverageLatencyMs() { return averageLatencyMs; }
        public void setAverageLatencyMs(double averageLatencyMs) { this.averageLatencyMs = averageLatencyMs; }
        public long getTotalTokens() { return totalTokens; }
        public void setTotalTokens(long totalTokens) { this.totalTokens = totalTokens; }
        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    }
}
