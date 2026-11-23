package com.llmops.platform.modules.tracing.repository;

import com.llmops.platform.modules.tracing.entity.Span;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpanRepository extends JpaRepository<Span, String> {
    List<Span> findByTraceIdOrderByStartTimeAsc(String traceId);
}
