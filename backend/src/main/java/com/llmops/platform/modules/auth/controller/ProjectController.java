package com.llmops.platform.modules.auth.controller;

import com.llmops.platform.common.dto.ApiResponse;
import com.llmops.platform.config.security.UserPrincipal;
import com.llmops.platform.modules.auth.dto.AuthDto.ApiKeyDto;
import com.llmops.platform.modules.auth.dto.AuthDto.CreateApiKeyRequest;
import com.llmops.platform.modules.auth.dto.AuthDto.ProjectDto;
import com.llmops.platform.modules.auth.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDto>>> getProjects(@RequestParam UUID workspaceId) {
        return ResponseEntity.ok(ApiResponse.ok(projectService.getProjectsByWorkspace(workspaceId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDto>> getProject(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(projectService.getProject(id)));
    }

    @GetMapping("/{id}/api-keys")
    public ResponseEntity<ApiResponse<List<ApiKeyDto>>> getProjectApiKeys(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(projectService.getProjectApiKeys(id)));
    }

    @PostMapping("/{id}/api-keys")
    public ResponseEntity<ApiResponse<ApiKeyDto>> createApiKey(
            @PathVariable UUID id,
            @Valid @RequestBody CreateApiKeyRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok("API Key created successfully", projectService.createApiKey(id, request, principal)));
    }

    @DeleteMapping("/api-keys/{apiKeyId}")
    public ResponseEntity<ApiResponse<Void>> revokeApiKey(@PathVariable UUID apiKeyId) {
        projectService.revokeApiKey(apiKeyId);
        return ResponseEntity.ok(ApiResponse.ok("API Key revoked", null));
    }
}
