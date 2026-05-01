package com.devmind.backend.agenttrace.model;

import java.time.LocalDateTime;

public record AdminTraceStep(
    String stepName,
    String toolName,
    String inputPayload,
    String outputPayload,
    long latencyMs,
    String status,
    String errorMessage,
    LocalDateTime createdAt
) {
}
