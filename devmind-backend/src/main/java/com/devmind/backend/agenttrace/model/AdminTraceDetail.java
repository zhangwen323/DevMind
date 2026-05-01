package com.devmind.backend.agenttrace.model;

import java.time.LocalDateTime;
import java.util.List;

public record AdminTraceDetail(
    String traceKey,
    Long sessionId,
    Long userId,
    String username,
    String sessionTitle,
    String agentName,
    String status,
    long totalLatencyMs,
    LocalDateTime startedAt,
    List<AdminTraceStep> steps
) {
}
