package com.devmind.backend.chat.model;

import java.util.List;

public record ChatSessionDetail(
    long id,
    Long knowledgeBaseId,
    String title,
    String sessionType,
    String contextType,
    String contextId,
    List<ChatMessageView> messages
) {
}
