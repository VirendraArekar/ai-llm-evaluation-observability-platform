package com.llmops.platform.modules.experiment.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.llmops.platform.common.exception.ResourceNotFoundException;
import com.llmops.platform.modules.auth.entity.Project;
import com.llmops.platform.modules.auth.repository.ProjectRepository;
import com.llmops.platform.modules.dataset.entity.Dataset;
import com.llmops.platform.modules.dataset.entity.DatasetItem;
import com.llmops.platform.modules.dataset.entity.DatasetVersion;
import com.llmops.platform.modules.dataset.repository.DatasetItemRepository;
import com.llmops.platform.modules.dataset.repository.DatasetRepository;
import com.llmops.platform.modules.dataset.repository.DatasetVersionRepository;
import com.llmops.platform.modules.evaluation.dto.EvaluationDto.JudgeResultDto;
import com.llmops.platform.modules.evaluation.dto.EvaluationDto.RunJudgeRequest;
import com.llmops.platform.modules.evaluation.service.DeterministicEvalService;
import com.llmops.platform.modules.evaluation.service.LlmJudgeService;
import com.llmops.platform.modules.experiment.dto.ExperimentDto;
import com.llmops.platform.modules.experiment.dto.ExperimentDto.*;
import com.llmops.platform.modules.experiment.entity.Experiment;
import com.llmops.platform.modules.experiment.entity.ExperimentResult;
import com.llmops.platform.modules.experiment.entity.ExperimentRun;
import com.llmops.platform.modules.experiment.repository.ExperimentRepository;
import com.llmops.platform.modules.experiment.repository.ExperimentResultRepository;
import com.llmops.platform.modules.experiment.repository.ExperimentRunRepository;
import com.llmops.platform.modules.prompt.entity.PromptVersion;
import com.llmops.platform.modules.prompt.repository.PromptVersionRepository;
import com.llmops.platform.modules.prompt.service.PromptService;
import com.llmops.platform.modules.provider.client.ChatMessage;
import com.llmops.platform.modules.provider.client.ChatRequest;
import com.llmops.platform.modules.provider.client.ChatResponse;
import com.llmops.platform.modules.provider.entity.Model;
import com.llmops.platform.modules.provider.repository.ModelRepository;
import com.llmops.platform.modules.provider.service.LlmExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExperimentExecutionService {

    private static final Logger log = LoggerFactory.getLogger(ExperimentExecutionService.class);

    private final ExperimentRepository experimentRepository;
    private final ExperimentRunRepository runRepository;
    private final ExperimentResultRepository resultRepository;
    private final DatasetRepository datasetRepository;
    private final DatasetVersionRepository datasetVersionRepository;
    private final DatasetItemRepository datasetItemRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final ModelRepository modelRepository;
    private final ProjectRepository projectRepository;
    private final LlmExecutionService executionService;
    private final LlmJudgeService judgeService;
    private final DeterministicEvalService deterministicEvalService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExperimentExecutionService(ExperimentRepository experimentRepository,
                                      ExperimentRunRepository runRepository,
                                      ExperimentResultRepository resultRepository,
                                      DatasetRepository datasetRepository,
                                      DatasetVersionRepository datasetVersionRepository,
                                      DatasetItemRepository datasetItemRepository,
                                      PromptVersionRepository promptVersionRepository,
                                      ModelRepository modelRepository,
                                      ProjectRepository projectRepository,
                                      LlmExecutionService executionService,
                                      LlmJudgeService judgeService,
                                      DeterministicEvalService deterministicEvalService) {
        this.experimentRepository = experimentRepository;
        this.runRepository = runRepository;
        this.resultRepository = resultRepository;
        this.datasetRepository = datasetRepository;
        this.datasetVersionRepository = datasetVersionRepository;
        this.datasetItemRepository = datasetItemRepository;
        this.promptVersionRepository = promptVersionRepository;
        this.modelRepository = modelRepository;
        this.projectRepository = projectRepository;
        this.executionService = executionService;
        this.judgeService = judgeService;
        this.deterministicEvalService = deterministicEvalService;
    }

    @Transactional(readOnly = true)
    public List<ExperimentDto> getExperiments(UUID projectId) {
        List<Experiment> list = experimentRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
        if (list.isEmpty()) {
            list = experimentRepository.findAll();
        }
        return list.stream()
                .map(this::mapToExperimentDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExperimentDto getExperiment(UUID experimentId) {
        Experiment experiment = experimentRepository.findById(experimentId)
                .orElseThrow(() -> new ResourceNotFoundException("Experiment not found: " + experimentId));
        return mapToExperimentDto(experiment);
    }

    @Transactional(readOnly = true)
    public List<ExperimentRunDto> getRuns(UUID experimentId) {
        return runRepository.findByExperimentId(experimentId).stream()
                .map(this::mapToRunDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExperimentDto createAndStartExperiment(UUID projectId, CreateExperimentRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
        Dataset dataset = datasetRepository.findById(request.getDatasetId())
                .orElseThrow(() -> new ResourceNotFoundException("Dataset not found: " + request.getDatasetId()));

        DatasetVersion version = datasetVersionRepository.findByDatasetIdAndVersionNumber(dataset.getId(), dataset.getCurrentVersion())
                .orElseThrow(() -> new ResourceNotFoundException("Dataset version not found"));

        List<DatasetItem> items = datasetItemRepository.findByDatasetVersionId(version.getId());

        Experiment experiment = new Experiment();
        experiment.setProject(project);
        experiment.setDataset(dataset);
        experiment.setName(request.getName());
        experiment.setDescription(request.getDescription());
        experiment.setStatus("RUNNING");
        experiment.setTotalRuns(items.size());
        experiment.setCompletedRuns(0);
        experiment.setFailedRuns(0);
        experiment = experimentRepository.save(experiment);

        runBatchAsync(experiment.getId(), version.getId(), request);

        return mapToExperimentDto(experiment);
    }

    @Async
    public void runBatchAsync(UUID experimentId, UUID datasetVersionId, CreateExperimentRequest request) {
        log.info("Starting async batch experiment execution: {}", experimentId);
        try {
            Experiment experiment = experimentRepository.findById(experimentId).orElse(null);
            if (experiment == null) return;

            List<DatasetItem> items = datasetItemRepository.findByDatasetVersionId(datasetVersionId);
            PromptVersion promptVer = request.getPromptVersionId() != null
                    ? promptVersionRepository.findById(request.getPromptVersionId()).orElse(null)
                    : null;

            String modelIdentifier = request.getModelIdentifier() != null ? request.getModelIdentifier()
                    : (promptVer != null && promptVer.getModel() != null ? promptVer.getModel().getModelIdentifier() : "gpt-4o-mini");

            Model model = modelRepository.findByModelIdentifier(modelIdentifier).orElse(null);

            double cumulativeScore = 0.0;
            long cumulativeLatency = 0;
            BigDecimal cumulativeCost = BigDecimal.ZERO;
            int completed = 0;
            int failed = 0;

            for (DatasetItem item : items) {
                ExperimentRun run = new ExperimentRun();
                run.setExperiment(experiment);
                run.setDatasetItem(item);
                run.setPromptVersion(promptVer);
                run.setModel(model);
                run.setStatus("RUNNING");

                Map<String, String> variables = new HashMap<>();
                try {
                    variables = objectMapper.readValue(item.getInputVariables(), new TypeReference<>() {});
                } catch (Exception ignored) {}

                String renderedPrompt = promptVer != null
                        ? PromptService.renderTemplate(promptVer.getTemplate(), variables)
                        : (variables.containsKey("query") ? variables.get("query") : item.getInputVariables());

                run.setInputText(renderedPrompt);

                try {
                    ChatRequest chatReq = new ChatRequest();
                    chatReq.setModel(modelIdentifier);
                    chatReq.setTemperature(promptVer != null ? promptVer.getTemperature().doubleValue() : 0.7);
                    chatReq.setMaxTokens(promptVer != null ? promptVer.getMaxTokens() : 2048);

                    List<ChatMessage> msgs = new ArrayList<>();
                    if (promptVer != null && promptVer.getSystemMessage() != null) {
                        msgs.add(ChatMessage.system(promptVer.getSystemMessage()));
                    }
                    msgs.add(ChatMessage.user(renderedPrompt));
                    chatReq.setMessages(msgs);

                    ChatResponse resp = executionService.execute(chatReq, "gpt-4o-mini");
                    run.setGeneratedOutput(resp.getContent());
                    run.setLatencyMs(resp.getLatencyMs());
                    run.setTokens(resp.getTotalTokens());
                    run.setCost(resp.getEstimatedCost());
                    run.setStatus("COMPLETED");

                    cumulativeLatency += resp.getLatencyMs();
                    cumulativeCost = cumulativeCost.add(resp.getEstimatedCost());
                    completed++;
                    run = runRepository.save(run);

                    // Run evaluations for this item
                    double itemAvgScore = evaluateRun(run, item, request.getMetrics(), modelIdentifier);
                    cumulativeScore += itemAvgScore;
                } catch (Exception e) {
                    log.error("Run error for experiment {}: {}", experimentId, e.getMessage());
                    run.setStatus("FAILED");
                    run.setErrorMessage(e.getMessage());
                    failed++;
                    runRepository.save(run);
                }
            }

            experiment.setCompletedRuns(completed);
            experiment.setFailedRuns(failed);
            experiment.setStatus("COMPLETED");
            experiment.setAverageLatency(completed > 0 ? cumulativeLatency / completed : 0);
            experiment.setTotalCost(cumulativeCost);
            experiment.setAverageScore(completed > 0 ? BigDecimal.valueOf(cumulativeScore / completed).setScale(4, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            experimentRepository.save(experiment);
            log.info("Batch experiment {} finished successfully with {} runs", experimentId, completed);
        } catch (Exception e) {
            log.error("Fatal failure in experiment execution: {}", e.getMessage(), e);
            experimentRepository.findById(experimentId).ifPresent(exp -> {
                exp.setStatus("FAILED");
                experimentRepository.save(exp);
            });
        }
    }

    private double evaluateRun(ExperimentRun run, DatasetItem item, List<String> metrics, String model) {
        List<String> evalMetrics = (metrics != null && !metrics.isEmpty()) ? metrics : List.of("RELEVANCE", "FAITHFULNESS");
        double totalScore = 0.0;

        for (String m : evalMetrics) {
            double score;
            boolean passFail;

            if ("EXACT_MATCH".equalsIgnoreCase(m)) {
                passFail = deterministicEvalService.exactMatch(run.getGeneratedOutput(), item.getExpectedOutput());
                score = passFail ? 1.0 : 0.0;
            } else if ("JSON_VALIDITY".equalsIgnoreCase(m)) {
                passFail = deterministicEvalService.isValidJson(run.getGeneratedOutput());
                score = passFail ? 1.0 : 0.0;
            } else {
                RunJudgeRequest judgeReq = new RunJudgeRequest();
                judgeReq.setMetricCode(m);
                judgeReq.setInputPrompt(run.getInputText());
                judgeReq.setGeneratedResponse(run.getGeneratedOutput());
                judgeReq.setSourceContext(item.getExpectedContext());
                judgeReq.setExpectedOutput(item.getExpectedOutput());
                judgeReq.setJudgeModel(model);

                JudgeResultDto jResult = judgeService.evaluate(judgeReq);
                score = jResult.getScore();
                passFail = jResult.isPassFail();
            }

            ExperimentResult er = new ExperimentResult();
            er.setExperimentRun(run);
            er.setMetricName(m);
            er.setScore(BigDecimal.valueOf(score).setScale(4, RoundingMode.HALF_UP));
            er.setPassFail(passFail);
            resultRepository.save(er);

            totalScore += score;
        }

        return !evalMetrics.isEmpty() ? totalScore / evalMetrics.size() : 0.85;
    }

    @Transactional(readOnly = true)
    public ExperimentComparisonDto compare(UUID baselineId, UUID candidateId) {
        Experiment base = experimentRepository.findById(baselineId)
                .orElseThrow(() -> new ResourceNotFoundException("Baseline experiment not found: " + baselineId));
        Experiment cand = experimentRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate experiment not found: " + candidateId));

        double baseScore = base.getAverageScore() != null ? base.getAverageScore().doubleValue() : 0.0;
        double candScore = cand.getAverageScore() != null ? cand.getAverageScore().doubleValue() : 0.0;
        double scoreDelta = candScore - baseScore;

        double latencyDelta = cand.getAverageLatency() - base.getAverageLatency();
        double costDelta = cand.getTotalCost().subtract(base.getTotalCost()).doubleValue();

        boolean regression = scoreDelta < -0.05 || (base.getAverageLatency() > 0 && ((double) latencyDelta / base.getAverageLatency()) > 0.35);

        String summary = regression
                ? String.format("Regression detected! Accuracy dropped by %.2f%% and latency increased by %d ms.", Math.abs(scoreDelta * 100), (long) latencyDelta)
                : String.format("Performance improved: score delta %.2f%% with %s latency.", scoreDelta * 100, latencyDelta <= 0 ? "faster" : "acceptable");

        ExperimentComparisonDto comp = new ExperimentComparisonDto();
        comp.setBaselineExperiment(mapToExperimentDto(base));
        comp.setCandidateExperiment(mapToExperimentDto(cand));
        comp.setScoreDelta(Math.round(scoreDelta * 1000.0) / 1000.0);
        comp.setLatencyDeltaMs(latencyDelta);
        comp.setCostDelta(costDelta);
        comp.setRegressionDetected(regression);
        comp.setRegressionSummary(summary);
        return comp;
    }

    private ExperimentDto mapToExperimentDto(Experiment e) {
        ExperimentDto dto = new ExperimentDto();
        dto.setId(e.getId());
        dto.setProjectId(e.getProject().getId());
        dto.setDatasetId(e.getDataset().getId());
        dto.setDatasetName(e.getDataset().getName());
        dto.setName(e.getName());
        dto.setDescription(e.getDescription());
        dto.setStatus(e.getStatus());
        dto.setTotalRuns(e.getTotalRuns());
        dto.setCompletedRuns(e.getCompletedRuns());
        dto.setFailedRuns(e.getFailedRuns());
        dto.setAverageScore(e.getAverageScore());
        dto.setAverageLatency(e.getAverageLatency());
        dto.setTotalCost(e.getTotalCost());
        dto.setCreatedAt(e.getCreatedAt());
        return dto;
    }

    private ExperimentRunDto mapToRunDto(ExperimentRun r) {
        ExperimentRunDto dto = new ExperimentRunDto();
        dto.setId(r.getId());
        dto.setExperimentId(r.getExperiment().getId());
        dto.setDatasetItemId(r.getDatasetItem().getId());
        dto.setStatus(r.getStatus());
        dto.setInputText(r.getInputText());
        dto.setGeneratedOutput(r.getGeneratedOutput());
        dto.setLatencyMs(r.getLatencyMs());
        dto.setTokens(r.getTokens());
        dto.setCost(r.getCost());
        dto.setErrorMessage(r.getErrorMessage());

        List<RunResultDto> results = resultRepository.findByExperimentRunId(r.getId()).stream()
                .map(res -> {
                    RunResultDto rd = new RunResultDto();
                    rd.setMetricName(res.getMetricName());
                    rd.setScore(res.getScore());
                    rd.setPassFail(res.isPassFail());
                    rd.setDetailsJson(res.getDetailsJson());
                    return rd;
                })
                .collect(Collectors.toList());
        dto.setResults(results);
        return dto;
    }
}
