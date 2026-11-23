package com.llmops.platform.modules.auth.service;

import com.llmops.platform.common.exception.ResourceNotFoundException;
import com.llmops.platform.modules.auth.dto.AuthDto.OrganizationDto;
import com.llmops.platform.modules.auth.dto.AuthDto.WorkspaceDto;
import com.llmops.platform.modules.auth.entity.Organization;
import com.llmops.platform.modules.auth.entity.Workspace;
import com.llmops.platform.modules.auth.repository.OrganizationRepository;
import com.llmops.platform.modules.auth.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkspaceService {

    private final OrganizationRepository organizationRepository;
    private final WorkspaceRepository workspaceRepository;

    public WorkspaceService(OrganizationRepository organizationRepository,
                            WorkspaceRepository workspaceRepository) {
        this.organizationRepository = organizationRepository;
        this.workspaceRepository = workspaceRepository;
    }

    @Transactional(readOnly = true)
    public List<OrganizationDto> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                .map(this::mapToOrgDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WorkspaceDto> getWorkspacesByOrganization(UUID organizationId) {
        return workspaceRepository.findByOrganizationId(organizationId).stream()
                .map(this::mapToWorkspaceDto)
                .collect(Collectors.toList());
    }

    private OrganizationDto mapToOrgDto(Organization org) {
        OrganizationDto dto = new OrganizationDto();
        dto.setId(org.getId());
        dto.setName(org.getName());
        dto.setSlug(org.getSlug());
        dto.setPlan(org.getPlan());
        dto.setCreatedAt(org.getCreatedAt());
        return dto;
    }

    private WorkspaceDto mapToWorkspaceDto(Workspace ws) {
        WorkspaceDto dto = new WorkspaceDto();
        dto.setId(ws.getId());
        dto.setOrganizationId(ws.getOrganization().getId());
        dto.setName(ws.getName());
        dto.setSlug(ws.getSlug());
        dto.setDescription(ws.getDescription());
        dto.setCreatedAt(ws.getCreatedAt());
        return dto;
    }
}
