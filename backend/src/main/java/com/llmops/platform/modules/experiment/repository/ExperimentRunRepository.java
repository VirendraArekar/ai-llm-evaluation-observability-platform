package com.llmops.platform.modules.experiment.repository;

import com.llmops.platform.modules.experiment.entity.ExperimentRun;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExperimentRunRepository extends JpaRepository<ExperimentRun, UUID> {
    List<ExperimentRun> findByExperimentId(UUID experimentId);
    Page<ExperimentRun> findByExperimentId(UUID experimentId, Pageable pageable);
}
