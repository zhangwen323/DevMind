package com.devmind.backend.report.model;

public record ReportCitation(
    long documentId,
    String documentName,
    String excerpt
) {
}
