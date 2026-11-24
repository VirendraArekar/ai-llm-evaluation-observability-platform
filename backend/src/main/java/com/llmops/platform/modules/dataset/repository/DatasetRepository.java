package com.llmops.platform.modules.dataset.repository;

import com.llmops.platform.modules.dataset.entity.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, UUID> {
    List<Dataset> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
