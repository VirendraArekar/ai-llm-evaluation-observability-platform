package com.llmops.platform.modules.provider.repository;

import com.llmops.platform.modules.provider.entity.ProviderCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderCredentialRepository extends JpaRepository<ProviderCredential, UUID> {
    Optional<ProviderCredential> findByProviderId(UUID providerId);
}
