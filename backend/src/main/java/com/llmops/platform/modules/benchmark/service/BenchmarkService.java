package com.llmops.platform.modules.benchmark.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.llmops.platform.common.exception.ResourceNotFoundException;
import com.llmops.platform.modules.auth.entity.Project;
import com.llmops.platform.modules.auth.repository.ProjectRepository;
import com.llmops.platform.modules.benchmark.dto.BenchmarkDto.*;
import com.llmops.platform.modules.benchmark.entity.ModelComparison;
import com.llmops.platform.modules.benchmark.repository.ModelComparisonRepository;
import com.llmops.platform.modules.evaluation.dto.EvaluationDto.RunJudgeRequest;
import com.llmops.platform.modules.evaluation.service.LlmJudgeService;
import com.llmops.platform.modules.provider.client.ChatMessage;
import com.llmops.platform.modules.provider.client.ChatRequest;
import com.llmops.platform.modules.provider.client.ChatResponse;
import com.llmops.platform.modules.provider.service.LlmExecutionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BenchmarkService {

    private final ModelComparisonRepository comparisonRepository;
    private final ProjectRepository projectRepository;
    private final LlmExecutionService executionService;
    private final LlmJudgeService judgeService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BenchmarkService(ModelComparisonRepository comparisonRepository,
                            ProjectRepository projectRepository,
                            LlmExecutionService executionService,
                            LlmJudgeService judgeService) {
        this.comparisonRepository = comparisonRepository;
        this.projectRepository = projectRepository;
        this.executionService = executionService;
        this.judgeService = judgeService;
    }

    @Transactional(readOnly = true)
    public List<BenchmarkComparisonResponse> getComparisons(UUID projectId) {
        List<ModelComparison> list = comparisonRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
        if (list.isEmpty()) {
            list = comparisonRepository.findAll();
        }
        return list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BenchmarkComparisonResponse runBenchmark(UUID projectId, RunBenchmarkRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        Set<String> modelsToTest = new LinkedHashSet<>();
        if (request.getBaselineModel() != null) modelsToTest.add(request.getBaselineModel());
        if (request.getComparisonModels() != null) modelsToTest.addAll(request.getComparisonModels());
        if (modelsToTest.isEmpty()) {
            modelsToTest.addAll(List.of("gpt-4o", "gpt-4o-mini", "gemini-1.5-pro", "claude-3-5-sonnet"));
        }

        String promptToRun = (request.getInputPrompt() != null && !request.getInputPrompt().isBlank())
                ? request.getInputPrompt()
                : "Implement a thread-safe token bucket rate limiter in Java 25 with virtual threads and explain the concurrency design.";

        List<ModelBenchmarkResultDto> results = new ArrayList<>();
        for (String m : modelsToTest) {
            ChatRequest chatReq = new ChatRequest();
            chatReq.setModel(m);
            chatReq.setTemperature(request.getTemperature());
            chatReq.setMaxTokens(request.getMaxTokens());
            chatReq.setMessages(List.of(ChatMessage.user(promptToRun)));

            try {
                ChatResponse resp = executionService.execute(chatReq, "gpt-4o-mini");

                // Evaluate quality
                RunJudgeRequest judgeReq = new RunJudgeRequest();
                judgeReq.setMetricCode("RELEVANCE");
                judgeReq.setInputPrompt(request.getInputPrompt());
                judgeReq.setGeneratedResponse(resp.getContent());
                judgeReq.setJudgeModel("gpt-4o-mini");
                var judgeRes = judgeService.evaluate(judgeReq);

                ModelBenchmarkResultDto r = new ModelBenchmarkResultDto();
                r.setModelIdentifier(m);
                r.setDisplayName(formatDisplayName(m));
                r.setOutput(resp.getContent());
                r.setLatencyMs(resp.getLatencyMs());
                r.setTokens(resp.getTotalTokens());
                r.setCost(resp.getEstimatedCost());
                r.setQualityScore(judgeRes.getScore());
                r.setHallucinationRisk(Math.max(0.02, 1.0 - judgeRes.getScore()));
                results.add(r);
            } catch (Exception e) {
                ModelBenchmarkResultDto r = new ModelBenchmarkResultDto();
                r.setModelIdentifier(m);
                r.setDisplayName(formatDisplayName(m));
                r.setOutput("Error generating response: " + e.getMessage());
                r.setLatencyMs(500);
                r.setTokens(0);
                r.setQualityScore(0.0);
                r.setHallucinationRisk(1.0);
                results.add(r);
            }
        }

        // Calculate ranks
        results.sort(Comparator.comparingDouble(ModelBenchmarkResultDto::getQualityScore).reversed()
                .thenComparingLong(ModelBenchmarkResultDto::getLatencyMs));
        for (int i = 0; i < results.size(); i++) {
            results.get(i).setRank(i + 1);
        }

        String bestOverall = !results.isEmpty() ? results.get(0).getModelIdentifier() : "N/A";
        String lowestLatency = results.stream().min(Comparator.comparingLong(ModelBenchmarkResultDto::getLatencyMs))
                .map(ModelBenchmarkResultDto::getModelIdentifier).orElse("N/A");
        String lowestCost = results.stream().min(Comparator.comparing(ModelBenchmarkResultDto::getCost))
                .map(ModelBenchmarkResultDto::getModelIdentifier).orElse("N/A");

        ModelComparison mc = new ModelComparison();
        mc.setProject(project);
        mc.setName(request.getName() != null ? request.getName() : "Multi-Model Benchmark");
        mc.setInputPrompt(request.getInputPrompt());
        mc.setBaselineModel(request.getBaselineModel() != null ? request.getBaselineModel() : "gpt-4o");
        try {
            mc.setComparisonModelsJson(objectMapper.writeValueAsString(modelsToTest));
            mc.setResultsJson(objectMapper.writeValueAsString(results));
        } catch (Exception ignored) {}
        mc = comparisonRepository.save(mc);

        BenchmarkComparisonResponse resp = new BenchmarkComparisonResponse();
        resp.setId(mc.getId());
        resp.setName(mc.getName());
        resp.setInputPrompt(mc.getInputPrompt());
        resp.setBaselineModel(mc.getBaselineModel());
        resp.setModelResults(results);
        resp.setBestOverallModel(bestOverall);
        resp.setLowestLatencyModel(lowestLatency);
        resp.setMostCostEffectiveModel(lowestCost);
        resp.setCreatedAt(mc.getCreatedAt());
        return resp;
    }

    private BenchmarkComparisonResponse mapToResponse(ModelComparison mc) {
        BenchmarkComparisonResponse resp = new BenchmarkComparisonResponse();
        resp.setId(mc.getId());
        resp.setName(mc.getName());
        resp.setInputPrompt(mc.getInputPrompt());
        resp.setBaselineModel(mc.getBaselineModel());
        resp.setCreatedAt(mc.getCreatedAt());

        try {
            List<ModelBenchmarkResultDto> list = objectMapper.readValue(mc.getResultsJson(), new TypeReference<>() {});
            resp.setModelResults(list);
            if (!list.isEmpty()) {
                resp.setBestOverallModel(list.get(0).getModelIdentifier());
            }
        } catch (Exception ignored) {}
        return resp;
    }

    private String formatDisplayName(String model) {
        return switch (model) {
            case "gpt-4o" -> "OpenAI GPT-4o";
            case "gpt-4o-mini" -> "OpenAI GPT-4o Mini";
            case "gemini-1.5-pro" -> "Google Gemini 1.5 Pro";
            case "gemini-1.5-flash" -> "Google Gemini 1.5 Flash";
            case "claude-3-5-sonnet" -> "Anthropic Claude 3.5 Sonnet";
            case "llama3.1" -> "Meta Llama 3.1 8B";
            default -> model;
        };
    }
}
