package com.llmops.platform.modules.benchmark.repository;

import com.llmops.platform.modules.benchmark.entity.ModelComparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModelComparisonRepository extends JpaRepository<ModelComparison, UUID> {
    List<ModelComparison> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
