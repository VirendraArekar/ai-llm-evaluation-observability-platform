package com.llmops.platform.modules.experiment.repository;

import com.llmops.platform.modules.experiment.entity.ExperimentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExperimentResultRepository extends JpaRepository<ExperimentResult, UUID> {
    List<ExperimentResult> findByExperimentRunId(UUID experimentRunId);
}
