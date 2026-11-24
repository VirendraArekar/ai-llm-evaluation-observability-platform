package com.llmops.platform.modules.recommendation.repository;

import com.llmops.platform.modules.recommendation.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {
    List<Recommendation> findByProjectId(UUID projectId);
    List<Recommendation> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    List<Recommendation> findByProjectIdAndStatusOrderByCreatedAtDesc(UUID projectId, String status);
}
