package com.devmind.backend.document.service;

import com.devmind.backend.common.exception.BusinessException;
import com.devmind.backend.document.model.DocumentRecord;
import com.devmind.backend.infra.storage.ObjectStorageKeys;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DocumentIngestionService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "md", "txt", "docx", "json", "sql");

    private final AtomicLong ids = new AtomicLong(1000);
    private final Map<Long, DocumentRecord> documents = new ConcurrentHashMap<>();

    public DocumentRecord acceptUpload(long knowledgeBaseId, MultipartFile file) {
        String extension = extensionOf(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(400, "unsupported file type");
        }

        long id = ids.incrementAndGet();
        DocumentRecord record = new DocumentRecord(
            id,
            knowledgeBaseId,
            file.getOriginalFilename(),
            extension,
            ObjectStorageKeys.documentSource("local", knowledgeBaseId, id, file.getOriginalFilename()),
            "UPLOADED"
        );
        documents.put(id, record);
        return record;
    }

    public DocumentRecord getDocument(long documentId) {
        return documents.get(documentId);
    }

    private String extensionOf(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
}
