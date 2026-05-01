package com.devmind.backend.report.model;

import com.devmind.backend.knowledge.model.DocumentSummary;

import java.time.LocalDateTime;
import java.util.List;

public record ReportDetail(
    long id,
    String title,
    String reportType,
    long knowledgeBaseId,
    String knowledgeBaseName,
    String createdByUsername,
    Long sessionId,
    String traceKey,
    String guidance,
    String content,
    List<ReportCitation> citations,
    List<DocumentSummary> documents,
    LocalDateTime createdAt
) {
}
