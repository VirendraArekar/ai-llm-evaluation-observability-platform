package com.llmops.platform.modules.prompt.service;

import com.llmops.platform.common.exception.ResourceNotFoundException;
import com.llmops.platform.config.security.UserPrincipal;
import com.llmops.platform.modules.auth.entity.Project;
import com.llmops.platform.modules.auth.entity.User;
import com.llmops.platform.modules.auth.repository.ProjectRepository;
import com.llmops.platform.modules.auth.repository.UserRepository;
import com.llmops.platform.modules.prompt.dto.PromptDto;
import com.llmops.platform.modules.prompt.dto.PromptDto.*;
import com.llmops.platform.modules.prompt.entity.Prompt;
import com.llmops.platform.modules.prompt.entity.PromptVersion;
import com.llmops.platform.modules.prompt.repository.PromptRepository;
import com.llmops.platform.modules.prompt.repository.PromptVersionRepository;
import com.llmops.platform.modules.provider.client.ChatMessage;
import com.llmops.platform.modules.provider.client.ChatRequest;
import com.llmops.platform.modules.provider.client.ChatResponse;
import com.llmops.platform.modules.provider.entity.Model;
import com.llmops.platform.modules.provider.repository.ModelRepository;
import com.llmops.platform.modules.provider.service.LlmExecutionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PromptService {

    private final PromptRepository promptRepository;
    private final PromptVersionRepository versionRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ModelRepository modelRepository;
    private final LlmExecutionService executionService;

    public PromptService(PromptRepository promptRepository,
                         PromptVersionRepository versionRepository,
                         ProjectRepository projectRepository,
                         UserRepository userRepository,
                         ModelRepository modelRepository,
                         LlmExecutionService executionService) {
        this.promptRepository = promptRepository;
        this.versionRepository = versionRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.modelRepository = modelRepository;
        this.executionService = executionService;
    }

    @Transactional(readOnly = true)
    public List<PromptDto> getPromptsByProject(UUID projectId) {
        List<Prompt> list = promptRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
        if (list.isEmpty()) {
            list = promptRepository.findAll();
        }
        return list.stream()
                .map(this::mapToPromptDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PromptDto getPrompt(UUID promptId) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new ResourceNotFoundException("Prompt not found: " + promptId));
        return mapToPromptDto(prompt);
    }

    @Transactional(readOnly = true)
    public List<PromptVersionDto> getVersions(UUID promptId) {
        return versionRepository.findByPromptIdOrderByVersionNumberDesc(promptId).stream()
                .map(this::mapToVersionDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PromptDto createPrompt(UUID projectId, CreatePromptRequest request, UserPrincipal principal) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
        User user = principal != null ? userRepository.findById(principal.getId()).orElse(null) : null;

        String slug = request.getName().toLowerCase().replaceAll("[^a-z0-9]", "-") + "-" + UUID.randomUUID().toString().substring(0, 5);

        Prompt prompt = new Prompt();
        prompt.setProject(project);
        prompt.setName(request.getName());
        prompt.setSlug(slug);
        prompt.setDescription(request.getDescription());
        prompt.setTags(request.getTags());
        prompt.setActive(true);
        prompt = promptRepository.save(prompt);

        Model model = null;
        if (request.getModelIdentifier() != null) {
            model = modelRepository.findByModelIdentifier(request.getModelIdentifier()).orElse(null);
        }

        PromptVersion version = new PromptVersion();
        version.setPrompt(prompt);
        version.setVersionNumber(1);
        version.setTemplate(request.getTemplate());
        version.setSystemMessage(request.getSystemMessage());
        version.setModel(model);
        version.setTemperature(request.getTemperature() != null ? request.getTemperature() : BigDecimal.valueOf(0.70));
        version.setMaxTokens(request.getMaxTokens() != null ? request.getMaxTokens() : 2048);
        version.setChangeSummary("Initial version");
        version.setPublished(true);
        version.setCreatedBy(user);
        versionRepository.save(version);

        return mapToPromptDto(prompt);
    }

    @Transactional
    public PromptVersionDto createVersion(UUID promptId, CreateVersionRequest request, UserPrincipal principal) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new ResourceNotFoundException("Prompt not found: " + promptId));
        User user = principal != null ? userRepository.findById(principal.getId()).orElse(null) : null;

        List<PromptVersion> existing = versionRepository.findByPromptIdOrderByVersionNumberDesc(promptId);
        int nextVersionNumber = existing.isEmpty() ? 1 : existing.get(0).getVersionNumber() + 1;

        Model model = null;
        if (request.getModelIdentifier() != null) {
            model = modelRepository.findByModelIdentifier(request.getModelIdentifier()).orElse(null);
        }

        PromptVersion version = new PromptVersion();
        version.setPrompt(prompt);
        version.setVersionNumber(nextVersionNumber);
        version.setTemplate(request.getTemplate());
        version.setSystemMessage(request.getSystemMessage());
        version.setModel(model);
        version.setTemperature(request.getTemperature() != null ? request.getTemperature() : BigDecimal.valueOf(0.70));
        version.setMaxTokens(request.getMaxTokens() != null ? request.getMaxTokens() : 2048);
        version.setChangeSummary(request.getChangeSummary() != null ? request.getChangeSummary() : "Version " + nextVersionNumber);
        version.setPublished(false);
        version.setCreatedBy(user);
        version = versionRepository.save(version);

        return mapToVersionDto(version);
    }

    @Transactional
    public void publishVersion(UUID promptId, int versionNumber) {
        List<PromptVersion> versions = versionRepository.findByPromptIdOrderByVersionNumberDesc(promptId);
        for (PromptVersion v : versions) {
            if (v.getVersionNumber() == versionNumber) {
                v.setPublished(true);
            } else if (v.isPublished()) {
                v.setPublished(false);
            }
            versionRepository.save(v);
        }
    }

    @Transactional
    public PromptVersionDto rollbackToVersion(UUID promptId, int versionNumber, UserPrincipal principal) {
        PromptVersion target = versionRepository.findByPromptIdAndVersionNumber(promptId, versionNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Version not found: " + versionNumber));

        CreateVersionRequest req = new CreateVersionRequest();
        req.setTemplate(target.getTemplate());
        req.setSystemMessage(target.getSystemMessage());
        req.setModelIdentifier(target.getModel() != null ? target.getModel().getModelIdentifier() : null);
        req.setTemperature(target.getTemperature());
        req.setMaxTokens(target.getMaxTokens());
        req.setChangeSummary("Rollback to version " + versionNumber);

        PromptVersionDto newVer = createVersion(promptId, req, principal);
        publishVersion(promptId, newVer.getVersionNumber());
        newVer.setPublished(true);
        return newVer;
    }

    @Transactional(readOnly = true)
    public PromptExecutionResponse executePrompt(UUID promptId, Integer versionNumber, PromptExecutionRequest request) {
        PromptVersion version;
        if (versionNumber != null) {
            version = versionRepository.findByPromptIdAndVersionNumber(promptId, versionNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("Version not found: " + versionNumber));
        } else {
            version = versionRepository.findByPromptIdAndPublishedTrue(promptId)
                    .orElseGet(() -> versionRepository.findByPromptIdOrderByVersionNumberDesc(promptId).stream()
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("No versions found for prompt: " + promptId)));
        }

        String rendered = renderTemplate(version.getTemplate(), request.getVariables());
        String modelName = request.getModelOverride() != null ? request.getModelOverride() :
                (version.getModel() != null ? version.getModel().getModelIdentifier() : "gpt-4o-mini");

        ChatRequest chatReq = new ChatRequest();
        chatReq.setModel(modelName);
        chatReq.setTemperature(request.getTemperatureOverride() != null ? request.getTemperatureOverride() : version.getTemperature().doubleValue());
        chatReq.setMaxTokens(version.getMaxTokens());

        List<ChatMessage> messages = new ArrayList<>();
        if (version.getSystemMessage() != null && !version.getSystemMessage().isBlank()) {
            messages.add(ChatMessage.system(version.getSystemMessage()));
        }
        messages.add(ChatMessage.user(rendered));
        chatReq.setMessages(messages);

        ChatResponse response = executionService.execute(chatReq, "gpt-4o-mini");

        PromptExecutionResponse execResp = new PromptExecutionResponse();
        execResp.setRenderedPrompt(rendered);
        execResp.setOutput(response.getContent());
        execResp.setTokens(response.getTotalTokens());
        execResp.setLatencyMs(response.getLatencyMs());
        execResp.setEstimatedCost(response.getEstimatedCost());
        execResp.setModelUsed(response.getModel());
        return execResp;
    }

    public static String renderTemplate(String template, Map<String, String> variables) {
        if (template == null) return "";
        if (variables == null || variables.isEmpty()) return template;

        String rendered = template;
        Pattern pattern = Pattern.compile("\\{\\{([a-zA-Z0-9_]+)\\}\\}");
        Matcher matcher = pattern.matcher(template);
        while (matcher.find()) {
            String key = matcher.group(1);
            if (variables.containsKey(key)) {
                rendered = rendered.replace("{{" + key + "}}", variables.get(key));
            }
        }
        return rendered;
    }

    private PromptDto mapToPromptDto(Prompt p) {
        PromptDto dto = new PromptDto();
        dto.setId(p.getId());
        dto.setProjectId(p.getProject().getId());
        dto.setName(p.getName());
        dto.setSlug(p.getSlug());
        dto.setDescription(p.getDescription());
        dto.setTags(p.getTags());
        dto.setActive(p.isActive());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());

        List<PromptVersion> versions = versionRepository.findByPromptIdOrderByVersionNumberDesc(p.getId());
        dto.setLatestVersion(versions.isEmpty() ? 0 : versions.get(0).getVersionNumber());
        versions.stream().filter(PromptVersion::isPublished).findFirst().ifPresent(pub -> {
            dto.setPublishedVersion(mapToVersionDto(pub));
        });
        return dto;
    }

    private PromptVersionDto mapToVersionDto(PromptVersion v) {
        PromptVersionDto dto = new PromptVersionDto();
        dto.setId(v.getId());
        dto.setPromptId(v.getPrompt().getId());
        dto.setVersionNumber(v.getVersionNumber());
        dto.setTemplate(v.getTemplate());
        dto.setSystemMessage(v.getSystemMessage());
        dto.setVariablesSchema(v.getVariablesSchema());
        if (v.getModel() != null) {
            dto.setModelId(v.getModel().getId());
            dto.setModelIdentifier(v.getModel().getModelIdentifier());
        }
        dto.setTemperature(v.getTemperature());
        dto.setMaxTokens(v.getMaxTokens());
        dto.setChangeSummary(v.getChangeSummary());
        dto.setPublished(v.isPublished());
        dto.setCreatedAt(v.getCreatedAt());
        if (v.getCreatedBy() != null) {
            dto.setCreatedByName(v.getCreatedBy().getFullName());
        }
        return dto;
    }
}
