package com.llmops.platform.modules.tracing.repository;

import com.llmops.platform.modules.tracing.entity.Trace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface TraceRepository extends JpaRepository<Trace, String> {

    Page<Trace> findByProjectIdOrderByStartTimeDesc(UUID projectId, Pageable pageable);
    Page<Trace> findByProjectIdAndStatusOrderByStartTimeDesc(UUID projectId, String status, Pageable pageable);
    Page<Trace> findByProjectIdAndNameContainingIgnoreCaseOrderByStartTimeDesc(UUID projectId, String name, Pageable pageable);
    Page<Trace> findByProjectIdAndStatusAndNameContainingIgnoreCaseOrderByStartTimeDesc(UUID projectId, String status, String name, Pageable pageable);

    long countByProjectId(UUID projectId);
    long countByProjectIdAndStatus(UUID projectId, String status);

    @Query("SELECT AVG(t.latencyMs) FROM Trace t WHERE t.project.id = :projectId")
    Double getAverageLatency(@Param("projectId") UUID projectId);

    @Query("SELECT SUM(t.totalTokens) FROM Trace t WHERE t.project.id = :projectId")
    Long getTotalTokens(@Param("projectId") UUID projectId);

    @Query("SELECT SUM(t.estimatedCost) FROM Trace t WHERE t.project.id = :projectId")
    BigDecimal getTotalCost(@Param("projectId") UUID projectId);

    List<Trace> findTop10ByProjectIdOrderByStartTimeDesc(UUID projectId);
}
