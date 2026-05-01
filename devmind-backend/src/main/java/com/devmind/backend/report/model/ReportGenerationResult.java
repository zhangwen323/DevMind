package com.devmind.backend.report.model;

import java.util.List;

public record ReportGenerationResult(
    String title,
    String reportType,
    String content,
    List<ReportCitation> citations
) {
}
