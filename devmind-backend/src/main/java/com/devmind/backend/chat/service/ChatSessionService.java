package com.devmind.backend.chat.service;

import com.devmind.backend.chat.model.ChatMessageRecord;
import com.devmind.backend.chat.model.ChatMessageView;
import com.devmind.backend.chat.model.ChatSessionDetail;
import com.devmind.backend.chat.model.ChatSessionRecord;
import com.devmind.backend.chat.model.ChatSessionSummary;
import com.devmind.backend.chat.persistence.ChatMessageMapper;
import com.devmind.backend.chat.persistence.ChatSessionMapper;
import com.devmind.backend.common.exception.BusinessException;
import com.devmind.backend.common.exception.ErrorCode;
import com.devmind.backend.knowledge.model.PageResult;
import com.devmind.backend.knowledge.model.UserAccount;
import com.devmind.backend.knowledge.persistence.UserAccountMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatSessionService {

    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final UserAccountMapper userAccountMapper;

    public ChatSessionService(
        ChatSessionMapper chatSessionMapper,
        ChatMessageMapper chatMessageMapper,
        UserAccountMapper userAccountMapper
    ) {
        this.chatSessionMapper = chatSessionMapper;
        this.chatMessageMapper = chatMessageMapper;
        this.userAccountMapper = userAccountMapper;
    }

    public PageResult<ChatSessionSummary> list(Authentication authentication, String keyword, String sessionType, int page, int size) {
        UserAccount currentUser = requireUser(authentication);
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int offset = (safePage - 1) * safeSize;
        List<ChatSessionSummary> items = chatSessionMapper.findOwnedPage(currentUser.id(), keyword, normalize(sessionType), safeSize, offset);
        long total = chatSessionMapper.countOwned(currentUser.id(), keyword, normalize(sessionType));
        return new PageResult<>(items, total, safePage, safeSize);
    }

    public ChatSessionDetail getDetail(Authentication authentication, long sessionId) {
        UserAccount currentUser = requireUser(authentication);
        ChatSessionRecord session = requireOwnedSession(currentUser.id(), sessionId);
        List<ChatMessageView> messages = chatMessageMapper.findBySessionId(sessionId);
        return new ChatSessionDetail(
            session.getId(),
            session.getKnowledgeBaseId(),
            session.getTitle(),
            session.getSessionType(),
            session.getContextType(),
            session.getContextId(),
            messages
        );
    }

    @Transactional
    public void delete(Authentication authentication, long sessionId) {
        UserAccount currentUser = requireUser(authentication);
        requireOwnedSession(currentUser.id(), sessionId);
        chatMessageMapper.deleteBySessionId(sessionId);
        chatSessionMapper.deleteOwned(sessionId, currentUser.id());
    }

    @Transactional
    public ChatSessionRecord appendRagConversation(
        Authentication authentication,
        Long sessionId,
        Long knowledgeBaseId,
        String question,
        String answer
    ) {
        if (knowledgeBaseId == null || knowledgeBaseId < 1) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "knowledge base is required");
        }
        UserAccount currentUser = requireUser(authentication);
        ChatSessionRecord session = sessionId == null
            ? createSession(currentUser.id(), titleOf(question), "RAG", knowledgeBaseId, null, null)
            : requireOwnedSession(currentUser.id(), sessionId);

        if (!"RAG".equals(session.getSessionType())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "session type mismatch");
        }
        if (!knowledgeBaseId.equals(session.getKnowledgeBaseId())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "knowledge base mismatch");
        }

        insertMessage(session.getId(), "USER", question, null);
        insertMessage(session.getId(), "ASSISTANT", answer, "rag-agent");
        chatSessionMapper.touch(session);
        return session;
    }

    @Transactional
    public ChatSessionRecord appendAgentConversation(
        Authentication authentication,
        Long sessionId,
        Long knowledgeBaseId,
        String contextType,
        String contextId,
        String userInput,
        String responseText,
        String agentName
    ) {
        UserAccount currentUser = requireUser(authentication);
        ChatSessionRecord session = sessionId == null
            ? createSession(currentUser.id(), titleOf(userInput), "AGENT", knowledgeBaseId, contextType, contextId)
            : requireOwnedSession(currentUser.id(), sessionId);

        if (!"AGENT".equals(session.getSessionType())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "session type mismatch");
        }

        insertMessage(session.getId(), "USER", userInput, null);
        insertMessage(session.getId(), "ASSISTANT", responseText, agentName);
        if (sessionId == null) {
            return session;
        }
        session.setKnowledgeBaseId(knowledgeBaseId != null ? knowledgeBaseId : session.getKnowledgeBaseId());
        session.setContextType(contextType != null ? contextType : session.getContextType());
        session.setContextId(contextId != null ? contextId : session.getContextId());
        chatSessionMapper.touch(session);
        return session;
    }

    private ChatSessionRecord createSession(
        long userId,
        String title,
        String sessionType,
        Long knowledgeBaseId,
        String contextType,
        String contextId
    ) {
        ChatSessionRecord session = new ChatSessionRecord();
        session.setUserId(userId);
        session.setTitle(title);
        session.setSessionType(sessionType);
        session.setKnowledgeBaseId(knowledgeBaseId);
        session.setContextType(contextType);
        session.setContextId(contextId);
        chatSessionMapper.insert(session);
        return requireOwnedSession(userId, session.getId());
    }

    private void insertMessage(long sessionId, String roleCode, String content, String agentName) {
        ChatMessageRecord message = new ChatMessageRecord();
        message.setSessionId(sessionId);
        message.setRoleCode(roleCode);
        message.setContent(content);
        message.setAgentName(agentName);
        chatMessageMapper.insert(message);
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

    private String titleOf(String source) {
        String normalized = source == null ? "" : source.trim();
        if (normalized.isEmpty()) {
            return "New session";
        }
        return normalized.length() > 60 ? normalized.substring(0, 60) : normalized;
    }

    private String normalize(String value) {
        if (value == null || value.isBlank() || "ALL".equalsIgnoreCase(value)) {
            return null;
        }
        return value.toUpperCase();
    }
}
