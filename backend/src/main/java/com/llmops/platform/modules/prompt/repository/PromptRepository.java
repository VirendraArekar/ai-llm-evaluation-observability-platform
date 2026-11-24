package com.llmops.platform.modules.prompt.repository;

import com.llmops.platform.modules.prompt.entity.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromptRepository extends JpaRepository<Prompt, UUID> {
    List<Prompt> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    Optional<Prompt> findByProjectIdAndSlug(UUID projectId, String slug);
}
