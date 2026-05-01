package com.devmind.backend.modelconfig.service;

import com.devmind.backend.common.exception.BusinessException;
import com.devmind.backend.common.exception.ErrorCode;
import com.devmind.backend.knowledge.model.UserAccount;
import com.devmind.backend.knowledge.persistence.UserAccountMapper;
import com.devmind.backend.modelconfig.model.EffectiveModelConfigurationOverview;
import com.devmind.backend.modelconfig.model.ModelConfigAuditEventRecord;
import com.devmind.backend.modelconfig.model.ModelProviderConfigRecord;
import com.devmind.backend.modelconfig.model.ModelProviderConfigView;
import com.devmind.backend.modelconfig.model.ResolvedModelProviderConfig;
import com.devmind.backend.modelconfig.persistence.ModelConfigAuditEventMapper;
import com.devmind.backend.modelconfig.persistence.ModelProviderConfigMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ModelConfigurationService {

    private final ModelProviderConfigMapper configMapper;
    private final ModelConfigAuditEventMapper auditEventMapper;
    private final UserAccountMapper userAccountMapper;
    private final ObjectMapper objectMapper;

    public ModelConfigurationService(
        ModelProviderConfigMapper configMapper,
        ModelConfigAuditEventMapper auditEventMapper,
        UserAccountMapper userAccountMapper,
        ObjectMapper objectMapper
    ) {
        this.configMapper = configMapper;
        this.auditEventMapper = auditEventMapper;
        this.userAccountMapper = userAccountMapper;
        this.objectMapper = objectMapper;
    }

    public EffectiveModelConfigurationOverview overview(Authentication authentication) {
        UserAccount actor = requireAdmin(authentication);
        ModelProviderConfigRecord defaultChat = configMapper.findDefaultChat();
        ModelProviderConfigRecord defaultEmbedding = configMapper.findDefaultEmbedding();
        return new EffectiveModelConfigurationOverview(
            toDefaultView(defaultChat, true),
            toDefaultView(defaultEmbedding, false),
            configMapper.countEnabled()
        );
    }

    public List<ModelProviderConfigView> list(Authentication authentication) {
        requireAdmin(authentication);
        return configMapper.findAll().stream().map(this::toView).toList();
    }

    @Transactional
    public ModelProviderConfigView create(Authentication authentication, CreateOrUpdateModelConfig command) {
        UserAccount actor = requireAdmin(authentication);
        ModelProviderConfigRecord record = fromCommand(new ModelProviderConfigRecord(), command);
        record.setEnabled(command.enabled());
        record.setDefaultChat(false);
        record.setDefaultEmbedding(false);
        configMapper.insert(record);
        audit(actor.id(), record.getId(), "CREATE", Map.of("providerCode", record.getProviderCode()));
        return toView(configMapper.findById(record.getId()));
    }

    @Transactional
    public ModelProviderConfigView update(Authentication authentication, long id, CreateOrUpdateModelConfig command) {
        UserAccount actor = requireAdmin(authentication);
        ModelProviderConfigRecord existing = requireConfig(id);
        fromCommand(existing, command);
        configMapper.update(existing);
        if (existing.isDefaultChat() || existing.isDefaultEmbedding()) {
            if (!existing.isEnabled()) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "default provider must remain enabled");
            }
        }
        audit(actor.id(), existing.getId(), "UPDATE", Map.of("providerCode", existing.getProviderCode()));
        return toView(configMapper.findById(existing.getId()));
    }

    @Transactional
    public ModelProviderConfigView setEnabled(Authentication authentication, long id, boolean enabled) {
        UserAccount actor = requireAdmin(authentication);
        ModelProviderConfigRecord existing = requireConfig(id);
        if (!enabled && (existing.isDefaultChat() || existing.isDefaultEmbedding())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "move default assignment before disabling provider");
        }
        configMapper.updateEnabled(id, enabled);
        audit(actor.id(), id, enabled ? "ENABLE" : "DISABLE", Map.of("enabled", enabled));
        return toView(configMapper.findById(id));
    }

    @Transactional
    public ModelProviderConfigView setDefaultChat(Authentication authentication, long id) {
        UserAccount actor = requireAdmin(authentication);
        ModelProviderConfigRecord existing = requireEnabledConfig(id);
        configMapper.clearDefaultChat();
        configMapper.setDefaultChat(id);
        audit(actor.id(), id, "SET_DEFAULT_CHAT", Map.of("providerCode", existing.getProviderCode()));
        return toView(configMapper.findById(id));
    }

    @Transactional
    public ModelProviderConfigView setDefaultEmbedding(Authentication authentication, long id) {
        UserAccount actor = requireAdmin(authentication);
        ModelProviderConfigRecord existing = requireEnabledConfig(id);
        configMapper.clearDefaultEmbedding();
        configMapper.setDefaultEmbedding(id);
        audit(actor.id(), id, "SET_DEFAULT_EMBEDDING", Map.of("providerCode", existing.getProviderCode()));
        return toView(configMapper.findById(id));
    }

    public ResolvedModelProviderConfig resolveDefaultChatProvider() {
        ModelProviderConfigRecord record = requireEnabledDefault(configMapper.findDefaultChat(), "default chat provider");
        return new ResolvedModelProviderConfig(
            record.getId(),
            record.getProviderCode(),
            record.getBaseUrl(),
            resolveSecret(record.getApiKeyEnvName()),
            record.getChatModel(),
            record.getTimeoutMs(),
            record.getTemperature()
        );
    }

    public ResolvedModelProviderConfig resolveDefaultEmbeddingProvider() {
        ModelProviderConfigRecord record = requireEnabledDefault(configMapper.findDefaultEmbedding(), "default embedding provider");
        return new ResolvedModelProviderConfig(
            record.getId(),
            record.getProviderCode(),
            record.getBaseUrl(),
            resolveSecret(record.getApiKeyEnvName()),
            record.getEmbeddingModel(),
            record.getTimeoutMs(),
            record.getTemperature()
        );
    }

    private String resolveSecret(String apiKeyEnvName) {
        String value = System.getenv(apiKeyEnvName);
        if (value == null || value.isBlank()) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "provider secret is missing from environment");
        }
        return value;
    }

    private ModelProviderConfigRecord requireEnabledDefault(ModelProviderConfigRecord record, String label) {
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, label + " is not configured");
        }
        if (!record.isEnabled()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, label + " must be enabled");
        }
        return record;
    }

    private EffectiveModelConfigurationOverview.ModelDefaultView toDefaultView(ModelProviderConfigRecord record, boolean chat) {
        if (record == null) {
            return null;
        }
        return new EffectiveModelConfigurationOverview.ModelDefaultView(
            record.getProviderCode(),
            record.getDisplayName(),
            chat ? record.getChatModel() : record.getEmbeddingModel(),
            record.getBaseUrl(),
            record.getApiKeyEnvName()
        );
    }

    private ModelProviderConfigView toView(ModelProviderConfigRecord record) {
        return new ModelProviderConfigView(
            record.getId(),
            record.getProviderCode(),
            record.getDisplayName(),
            record.getBaseUrl(),
            record.getApiKeyEnvName(),
            record.getChatModel(),
            record.getEmbeddingModel(),
            record.getTimeoutMs(),
            record.getTemperature(),
            record.getNotes(),
            record.isEnabled(),
            record.isDefaultChat(),
            record.isDefaultEmbedding(),
            record.getUpdatedAt()
        );
    }

    private ModelProviderConfigRecord fromCommand(ModelProviderConfigRecord target, CreateOrUpdateModelConfig command) {
        target.setProviderCode(requireText(command.providerCode(), "provider code"));
        target.setDisplayName(requireText(command.displayName(), "display name"));
        target.setBaseUrl(blankToNull(command.baseUrl()));
        target.setApiKeyEnvName(requireText(command.apiKeyEnvName(), "api key environment name"));
        target.setChatModel(requireText(command.chatModel(), "chat model"));
        target.setEmbeddingModel(requireText(command.embeddingModel(), "embedding model"));
        target.setTimeoutMs(command.timeoutMs() == null ? 30000 : Math.max(command.timeoutMs(), 1000));
        target.setTemperature(command.temperature() == null ? new BigDecimal("0.20") : command.temperature());
        target.setNotes(blankToNull(command.notes()));
        target.setEnabled(command.enabled());
        return target;
    }

    private ModelProviderConfigRecord requireConfig(long id) {
        ModelProviderConfigRecord record = configMapper.findById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "model provider configuration not found");
        }
        return record;
    }

    private ModelProviderConfigRecord requireEnabledConfig(long id) {
        ModelProviderConfigRecord record = requireConfig(id);
        if (!record.isEnabled()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "only enabled providers can be assigned as default");
        }
        return record;
    }

    private UserAccount requireAdmin(Authentication authentication) {
        UserAccount user = userAccountMapper.findByUsername(authentication.getName());
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "user not found");
        }
        boolean admin = authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch("ROLE_SYSTEM_ADMIN"::equals);
        if (!admin) {
            throw new AccessDeniedException("forbidden");
        }
        return user;
    }

    private void audit(long actorUserId, Long providerConfigId, String actionCode, Map<String, Object> details) {
        ModelConfigAuditEventRecord audit = new ModelConfigAuditEventRecord();
        audit.setActorUserId(actorUserId);
        audit.setProviderConfigId(providerConfigId);
        audit.setActionCode(actionCode);
        audit.setOutcome("SUCCESS");
        audit.setDetailsJson(serialize(details));
        auditEventMapper.insert(audit);
    }

    private String serialize(Map<String, Object> details) {
        try {
            return objectMapper.writeValueAsString(details);
        } catch (JsonProcessingException ex) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "failed to serialize audit details");
        }
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, fieldName + " is required");
        }
        return value.trim();
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public record CreateOrUpdateModelConfig(
        String providerCode,
        String displayName,
        String baseUrl,
        String apiKeyEnvName,
        String chatModel,
        String embeddingModel,
        Integer timeoutMs,
        BigDecimal temperature,
        String notes,
        boolean enabled
    ) {
    }
}
