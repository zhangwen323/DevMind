package com.devmind.backend.agent.web;

import com.devmind.backend.agent.model.CodeAnalysisResult;
import com.devmind.backend.agent.service.AgentExecutionService;
import com.devmind.backend.agent.service.CodeAnalysisService;
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

import java.util.List;

@RestController
@RequestMapping("/api/agents/code-analysis")
public class CodeAnalysisController {

    private final CodeAnalysisService codeAnalysisService;
    private final AgentExecutionService agentExecutionService;
    private final AgentTraceService agentTraceService;
    private final ChatSessionService chatSessionService;
    private final UserAccountMapper userAccountMapper;

    public CodeAnalysisController(
        CodeAnalysisService codeAnalysisService,
        AgentExecutionService agentExecutionService,
        AgentTraceService agentTraceService,
        ChatSessionService chatSessionService,
        UserAccountMapper userAccountMapper
    ) {
        this.codeAnalysisService = codeAnalysisService;
        this.agentExecutionService = agentExecutionService;
        this.agentTraceService = agentTraceService;
        this.chatSessionService = chatSessionService;
        this.userAccountMapper = userAccountMapper;
    }

    @PostMapping
    public ApiResponse<CodeAnalysisResponse> analyze(Authentication authentication, @Valid @RequestBody CodeAnalysisRequest request) {
        CodeAnalysisResult analysis = codeAnalysisService.analyze(
            request.language(),
            request.analysisType(),
            request.question(),
            request.codeSnippet()
        );
        AgentExecutionService.AgentExecutionResult execution = agentExecutionService.execute(
            "code-analysis",
            "code-analysis:read",
            request.codeSnippet()
        );
        ChatSessionRecord session = chatSessionService.appendAgentConversation(
            authentication,
            request.sessionId(),
            null,
            "CODE_ANALYSIS",
            analysis.language(),
            request.codeSnippet(),
            renderAssistantMessage(analysis),
            execution.agentName()
        );

        UserAccount currentUser = userAccountMapper.findByUsername(authentication.getName());
        List<AgentTraceService.TraceStepInput> traceSteps = execution.steps().stream()
            .map(step -> new AgentTraceService.TraceStepInput(
                step.stepName(),
                step.toolName(),
                step.inputPayload(),
                step.outputPayload(),
                step.latencyMs(),
                step.status(),
                step.errorMessage()
            ))
            .toList();
        agentTraceService.recordTrace(session.getId(), currentUser.id(), execution.agentName(), traceSteps);

        return ApiResponse.success(new CodeAnalysisResponse(
            session.getId(),
            execution.agentName(),
            analysis
        ));
    }

    private String renderAssistantMessage(CodeAnalysisResult analysis) {
        return """
            Overview:
            %s

            Potential issues:
            - %s

            Optimization suggestions:
            - %s

            Generated notes:
            - %s
            """.formatted(
            analysis.overview(),
            String.join("\n- ", analysis.potentialIssues()),
            String.join("\n- ", analysis.optimizationSuggestions()),
            String.join("\n- ", analysis.generatedNotes())
        ).trim();
    }

    public record CodeAnalysisRequest(
        Long sessionId,
        String language,
        String analysisType,
        String question,
        @NotBlank String codeSnippet
    ) {
    }

    public record CodeAnalysisResponse(
        Long sessionId,
        String agentName,
        CodeAnalysisResult analysis
    ) {
    }
}
