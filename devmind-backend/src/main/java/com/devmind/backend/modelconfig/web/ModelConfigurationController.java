package com.devmind.backend.modelconfig.web;

import com.devmind.backend.common.api.ApiResponse;
import com.devmind.backend.modelconfig.model.EffectiveModelConfigurationOverview;
import com.devmind.backend.modelconfig.model.ModelProviderConfigView;
import com.devmind.backend.modelconfig.service.ModelConfigurationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/admin/model-configurations")
public class ModelConfigurationController {

    private final ModelConfigurationService modelConfigurationService;

    public ModelConfigurationController(ModelConfigurationService modelConfigurationService) {
        this.modelConfigurationService = modelConfigurationService;
    }

    @GetMapping("/overview")
    public ApiResponse<EffectiveModelConfigurationOverview> overview(Authentication authentication) {
        return ApiResponse.success(modelConfigurationService.overview(authentication));
    }

    @GetMapping
    public ApiResponse<List<ModelProviderConfigView>> list(Authentication authentication) {
        return ApiResponse.success(modelConfigurationService.list(authentication));
    }

    @PostMapping
    public ApiResponse<ModelProviderConfigView> create(Authentication authentication, @Valid @RequestBody ModelConfigRequest request) {
        return ApiResponse.success(modelConfigurationService.create(authentication, request.toCommand()));
    }

    @PutMapping("/{configId}")
    public ApiResponse<ModelProviderConfigView> update(
        Authentication authentication,
        @PathVariable long configId,
        @Valid @RequestBody ModelConfigRequest request
    ) {
        return ApiResponse.success(modelConfigurationService.update(authentication, configId, request.toCommand()));
    }

    @PatchMapping("/{configId}/enabled")
    public ApiResponse<ModelProviderConfigView> updateEnabled(
        Authentication authentication,
        @PathVariable long configId,
        @Valid @RequestBody EnabledRequest request
    ) {
        return ApiResponse.success(modelConfigurationService.setEnabled(authentication, configId, request.enabled()));
    }

    @PatchMapping("/{configId}/default-chat")
    public ApiResponse<ModelProviderConfigView> setDefaultChat(Authentication authentication, @PathVariable long configId) {
        return ApiResponse.success(modelConfigurationService.setDefaultChat(authentication, configId));
    }

    @PatchMapping("/{configId}/default-embedding")
    public ApiResponse<ModelProviderConfigView> setDefaultEmbedding(Authentication authentication, @PathVariable long configId) {
        return ApiResponse.success(modelConfigurationService.setDefaultEmbedding(authentication, configId));
    }

    public record ModelConfigRequest(
        @NotBlank String providerCode,
        @NotBlank String displayName,
        String baseUrl,
        @NotBlank String apiKeyEnvName,
        @NotBlank String chatModel,
        @NotBlank String embeddingModel,
        @Min(1000) Integer timeoutMs,
        @DecimalMin("0.00") @DecimalMax("2.00") BigDecimal temperature,
        String notes,
        boolean enabled
    ) {
        ModelConfigurationService.CreateOrUpdateModelConfig toCommand() {
            return new ModelConfigurationService.CreateOrUpdateModelConfig(
                providerCode,
                displayName,
                baseUrl,
                apiKeyEnvName,
                chatModel,
                embeddingModel,
                timeoutMs,
                temperature,
                notes,
                enabled
            );
        }
    }

    public record EnabledRequest(boolean enabled) {
    }
}
