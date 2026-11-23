package com.llmops.platform.modules.auth.controller;

import com.llmops.platform.common.dto.ApiResponse;
import com.llmops.platform.modules.auth.dto.AuthDto.OrganizationDto;
import com.llmops.platform.modules.auth.dto.AuthDto.WorkspaceDto;
import com.llmops.platform.modules.auth.service.WorkspaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping("/organizations")
    public ResponseEntity<ApiResponse<List<OrganizationDto>>> getOrganizations() {
        return ResponseEntity.ok(ApiResponse.ok(workspaceService.getAllOrganizations()));
    }

    @GetMapping("/organizations/{orgId}/workspaces")
    public ResponseEntity<ApiResponse<List<WorkspaceDto>>> getWorkspaces(@PathVariable UUID orgId) {
        return ResponseEntity.ok(ApiResponse.ok(workspaceService.getWorkspacesByOrganization(orgId)));
    }
}
