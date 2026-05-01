package com.devmind.backend.modelconfig.model;

public class ModelConfigAuditEventRecord {

    private Long id;
    private Long actorUserId;
    private Long providerConfigId;
    private String actionCode;
    private String outcome;
    private String detailsJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(Long actorUserId) {
        this.actorUserId = actorUserId;
    }

    public Long getProviderConfigId() {
        return providerConfigId;
    }

    public void setProviderConfigId(Long providerConfigId) {
        this.providerConfigId = providerConfigId;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getDetailsJson() {
        return detailsJson;
    }

    public void setDetailsJson(String detailsJson) {
        this.detailsJson = detailsJson;
    }
}
