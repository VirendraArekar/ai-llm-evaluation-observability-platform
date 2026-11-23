package com.llmops.platform.modules.auth.controller;

import com.llmops.platform.common.dto.ApiResponse;
import com.llmops.platform.common.dto.PagedResponse;
import com.llmops.platform.modules.auth.entity.AuditLog;
import com.llmops.platform.modules.auth.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AuditLog>>> getLogs(
            @RequestParam UUID organizationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(auditLogService.getLogs(organizationId, page, size)));
    }
}
