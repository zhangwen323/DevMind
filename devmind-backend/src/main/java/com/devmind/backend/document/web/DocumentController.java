package com.devmind.backend.document.web;

import com.devmind.backend.common.api.ApiResponse;
import com.devmind.backend.document.model.DocumentRecord;
import com.devmind.backend.document.service.DocumentIngestionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping
public class DocumentController {

    private final DocumentIngestionService documentIngestionService;

    public DocumentController(DocumentIngestionService documentIngestionService) {
        this.documentIngestionService = documentIngestionService;
    }

    @PostMapping(path = "/api/kb/{knowledgeBaseId}/documents/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<DocumentRecord> upload(@PathVariable long knowledgeBaseId, @RequestPart("file") MultipartFile file) {
        return ApiResponse.success(documentIngestionService.acceptUpload(knowledgeBaseId, file));
    }

    @GetMapping("/api/documents/{documentId}")
    public ApiResponse<DocumentRecord> getDocument(@PathVariable long documentId) {
        return ApiResponse.success(documentIngestionService.getDocument(documentId));
    }
}
