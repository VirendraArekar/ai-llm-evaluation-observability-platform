package com.llmops.platform.modules.auth.service;

import com.llmops.platform.common.dto.PagedResponse;
import com.llmops.platform.modules.auth.entity.AuditLog;
import com.llmops.platform.modules.auth.entity.Organization;
import com.llmops.platform.modules.auth.entity.User;
import com.llmops.platform.modules.auth.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Async
    @Transactional
    public void log(Organization org, User user, String action, String resourceType, String resourceId, String detailsJson, String ip) {
        AuditLog auditLog = new AuditLog();
        auditLog.setOrganization(org);
        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setResourceType(resourceType);
        auditLog.setResourceId(resourceId);
        auditLog.setDetailsJson(detailsJson);
        auditLog.setIpAddress(ip);
        auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public PagedResponse<AuditLog> getLogs(UUID orgId, int page, int size) {
        Page<AuditLog> pageResult = auditLogRepository.findByOrganizationIdOrderByCreatedAtDesc(orgId, PageRequest.of(page, size));
        return new PagedResponse<>(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isLast()
        );
    }
}
