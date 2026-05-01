package com.devmind.backend.modelconfig.model;

import java.math.BigDecimal;

public record ResolvedModelProviderConfig(
    long id,
    String providerCode,
    String baseUrl,
    String apiKey,
    String model,
    int timeoutMs,
    BigDecimal temperature
) {
}
