package com.devmind.backend.knowledge.model;

import java.util.List;

public record KnowledgeBaseDetail(
    long id,
    String name,
    String description,
    String visibility,
    String ownerUsername,
    boolean deleted,
    List<DocumentSummary> documents
) {
}
