package com.llmops.platform.modules.monitoring.controller;

import com.llmops.platform.common.dto.ApiResponse;
import com.llmops.platform.modules.monitoring.dto.MonitoringDto.*;
import com.llmops.platform.modules.monitoring.service.MonitoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/monitoring")
public class MonitoringController {

    private final MonitoringService monitoringService;

    public MonitoringController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<AlertDto>>> getAlerts(@RequestParam UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok(monitoringService.getAlerts(projectId)));
    }

    @PostMapping("/alerts")
    public ResponseEntity<ApiResponse<AlertDto>> createAlert(
            @RequestParam UUID projectId,
            @RequestBody CreateAlertRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Alert created", monitoringService.createAlert(projectId, request)));
    }

    @PatchMapping("/alerts/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleAlert(@PathVariable UUID id) {
        monitoringService.toggleAlert(id);
        return ResponseEntity.ok(ApiResponse.ok("Alert toggled", null));
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getNotifications() {
        return ResponseEntity.ok(ApiResponse.ok(monitoringService.getNotifications()));
    }

    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID id) {
        monitoringService.markNotificationAsRead(id);
        return ResponseEntity.ok(ApiResponse.ok("Notification marked as read", null));
    }
}
