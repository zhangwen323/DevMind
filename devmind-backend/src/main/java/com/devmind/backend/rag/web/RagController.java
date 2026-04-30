package com.devmind.backend.rag.web;

import com.devmind.backend.common.api.ApiResponse;
import com.devmind.backend.rag.service.RagQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagQueryService ragQueryService;

    public RagController(RagQueryService ragQueryService) {
        this.ragQueryService = ragQueryService;
    }

    @PostMapping("/ask")
    public ApiResponse<RagQueryService.RagAnswer> ask(@Valid @RequestBody RagRequest request) {
        return ApiResponse.success(ragQueryService.ask(request.knowledgeBaseId(), request.question()));
    }

    public record RagRequest(@Min(1) long knowledgeBaseId, @NotBlank String question) {
    }
}
