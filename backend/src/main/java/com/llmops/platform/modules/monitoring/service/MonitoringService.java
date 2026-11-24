package com.llmops.platform.modules.monitoring.service;

import com.llmops.platform.common.exception.ResourceNotFoundException;
import com.llmops.platform.modules.auth.entity.Project;
import com.llmops.platform.modules.auth.repository.ProjectRepository;
import com.llmops.platform.modules.monitoring.dto.MonitoringDto.*;
import com.llmops.platform.modules.monitoring.entity.Alert;
import com.llmops.platform.modules.monitoring.entity.Notification;
import com.llmops.platform.modules.monitoring.repository.AlertRepository;
import com.llmops.platform.modules.monitoring.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MonitoringService {

    private final AlertRepository alertRepository;
    private final NotificationRepository notificationRepository;
    private final ProjectRepository projectRepository;

    public MonitoringService(AlertRepository alertRepository,
                             NotificationRepository notificationRepository,
                             ProjectRepository projectRepository) {
        this.alertRepository = alertRepository;
        this.notificationRepository = notificationRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<AlertDto> getAlerts(UUID projectId) {
        return alertRepository.findByProjectIdOrderByCreatedAtDesc(projectId).stream()
                .map(this::mapToAlertDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AlertDto createAlert(UUID projectId, CreateAlertRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        Alert alert = new Alert();
        alert.setProject(project);
        alert.setName(request.getName());
        alert.setAlertType(request.getAlertType());
        alert.setThresholdValue(request.getThresholdValue());
        alert.setTimeWindowMinutes(request.getTimeWindowMinutes());
        alert.setSeverity(request.getSeverity() != null ? request.getSeverity() : "WARNING");
        alert.setEnabled(true);
        alert = alertRepository.save(alert);

        return mapToAlertDto(alert);
    }

    @Transactional
    public void toggleAlert(UUID alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found: " + alertId));
        alert.setEnabled(!alert.isEnabled());
        alertRepository.save(alert);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotifications() {
        return notificationRepository.findTop20ByOrderByCreatedAtDesc().stream()
                .map(this::mapToNotificationDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markNotificationAsRead(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    private AlertDto mapToAlertDto(Alert a) {
        AlertDto dto = new AlertDto();
        dto.setId(a.getId());
        dto.setProjectId(a.getProject().getId());
        dto.setName(a.getName());
        dto.setAlertType(a.getAlertType());
        dto.setThresholdValue(a.getThresholdValue());
        dto.setTimeWindowMinutes(a.getTimeWindowMinutes());
        dto.setEnabled(a.isEnabled());
        dto.setSeverity(a.getSeverity());
        dto.setLastTriggeredAt(a.getLastTriggeredAt());
        dto.setCreatedAt(a.getCreatedAt());
        return dto;
    }

    private NotificationDto mapToNotificationDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setType(n.getType());
        dto.setRead(n.isRead());
        dto.setLinkUrl(n.getLinkUrl());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}
