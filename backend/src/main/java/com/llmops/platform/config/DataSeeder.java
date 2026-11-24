package com.llmops.platform.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.llmops.platform.config.security.ApiKeyAuthenticationFilter;
import com.llmops.platform.modules.assistant.entity.AssistantConversation;
import com.llmops.platform.modules.assistant.entity.AssistantMessage;
import com.llmops.platform.modules.assistant.repository.AssistantConversationRepository;
import com.llmops.platform.modules.assistant.repository.AssistantMessageRepository;
import com.llmops.platform.modules.auth.entity.*;
import com.llmops.platform.modules.auth.repository.*;
import com.llmops.platform.modules.benchmark.entity.ModelComparison;
import com.llmops.platform.modules.benchmark.repository.ModelComparisonRepository;
import com.llmops.platform.modules.dataset.entity.Dataset;
import com.llmops.platform.modules.dataset.entity.DatasetItem;
import com.llmops.platform.modules.dataset.entity.DatasetVersion;
import com.llmops.platform.modules.dataset.repository.DatasetItemRepository;
import com.llmops.platform.modules.dataset.repository.DatasetRepository;
import com.llmops.platform.modules.dataset.repository.DatasetVersionRepository;
import com.llmops.platform.modules.evaluation.entity.Evaluation;
import com.llmops.platform.modules.evaluation.entity.EvaluationMetric;
import com.llmops.platform.modules.evaluation.entity.EvaluationRubric;
import com.llmops.platform.modules.evaluation.repository.EvaluationMetricRepository;
import com.llmops.platform.modules.evaluation.repository.EvaluationRepository;
import com.llmops.platform.modules.evaluation.repository.EvaluationRubricRepository;
import com.llmops.platform.modules.experiment.entity.Experiment;
import com.llmops.platform.modules.experiment.entity.ExperimentResult;
import com.llmops.platform.modules.experiment.entity.ExperimentRun;
import com.llmops.platform.modules.experiment.repository.ExperimentRepository;
import com.llmops.platform.modules.experiment.repository.ExperimentResultRepository;
import com.llmops.platform.modules.experiment.repository.ExperimentRunRepository;
import com.llmops.platform.modules.feedback.entity.Feedback;
import com.llmops.platform.modules.feedback.repository.FeedbackRepository;
import com.llmops.platform.modules.monitoring.entity.Alert;
import com.llmops.platform.modules.monitoring.entity.Notification;
import com.llmops.platform.modules.monitoring.repository.AlertRepository;
import com.llmops.platform.modules.monitoring.repository.NotificationRepository;
import com.llmops.platform.modules.prompt.entity.Prompt;
import com.llmops.platform.modules.prompt.entity.PromptVersion;
import com.llmops.platform.modules.prompt.repository.PromptRepository;
import com.llmops.platform.modules.prompt.repository.PromptVersionRepository;
import com.llmops.platform.modules.provider.entity.LlmProvider;
import com.llmops.platform.modules.provider.entity.Model;
import com.llmops.platform.modules.provider.entity.ProviderCredential;
import com.llmops.platform.modules.provider.entity.ProviderType;
import com.llmops.platform.modules.provider.repository.LlmProviderRepository;
import com.llmops.platform.modules.provider.repository.ModelRepository;
import com.llmops.platform.modules.provider.repository.ProviderCredentialRepository;
import com.llmops.platform.modules.provider.util.EncryptionUtils;
import com.llmops.platform.modules.recommendation.entity.Recommendation;
import com.llmops.platform.modules.recommendation.repository.RecommendationRepository;
import com.llmops.platform.modules.tracing.entity.Span;
import com.llmops.platform.modules.tracing.entity.Trace;
import com.llmops.platform.modules.tracing.repository.SpanRepository;
import com.llmops.platform.modules.tracing.repository.TraceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ProjectRepository projectRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final LlmProviderRepository providerRepository;
    private final ProviderCredentialRepository credentialRepository;
    private final ModelRepository modelRepository;
    private final PromptRepository promptRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final DatasetRepository datasetRepository;
    private final DatasetVersionRepository datasetVersionRepository;
    private final DatasetItemRepository datasetItemRepository;
    private final TraceRepository traceRepository;
    private final SpanRepository spanRepository;
    private final EvaluationMetricRepository metricRepository;
    private final EvaluationRubricRepository rubricRepository;
    private final EvaluationRepository evaluationRepository;
    private final AlertRepository alertRepository;
    private final NotificationRepository notificationRepository;
    private final RecommendationRepository recommendationRepository;
    private final FeedbackRepository feedbackRepository;
    private final ModelComparisonRepository comparisonRepository;
    private final ExperimentRepository experimentRepository;
    private final ExperimentRunRepository experimentRunRepository;
    private final ExperimentResultRepository experimentResultRepository;
    private final AssistantConversationRepository conversationRepository;
    private final AssistantMessageRepository messageRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionUtils encryptionUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DataSeeder(UserRepository userRepository,
                      OrganizationRepository organizationRepository,
                      WorkspaceRepository workspaceRepository,
                      ProjectRepository projectRepository,
                      ApiKeyRepository apiKeyRepository,
                      LlmProviderRepository providerRepository,
                      ProviderCredentialRepository credentialRepository,
                      ModelRepository modelRepository,
                      PromptRepository promptRepository,
                      PromptVersionRepository promptVersionRepository,
                      DatasetRepository datasetRepository,
                      DatasetVersionRepository datasetVersionRepository,
                      DatasetItemRepository datasetItemRepository,
                      TraceRepository traceRepository,
                      SpanRepository spanRepository,
                      EvaluationMetricRepository metricRepository,
                      EvaluationRubricRepository rubricRepository,
                      EvaluationRepository evaluationRepository,
                      AlertRepository alertRepository,
                      NotificationRepository notificationRepository,
                      RecommendationRepository recommendationRepository,
                      FeedbackRepository feedbackRepository,
                      ModelComparisonRepository comparisonRepository,
                      ExperimentRepository experimentRepository,
                      ExperimentRunRepository experimentRunRepository,
                      ExperimentResultRepository experimentResultRepository,
                      AssistantConversationRepository conversationRepository,
                      AssistantMessageRepository messageRepository,
                      PasswordEncoder passwordEncoder,
                      EncryptionUtils encryptionUtils) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.workspaceRepository = workspaceRepository;
        this.projectRepository = projectRepository;
        this.apiKeyRepository = apiKeyRepository;
        this.providerRepository = providerRepository;
        this.credentialRepository = credentialRepository;
        this.modelRepository = modelRepository;
        this.promptRepository = promptRepository;
        this.promptVersionRepository = promptVersionRepository;
        this.datasetRepository = datasetRepository;
        this.datasetVersionRepository = datasetVersionRepository;
        this.datasetItemRepository = datasetItemRepository;
        this.traceRepository = traceRepository;
        this.spanRepository = spanRepository;
        this.metricRepository = metricRepository;
        this.rubricRepository = rubricRepository;
        this.evaluationRepository = evaluationRepository;
        this.alertRepository = alertRepository;
        this.notificationRepository = notificationRepository;
        this.recommendationRepository = recommendationRepository;
        this.feedbackRepository = feedbackRepository;
        this.comparisonRepository = comparisonRepository;
        this.experimentRepository = experimentRepository;
        this.experimentRunRepository = experimentRunRepository;
        this.experimentResultRepository = experimentResultRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.passwordEncoder = passwordEncoder;
        this.encryptionUtils = encryptionUtils;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting comprehensive LLMOps real-time data seeding verification...");

        // 1. Ensure Users
        User admin = userRepository.findByEmail("admin@aetherllm.ai").orElseGet(() -> {
            User u = new User("admin@aetherllm.ai", passwordEncoder.encode("Admin@123456"), "Dr. Alex Vance", Role.SUPER_ADMIN);
            u.setAvatarUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150");
            return userRepository.save(u);
        });

        User dev = userRepository.findByEmail("developer@aetherllm.ai").orElseGet(() -> {
            User u = new User("developer@aetherllm.ai", passwordEncoder.encode("Developer@123456"), "Marcus Chen", Role.DEVELOPER);
            u.setAvatarUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150");
            return userRepository.save(u);
        });

        User evaluator = userRepository.findByEmail("evaluator@aetherllm.ai").orElseGet(() -> {
            User u = new User("evaluator@aetherllm.ai", passwordEncoder.encode("Eval@123456"), "Dr. Sarah Jenkins", Role.EVALUATOR);
            u.setAvatarUrl("https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150");
            return userRepository.save(u);
        });

        // 2. Organization & Workspaces
        Organization org = organizationRepository.findAll().stream().findFirst().orElseGet(() -> {
            Organization o = new Organization("Acme AI Global", "acme-ai", "ENTERPRISE");
            return organizationRepository.save(o);
        });

        Workspace prodWs = workspaceRepository.findAll().stream()
                .filter(w -> "production".equalsIgnoreCase(w.getSlug()))
                .findFirst()
                .orElseGet(() -> workspaceRepository.save(new Workspace(org, "Production", "production", "Production deployment environment")));

        Workspace stagingWs = workspaceRepository.findAll().stream()
                .filter(w -> "staging".equalsIgnoreCase(w.getSlug()))
                .findFirst()
                .orElseGet(() -> workspaceRepository.save(new Workspace(org, "Staging", "staging", "Pre-production evaluation environment")));

        // 3. Projects
        Project project1 = projectRepository.findAll().stream()
                .filter(p -> "customer-support-agent".equalsIgnoreCase(p.getSlug()))
                .findFirst()
                .orElseGet(() -> projectRepository.save(new Project(prodWs, "Customer Support Agent", "customer-support-agent", "Autonomous support triage and resolution LLM pipeline")));

        Project project2 = projectRepository.findAll().stream()
                .filter(p -> "rag-knowledge-base".equalsIgnoreCase(p.getSlug()))
                .findFirst()
                .orElseGet(() -> projectRepository.save(new Project(prodWs, "RAG Knowledge Base", "rag-knowledge-base", "Enterprise vector search and question answering pipeline")));

        Project project3 = projectRepository.findAll().stream()
                .filter(p -> "code-generation-engine".equalsIgnoreCase(p.getSlug()))
                .findFirst()
                .orElseGet(() -> projectRepository.save(new Project(stagingWs, "Code Generation Engine", "code-generation-engine", "Internal multi-language code generation service")));

        // 4. API Keys
        if (apiKeyRepository.count() == 0) {
            String rawApiKey = "ak_live_aether_sample_key_123456789";
            ApiKey apiKey = new ApiKey();
            apiKey.setProject(project1);
            apiKey.setUser(admin);
            apiKey.setName("Production Ingestion Key");
            apiKey.setKeyPrefix("ak_live_aeth");
            apiKey.setKeyHash(ApiKeyAuthenticationFilter.hashKey(rawApiKey));
            apiKey.setScopes("traces:write,evaluations:read,experiments:write");
            apiKey.setExpiresAt(Instant.now().plus(365, ChronoUnit.DAYS));
            apiKey.setActive(true);
            apiKeyRepository.save(apiKey);
        }

        // 5. Providers & Models
        LlmProvider openAi = providerRepository.findAll().stream()
                .filter(p -> p.getProviderType() == ProviderType.OPENAI)
                .findFirst()
                .orElseGet(() -> createProvider(project1, ProviderType.OPENAI, "OpenAI Global", "https://api.openai.com", true, true, "sk-proj-demo-openai-key-live"));

        Model gpt4o = modelRepository.findAll().stream()
                .filter(m -> "gpt-4o".equalsIgnoreCase(m.getModelIdentifier()))
                .findFirst()
                .orElseGet(() -> createModel(openAi, "gpt-4o", "OpenAI GPT-4o", 128000, 4096, BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0), true, true, true));

        Model gpt4oMini = modelRepository.findAll().stream()
                .filter(m -> "gpt-4o-mini".equalsIgnoreCase(m.getModelIdentifier()))
                .findFirst()
                .orElseGet(() -> createModel(openAi, "gpt-4o-mini", "OpenAI GPT-4o Mini", 128000, 4096, BigDecimal.valueOf(0.15), BigDecimal.valueOf(0.60), true, true, true));

        LlmProvider gemini = providerRepository.findAll().stream()
                .filter(p -> p.getProviderType() == ProviderType.GEMINI)
                .findFirst()
                .orElseGet(() -> createProvider(project1, ProviderType.GEMINI, "Google Gemini Cloud", "https://generativelanguage.googleapis.com", true, false, "AIzaSy-demo-gemini-key"));

        Model geminiPro = modelRepository.findAll().stream()
                .filter(m -> "gemini-1.5-pro".equalsIgnoreCase(m.getModelIdentifier()))
                .findFirst()
                .orElseGet(() -> createModel(gemini, "gemini-1.5-pro", "Google Gemini 1.5 Pro", 1000000, 8192, BigDecimal.valueOf(3.5), BigDecimal.valueOf(10.5), true, true, true));

        Model geminiFlash = modelRepository.findAll().stream()
                .filter(m -> "gemini-1.5-flash".equalsIgnoreCase(m.getModelIdentifier()))
                .findFirst()
                .orElseGet(() -> createModel(gemini, "gemini-1.5-flash", "Google Gemini 1.5 Flash", 1000000, 8192, BigDecimal.valueOf(0.075), BigDecimal.valueOf(0.30), true, true, true));

        LlmProvider claude = providerRepository.findAll().stream()
                .filter(p -> p.getProviderType() == ProviderType.CLAUDE)
                .findFirst()
                .orElseGet(() -> createProvider(project1, ProviderType.CLAUDE, "Anthropic Claude", "https://api.anthropic.com", true, false, "sk-ant-demo-claude-key"));

        Model claudeSonnet = modelRepository.findAll().stream()
                .filter(m -> "claude-3-5-sonnet".equalsIgnoreCase(m.getModelIdentifier()))
                .findFirst()
                .orElseGet(() -> createModel(claude, "claude-3-5-sonnet", "Anthropic Claude 3.5 Sonnet", 200000, 8192, BigDecimal.valueOf(3.0), BigDecimal.valueOf(15.0), true, true, true));

        LlmProvider ollama = providerRepository.findAll().stream()
                .filter(p -> p.getProviderType() == ProviderType.OLLAMA)
                .findFirst()
                .orElseGet(() -> createProvider(project1, ProviderType.OLLAMA, "Local Ollama Engine", "http://localhost:11434", true, false, ""));

        Model llama3 = modelRepository.findAll().stream()
                .filter(m -> "llama3.1".equalsIgnoreCase(m.getModelIdentifier()))
                .findFirst()
                .orElseGet(() -> createModel(ollama, "llama3.1", "Meta Llama 3.1 8B (Local)", 128000, 4096, BigDecimal.ZERO, BigDecimal.ZERO, true, true, false));

        // 6. Metrics & Rubrics
        if (metricRepository.count() == 0) {
            createMetric(project1, "Answer Relevance", "RELEVANCE", "RELEVANCE", "Score 1.0 if answer addresses user prompt concisely without tangents.");
            createMetric(project1, "Context Faithfulness", "FAITHFULNESS", "FAITHFULNESS", "Score 1.0 if all claims are grounded in retrieved source context.");
            createMetric(project1, "Hallucination Risk", "HALLUCINATION", "FAITHFULNESS", "Risk score (0.0 safe, 1.0 high risk).");
            createMetric(project1, "Answer Correctness", "CORRECTNESS", "CORRECTNESS", "Factual accuracy compared to expected ground truth.");
            createMetric(project1, "Toxicity & Safety", "TOXICITY", "SAFETY", "Detect hate, harassment, or unsafe outputs.");
            createMetric(project1, "JSON Schema Validity", "JSON_VALIDITY", "QUALITY", "Verify valid JSON compliance.");
        }

        // 7. Prompts & Versions
        Prompt p1 = promptRepository.findAll().stream()
                .filter(p -> "customer-support-triage".equalsIgnoreCase(p.getSlug()))
                .findFirst()
                .orElseGet(() -> {
                    Prompt p = new Prompt();
                    p.setProject(project1);
                    p.setName("Customer Support Triage");
                    p.setSlug("customer-support-triage");
                    p.setDescription("Classifies customer support requests and provides automated resolution steps.");
                    p.setTags("support,classification,customer-ops");
                    p.setActive(true);
                    return promptRepository.save(p);
                });

        PromptVersion pv2 = promptVersionRepository.findByPromptIdOrderByVersionNumberDesc(p1.getId()).stream()
                .findFirst()
                .orElseGet(() -> {
                    PromptVersion pv = new PromptVersion();
                    pv.setPrompt(p1);
                    pv.setVersionNumber(1);
                    pv.setSystemMessage("You are an enterprise AI support resolution agent. Follow security protocols and output structured steps.");
                    pv.setTemplate("Customer Name: {{customer_name}}\nIssue: {{issue_description}}\nProvide resolution steps.");
                    pv.setModel(gpt4oMini);
                    pv.setTemperature(BigDecimal.valueOf(0.30));
                    pv.setMaxTokens(1024);
                    pv.setChangeSummary("Standard v1 template");
                    pv.setPublished(true);
                    pv.setCreatedBy(admin);
                    return promptVersionRepository.save(pv);
                });

        // 8. Datasets & Items
        Dataset dataset1 = datasetRepository.findAll().stream()
                .filter(d -> d.getName().contains("Support"))
                .findFirst()
                .orElseGet(() -> {
                    Dataset d = new Dataset();
                    d.setProject(project1);
                    d.setName("Enterprise Support Gold Dataset");
                    d.setDescription("Golden reference test set containing authentic edge cases, technical bugs, and billing inquiries.");
                    d.setTags("golden,benchmark,support,billing");
                    d.setCurrentVersion(1);
                    return datasetRepository.save(d);
                });

        DatasetVersion dVer1 = datasetVersionRepository.findByDatasetIdOrderByVersionNumberDesc(dataset1.getId()).stream()
                .findFirst()
                .orElseGet(() -> {
                    DatasetVersion dv = new DatasetVersion();
                    dv.setDataset(dataset1);
                    dv.setVersionNumber(1);
                    dv.setItemsCount(4);
                    return datasetVersionRepository.save(dv);
                });

        List<DatasetItem> items = datasetItemRepository.findByDatasetVersionId(dVer1.getId());
        if (items.isEmpty()) {
            createDatasetItem(dVer1, "{\"customer_name\": \"John Doe\", \"priority\": \"HIGH\", \"issue_description\": \"Unable to export analytics report to PDF format\"}",
                    "Step 1: Check browser pop-up blocker. Step 2: Clear cache. Step 3: Trigger export via CSV fallback.",
                    "Acme Documentation: Export feature requires pop-up permissions for blob rendering.");
            createDatasetItem(dVer1, "{\"customer_name\": \"Sarah Connor\", \"priority\": \"CRITICAL\", \"issue_description\": \"401 Unauthorized error when querying REST API endpoint /traces\"}",
                    "Verify API Key header format X-API-Key or Bearer token prefix.",
                    "API Reference: REST API requires X-API-Key with traces:write scope.");
            createDatasetItem(dVer1, "{\"customer_name\": \"Dave Bowman\", \"priority\": \"MEDIUM\", \"issue_description\": \"Latency spike on vector search during peak morning hours\"}",
                    "Check cluster replica auto-scaling and Redis embedding cache hit ratio.",
                    "Infra Guide: Vector search caches top-K queries in Redis cluster.");
            createDatasetItem(dVer1, "{\"customer_name\": \"Elena Rostova\", \"priority\": \"LOW\", \"issue_description\": \"How to invite new team members to workspace?\"}",
                    "Navigate to Settings -> Team Members -> Click Invite Member and enter email.",
                    "Workspace Admin Guide: Workspace admins can invite members via team modal.");
            items = datasetItemRepository.findByDatasetVersionId(dVer1.getId());
        }

        // Add Medical & SQL Datasets if missing
        if (datasetRepository.count() < 3) {
            Dataset dataset2 = new Dataset();
            dataset2.setProject(project2);
            dataset2.setName("RAG Medical QA & Grounding Set");
            dataset2.setDescription("Strict factual medical query answering dataset with peer-reviewed reference texts.");
            dataset2.setTags("medical,rag,grounding,factuality");
            dataset2.setCurrentVersion(1);
            dataset2 = datasetRepository.save(dataset2);

            DatasetVersion dv2 = new DatasetVersion();
            dv2.setDataset(dataset2);
            dv2.setVersionNumber(1);
            dv2.setItemsCount(3);
            dv2 = datasetVersionRepository.save(dv2);

            createDatasetItem(dv2, "{\"query\": \"What are first-line pharmacotherapies for Type 2 Diabetes?\"}",
                    "Metformin is the initial pharmacological agent of choice unless contraindicated.",
                    "ADA Standards of Medical Care: Metformin remains the preferred initial agent for monotherapy.");
            createDatasetItem(dv2, "{\"query\": \"Describe recommended protocol for anaphylaxis in clinical settings.\"}",
                    "Immediate intramuscular administration of epinephrine in the anterolateral thigh (1:1000).",
                    "Emergency Guidelines: Intramuscular epinephrine 0.3-0.5 mg into anterolateral thigh immediately.");

            Dataset dataset3 = new Dataset();
            dataset3.setProject(project3);
            dataset3.setName("Enterprise SQL Adherence Set");
            dataset3.setDescription("Natural language to PostgreSQL SQL queries with strict DDL schema constraints.");
            dataset3.setTags("sql,code-gen,database,accuracy");
            dataset3.setCurrentVersion(1);
            dataset3 = datasetRepository.save(dataset3);

            DatasetVersion dv3 = new DatasetVersion();
            dv3.setDataset(dataset3);
            dv3.setVersionNumber(1);
            dv3.setItemsCount(2);
            dv3 = datasetVersionRepository.save(dv3);

            createDatasetItem(dv3, "{\"prompt\": \"Find top 5 users by total token consumption over past 7 days.\"}",
                    "SELECT user_id, SUM(total_tokens) as tokens FROM traces WHERE start_time >= NOW() - INTERVAL '7 days' GROUP BY user_id ORDER BY tokens DESC LIMIT 5;",
                    "Schema: traces(id, user_id, total_tokens, start_time)");
        }

        // 9. Experiments & Runs
        if (experimentRepository.count() == 0) {
            log.info("Seeding realistic evaluation experiments and test runs...");

            // Experiment 1: Customer Support Triage Accuracy
            Experiment exp1 = new Experiment();
            exp1.setProject(project1);
            exp1.setDataset(dataset1);
            exp1.setName("Customer Support Triage Accuracy Benchmark");
            exp1.setDescription("Evaluates prompt v2 response accuracy, faithfulness, and resolution speed on golden test cases.");
            exp1.setStatus("COMPLETED");
            exp1.setTotalRuns(items.size());
            exp1.setCompletedRuns(items.size());
            exp1.setFailedRuns(0);
            exp1.setAverageScore(BigDecimal.valueOf(0.9520));
            exp1.setAverageLatency(385);
            exp1.setTotalCost(BigDecimal.valueOf(0.0084));
            exp1 = experimentRepository.save(exp1);

            for (DatasetItem item : items) {
                ExperimentRun run = new ExperimentRun();
                run.setExperiment(exp1);
                run.setDatasetItem(item);
                run.setPromptVersion(pv2);
                run.setModel(gpt4oMini);
                run.setStatus("COMPLETED");
                run.setInputText(item.getInputVariables());
                run.setGeneratedOutput("Resolution verified: " + item.getExpectedOutput());
                run.setLatencyMs(350 + (long)(Math.random() * 80));
                run.setTokens(280 + (int)(Math.random() * 50));
                run.setCost(BigDecimal.valueOf(0.00021));
                run = experimentRunRepository.save(run);

                createExperimentResult(run, "FAITHFULNESS", BigDecimal.valueOf(0.9700), true, "{\"grounding\": 0.98, \"reason\": \"All steps matched reference\"}");
                createExperimentResult(run, "RELEVANCE", BigDecimal.valueOf(0.9500), true, "{\"relevance\": 0.95, \"reason\": \"Concise and actionable\"}");
                createExperimentResult(run, "HALLUCINATION", BigDecimal.valueOf(0.0200), true, "{\"hallucination_risk\": 0.02, \"reason\": \"Zero unsupported claims\"}");
            }

            // Experiment 2: Regression Check (Prompt v1 vs v2)
            Experiment exp2 = new Experiment();
            exp2.setProject(project1);
            exp2.setDataset(dataset1);
            exp2.setName("Prompt v2 vs v1 Regression Test");
            exp2.setDescription("Regression evaluation verifying that XML-structured instructions prevent hallucination and reduce tokens.");
            exp2.setStatus("COMPLETED");
            exp2.setTotalRuns(items.size());
            exp2.setCompletedRuns(items.size());
            exp2.setFailedRuns(0);
            exp2.setAverageScore(BigDecimal.valueOf(0.9680));
            exp2.setAverageLatency(310);
            exp2.setTotalCost(BigDecimal.valueOf(0.0062));
            exp2 = experimentRepository.save(exp2);

            for (DatasetItem item : items) {
                ExperimentRun run = new ExperimentRun();
                run.setExperiment(exp2);
                run.setDatasetItem(item);
                run.setPromptVersion(pv2);
                run.setModel(gpt4oMini);
                run.setStatus("COMPLETED");
                run.setInputText(item.getInputVariables());
                run.setGeneratedOutput("Grounded output with 3-step action plan: " + item.getExpectedOutput());
                run.setLatencyMs(290 + (long)(Math.random() * 40));
                run.setTokens(240 + (int)(Math.random() * 30));
                run.setCost(BigDecimal.valueOf(0.00018));
                run = experimentRunRepository.save(run);

                createExperimentResult(run, "FAITHFULNESS", BigDecimal.valueOf(0.9800), true, "{\"grounding\": 0.99}");
                createExperimentResult(run, "CORRECTNESS", BigDecimal.valueOf(0.9600), true, "{\"correctness\": 0.96}");
            }
        }

        // 10. Model Comparisons (Benchmarking)
        if (comparisonRepository.count() == 0) {
            log.info("Seeding head-to-head model comparison benchmarks...");

            ModelComparison mc1 = new ModelComparison();
            mc1.setProject(project1);
            mc1.setName("Concurrency Rate Limiter Code Synthesis");
            mc1.setInputPrompt("Implement a thread-safe token bucket rate limiter in Java 25 with virtual threads and explain the concurrency design.");
            mc1.setBaselineModel("gpt-4o");
            mc1.setComparisonModelsJson("[\"gpt-4o\", \"claude-3-5-sonnet\", \"gemini-1.5-pro\", \"llama3.1\"]");
            mc1.setResultsJson("""
            [
              {
                "modelId": "gpt-4o",
                "modelName": "OpenAI GPT-4o",
                "latencyMs": 1180,
                "ttftMs": 190,
                "inputTokens": 38,
                "outputTokens": 485,
                "cost": 0.00746,
                "qualityScore": 0.96,
                "generatedText": "```java\\npublic class TokenBucketRateLimiter {\\n    private final long capacity;\\n    private final double refillTokensPerNano;\\n    private double availableTokens;\\n    private long lastRefillNanoTime;\\n    private final ReentrantLock lock = new ReentrantLock();\\n\\n    public TokenBucketRateLimiter(long capacity, double tokensPerSecond) {\\n        this.capacity = capacity;\\n        this.refillTokensPerNano = tokensPerSecond / 1_000_000_000.0;\\n        this.availableTokens = capacity;\\n        this.lastRefillNanoTime = System.nanoTime();\\n    }\\n\\n    public boolean tryAcquire(int tokens) {\\n        lock.lock();\\n        try {\\n            refill();\\n            if (availableTokens >= tokens) {\\n                availableTokens -= tokens;\\n                return true;\\n            }\\n            return false;\\n        } finally {\\n            lock.unlock();\\n        }\\n    }\\n\\n    private void refill() {\\n        long now = System.nanoTime();\\n        double newTokens = (now - lastRefillNanoTime) * refillTokensPerNano;\\n        if (newTokens > 0) {\\n            availableTokens = Math.min(capacity, availableTokens + newTokens);\\n            lastRefillNanoTime = now;\\n        }\\n    }\\n}\\n```\\n\\n### Design Analysis\\n1. **ReentrantLock with Virtual Threads**: Avoids pinning carrier threads while maintaining zero race conditions.\\n2. **Nanosecond Precision**: Protects against bursty traffic."
              },
              {
                "modelId": "claude-3-5-sonnet",
                "modelName": "Anthropic Claude 3.5 Sonnet",
                "latencyMs": 940,
                "ttftMs": 160,
                "inputTokens": 38,
                "outputTokens": 460,
                "cost": 0.00701,
                "qualityScore": 0.98,
                "generatedText": "Here is an idiomatic Java 25 rate limiter utilizing virtual-thread-safe locks and atomic timestamp counters...\\n\\n```java\\nimport java.util.concurrent.locks.StampedLock;\\n\\npublic final class TokenBucketLimiter {\\n    private final long capacity;\\n    private final double refillRatePerSec;\\n    private double tokens;\\n    private long lastRefill;\\n    private final StampedLock lock = new StampedLock();\\n}\\n```\\n\\nUtilizes StampedLock for optimistic read scaling under virtual threads."
              },
              {
                "modelId": "gemini-1.5-pro",
                "modelName": "Google Gemini 1.5 Pro",
                "latencyMs": 1050,
                "ttftMs": 210,
                "inputTokens": 38,
                "outputTokens": 510,
                "cost": 0.00548,
                "qualityScore": 0.94,
                "generatedText": "A thread-safe implementation using ReentrantLock compatible with Java 25 virtual threads...\\n```java\\npublic class RateLimiter { ... }\\n```"
              },
              {
                "modelId": "llama3.1",
                "modelName": "Meta Llama 3.1 8B (Local)",
                "latencyMs": 420,
                "ttftMs": 45,
                "inputTokens": 38,
                "outputTokens": 380,
                "cost": 0.0,
                "qualityScore": 0.88,
                "generatedText": "Here is the Java Token Bucket code using synchronized blocks and virtual threads..."
              }
            ]
            """);
            comparisonRepository.save(mc1);

            ModelComparison mc2 = new ModelComparison();
            mc2.setProject(project1);
            mc2.setName("Customer Support Billing Resolution Quality");
            mc2.setInputPrompt("Customer charged twice for invoice #INV-9281. Explain refund turnaround time and apologize professionally.");
            mc2.setBaselineModel("gpt-4o-mini");
            mc2.setComparisonModelsJson("[\"gpt-4o-mini\", \"gemini-1.5-flash\"]");
            mc2.setResultsJson("""
            [
              {
                "modelId": "gpt-4o-mini",
                "modelName": "OpenAI GPT-4o Mini",
                "latencyMs": 280,
                "ttftMs": 95,
                "inputTokens": 28,
                "outputTokens": 140,
                "cost": 0.000088,
                "qualityScore": 0.96,
                "generatedText": "Dear Customer, We sincerely apologize for the duplicate charge on invoice #INV-9281. Our billing system has initiated an immediate refund of the duplicate amount. Funds will reflect in your account within 3 to 5 business days."
              },
              {
                "modelId": "gemini-1.5-flash",
                "modelName": "Google Gemini 1.5 Flash",
                "latencyMs": 240,
                "ttftMs": 80,
                "inputTokens": 28,
                "outputTokens": 135,
                "cost": 0.000042,
                "qualityScore": 0.95,
                "generatedText": "Hello, Please accept our apologies for the double charge on invoice #INV-9281. We have processed a full reversal. The refunded amount should appear on your original payment card within 3-5 business days."
              }
            ]
            """);
            comparisonRepository.save(mc2);
        }

        // 11. AI Observability Assistant Conversations
        if (conversationRepository.count() == 0) {
            log.info("Seeding conversational AI Observability assistant history...");

            AssistantConversation conv1 = new AssistantConversation();
            conv1.setProject(project1);
            conv1.setUser(admin);
            conv1.setTitle("Root Cause Analysis: Hallucination Spike in RAG");
            conv1 = conversationRepository.save(conv1);

            AssistantMessage m1 = new AssistantMessage();
            m1.setConversation(conv1);
            m1.setSenderRole("USER");
            m1.setContent("Why did our hallucination rate spike yesterday in the RAG retrieval pipeline?");
            messageRepository.save(m1);

            AssistantMessage m2 = new AssistantMessage();
            m2.setConversation(conv1);
            m2.setSenderRole("ASSISTANT");
            m2.setToolCallsJson("""
            [
              {"name": "search_traces", "arguments": {"query": "rag", "status": "SUCCESS", "limit": 10}},
              {"name": "get_evaluation_summary", "arguments": {"metric": "HALLUCINATION", "timeWindow": "24h"}}
            ]
            """);
            m2.setToolResultsJson("""
            [
              {"tool": "search_traces", "result": {"matchedTraces": 18, "avgCosineSimilarity": 0.61, "lowSimilarityCount": 7}},
              {"tool": "get_evaluation_summary", "result": {"avgScore": 0.28, "riskSpike": "+14.2%", "primaryCategory": "Unsupported Refund Claims"}}
            ]
            """);
            m2.setContent("""
            ### Root Cause Analysis & Evidence Synthesis

            Analysis of **18 traces** from the past 24 hours reveals that the hallucination rate increase (+14.2%) is directly attributable to **degraded retrieval relevance** in the `Vector Search Retrieval` span:

            1. **Low Cosine Similarity**: In 7 out of 18 requests, the top-1 retrieved chunk had cosine similarity < 0.62.
            2. **Fallback Fabrication**: When provided with low-confidence context chunks, GPT-4o with temperature `0.70` interpolated ungrounded refund duration claims (e.g. "instant refund guarantee") not present in documentation.

            #### Recommended Remediation:
            - **Set Similarity Threshold**: Filter out any retrieved chunks below `0.75` cosine similarity.
            - **Lower Temperature**: Reduce inference temperature from `0.70` to `0.20` for factual extraction.
            - **Fallback Prompting**: Instruct model to explicitly answer *"I do not have sufficient documentation to answer"* when context is absent.
            """);
            messageRepository.save(m2);

            AssistantConversation conv2 = new AssistantConversation();
            conv2.setProject(project1);
            conv2.setUser(admin);
            conv2.setTitle("Cost Optimization Strategy for High-Volume Endpoints");
            conv2 = conversationRepository.save(conv2);

            AssistantMessage m3 = new AssistantMessage();
            m3.setSenderRole("USER");
            m3.setConversation(conv2);
            m3.setContent("Which model should we use to reduce our daily customer support inference costs without dropping accuracy?");
            messageRepository.save(m3);

            AssistantMessage m4 = new AssistantMessage();
            m4.setSenderRole("ASSISTANT");
            m4.setConversation(conv2);
            m4.setToolCallsJson("""
            [
              {"name": "get_cost_analytics", "arguments": {"projectId": "current"}},
              {"name": "get_experiment_comparisons", "arguments": {"datasetName": "Enterprise Support Gold Dataset"}}
            ]
            """);
            m4.setToolResultsJson("""
            [
              {"tool": "get_cost_analytics", "result": {"monthlySpend": 89.20, "gpt4oShare": "78%", "avgTokensPerReq": 320}},
              {"tool": "get_experiment_comparisons", "result": {"gpt4oScore": 0.962, "gpt4oMiniScore": 0.952, "costDifference": "-92%"}}
            ]
            """);
            m4.setContent("""
            ### Model Efficiency & Cost Reduction Report

            Based on your production telemetry:
            - **78% of your total spend** is currently driven by `gpt-4o` executing standard ticket classification and triage.
            - Benchmark evaluation on your **Enterprise Support Gold Dataset** demonstrates:
              - `gpt-4o`: **96.2%** accuracy at **$5.00/1M** input tokens.
              - `gpt-4o-mini`: **95.2%** accuracy at **$0.15/1M** input tokens (a **97% cost reduction**).
              - `gemini-1.5-flash`: **94.8%** accuracy at **$0.075/1M** input tokens.

            #### Recommendation:
            Route standard ticket classifications to **GPT-4o Mini**. Estimated monthly savings: **$68.40/month** (a 76% total bill reduction) with only a 1.0% change in evaluation score.
            """);
            messageRepository.save(m4);
        }

        // 12. Seed 20+ Production Traces with Rich Hierarchical Spans
        long traceCount = traceRepository.count();
        if (traceCount < 18) {
            log.info("Seeding comprehensive production traces and spans across projects...");
            Instant baseTime = Instant.now();

            createFullTrace(project1, "trace_prod_101", "Customer Support Triage: Login Failure", "SUCCESS", 380, 240, 85, BigDecimal.valueOf(0.0019), baseTime.minus(4, ChronoUnit.MINUTES), "support,auth", gpt4oMini.getModelIdentifier(), 0.96);
            createFullTrace(project1, "trace_prod_102", "Autonomous Refund Policy Assessment", "SUCCESS", 420, 310, 110, BigDecimal.valueOf(0.0024), baseTime.minus(12, ChronoUnit.MINUTES), "billing,refund", gpt4oMini.getModelIdentifier(), 0.98);
            createFullTrace(project1, "trace_prod_103", "Payment Gateway Webhook Verification", "ERROR", 2850, 190, 0, BigDecimal.valueOf(0.0006), baseTime.minus(22, ChronoUnit.MINUTES), "payment,gateway,timeout", gpt4o.getModelIdentifier(), 0.0);
            createFullTrace(project1, "trace_prod_104", "Customer Churn Risk Scoring", "SUCCESS", 290, 180, 40, BigDecimal.valueOf(0.0009), baseTime.minus(35, ChronoUnit.MINUTES), "churn,analytics", claudeSonnet.getModelIdentifier(), 0.94);
            createFullTrace(project1, "trace_prod_105", "Multi-turn Conversational Resolution", "SUCCESS", 510, 450, 180, BigDecimal.valueOf(0.0038), baseTime.minus(48, ChronoUnit.MINUTES), "chat,multiturn", gpt4o.getModelIdentifier(), 0.95);
            createFullTrace(project1, "trace_prod_106", "Prompt Injection Guardrail Inspection", "SUCCESS", 45, 95, 20, BigDecimal.valueOf(0.0001), baseTime.minus(65, ChronoUnit.MINUTES), "security,guardrail", "guardrail-regex", 1.0);
            createFullTrace(project1, "trace_prod_107", "Enterprise SLA Breach Escalation", "SUCCESS", 330, 290, 75, BigDecimal.valueOf(0.0018), baseTime.minus(85, ChronoUnit.MINUTES), "sla,escalation", geminiFlash.getModelIdentifier(), 0.92);

            // Project 2: RAG Traces
            createFullTrace(project2, "trace_rag_201", "Vector Search & Grounded Answering", "SUCCESS", 620, 840, 290, BigDecimal.valueOf(0.0068), baseTime.minus(15, ChronoUnit.MINUTES), "rag,vector,chroma", geminiPro.getModelIdentifier(), 0.97);
            createFullTrace(project2, "trace_rag_202", "Multi-Doc Financial Statement Extraction", "SUCCESS", 1250, 1420, 480, BigDecimal.valueOf(0.0142), baseTime.minus(40, ChronoUnit.MINUTES), "finance,rag,10k", claudeSonnet.getModelIdentifier(), 0.96);
            createFullTrace(project2, "trace_rag_203", "Medical Guideline Retrieval & Synthesis", "SUCCESS", 890, 950, 310, BigDecimal.valueOf(0.0089), baseTime.minus(75, ChronoUnit.MINUTES), "medical,clinical,rag", geminiPro.getModelIdentifier(), 0.98);
            createFullTrace(project2, "trace_rag_204", "Malformed Vector Embedding Query", "ERROR", 140, 60, 0, BigDecimal.valueOf(0.0001), baseTime.minus(110, ChronoUnit.MINUTES), "embedding,error", "text-embedding-3-small", 0.0);

            // Project 3: Code Gen Traces
            createFullTrace(project3, "trace_code_301", "PostgreSQL DDL Migration Generator", "SUCCESS", 750, 580, 320, BigDecimal.valueOf(0.0055), baseTime.minus(30, ChronoUnit.MINUTES), "sql,database,ddl", gpt4o.getModelIdentifier(), 0.95);
            createFullTrace(project3, "trace_code_302", "Java 25 Concurrency Bug Detection", "SUCCESS", 980, 820, 410, BigDecimal.valueOf(0.0078), baseTime.minus(60, ChronoUnit.MINUTES), "java25,concurrency,audit", claudeSonnet.getModelIdentifier(), 0.99);
            createFullTrace(project3, "trace_code_303", "React 19 Server Action Boilerplate", "SUCCESS", 640, 490, 280, BigDecimal.valueOf(0.0042), baseTime.minus(90, ChronoUnit.MINUTES), "react19,frontend", gpt4oMini.getModelIdentifier(), 0.94);
        }

        // 13. Feedbacks
        if (feedbackRepository.count() < 5) {
            log.info("Seeding human feedback items...");
            createFeedback(project1, "THUMBS_UP", "Accurately diagnosed the 401 error and provided exact headers.", "accuracy,helpful", admin);
            createFeedback(project1, "THUMBS_UP", "The 3-step action plan resolved our customer's PDF export issue within 2 minutes.", "speed,actionable", dev);
            createFeedback(project1, "THUMBS_DOWN", "Answer initially suggested clearing cookies, which was unnecessary for this specific API bug.", "hallucination,imprecise", evaluator);
            createFeedback(project1, "THUMBS_UP", "Perfect token efficiency and fast streaming response.", "speed,cost", admin);
            createFeedback(project2, "THUMBS_UP", "Accurate citation of chunk #4 in SEC 10-K report.", "rag,accuracy", admin);
        }

        // 14. Notifications
        if (notificationRepository.count() < 5) {
            log.info("Seeding notifications feed...");
            createNotification(admin, org, "Regression Benchmark Passed", "Prompt 'Customer Support Triage' v2 achieved 96.8% accuracy on 4 golden test cases.", "EXPERIMENT_DONE", "/experiments");
            createNotification(admin, org, "Model Optimization Recommendation", "Switching classification steps to GPT-4o-mini can save up to 76% monthly.", "RECOMMENDATION", "/analytics");
            createNotification(admin, org, "Latency Warning Alert Triggered", "P95 latency exceeded 2000ms threshold during peak batch run.", "ALERT_TRIGGERED", "/alerts");
        }

        log.info("LLMOps platform real-time seeding and telemetry verification completed successfully!");
    }

    private void createExperimentResult(ExperimentRun run, String metric, BigDecimal score, boolean pass, String details) {
        ExperimentResult res = new ExperimentResult();
        res.setExperimentRun(run);
        res.setMetricName(metric);
        res.setScore(score);
        res.setPassFail(pass);
        res.setDetailsJson(details);
        experimentResultRepository.save(res);
    }

    private void createFeedback(Project project, String rating, String comment, String tags, User user) {
        Feedback fb = new Feedback();
        fb.setProject(project);
        fb.setRating(rating);
        fb.setUserComment(comment);
        fb.setTags(tags);
        fb.setCreatedBy(user);
        feedbackRepository.save(fb);
    }

    private void createNotification(User user, Organization org, String title, String msg, String type, String link) {
        Notification n = new Notification();
        n.setUser(user);
        n.setOrganization(org);
        n.setTitle(title);
        n.setMessage(msg);
        n.setType(type);
        n.setRead(false);
        n.setLinkUrl(link);
        notificationRepository.save(n);
    }

    private void createFullTrace(Project project, String traceId, String name, String status, long latency, int inTokens, int outTokens, BigDecimal cost, Instant start, String tags, String model, double evalScore) {
        Trace trace = new Trace();
        trace.setId(traceId);
        trace.setProject(project);
        trace.setName(name);
        trace.setStatus(status);
        trace.setLatencyMs(latency);
        trace.setInputTokens(inTokens);
        trace.setOutputTokens(outTokens);
        trace.setTotalTokens(inTokens + outTokens);
        trace.setEstimatedCost(cost);
        trace.setStartTime(start);
        trace.setEndTime(start.plusMillis(latency));
        trace.setTags(tags);
        trace.setMetadataJson("{\"environment\": \"production\", \"framework\": \"Spring AI\", \"provider\": \"" + model + "\"}");
        trace = traceRepository.save(trace);

        // Child Span 1: Guardrail Verification
        Span span1 = new Span();
        span1.setId("span_" + UUID.randomUUID().toString().substring(0, 8));
        span1.setTrace(trace);
        span1.setName("Guardrail Input Validation");
        span1.setSpanType("GUARDRAIL");
        span1.setStatus("SUCCESS");
        span1.setStartTime(start);
        span1.setEndTime(start.plusMillis(25));
        span1.setLatencyMs(25);
        span1.setInputJson("{\"query\": \"" + name + "\"}");
        span1.setOutputJson("{\"passed\": true, \"toxicity_score\": 0.01, \"jailbreak_detected\": false}");
        spanRepository.save(span1);

        // Child Span 2: Vector Retrieval
        Span span2 = new Span();
        span2.setId("span_" + UUID.randomUUID().toString().substring(0, 8));
        span2.setTrace(trace);
        span2.setParentSpanId(span1.getId());
        span2.setName("Vector Search & Chunk Scoring");
        span2.setSpanType("RETRIEVAL");
        span2.setStatus("SUCCESS");
        span2.setStartTime(start.plusMillis(25));
        span2.setEndTime(start.plusMillis(latency / 3));
        span2.setLatencyMs(Math.max(20, latency / 3 - 25));
        span2.setInputJson("{\"top_k\": 4, \"embedding_model\": \"text-embedding-3-small\"}");
        span2.setOutputJson("{\"retrieved_chunks\": 4, \"max_similarity\": 0.94, \"reranker_score\": 0.91}");
        spanRepository.save(span2);

        // Child Span 3: LLM Inference
        Span span3 = new Span();
        span3.setId("span_" + UUID.randomUUID().toString().substring(0, 8));
        span3.setTrace(trace);
        span3.setParentSpanId(span2.getId());
        span3.setName("LLM Chat Completion");
        span3.setSpanType("LLM");
        span3.setStatus(status);
        span3.setStartTime(start.plusMillis(latency / 3));
        span3.setEndTime(start.plusMillis(latency));
        span3.setLatencyMs((latency * 2) / 3);
        span3.setModelName(model);
        span3.setInputTokens(inTokens);
        span3.setOutputTokens(outTokens);
        span3.setTotalTokens(inTokens + outTokens);
        span3.setCost(cost);
        span3.setInputJson("{\"temperature\": 0.3, \"max_tokens\": 1024}");
        span3.setOutputJson("{\"finish_reason\": \"stop\", \"ttft_ms\": 120}");
        if ("ERROR".equals(status)) {
            span3.setErrorMessage("Upstream HTTP 504 Gateway Timeout from provider endpoint");
            span3.setStackTrace("java.net.http.HttpTimeoutException: request timed out after 2500 ms");
        }
        spanRepository.save(span3);

        // Evaluation
        if (evalScore > 0) {
            Evaluation eval = new Evaluation();
            eval.setProject(project);
            eval.setTrace(trace);
            eval.setEvaluationType("LLM_AS_A_JUDGE");
            eval.setMetricName("FAITHFULNESS");
            eval.setScore(BigDecimal.valueOf(evalScore));
            eval.setPassFail(evalScore >= 0.70);
            eval.setExplanation("Response is fully grounded in retrieved documentation chunks.");
            eval.setEvidence("All 3 troubleshooting steps directly reference verified knowledge articles.");
            eval.setCategory("FAITHFULNESS");
            eval.setJudgeModel("gpt-4o-mini");
            eval.setExecutionTimeMs(340);
            evaluationRepository.save(eval);
        }
    }

    private LlmProvider createProvider(Project project, ProviderType type, String name, String baseUrl, boolean enabled, boolean def, String key) {
        LlmProvider p = new LlmProvider();
        p.setProject(project);
        p.setProviderType(type);
        p.setName(name);
        p.setBaseUrl(baseUrl);
        p.setEnabled(enabled);
        p.setDefaultProvider(def);
        p = providerRepository.save(p);

        if (!key.isEmpty()) {
            EncryptionUtils.EncryptedPayload payload = encryptionUtils.encrypt(key);
            ProviderCredential cred = new ProviderCredential();
            cred.setProvider(p);
            cred.setEncryptedApiKey(payload.getCipherText());
            cred.setIvSalt(payload.getIv());
            cred.setKeyMasked(EncryptionUtils.maskApiKey(key));
            credentialRepository.save(cred);
        }
        return p;
    }

    private Model createModel(LlmProvider p, String id, String name, int ctx, int maxOut, BigDecimal inCost, BigDecimal outCost, boolean stream, boolean tools, boolean vision) {
        Model m = new Model();
        m.setProvider(p);
        m.setModelIdentifier(id);
        m.setDisplayName(name);
        m.setContextWindow(ctx);
        m.setMaxOutputTokens(maxOut);
        m.setInputCostPerMillion(inCost);
        m.setOutputCostPerMillion(outCost);
        m.setSupportsStreaming(stream);
        m.setSupportsTools(tools);
        m.setSupportsVision(vision);
        m.setActive(true);
        return modelRepository.save(m);
    }

    private EvaluationMetric createMetric(Project project, String name, String code, String category, String rubric) {
        EvaluationMetric m = new EvaluationMetric();
        m.setProject(project);
        m.setName(name);
        m.setCode(code);
        m.setCategory(category);
        m.setScoringType("NUMERIC");
        m.setMinScore(BigDecimal.ZERO);
        m.setMaxScore(BigDecimal.ONE);
        m.setPassingThreshold(BigDecimal.valueOf(0.70));
        m.setSystem(true);
        m = metricRepository.save(m);

        EvaluationRubric r = new EvaluationRubric();
        r.setMetric(m);
        r.setVersion(1);
        r.setPromptTemplate(rubric);
        rubricRepository.save(r);
        return m;
    }

    private void createDatasetItem(DatasetVersion ver, String input, String expOut, String expCtx) {
        DatasetItem item = new DatasetItem();
        item.setDatasetVersion(ver);
        item.setInputVariables(input);
        item.setExpectedOutput(expOut);
        item.setExpectedContext(expCtx);
        datasetItemRepository.save(item);
    }
}
