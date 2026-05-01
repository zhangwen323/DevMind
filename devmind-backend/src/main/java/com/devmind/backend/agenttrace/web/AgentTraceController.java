package com.devmind.backend.agenttrace.web;

import com.devmind.backend.agenttrace.model.AdminTraceDetail;
import com.devmind.backend.agenttrace.model.AdminTraceListItem;
import com.devmind.backend.agenttrace.model.UserTraceSummary;
import com.devmind.backend.agenttrace.service.AgentTraceService;
import com.devmind.backend.common.api.ApiResponse;
import com.devmind.backend.knowledge.model.PageResult;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AgentTraceController {

    private final AgentTraceService agentTraceService;

    public AgentTraceController(AgentTraceService agentTraceService) {
        this.agentTraceService = agentTraceService;
    }

    @GetMapping("/api/agent-traces")
    public ApiResponse<PageResult<AdminTraceListItem>> list(
        Authentication authentication,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String agentName,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success(agentTraceService.listAdmin(authentication, status, agentName, keyword, page, size));
    }

    @GetMapping("/api/agent-traces/{traceKey}")
    public ApiResponse<AdminTraceDetail> detail(Authentication authentication, @PathVariable String traceKey) {
        return ApiResponse.success(agentTraceService.getAdminDetail(authentication, traceKey));
    }

    @GetMapping("/api/chat/sessions/{sessionId}/agent-traces")
    public ApiResponse<List<UserTraceSummary>> listSessionSummaries(Authentication authentication, @PathVariable long sessionId) {
        return ApiResponse.success(agentTraceService.listSessionSummaries(authentication, sessionId));
    }

    @GetMapping("/api/chat/sessions/{sessionId}/agent-traces/{traceKey}")
    public ApiResponse<UserTraceSummary> getSessionSummary(
        Authentication authentication,
        @PathVariable long sessionId,
        @PathVariable String traceKey
    ) {
        return ApiResponse.success(agentTraceService.getSessionSummary(authentication, sessionId, traceKey));
    }
}
