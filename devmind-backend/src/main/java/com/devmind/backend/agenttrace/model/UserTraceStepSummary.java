package com.devmind.backend.agenttrace.model;

public record UserTraceStepSummary(
    String stepName,
    String toolName,
    String status,
    long latencyMs,
    String summary
) {
}
