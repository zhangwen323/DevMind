package com.devmind.backend.agenttrace.service;

import com.devmind.backend.agenttrace.model.AdminTraceDetail;
import com.devmind.backend.agenttrace.model.AdminTraceListItem;
import com.devmind.backend.agenttrace.model.AdminTraceStep;
import com.devmind.backend.agenttrace.model.AgentTraceRecord;
import com.devmind.backend.agenttrace.model.UserTraceStepSummary;
import com.devmind.backend.agenttrace.model.UserTraceSummary;
import com.devmind.backend.agenttrace.persistence.AgentTraceMapper;
import com.devmind.backend.chat.model.ChatSessionRecord;
import com.devmind.backend.chat.persistence.ChatSessionMapper;
import com.devmind.backend.common.exception.BusinessException;
import com.devmind.backend.common.exception.ErrorCode;
import com.devmind.backend.knowledge.model.PageResult;
import com.devmind.backend.knowledge.model.UserAccount;
import com.devmind.backend.knowledge.persistence.UserAccountMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AgentTraceService {

    private final AgentTraceMapper agentTraceMapper;
    private final ChatSessionMapper chatSessionMapper;
    private final UserAccountMapper userAccountMapper;

    public AgentTraceService(
        AgentTraceMapper agentTraceMapper,
        ChatSessionMapper chatSessionMapper,
        UserAccountMapper userAccountMapper
    ) {
        this.agentTraceMapper = agentTraceMapper;
        this.chatSessionMapper = chatSessionMapper;
        this.userAccountMapper = userAccountMapper;
    }

    public PageResult<AdminTraceListItem> listAdmin(Authentication authentication, String status, String agentName, String keyword, int page, int size) {
        requireAdmin(authentication);
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int offset = (safePage - 1) * safeSize;
        List<AdminTraceListItem> items = agentTraceMapper.findAdminPage(normalize(status), normalize(agentName), emptyToNull(keyword), safeSize, offset);
        long total = agentTraceMapper.countAdminPage(normalize(status), normalize(agentName), emptyToNull(keyword));
        return new PageResult<>(items, total, safePage, safeSize);
    }

    public AdminTraceDetail getAdminDetail(Authentication authentication, String traceKey) {
        requireAdmin(authentication);
        List<AgentTraceMapper.AgentTraceRow> rows = agentTraceMapper.findRowsByTraceKey(traceKey);
        if (rows.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "trace not found");
        }
        return toAdminDetail(rows);
    }

    public List<UserTraceSummary> listSessionSummaries(Authentication authentication, long sessionId) {
        UserAccount user = requireUser(authentication);
        requireOwnedSession(user.id(), sessionId);
        List<AgentTraceMapper.AgentTraceRow> rows = agentTraceMapper.findRowsBySessionId(sessionId);
        return groupUserSummaries(rows);
    }

    public UserTraceSummary getSessionSummary(Authentication authentication, long sessionId, String traceKey) {
        UserAccount user = requireUser(authentication);
        requireOwnedSession(user.id(), sessionId);
        List<AgentTraceMapper.AgentTraceRow> rows = agentTraceMapper.findRowsByTraceKey(traceKey);
        if (rows.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "trace not found");
        }
        if (!sessionIdEquals(rows.get(0).sessionId(), sessionId)) {
            throw new AccessDeniedException("forbidden");
        }
        if (!sessionIdEquals(rows.get(0).userId(), user.id())) {
            throw new AccessDeniedException("forbidden");
        }
        return toUserSummary(rows);
    }

    @Transactional
    public String recordTrace(long sessionId, long userId, String agentName, List<TraceStepInput> steps) {
        String traceKey = UUID.randomUUID().toString();
        for (TraceStepInput step : steps) {
            AgentTraceRecord record = new AgentTraceRecord();
            record.setTraceKey(traceKey);
            record.setSessionId(sessionId);
            record.setUserId(userId);
            record.setAgentName(agentName);
            record.setStepName(step.stepName());
            record.setToolName(step.toolName());
            record.setInputPayload(step.inputPayload());
            record.setOutputPayload(step.outputPayload());
            record.setLatencyMs(step.latencyMs());
            record.setStatus(step.status());
            record.setErrorMessage(step.errorMessage());
            agentTraceMapper.insert(record);
        }
        return traceKey;
    }

    private AdminTraceDetail toAdminDetail(List<AgentTraceMapper.AgentTraceRow> rows) {
        AgentTraceMapper.AgentTraceRow first = rows.get(0);
        List<AdminTraceStep> steps = rows.stream()
            .map(row -> new AdminTraceStep(
                row.stepName(),
                row.toolName(),
                row.inputPayload(),
                row.outputPayload(),
                row.latencyMs() == null ? 0L : row.latencyMs(),
                row.status(),
                row.errorMessage(),
                row.createdAt()
            ))
            .toList();
        return new AdminTraceDetail(
            first.traceKey(),
            first.sessionId(),
            first.userId(),
            first.username(),
            first.sessionTitle(),
            first.agentName(),
            aggregateStatus(rows),
            totalLatency(rows),
            first.createdAt(),
            steps
        );
    }

    private List<UserTraceSummary> groupUserSummaries(List<AgentTraceMapper.AgentTraceRow> rows) {
        Map<String, List<AgentTraceMapper.AgentTraceRow>> grouped = new LinkedHashMap<>();
        for (AgentTraceMapper.AgentTraceRow row : rows) {
            grouped.computeIfAbsent(row.traceKey(), ignored -> new ArrayList<>()).add(row);
        }
        return grouped.values().stream().map(this::toUserSummary).toList();
    }

    private UserTraceSummary toUserSummary(List<AgentTraceMapper.AgentTraceRow> rows) {
        AgentTraceMapper.AgentTraceRow first = rows.get(0);
        List<UserTraceStepSummary> steps = rows.stream()
            .map(row -> new UserTraceStepSummary(
                row.stepName(),
                row.toolName(),
                row.status(),
                row.latencyMs() == null ? 0L : row.latencyMs(),
                summarize(row)
            ))
            .toList();
        return new UserTraceSummary(
            first.traceKey(),
            first.agentName(),
            aggregateStatus(rows),
            totalLatency(rows),
            first.createdAt(),
            steps
        );
    }

    private String summarize(AgentTraceMapper.AgentTraceRow row) {
        if ("FAILED".equalsIgnoreCase(row.status())) {
            if (row.toolName() != null && !row.toolName().isBlank()) {
                return "Tool " + row.toolName() + " failed: " + safeError(row.errorMessage()) + ".";
            }
            return "Step " + row.stepName() + " failed: " + safeError(row.errorMessage()) + ".";
        }
        if (row.toolName() != null && !row.toolName().isBlank()) {
            return "Tool " + row.toolName() + " completed successfully.";
        }
        return "Step " + row.stepName() + " completed successfully.";
    }

    private String safeError(String errorMessage) {
        if (errorMessage == null || errorMessage.isBlank()) {
            return "execution error";
        }
        return errorMessage;
    }

    private long totalLatency(List<AgentTraceMapper.AgentTraceRow> rows) {
        return rows.stream()
            .map(AgentTraceMapper.AgentTraceRow::latencyMs)
            .filter(java.util.Objects::nonNull)
            .mapToLong(Long::longValue)
            .sum();
    }

    private String aggregateStatus(List<AgentTraceMapper.AgentTraceRow> rows) {
        return rows.stream().anyMatch(row -> "FAILED".equalsIgnoreCase(row.status())) ? "FAILED" : "SUCCESS";
    }

    private ChatSessionRecord requireOwnedSession(long userId, long sessionId) {
        ChatSessionRecord session = chatSessionMapper.findOwnedById(sessionId, userId);
        if (session == null) {
            throw new AccessDeniedException("forbidden");
        }
        return session;
    }

    private UserAccount requireUser(Authentication authentication) {
        UserAccount user = userAccountMapper.findByUsername(authentication.getName());
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "user not found");
        }
        return user;
    }

    private void requireAdmin(Authentication authentication) {
        requireUser(authentication);
        boolean admin = authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch("ROLE_SYSTEM_ADMIN"::equals);
        if (!admin) {
            throw new AccessDeniedException("forbidden");
        }
    }

    private boolean sessionIdEquals(Long value, long expected) {
        return value != null && value == expected;
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public record TraceStepInput(
        String stepName,
        String toolName,
        String inputPayload,
        String outputPayload,
        Long latencyMs,
        String status,
        String errorMessage
    ) {
    }
}
