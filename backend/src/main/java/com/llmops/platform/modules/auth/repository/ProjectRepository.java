package com.llmops.platform.modules.auth.repository;

import com.llmops.platform.modules.auth.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByWorkspaceId(UUID workspaceId);
    Optional<Project> findByWorkspaceIdAndSlug(UUID workspaceId, String slug);
}
