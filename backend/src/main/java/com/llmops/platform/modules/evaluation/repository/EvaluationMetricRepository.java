package com.llmops.platform.modules.evaluation.repository;

import com.llmops.platform.modules.evaluation.entity.EvaluationMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EvaluationMetricRepository extends JpaRepository<EvaluationMetric, UUID> {
    List<EvaluationMetric> findByProjectIdOrSystemTrue(UUID projectId);
    Optional<EvaluationMetric> findByCode(String code);
}
