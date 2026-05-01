package com.devmind.backend.modelconfig.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ModelProviderConfigView(
    long id,
    String providerCode,
    String displayName,
    String baseUrl,
    String apiKeyEnvName,
    String chatModel,
    String embeddingModel,
    int timeoutMs,
    BigDecimal temperature,
    String notes,
    boolean enabled,
    boolean defaultChat,
    boolean defaultEmbedding,
    LocalDateTime updatedAt
) {
}
