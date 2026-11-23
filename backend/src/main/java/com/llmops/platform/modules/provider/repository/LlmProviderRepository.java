package com.llmops.platform.modules.provider.repository;

import com.llmops.platform.modules.provider.entity.LlmProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LlmProviderRepository extends JpaRepository<LlmProvider, UUID> {
    List<LlmProvider> findByProjectId(UUID projectId);
    Optional<LlmProvider> findByProjectIdAndDefaultProviderTrue(UUID projectId);
}
