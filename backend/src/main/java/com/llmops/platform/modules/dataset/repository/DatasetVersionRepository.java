package com.llmops.platform.modules.dataset.repository;

import com.llmops.platform.modules.dataset.entity.DatasetVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DatasetVersionRepository extends JpaRepository<DatasetVersion, UUID> {
    List<DatasetVersion> findByDatasetIdOrderByVersionNumberDesc(UUID datasetId);
    Optional<DatasetVersion> findByDatasetIdAndVersionNumber(UUID datasetId, int versionNumber);
}
