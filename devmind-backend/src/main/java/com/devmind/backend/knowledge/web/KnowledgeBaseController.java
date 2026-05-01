package com.devmind.backend.knowledge.web;

import com.devmind.backend.common.api.ApiResponse;
import com.devmind.backend.knowledge.model.DocumentSummary;
import com.devmind.backend.knowledge.model.KnowledgeBaseDetail;
import com.devmind.backend.knowledge.model.KnowledgeBaseRecord;
import com.devmind.backend.knowledge.model.PageResult;
import com.devmind.backend.knowledge.service.KnowledgeBaseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/knowledge-bases")
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    public KnowledgeBaseController(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    @GetMapping
    public ApiResponse<PageResult<KnowledgeBaseRecord>> list(
        Authentication authentication,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success(knowledgeBaseService.list(authentication, keyword, page, size));
    }

    @PostMapping
    public ApiResponse<KnowledgeBaseRecord> create(
        Authentication authentication,
        @Valid @RequestBody CreateKnowledgeBaseRequest request
    ) {
        return ApiResponse.success(knowledgeBaseService.create(authentication, request.name(), request.description()));
    }

    @PutMapping("/{knowledgeBaseId}")
    public ApiResponse<KnowledgeBaseRecord> update(
        Authentication authentication,
        @PathVariable long knowledgeBaseId,
        @Valid @RequestBody UpdateKnowledgeBaseRequest request
    ) {
        return ApiResponse.success(
            knowledgeBaseService.update(authentication, knowledgeBaseId, request.name(), request.description())
        );
    }

    @GetMapping("/{knowledgeBaseId}")
    public ApiResponse<KnowledgeBaseDetail> detail(Authentication authentication, @PathVariable long knowledgeBaseId) {
        return ApiResponse.success(knowledgeBaseService.getDetail(authentication, knowledgeBaseId));
    }

    @GetMapping("/{knowledgeBaseId}/documents")
    public ApiResponse<List<DocumentSummary>> documents(Authentication authentication, @PathVariable long knowledgeBaseId) {
        return ApiResponse.success(knowledgeBaseService.getDetail(authentication, knowledgeBaseId).documents());
    }

    @DeleteMapping("/{knowledgeBaseId}")
    public ApiResponse<Void> delete(Authentication authentication, @PathVariable long knowledgeBaseId) {
        knowledgeBaseService.delete(authentication, knowledgeBaseId);
        return ApiResponse.success(null);
    }

    public record CreateKnowledgeBaseRequest(@NotBlank String name, String description) {
    }

    public record UpdateKnowledgeBaseRequest(@NotBlank String name, String description) {
    }
}
