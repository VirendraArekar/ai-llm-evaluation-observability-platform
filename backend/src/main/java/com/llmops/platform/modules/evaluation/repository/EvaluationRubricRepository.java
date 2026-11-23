package com.llmops.platform.modules.evaluation.repository;

import com.llmops.platform.modules.evaluation.entity.EvaluationRubric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EvaluationRubricRepository extends JpaRepository<EvaluationRubric, UUID> {
    List<EvaluationRubric> findByMetricIdOrderByVersionDesc(UUID metricId);
}
