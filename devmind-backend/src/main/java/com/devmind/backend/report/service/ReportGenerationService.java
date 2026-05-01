package com.devmind.backend.report.service;

import com.devmind.backend.knowledge.model.DocumentSummary;
import com.devmind.backend.report.model.ReportCitation;
import com.devmind.backend.report.model.ReportGenerationResult;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportGenerationService {

    public ReportGenerationResult generate(
        String knowledgeBaseName,
        String reportType,
        String guidance,
        List<DocumentSummary> documents
    ) {
        String title = titleOf(reportType, knowledgeBaseName);
        List<ReportCitation> citations = documents.stream()
            .map(document -> new ReportCitation(
                document.id(),
                document.fileName(),
                "Derived from " + document.fileName() + " in " + knowledgeBaseName + "."
            ))
            .toList();

        String sourceList = documents.stream()
            .map(DocumentSummary::fileName)
            .map("- "::concat)
            .reduce((left, right) -> left + "\n" + right)
            .orElse("- No source documents");

        String content = """
            # %s

            ## Overview
            This %s is generated from the selected knowledge-base documents in %s.

            ## Source Coverage
            %s

            ## Key Findings
            The current document set highlights active implementation context, delivery details, and constraints that should be reflected in the report.

            ## Recommendations
            Prioritize the referenced materials when refining follow-up actions and keep the report aligned with the selected document scope.

            ## Guidance Notes
            %s
            """.formatted(
            title,
            humanReportType(reportType),
            knowledgeBaseName,
            sourceList,
            guidance == null || guidance.isBlank() ? "No additional guidance supplied." : guidance.trim()
        ).trim();

        return new ReportGenerationResult(title, reportType, content, citations);
    }

    private String titleOf(String reportType, String knowledgeBaseName) {
        return switch (reportType) {
            case "daily-summary" -> "Daily Summary - " + knowledgeBaseName + " - " + today();
            case "weekly-summary" -> "Weekly Summary - " + knowledgeBaseName + " - " + today();
            case "project-summary" -> "Project Summary - " + knowledgeBaseName;
            case "technical-solution-draft" -> "Technical Solution Draft - " + knowledgeBaseName;
            default -> "Report - " + knowledgeBaseName;
        };
    }

    private String humanReportType(String reportType) {
        return switch (reportType) {
            case "daily-summary" -> "daily summary";
            case "weekly-summary" -> "weekly summary";
            case "project-summary" -> "project summary";
            case "technical-solution-draft" -> "technical solution draft";
            default -> "report";
        };
    }

    private String today() {
        return LocalDate.now().format(DateTimeFormatter.ISO_DATE);
    }
}
