package com.llmops.platform.modules.prompt.repository;

import com.llmops.platform.modules.prompt.entity.PromptVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromptVersionRepository extends JpaRepository<PromptVersion, UUID> {
    List<PromptVersion> findByPromptIdOrderByVersionNumberDesc(UUID promptId);
    Optional<PromptVersion> findByPromptIdAndVersionNumber(UUID promptId, int versionNumber);
    Optional<PromptVersion> findByPromptIdAndPublishedTrue(UUID promptId);
}
