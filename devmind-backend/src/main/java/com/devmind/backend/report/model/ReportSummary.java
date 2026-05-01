package com.devmind.backend.report.model;

import java.time.LocalDateTime;

public record ReportSummary(
    long id,
    String title,
    String reportType,
    long knowledgeBaseId,
    String knowledgeBaseName,
    String createdByUsername,
    LocalDateTime createdAt
) {
}
