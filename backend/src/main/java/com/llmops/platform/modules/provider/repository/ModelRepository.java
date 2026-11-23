package com.llmops.platform.modules.provider.repository;

import com.llmops.platform.modules.provider.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModelRepository extends JpaRepository<Model, UUID> {
    List<Model> findByProviderId(UUID providerId);
    Optional<Model> findByModelIdentifier(String modelIdentifier);
    List<Model> findByProviderProjectId(UUID projectId);
}
