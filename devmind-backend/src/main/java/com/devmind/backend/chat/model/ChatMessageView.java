package com.devmind.backend.chat.model;

import java.time.LocalDateTime;

public record ChatMessageView(
    long id,
    String roleCode,
    String content,
    String agentName,
    LocalDateTime createdAt
) {
}
