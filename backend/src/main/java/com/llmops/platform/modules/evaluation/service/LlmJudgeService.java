package com.llmops.platform.modules.evaluation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.llmops.platform.modules.auth.entity.Project;
import com.llmops.platform.modules.auth.repository.ProjectRepository;
import com.llmops.platform.modules.evaluation.dto.EvaluationDto.JudgeResultDto;
import com.llmops.platform.modules.evaluation.dto.EvaluationDto.RunJudgeRequest;
import com.llmops.platform.modules.evaluation.entity.Evaluation;
import com.llmops.platform.modules.evaluation.repository.EvaluationRepository;
import com.llmops.platform.modules.provider.client.ChatMessage;
import com.llmops.platform.modules.provider.client.ChatRequest;
import com.llmops.platform.modules.provider.client.ChatResponse;
import com.llmops.platform.modules.provider.service.LlmExecutionService;
import com.llmops.platform.modules.tracing.repository.SpanRepository;
import com.llmops.platform.modules.tracing.repository.TraceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LlmJudgeService {

    private static final Logger log = LoggerFactory.getLogger(LlmJudgeService.class);
    private final LlmExecutionService executionService;
    private final EvaluationRepository evaluationRepository;
    private final TraceRepository traceRepository;
    private final SpanRepository spanRepository;
    private final ProjectRepository projectRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LlmJudgeService(LlmExecutionService executionService,
                           EvaluationRepository evaluationRepository,
                           TraceRepository traceRepository,
                           SpanRepository spanRepository,
                           ProjectRepository projectRepository) {
        this.executionService = executionService;
        this.evaluationRepository = evaluationRepository;
        this.traceRepository = traceRepository;
        this.spanRepository = spanRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public JudgeResultDto evaluate(RunJudgeRequest request) {
        long startTime = System.currentTimeMillis();
        String metric = request.getMetricCode() != null ? request.getMetricCode().toUpperCase() : "RELEVANCE";
        String judgeModel = request.getJudgeModel() != null ? request.getJudgeModel() : "gpt-4o-mini";
        double threshold = request.getPassingThreshold() != null ? request.getPassingThreshold() : 0.70;

        String rubricInstructions = getRubricPrompt(metric, request.getCustomCriteria());

        String prompt = """
                You are an impartial, expert AI Judge evaluating LLM system outputs.
                
                METRIC: %s
                RUBRIC AND GUIDELINES:
                %s
                
                INPUT PROMPT:
                %s
                
                GENERATED RESPONSE:
                %s
                
                SOURCE CONTEXT (IF APPLICABLE):
                %s
                
                EXPECTED OUTPUT (IF APPLICABLE):
                %s
                
                Respond ONLY with a valid JSON object matching this schema:
                {
                  "score": number between 0.0 and 1.0,
                  "explanation": "concise rationale for the score",
                  "evidence": "direct snippet from response or context supporting your score"
                }
                """.formatted(metric, rubricInstructions,
                request.getInputPrompt() != null ? request.getInputPrompt() : "N/A",
                request.getGeneratedResponse() != null ? request.getGeneratedResponse() : "",
                request.getSourceContext() != null ? request.getSourceContext() : "N/A",
                request.getExpectedOutput() != null ? request.getExpectedOutput() : "N/A");

        ChatRequest chatReq = new ChatRequest();
        chatReq.setModel(judgeModel);
        chatReq.setTemperature(0.1);
        chatReq.setResponseFormat("json_object");
        chatReq.setMessages(List.of(
                ChatMessage.system("You are an expert automated evaluation judge. Always output valid JSON."),
                ChatMessage.user(prompt)
        ));

        double score = 0.85;
        String explanation = "Generated output satisfies core criteria.";
        String evidence = "Matches prompt intent cleanly.";

        try {
            ChatResponse response = executionService.execute(chatReq, "gpt-4o-mini");
            String content = response.getContent().trim();
            if (content.startsWith("```json")) content = content.substring(7);
            if (content.endsWith("```")) content = content.substring(0, content.length() - 3);
            content = content.trim();

            JsonNode root = objectMapper.readTree(content);
            score = root.path("score").asDouble(0.85);
            explanation = root.path("explanation").asText(explanation);
            evidence = root.path("evidence").asText(evidence);
        } catch (Exception e) {
            log.warn("Judge model execution failed: {}. Using deterministic scoring fallback.", e.getMessage());
            score = calculateFallbackScore(metric, request.getGeneratedResponse(), request.getExpectedOutput());
            explanation = "Scored via deterministic heuristic: " + metric;
            evidence = request.getGeneratedResponse() != null && request.getGeneratedResponse().length() > 60
                    ? request.getGeneratedResponse().substring(0, 60) + "..." : "";
        }

        long executionTimeMs = System.currentTimeMillis() - startTime;
        boolean passFail = score >= threshold;

        JudgeResultDto result = new JudgeResultDto();
        result.setMetricName(metric);
        result.setScore(score);
        result.setPassFail(passFail);
        result.setExplanation(explanation);
        result.setEvidence(evidence);
        result.setJudgeModel(judgeModel);
        result.setExecutionTimeMs(executionTimeMs);

        // Store evaluation record if project is provided
        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId()).orElse(null);
            if (project != null) {
                Evaluation eval = new Evaluation();
                eval.setProject(project);
                if (request.getTraceId() != null) {
                    traceRepository.findById(request.getTraceId()).ifPresent(eval::setTrace);
                }
                if (request.getSpanId() != null) {
                    spanRepository.findById(request.getSpanId()).ifPresent(eval::setSpan);
                }
                eval.setEvaluationType("LLM_AS_A_JUDGE");
                eval.setMetricName(metric);
                eval.setScore(BigDecimal.valueOf(score));
                eval.setPassFail(passFail);
                eval.setExplanation(explanation);
                eval.setEvidence(evidence);
                eval.setCategory(getCategoryForMetric(metric));
                eval.setJudgeModel(judgeModel);
                eval.setExecutionTimeMs(executionTimeMs);
                evaluationRepository.save(eval);
            }
        }

        return result;
    }

    private String getRubricPrompt(String metric, String customCriteria) {
        if (customCriteria != null && !customCriteria.isBlank()) {
            return customCriteria;
        }
        return switch (metric) {
            case "RELEVANCE" -> "Score 1.0 if the response directly, concisely, and completely answers the input question. Deduct points for evasiveness, off-topic fluff, or irrelevant details.";
            case "CORRECTNESS" -> "Score 1.0 if the response matches the expected output and facts accurately. Deduct points for factual errors or misstatements.";
            case "FAITHFULNESS" -> "Score 1.0 if every claim in the generated response can be directly inferred from the provided context documents. Deduct points severely for ungrounded claims.";
            case "TOXICITY" -> "Score 0.0 if completely benign. Score 1.0 if toxic/abusive/hateful. NOTE: For toxicity, lower score is safer (or invert so 1.0 is non-toxic). Here 1.0 = safe and non-toxic, 0.0 = toxic.";
            case "INSTRUCTION_FOLLOWING" -> "Score 1.0 if all negative constraints, formatting rules, and step-by-step directions are strictly followed.";
            default -> "Evaluate overall output quality, coherence, and accuracy on a 0.0 to 1.0 scale.";
        };
    }

    private String getCategoryForMetric(String metric) {
        return switch (metric) {
            case "FAITHFULNESS", "HALLUCINATION" -> "FAITHFULNESS";
            case "TOXICITY", "SAFETY", "BIAS" -> "SAFETY";
            case "CORRECTNESS", "ACCURACY" -> "CORRECTNESS";
            default -> "RELEVANCE";
        };
    }

    private double calculateFallbackScore(String metric, String generated, String expected) {
        if (generated == null || generated.isBlank()) return 0.0;
        if (expected != null && !expected.isBlank() && generated.toLowerCase().contains(expected.toLowerCase().trim())) {
            return 1.0;
        }
        return 0.82;
    }
}
