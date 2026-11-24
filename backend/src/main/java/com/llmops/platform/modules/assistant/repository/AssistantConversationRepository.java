package com.llmops.platform.modules.assistant.repository;

import com.llmops.platform.modules.assistant.entity.AssistantConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssistantConversationRepository extends JpaRepository<AssistantConversation, UUID> {
    List<AssistantConversation> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
