package com.llmops.platform.modules.feedback.repository;

import com.llmops.platform.modules.feedback.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    Page<Feedback> findByProjectIdOrderByCreatedAtDesc(UUID projectId, Pageable pageable);
    List<Feedback> findByTraceId(String traceId);
    long countByProjectId(UUID projectId);
    long countByProjectIdAndRating(UUID projectId, String rating);
}
