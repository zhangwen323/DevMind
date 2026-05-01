package com.devmind.backend.agent.web;

import com.devmind.backend.agent.service.AgentExecutionService;
import com.devmind.backend.chat.model.ChatSessionRecord;
import com.devmind.backend.chat.service.ChatSessionService;
import com.devmind.backend.common.api.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final AgentExecutionService agentExecutionService;
    private final ChatSessionService chatSessionService;

    public AgentController(AgentExecutionService agentExecutionService, ChatSessionService chatSessionService) {
        this.agentExecutionService = agentExecutionService;
        this.chatSessionService = chatSessionService;
    }

    @PostMapping("/execute")
    public ApiResponse<AgentExecutionService.AgentExecutionResult> execute(Authentication authentication, @Valid @RequestBody AgentRequest request) {
        AgentExecutionService.AgentExecutionResult result = agentExecutionService.execute(
            request.taskType(),
            request.toolScope(),
            request.userInput()
        );
        ChatSessionRecord session = chatSessionService.appendAgentConversation(
            authentication,
            request.sessionId(),
            request.knowledgeBaseId(),
            request.contextType(),
            request.contextId(),
            request.userInput(),
            result.responseText(),
            result.agentName()
        );
        return ApiResponse.success(new AgentExecutionService.AgentExecutionResult(
            session.getId(),
            result.agentName(),
            result.allowed(),
            result.responseText(),
            result.trace()
        ));
    }

    public record AgentRequest(
        Long sessionId,
        Long knowledgeBaseId,
        String contextType,
        String contextId,
        @NotBlank String taskType,
        @NotBlank String userInput,
        @NotBlank String toolScope
    ) {
    }
}
