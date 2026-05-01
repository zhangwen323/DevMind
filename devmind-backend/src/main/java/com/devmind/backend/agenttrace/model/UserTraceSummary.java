package com.devmind.backend.agenttrace.model;

import java.time.LocalDateTime;
import java.util.List;

public record UserTraceSummary(
    String traceKey,
    String agentName,
    String status,
    long totalLatencyMs,
    LocalDateTime startedAt,
    List<UserTraceStepSummary> steps
) {
}
