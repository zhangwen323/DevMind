package com.devmind.backend.agenttrace.model;

import java.time.LocalDateTime;

public record AdminTraceListItem(
    String traceKey,
    Long sessionId,
    Long userId,
    String username,
    String sessionTitle,
    String agentName,
    String status,
    int stepCount,
    long totalLatencyMs,
    LocalDateTime startedAt
) {
}
