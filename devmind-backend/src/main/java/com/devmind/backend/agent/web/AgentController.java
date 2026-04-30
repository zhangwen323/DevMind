package com.devmind.backend.agent.web;

import com.devmind.backend.agent.service.AgentExecutionService;
import com.devmind.backend.common.api.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final AgentExecutionService agentExecutionService;

    public AgentController(AgentExecutionService agentExecutionService) {
        this.agentExecutionService = agentExecutionService;
    }

    @PostMapping("/execute")
    public ApiResponse<AgentExecutionService.AgentExecutionResult> execute(@Valid @RequestBody AgentRequest request) {
        return ApiResponse.success(agentExecutionService.execute(request.taskType(), request.toolScope()));
    }

    public record AgentRequest(@NotBlank String taskType, @NotBlank String userInput, @NotBlank String toolScope) {
    }
}
