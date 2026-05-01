package com.devmind.backend.agent.service;

import org.springframework.stereotype.Service;

import java.util.List;

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
        String status = allowed ? "SUCCESS" : "FAILED";
        String errorMessage = allowed ? null : "tool scope not allowed";
        List<AgentTraceStep> steps = List.of(
            new AgentTraceStep(
                "route",
                null,
                "{\"taskType\":\"" + taskType + "\",\"toolScope\":\"" + toolScope + "\"}",
                "{\"decision\":\"" + agentName + "\"}",
                5L,
                status,
                errorMessage
            ),
            new AgentTraceStep(
                "tool-execute",
                toolNameFor(taskType),
                "{\"userInput\":\"" + userInput + "\"}",
                allowed ? "{\"result\":\"handled\"}" : null,
                12L,
                status,
                errorMessage
            )
        );
        return new AgentExecutionResult(
            null,
            agentName,
            allowed,
            "Agent handled: " + userInput,
            new AgentTrace("route", status, 5),
            steps
        );
    }

    private String toolNameFor(String taskType) {
        return switch (taskType) {
            case "rag" -> "knowledge-search";
            case "sql" -> "sql-read";
            case "report" -> "report-writer";
            default -> "agent-tool";
        };
    }

    public record AgentExecutionResult(
        Long sessionId,
        String agentName,
        boolean allowed,
        String responseText,
        AgentTrace trace,
        List<AgentTraceStep> steps
    ) {
    }

    public record AgentTrace(String stepName, String status, long latencyMs) {
    }

    public record AgentTraceStep(
        String stepName,
        String toolName,
        String inputPayload,
        String outputPayload,
        long latencyMs,
        String status,
        String errorMessage
    ) {
    }
}
