package com.llmops.platform.modules.auth.service;

import com.llmops.platform.common.exception.ResourceNotFoundException;
import com.llmops.platform.config.security.ApiKeyAuthenticationFilter;
import com.llmops.platform.config.security.UserPrincipal;
import com.llmops.platform.modules.auth.dto.AuthDto.*;
import com.llmops.platform.modules.auth.entity.ApiKey;
import com.llmops.platform.modules.auth.entity.Project;
import com.llmops.platform.modules.auth.entity.User;
import com.llmops.platform.modules.auth.repository.ApiKeyRepository;
import com.llmops.platform.modules.auth.repository.ProjectRepository;
import com.llmops.platform.modules.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository,
                          ApiKeyRepository apiKeyRepository,
                          UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.apiKeyRepository = apiKeyRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getProjectsByWorkspace(UUID workspaceId) {
        return projectRepository.findByWorkspaceId(workspaceId).stream()
                .map(this::mapToProjectDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectDto getProject(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
        return mapToProjectDto(project);
    }

    @Transactional
    public ApiKeyDto createApiKey(UUID projectId, CreateApiKeyRequest request, UserPrincipal currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        byte[] randomBytes = new byte[24];
        new SecureRandom().nextBytes(randomBytes);
        String randomSuffix = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        String rawKey = "ak_live_" + randomSuffix;
        String prefix = rawKey.substring(0, 12);
        String keyHash = ApiKeyAuthenticationFilter.hashKey(rawKey);

        Instant expiresAt = request.getExpiresInDays() != null && request.getExpiresInDays() > 0
                ? Instant.now().plus(request.getExpiresInDays(), ChronoUnit.DAYS)
                : null;

        ApiKey apiKey = new ApiKey();
        apiKey.setProject(project);
        apiKey.setUser(user);
        apiKey.setName(request.getName());
        apiKey.setKeyPrefix(prefix);
        apiKey.setKeyHash(keyHash);
        apiKey.setScopes(request.getScopes() != null ? request.getScopes() : "traces:write,evaluations:read");
        apiKey.setExpiresAt(expiresAt);
        apiKey.setActive(true);
        apiKey = apiKeyRepository.save(apiKey);

        ApiKeyDto dto = mapToApiKeyDto(apiKey);
        dto.setRawKey(rawKey);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<ApiKeyDto> getProjectApiKeys(UUID projectId) {
        return apiKeyRepository.findByProjectId(projectId).stream()
                .map(this::mapToApiKeyDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void revokeApiKey(UUID apiKeyId) {
        ApiKey apiKey = apiKeyRepository.findById(apiKeyId)
                .orElseThrow(() -> new ResourceNotFoundException("API Key not found: " + apiKeyId));
        apiKey.setActive(false);
        apiKeyRepository.save(apiKey);
    }

    private ProjectDto mapToProjectDto(Project project) {
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setWorkspaceId(project.getWorkspace().getId());
        dto.setName(project.getName());
        dto.setSlug(project.getSlug());
        dto.setDescription(project.getDescription());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setTotalTraces(0);
        dto.setTotalEvaluations(0);
        return dto;
    }

    private ApiKeyDto mapToApiKeyDto(ApiKey apiKey) {
        ApiKeyDto dto = new ApiKeyDto();
        dto.setId(apiKey.getId());
        dto.setProjectId(apiKey.getProject().getId());
        dto.setName(apiKey.getName());
        dto.setKeyPrefix(apiKey.getKeyPrefix());
        dto.setScopes(apiKey.getScopes());
        dto.setLastUsedAt(apiKey.getLastUsedAt());
        dto.setExpiresAt(apiKey.getExpiresAt());
        dto.setActive(apiKey.isActive());
        dto.setCreatedAt(apiKey.getCreatedAt());
        return dto;
    }
}
