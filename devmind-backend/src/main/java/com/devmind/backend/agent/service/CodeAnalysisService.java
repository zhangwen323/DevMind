package com.devmind.backend.agent.service;

import com.devmind.backend.agent.model.CodeAnalysisResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class CodeAnalysisService {

    public CodeAnalysisResult analyze(String language, String analysisType, String question, String codeSnippet) {
        String resolvedLanguage = resolveLanguage(language, codeSnippet);
        String focus = question == null || question.isBlank()
            ? "general maintainability and correctness"
            : question.trim();
        String normalizedType = analysisType == null || analysisType.isBlank()
            ? "GENERAL"
            : analysisType.trim().toUpperCase(Locale.ROOT);

        return new CodeAnalysisResult(
            resolvedLanguage,
            "This " + resolvedLanguage + " snippet was reviewed with focus on " + focus + " under " + normalizedType + " analysis.",
            List.of(issueFor(resolvedLanguage, codeSnippet)),
            List.of(optimizationFor(resolvedLanguage, normalizedType)),
            List.of(noteFor(resolvedLanguage, normalizedType))
        );
    }

    private String resolveLanguage(String language, String codeSnippet) {
        if (language != null && !language.isBlank()) {
            return language.trim().toLowerCase(Locale.ROOT);
        }
        String source = codeSnippet == null ? "" : codeSnippet.toLowerCase(Locale.ROOT);
        if (source.contains("select ") || source.contains(" from ")) {
            return "sql";
        }
        if (source.contains("function ") || source.contains("const ") || source.contains("=>")) {
            return "javascript";
        }
        if (source.contains("public ") || source.contains("class ") || source.contains("return ")) {
            return "java";
        }
        return "plaintext";
    }

    private String issueFor(String language, String codeSnippet) {
        String lower = codeSnippet == null ? "" : codeSnippet.toLowerCase(Locale.ROOT);
        if ("java".equals(language) && lower.contains(".getname()")) {
            return "Possible NullPointerException when dereferencing user before validating it.";
        }
        if ("sql".equals(language) && lower.contains("select *")) {
            return "Broad column selection may fetch unnecessary data and hide intent.";
        }
        return "Review boundary conditions and missing validation paths for this snippet.";
    }

    private String optimizationFor(String language, String analysisType) {
        if ("COMMENTS".equals(analysisType)) {
            return "Add intent-revealing comments at the entry point and around non-obvious branches.";
        }
        if ("sql".equals(language)) {
            return "Project only the required columns and document expected filters explicitly.";
        }
        return "Reduce implicit assumptions and make edge-case handling explicit for maintainers.";
    }

    private String noteFor(String language, String analysisType) {
        if ("EXPLAIN".equals(analysisType)) {
            return "Document the high-level responsibility of the snippet before the implementation details.";
        }
        return "Keep " + language + " naming and guard clauses aligned with the surrounding module conventions.";
    }
}
