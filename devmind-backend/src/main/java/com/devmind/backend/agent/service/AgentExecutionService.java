package com.devmind.backend.agent.service;

import org.springframework.stereotype.Service;

@Service
public class AgentExecutionService {

    public AgentExecutionResult execute(String taskType, String toolScope) {
        String agentName = switch (taskType) {
            case "rag" -> "rag-agent";
            case "sql" -> "sql-agent";
            default -> "router-agent";
        };
        boolean allowed = toolScope.startsWith(taskType) || "router-agent".equals(agentName);
        return new AgentExecutionResult(agentName, allowed, new AgentTrace("route", "SUCCESS", 5));
    }

    public record AgentExecutionResult(String agentName, boolean allowed, AgentTrace trace) {
    }

    public record AgentTrace(String stepName, String status, long latencyMs) {
    }
}
