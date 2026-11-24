package com.llmops.platform.modules.simulation;

import com.llmops.platform.config.DataSeeder;
import com.llmops.platform.modules.auth.entity.Project;
import com.llmops.platform.modules.auth.repository.ProjectRepository;
import com.llmops.platform.modules.evaluation.entity.Evaluation;
import com.llmops.platform.modules.evaluation.repository.EvaluationRepository;
import com.llmops.platform.modules.tracing.dto.TraceDto;
import com.llmops.platform.modules.tracing.dto.TraceDto.TraceDetailDto;
import com.llmops.platform.modules.tracing.entity.Span;
import com.llmops.platform.modules.tracing.entity.Trace;
import com.llmops.platform.modules.tracing.repository.SpanRepository;
import com.llmops.platform.modules.tracing.repository.TraceRepository;
import com.llmops.platform.modules.tracing.service.TraceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class TelemetrySimulationService {

    private static final Logger log = LoggerFactory.getLogger(TelemetrySimulationService.class);

    private final ProjectRepository projectRepository;
    private final TraceRepository traceRepository;
    private final SpanRepository spanRepository;
    private final EvaluationRepository evaluationRepository;
    private final TraceService traceService;
    private final DataSeeder dataSeeder;

    private final Random random = new Random();

    private final List<Scenario> scenarios = List.of(
            new Scenario("Customer Support: Refund Inquiry #9410", "gpt-4o-mini", 310, 240, 75, "support,billing,refund", 0.98, true),
            new Scenario("RAG Vector Document Synthesis", "gemini-1.5-pro", 480, 520, 190, "rag,retrieval,chroma", 0.95, true),
            new Scenario("SQL Query Optimization & DDL Check", "gpt-4o", 680, 420, 260, "sql,agent,database", 0.94, true),
            new Scenario("Claude Sentiment & Risk Analysis", "claude-3-5-sonnet", 290, 180, 45, "risk,classification", 0.97, true),
            new Scenario("Real-Time Multi-turn Chat Assistance", "gpt-4o-mini", 410, 390, 140, "chat,multiturn", 0.96, true),
            new Scenario("Payment Gateway Tool Retry", "gpt-4o", 1850, 220, 0, "billing,error,timeout", 0.0, false),
            new Scenario("Guardrail Jailbreak Defense Filter", "guardrail-regex", 35, 80, 15, "security,guardrail", 1.0, true),
            new Scenario("High-Throughput Batch Ticket Categorization", "gemini-1.5-flash", 220, 310, 60, "support,batch", 0.93, true)
    );

    public TelemetrySimulationService(ProjectRepository projectRepository,
                                     TraceRepository traceRepository,
                                     SpanRepository spanRepository,
                                     EvaluationRepository evaluationRepository,
                                     TraceService traceService,
                                     DataSeeder dataSeeder) {
        this.projectRepository = projectRepository;
        this.traceRepository = traceRepository;
        this.spanRepository = spanRepository;
        this.evaluationRepository = evaluationRepository;
        this.traceService = traceService;
        this.dataSeeder = dataSeeder;
    }

    @Transactional
    public TraceDetailDto generateLiveTelemetry(UUID projectId) {
        Project project;
        if (projectId != null) {
            project = projectRepository.findById(projectId).orElse(null);
        } else {
            project = null;
        }

        if (project == null) {
            project = projectRepository.findAll().stream().findFirst().orElseThrow(
                    () -> new IllegalStateException("No project found to generate telemetry"));
        }

        Scenario s = scenarios.get(random.nextInt(scenarios.size()));
        Instant start = Instant.now();
        String traceId = "live_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        long latency = s.baseLatency + random.nextInt(80) - 40;
        int inTokens = s.inTokens + random.nextInt(30) - 15;
        int outTokens = s.success ? (s.outTokens + random.nextInt(30) - 15) : 0;
        int totalTokens = inTokens + outTokens;
        BigDecimal cost = BigDecimal.valueOf((totalTokens * 0.000008) + (random.nextDouble() * 0.0005));

        Trace trace = new Trace();
        trace.setId(traceId);
        trace.setProject(project);
        trace.setName(s.name);
        trace.setStatus(s.success ? "SUCCESS" : "ERROR");
        trace.setLatencyMs(Math.max(25, latency));
        trace.setInputTokens(inTokens);
        trace.setOutputTokens(outTokens);
        trace.setTotalTokens(totalTokens);
        trace.setEstimatedCost(cost);
        trace.setStartTime(start);
        trace.setEndTime(start.plusMillis(latency));
        trace.setTags(s.tags);
        trace.setMetadataJson("{\"telemetry\": \"live_simulation\", \"model\": \"" + s.model + "\", \"timestamp\": \"" + start + "\"}");
        trace = traceRepository.save(trace);

        // Child Span 1: Guardrail
        Span span1 = new Span();
        span1.setId("span_" + UUID.randomUUID().toString().substring(0, 8));
        span1.setTrace(trace);
        span1.setName("Guardrail Input Sanity Check");
        span1.setSpanType("GUARDRAIL");
        span1.setStatus("SUCCESS");
        span1.setStartTime(start);
        span1.setEndTime(start.plusMillis(20));
        span1.setLatencyMs(20);
        span1.setInputJson("{\"action\": \"validate_input\"}");
        span1.setOutputJson("{\"passed\": true, \"risk\": 0.01}");
        spanRepository.save(span1);

        // Child Span 2: Retrieval
        Span span2 = new Span();
        span2.setId("span_" + UUID.randomUUID().toString().substring(0, 8));
        span2.setTrace(trace);
        span2.setParentSpanId(span1.getId());
        span2.setName("Knowledge Vector Search");
        span2.setSpanType("RETRIEVAL");
        span2.setStatus("SUCCESS");
        span2.setStartTime(start.plusMillis(20));
        span2.setEndTime(start.plusMillis(latency / 3));
        span2.setLatencyMs(Math.max(15, (latency / 3) - 20));
        span2.setInputJson("{\"top_k\": 3, \"query\": \"" + s.name + "\"}");
        span2.setOutputJson("{\"retrieved_chunks\": 3, \"cosine_similarity\": 0.92}");
        spanRepository.save(span2);

        // Child Span 3: LLM Inference
        Span span3 = new Span();
        span3.setId("span_" + UUID.randomUUID().toString().substring(0, 8));
        span3.setTrace(trace);
        span3.setParentSpanId(span2.getId());
        span3.setName("Model Generation");
        span3.setSpanType("LLM");
        span3.setStatus(s.success ? "SUCCESS" : "ERROR");
        span3.setStartTime(start.plusMillis(latency / 3));
        span3.setEndTime(start.plusMillis(latency));
        span3.setLatencyMs((latency * 2) / 3);
        span3.setModelName(s.model);
        span3.setInputTokens(inTokens);
        span3.setOutputTokens(outTokens);
        span3.setTotalTokens(totalTokens);
        span3.setCost(cost);
        span3.setInputJson("{\"temperature\": 0.3, \"max_tokens\": 1024}");
        span3.setOutputJson("{\"finish_reason\": \"" + (s.success ? "stop" : "error") + "\", \"ttft_ms\": 110}");
        if (!s.success) {
            span3.setErrorMessage("504 Gateway Timeout: Upstream model provider did not respond in 2000ms");
            span3.setStackTrace("java.net.http.HttpTimeoutException: request timed out");
        }
        spanRepository.save(span3);

        // Evaluation
        if (s.success && s.evalScore > 0) {
            Evaluation eval = new Evaluation();
            eval.setProject(project);
            eval.setTrace(trace);
            eval.setEvaluationType("LLM_AS_A_JUDGE");
            eval.setMetricName("FAITHFULNESS");
            eval.setScore(BigDecimal.valueOf(s.evalScore));
            eval.setPassFail(s.evalScore >= 0.70);
            eval.setExplanation("Response verified against contextual ground truth without hallucinations.");
            eval.setEvidence("All key claims match retrieved vectors.");
            eval.setCategory("FAITHFULNESS");
            eval.setJudgeModel("gpt-4o-mini");
            eval.setExecutionTimeMs(280);
            evaluationRepository.save(eval);
        }

        log.info("Live telemetry trace generated: id={}, name={}, status={}, latency={}ms",
                trace.getId(), trace.getName(), trace.getStatus(), trace.getLatencyMs());

        return traceService.getTraceDetail(trace.getId());
    }

    @Transactional
    public Map<String, Object> forceSeedAll() {
        try {
            dataSeeder.run();
            return Map.of(
                    "status", "SUCCESS",
                    "message", "All platform modules, benchmarks, and real-time data successfully verified and seeded",
                    "timestamp", Instant.now().toString()
            );
        } catch (Exception e) {
            log.error("Failed to seed data: ", e);
            return Map.of("status", "ERROR", "message", e.getMessage());
        }
    }

    private static class Scenario {
        String name;
        String model;
        long baseLatency;
        int inTokens;
        int outTokens;
        String tags;
        double evalScore;
        boolean success;

        Scenario(String name, String model, long baseLatency, int inTokens, int outTokens, String tags, double evalScore, boolean success) {
            this.name = name;
            this.model = model;
            this.baseLatency = baseLatency;
            this.inTokens = inTokens;
            this.outTokens = outTokens;
            this.tags = tags;
            this.evalScore = evalScore;
            this.success = success;
        }
    }
}
