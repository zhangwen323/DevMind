package com.devmind.backend.agent.model;

import java.util.List;

public record CodeAnalysisResult(
    String language,
    String overview,
    List<String> potentialIssues,
    List<String> optimizationSuggestions,
    List<String> generatedNotes
) {
}
