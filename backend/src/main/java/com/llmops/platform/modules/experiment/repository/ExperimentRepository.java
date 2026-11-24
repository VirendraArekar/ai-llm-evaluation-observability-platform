package com.llmops.platform.modules.experiment.repository;

import com.llmops.platform.modules.experiment.entity.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExperimentRepository extends JpaRepository<Experiment, UUID> {
    List<Experiment> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
