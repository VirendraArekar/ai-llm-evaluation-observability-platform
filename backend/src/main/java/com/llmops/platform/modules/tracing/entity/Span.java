package com.llmops.platform.modules.tracing.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "spans")
public class Span {

    @Id
    @Column(length = 64, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trace_id", nullable = false)
    private Trace trace;

    @Column(name = "parent_span_id", length = 64)
    private String parentSpanId;

    @Column(nullable = false)
    private String name;

    @Column(name = "span_type", length = 50, nullable = false)
    private String spanType; // LLM, TOOL, RETRIEVAL, AGENT, CHAIN

    @Column(length = 50, nullable = false)
    private String status = "SUCCESS";

    @Column(name = "start_time", nullable = false)
    private Instant startTime = Instant.now();

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "latency_ms", nullable = false)
    private long latencyMs = 0;

    @Column(name = "input_json", columnDefinition = "TEXT")
    private String inputJson;

    @Column(name = "output_json", columnDefinition = "TEXT")
    private String outputJson;

    @Column(name = "model_name", length = 100)
    private String modelName;

    @Column(name = "provider_name", length = 100)
    private String providerName;

    @Column(name = "input_tokens")
    private int inputTokens = 0;

    @Column(name = "output_tokens")
    private int outputTokens = 0;

    @Column(name = "total_tokens")
    private int totalTokens = 0;

    @Column(precision = 12, scale = 6)
    private BigDecimal cost = BigDecimal.ZERO;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Column(name = "attributes_json", columnDefinition = "TEXT")
    private String attributesJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Span() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Trace getTrace() { return trace; }
    public void setTrace(Trace trace) { this.trace = trace; }
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
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
