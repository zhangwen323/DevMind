package com.devmind.backend.modelconfig.model;

public record EffectiveModelConfigurationOverview(
    ModelDefaultView chat,
    ModelDefaultView embedding,
    long enabledProviderCount
) {

    public record ModelDefaultView(
        String providerCode,
        String displayName,
        String model,
        String baseUrl,
        String apiKeyEnvName
    ) {
    }
}
