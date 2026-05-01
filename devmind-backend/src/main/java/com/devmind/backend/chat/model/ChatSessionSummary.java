package com.devmind.backend.chat.model;

import java.time.LocalDateTime;

public record ChatSessionSummary(
    long id,
    Long knowledgeBaseId,
    String title,
    String sessionType,
    String contextType,
    String contextId,
    LocalDateTime updatedAt
) {
}
