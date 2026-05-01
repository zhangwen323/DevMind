package com.devmind.backend.chat.web;

import com.devmind.backend.chat.model.ChatSessionDetail;
import com.devmind.backend.chat.model.ChatSessionSummary;
import com.devmind.backend.chat.service.ChatSessionService;
import com.devmind.backend.common.api.ApiResponse;
import com.devmind.backend.knowledge.model.PageResult;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat/sessions")
public class ChatSessionController {

    private final ChatSessionService chatSessionService;

    public ChatSessionController(ChatSessionService chatSessionService) {
        this.chatSessionService = chatSessionService;
    }

    @GetMapping
    public ApiResponse<PageResult<ChatSessionSummary>> list(
        Authentication authentication,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String sessionType,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success(chatSessionService.list(authentication, keyword, sessionType, page, size));
    }

    @GetMapping("/{sessionId}")
    public ApiResponse<ChatSessionDetail> detail(Authentication authentication, @PathVariable long sessionId) {
        return ApiResponse.success(chatSessionService.getDetail(authentication, sessionId));
    }

    @DeleteMapping("/{sessionId}")
    public ApiResponse<Void> delete(Authentication authentication, @PathVariable long sessionId) {
        chatSessionService.delete(authentication, sessionId);
        return ApiResponse.success(null);
    }
}
