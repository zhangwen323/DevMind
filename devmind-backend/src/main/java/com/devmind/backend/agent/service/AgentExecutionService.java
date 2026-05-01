package com.devmind.backend.agent.service;

import org.springframework.stereotype.Service;

@Service
public class AgentExecutionService {

    public AgentExecutionResult execute(String taskType, String toolScope, String userInput) {
        String agentName = switch (taskType) {
            case "rag" -> "rag-agent";
            case "sql" -> "sql-agent";
            case "report" -> "report-agent";
            default -> "router-agent";
        };
        boolean allowed = toolScope.startsWith(taskType) || "router-agent".equals(agentName);
        return new AgentExecutionResult(null, agentName, allowed, "Agent handled: " + userInput, new AgentTrace("route", "SUCCESS", 5));
    }

    public record AgentExecutionResult(Long sessionId, String agentName, boolean allowed, String responseText, AgentTrace trace) {
    }

    public record AgentTrace(String stepName, String status, long latencyMs) {
    }
}
