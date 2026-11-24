package com.llmops.platform.modules.monitoring.repository;

import com.llmops.platform.modules.monitoring.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
    List<Alert> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
