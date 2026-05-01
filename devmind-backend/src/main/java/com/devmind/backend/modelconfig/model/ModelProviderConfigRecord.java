package com.devmind.backend.modelconfig.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ModelProviderConfigRecord {

    private Long id;
    private String providerCode;
    private String displayName;
    private String baseUrl;
    private String apiKeyEnvName;
    private String chatModel;
    private String embeddingModel;
    private Integer timeoutMs;
    private BigDecimal temperature;
    private String notes;
    private boolean enabled;
    private boolean defaultChat;
    private boolean defaultEmbedding;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKeyEnvName() {
        return apiKeyEnvName;
    }

    public void setApiKeyEnvName(String apiKeyEnvName) {
        this.apiKeyEnvName = apiKeyEnvName;
    }

    public String getChatModel() {
        return chatModel;
    }

    public void setChatModel(String chatModel) {
        this.chatModel = chatModel;
    }

    public String getEmbeddingModel() {
        return embeddingModel;
    }

    public void setEmbeddingModel(String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public Integer getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDefaultChat() {
        return defaultChat;
    }

    public void setDefaultChat(boolean defaultChat) {
        this.defaultChat = defaultChat;
    }

    public boolean isDefaultEmbedding() {
        return defaultEmbedding;
    }

    public void setDefaultEmbedding(boolean defaultEmbedding) {
        this.defaultEmbedding = defaultEmbedding;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
