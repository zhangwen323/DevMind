package com.devmind.backend.agent.web;

import com.devmind.backend.agent.service.AgentExecutionService;
import com.devmind.backend.agenttrace.service.AgentTraceService;
import com.devmind.backend.chat.model.ChatSessionRecord;
import com.devmind.backend.chat.service.ChatSessionService;
import com.devmind.backend.common.api.ApiResponse;
import com.devmind.backend.knowledge.model.UserAccount;
import com.devmind.backend.knowledge.persistence.UserAccountMapper;
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
    private final AgentTraceService agentTraceService;
    private final ChatSessionService chatSessionService;
    private final UserAccountMapper userAccountMapper;

    public AgentController(
        AgentExecutionService agentExecutionService,
        AgentTraceService agentTraceService,
        ChatSessionService chatSessionService,
        UserAccountMapper userAccountMapper
    ) {
        this.agentExecutionService = agentExecutionService;
        this.agentTraceService = agentTraceService;
        this.chatSessionService = chatSessionService;
        this.userAccountMapper = userAccountMapper;
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
        UserAccount currentUser = userAccountMapper.findByUsername(authentication.getName());
        agentTraceService.recordTrace(
            session.getId(),
            currentUser.id(),
            result.agentName(),
            result.steps().stream()
                .map(step -> new AgentTraceService.TraceStepInput(
                    step.stepName(),
                    step.toolName(),
                    step.inputPayload(),
                    step.outputPayload(),
                    step.latencyMs(),
                    step.status(),
                    step.errorMessage()
                ))
                .toList()
        );
        return ApiResponse.success(new AgentExecutionService.AgentExecutionResult(
            session.getId(),
            result.agentName(),
            result.allowed(),
            result.responseText(),
            result.trace(),
            result.steps()
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
