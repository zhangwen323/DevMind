package com.devmind.backend.report.service;

import com.devmind.backend.agent.model.CodeAnalysisResult;
import com.devmind.backend.agent.service.AgentExecutionService;
import com.devmind.backend.agenttrace.service.AgentTraceService;
import com.devmind.backend.chat.model.ChatSessionRecord;
import com.devmind.backend.chat.service.ChatSessionService;
import com.devmind.backend.common.exception.BusinessException;
import com.devmind.backend.common.exception.ErrorCode;
import com.devmind.backend.knowledge.model.DocumentSummary;
import com.devmind.backend.knowledge.model.KnowledgeBaseRecord;
import com.devmind.backend.knowledge.model.PageResult;
import com.devmind.backend.knowledge.model.UserAccount;
import com.devmind.backend.knowledge.persistence.KnowledgeBaseMapper;
import com.devmind.backend.knowledge.persistence.KnowledgeBasePermissionMapper;
import com.devmind.backend.knowledge.persistence.UserAccountMapper;
import com.devmind.backend.report.model.ReportCitation;
import com.devmind.backend.report.model.ReportDetail;
import com.devmind.backend.report.model.ReportGenerationResult;
import com.devmind.backend.report.model.ReportRecord;
import com.devmind.backend.report.model.ReportSummary;
import com.devmind.backend.report.persistence.ReportMapper;
import com.devmind.backend.report.persistence.ReportSourceDocumentMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReportService {

    private static final Set<String> SUPPORTED_REPORT_TYPES = Set.of(
        "daily-summary",
        "weekly-summary",
        "project-summary",
        "technical-solution-draft"
    );

    private final UserAccountMapper userAccountMapper;
    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final KnowledgeBasePermissionMapper permissionMapper;
    private final ReportMapper reportMapper;
    private final ReportSourceDocumentMapper reportSourceDocumentMapper;
    private final ReportGenerationService reportGenerationService;
    private final AgentExecutionService agentExecutionService;
    private final AgentTraceService agentTraceService;
    private final ChatSessionService chatSessionService;
    private final ObjectMapper objectMapper;

    public ReportService(
        UserAccountMapper userAccountMapper,
        KnowledgeBaseMapper knowledgeBaseMapper,
        KnowledgeBasePermissionMapper permissionMapper,
        ReportMapper reportMapper,
        ReportSourceDocumentMapper reportSourceDocumentMapper,
        ReportGenerationService reportGenerationService,
        AgentExecutionService agentExecutionService,
        AgentTraceService agentTraceService,
        ChatSessionService chatSessionService,
        ObjectMapper objectMapper
    ) {
        this.userAccountMapper = userAccountMapper;
        this.knowledgeBaseMapper = knowledgeBaseMapper;
        this.permissionMapper = permissionMapper;
        this.reportMapper = reportMapper;
        this.reportSourceDocumentMapper = reportSourceDocumentMapper;
        this.reportGenerationService = reportGenerationService;
        this.agentExecutionService = agentExecutionService;
        this.agentTraceService = agentTraceService;
        this.chatSessionService = chatSessionService;
        this.objectMapper = objectMapper;
    }

    public PageResult<ReportSummary> list(Authentication authentication, String reportType, int page, int size) {
        UserAccount currentUser = requireUser(authentication);
        boolean admin = isAdmin(authentication);
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int offset = (safePage - 1) * safeSize;
        List<ReportSummary> items = reportMapper.findVisiblePage(currentUser.id(), admin, normalizeReportType(reportType), safeSize, offset);
        long total = reportMapper.countVisible(currentUser.id(), admin, normalizeReportType(reportType));
        return new PageResult<>(items, total, safePage, safeSize);
    }

    public ReportDetail getDetail(Authentication authentication, long reportId) {
        UserAccount currentUser = requireUser(authentication);
        ReportMapper.ReportRow row = reportMapper.findVisibleById(reportId, currentUser.id(), isAdmin(authentication));
        if (row == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "report not found");
        }
        return toDetail(row);
    }

    @Transactional
    public GeneratedReport create(
        Authentication authentication,
        Long sessionId,
        long knowledgeBaseId,
        List<Long> documentIds,
        String reportType,
        String guidance
    ) {
        UserAccount currentUser = requireUser(authentication);
        KnowledgeBaseRecord knowledgeBase = requireVisibleKnowledgeBase(authentication, knowledgeBaseId);
        String normalizedReportType = requireSupportedReportType(reportType);
        List<Long> normalizedDocumentIds = normalizeDocumentIds(documentIds);
        List<DocumentSummary> documents = reportSourceDocumentMapper.findCompletedByKnowledgeBaseAndIds(knowledgeBaseId, normalizedDocumentIds);
        if (documents.size() != normalizedDocumentIds.size()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "report documents must be completed and scoped to the selected knowledge base");
        }

        ReportGenerationResult generated = reportGenerationService.generate(
            knowledgeBase.getName(),
            normalizedReportType,
            guidance,
            documents
        );
        AgentExecutionService.AgentExecutionResult execution = agentExecutionService.execute(
            "report-generation",
            "report-generation:write",
            generated.title()
        );

        ChatSessionRecord session = chatSessionService.appendAgentConversation(
            authentication,
            sessionId,
            knowledgeBaseId,
            "REPORT_GENERATION",
            normalizedReportType,
            renderUserRequest(normalizedReportType, documents, guidance),
            generated.content(),
            execution.agentName()
        );

        String traceKey = agentTraceService.recordTrace(
            session.getId(),
            currentUser.id(),
            execution.agentName(),
            execution.steps().stream()
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

        ReportRecord record = new ReportRecord();
        record.setTitle(generated.title());
        record.setReportType(normalizedReportType);
        record.setKnowledgeBaseId(knowledgeBaseId);
        record.setSessionId(session.getId());
        record.setTraceKey(traceKey);
        record.setGuidance(guidance);
        record.setContent(generated.content());
        record.setCitationsJson(serializeCitations(generated.citations()));
        record.setCreatedBy(currentUser.id());
        reportMapper.insert(record);

        for (Long documentId : normalizedDocumentIds) {
            reportSourceDocumentMapper.insert(record.getId(), documentId);
        }

        ReportDetail detail = getDetail(authentication, record.getId());
        return new GeneratedReport(detail, session.getId(), execution.agentName());
    }

    private ReportDetail toDetail(ReportMapper.ReportRow row) {
        List<DocumentSummary> documents = reportSourceDocumentMapper.findByReportId(row.id());
        return new ReportDetail(
            row.id(),
            row.title(),
            row.reportType(),
            row.knowledgeBaseId(),
            row.knowledgeBaseName(),
            row.createdByUsername(),
            row.sessionId(),
            row.traceKey(),
            row.guidance(),
            row.content(),
            parseCitations(row.citationsJson()),
            documents,
            row.createdAt()
        );
    }

    private String renderUserRequest(String reportType, List<DocumentSummary> documents, String guidance) {
        String documentList = documents.stream()
            .map(DocumentSummary::fileName)
            .reduce((left, right) -> left + ", " + right)
            .orElse("no documents");
        if (guidance == null || guidance.isBlank()) {
            return "Generate a " + reportType + " using: " + documentList;
        }
        return "Generate a " + reportType + " using: " + documentList + ". Guidance: " + guidance.trim();
    }

    private List<Long> normalizeDocumentIds(List<Long> documentIds) {
        if (documentIds == null || documentIds.isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "at least one document is required");
        }
        Set<Long> deduplicated = new LinkedHashSet<>();
        for (Long documentId : documentIds) {
            if (documentId == null || documentId < 1) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "document id is invalid");
            }
            deduplicated.add(documentId);
        }
        return List.copyOf(deduplicated);
    }

    private String requireSupportedReportType(String reportType) {
        String normalized = normalizeReportType(reportType);
        if (normalized == null || !SUPPORTED_REPORT_TYPES.contains(normalized)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "unsupported report type");
        }
        return normalized;
    }

    private String normalizeReportType(String reportType) {
        if (reportType == null || reportType.isBlank()) {
            return null;
        }
        return reportType.trim();
    }

    private KnowledgeBaseRecord requireVisibleKnowledgeBase(Authentication authentication, long knowledgeBaseId) {
        KnowledgeBaseRecord knowledgeBase = knowledgeBaseMapper.findActiveById(knowledgeBaseId);
        if (knowledgeBase == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "knowledge base not found");
        }
        UserAccount currentUser = requireUser(authentication);
        if (isAdmin(authentication)) {
            return knowledgeBase;
        }
        if (knowledgeBase.getOwnerUserId().equals(currentUser.id())) {
            return knowledgeBase;
        }
        if (permissionMapper.countPermission(knowledgeBaseId, currentUser.id()) > 0) {
            return knowledgeBase;
        }
        throw new AccessDeniedException("forbidden");
    }

    private UserAccount requireUser(Authentication authentication) {
        UserAccount user = userAccountMapper.findByUsername(authentication.getName());
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "user not found");
        }
        return user;
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch("ROLE_SYSTEM_ADMIN"::equals);
    }

    private String serializeCitations(List<ReportCitation> citations) {
        try {
            return objectMapper.writeValueAsString(citations);
        } catch (JsonProcessingException ex) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "failed to serialize report citations");
        }
    }

    private List<ReportCitation> parseCitations(String citationsJson) {
        try {
            return objectMapper.readValue(citationsJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException ex) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "failed to parse report citations");
        }
    }

    public record GeneratedReport(
        ReportDetail report,
        long sessionId,
        String agentName
    ) {
    }
}
