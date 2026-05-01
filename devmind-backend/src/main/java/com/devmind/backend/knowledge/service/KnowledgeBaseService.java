package com.devmind.backend.knowledge.service;

import com.devmind.backend.common.exception.BusinessException;
import com.devmind.backend.common.exception.ErrorCode;
import com.devmind.backend.knowledge.model.DocumentSummary;
import com.devmind.backend.knowledge.model.KnowledgeBaseDetail;
import com.devmind.backend.knowledge.model.KnowledgeBaseRecord;
import com.devmind.backend.knowledge.model.PageResult;
import com.devmind.backend.knowledge.model.UserAccount;
import com.devmind.backend.knowledge.persistence.KnowledgeBaseDocumentMapper;
import com.devmind.backend.knowledge.persistence.KnowledgeBaseMapper;
import com.devmind.backend.knowledge.persistence.KnowledgeBasePermissionMapper;
import com.devmind.backend.knowledge.persistence.UserAccountMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KnowledgeBaseService {

    private final UserAccountMapper userAccountMapper;
    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final KnowledgeBasePermissionMapper permissionMapper;
    private final KnowledgeBaseDocumentMapper documentMapper;

    public KnowledgeBaseService(
        UserAccountMapper userAccountMapper,
        KnowledgeBaseMapper knowledgeBaseMapper,
        KnowledgeBasePermissionMapper permissionMapper,
        KnowledgeBaseDocumentMapper documentMapper
    ) {
        this.userAccountMapper = userAccountMapper;
        this.knowledgeBaseMapper = knowledgeBaseMapper;
        this.permissionMapper = permissionMapper;
        this.documentMapper = documentMapper;
    }

    public PageResult<KnowledgeBaseRecord> list(Authentication authentication, String keyword, int page, int size) {
        UserAccount currentUser = requireUser(authentication);
        boolean admin = isAdmin(authentication);
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int offset = (safePage - 1) * safeSize;
        List<KnowledgeBaseRecord> items = knowledgeBaseMapper.findVisiblePage(currentUser.id(), admin, keyword, safeSize, offset);
        long total = knowledgeBaseMapper.countVisible(currentUser.id(), admin, keyword);
        return new PageResult<>(items, total, safePage, safeSize);
    }

    @Transactional
    public KnowledgeBaseRecord create(Authentication authentication, String name, String description) {
        UserAccount currentUser = requireUser(authentication);
        KnowledgeBaseRecord record = new KnowledgeBaseRecord();
        record.setName(name);
        record.setDescription(description);
        record.setOwnerUserId(currentUser.id());
        record.setOwnerUsername(currentUser.username());
        record.setVisibility("PRIVATE");
        record.setDeleted(false);
        knowledgeBaseMapper.insert(record);
        permissionMapper.insert(record.getId(), currentUser.id());
        return knowledgeBaseMapper.findActiveById(record.getId());
    }

    @Transactional
    public KnowledgeBaseRecord update(Authentication authentication, long knowledgeBaseId, String name, String description) {
        KnowledgeBaseRecord knowledgeBase = requireActiveKnowledgeBase(knowledgeBaseId);
        UserAccount currentUser = requireUser(authentication);
        if (!isAdmin(authentication) && !knowledgeBase.getOwnerUserId().equals(currentUser.id())) {
            throw new AccessDeniedException("forbidden");
        }

        knowledgeBase.setName(name);
        knowledgeBase.setDescription(description);
        knowledgeBaseMapper.update(knowledgeBase);
        return knowledgeBaseMapper.findActiveById(knowledgeBaseId);
    }

    public KnowledgeBaseDetail getDetail(Authentication authentication, long knowledgeBaseId) {
        KnowledgeBaseRecord knowledgeBase = requireVisibleKnowledgeBase(authentication, knowledgeBaseId);
        List<DocumentSummary> documents = documentMapper.findByKnowledgeBaseId(knowledgeBaseId);
        return new KnowledgeBaseDetail(
            knowledgeBase.getId(),
            knowledgeBase.getName(),
            knowledgeBase.getDescription(),
            knowledgeBase.getVisibility(),
            knowledgeBase.getOwnerUsername(),
            knowledgeBase.isDeleted(),
            documents
        );
    }

    @Transactional
    public void delete(Authentication authentication, long knowledgeBaseId) {
        KnowledgeBaseRecord knowledgeBase = requireActiveKnowledgeBase(knowledgeBaseId);
        UserAccount currentUser = requireUser(authentication);
        if (!isAdmin(authentication) && !knowledgeBase.getOwnerUserId().equals(currentUser.id())) {
            throw new AccessDeniedException("forbidden");
        }
        knowledgeBaseMapper.softDelete(knowledgeBaseId);
    }

    private KnowledgeBaseRecord requireVisibleKnowledgeBase(Authentication authentication, long knowledgeBaseId) {
        KnowledgeBaseRecord knowledgeBase = requireActiveKnowledgeBase(knowledgeBaseId);
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

    private KnowledgeBaseRecord requireActiveKnowledgeBase(long knowledgeBaseId) {
        KnowledgeBaseRecord knowledgeBase = knowledgeBaseMapper.findActiveById(knowledgeBaseId);
        if (knowledgeBase == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "knowledge base not found");
        }
        return knowledgeBase;
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
}
