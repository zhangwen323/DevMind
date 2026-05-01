package com.devmind.backend.rag.web;

import com.devmind.backend.common.api.ApiResponse;
import com.devmind.backend.chat.model.ChatSessionRecord;
import com.devmind.backend.chat.service.ChatSessionService;
import com.devmind.backend.rag.service.RagQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagQueryService ragQueryService;
    private final ChatSessionService chatSessionService;

    public RagController(RagQueryService ragQueryService, ChatSessionService chatSessionService) {
        this.ragQueryService = ragQueryService;
        this.chatSessionService = chatSessionService;
    }

    @PostMapping("/ask")
    public ApiResponse<RagQueryService.RagAnswer> ask(Authentication authentication, @Valid @RequestBody RagRequest request) {
        RagQueryService.RagAnswer answer = ragQueryService.ask(request.knowledgeBaseId(), request.question());
        ChatSessionRecord session = chatSessionService.appendRagConversation(
            authentication,
            request.sessionId(),
            request.knowledgeBaseId(),
            request.question(),
            answer.answer()
        );
        return ApiResponse.success(new RagQueryService.RagAnswer(
            session.getId(),
            answer.knowledgeBaseId(),
            answer.answer(),
            answer.references()
        ));
    }

    public record RagRequest(Long sessionId, @Min(1) long knowledgeBaseId, @NotBlank String question) {
    }
}
