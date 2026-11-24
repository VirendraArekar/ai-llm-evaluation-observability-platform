package com.llmops.platform.modules.dataset.repository;

import com.llmops.platform.modules.dataset.entity.DatasetItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DatasetItemRepository extends JpaRepository<DatasetItem, UUID> {
    List<DatasetItem> findByDatasetVersionId(UUID datasetVersionId);
    Page<DatasetItem> findByDatasetVersionId(UUID datasetVersionId, Pageable pageable);
    long countByDatasetVersionId(UUID datasetVersionId);
}
