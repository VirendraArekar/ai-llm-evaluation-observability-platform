package com.llmops.platform.modules.evaluation.repository;

import com.llmops.platform.modules.evaluation.entity.Evaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, UUID> {
    Page<Evaluation> findByProjectIdOrderByCreatedAtDesc(UUID projectId, Pageable pageable);
    List<Evaluation> findByTraceId(String traceId);
    List<Evaluation> findBySpanId(String spanId);
    List<Evaluation> findByExperimentRunId(UUID experimentRunId);

    @Query("SELECT AVG(e.score) FROM Evaluation e WHERE e.project.id = :projectId")
    Double getAverageScore(@Param("projectId") UUID projectId);

    long countByProjectId(UUID projectId);
    long countByProjectIdAndPassFailTrue(UUID projectId);
    long countByProjectIdAndPassFailFalse(UUID projectId);
}
