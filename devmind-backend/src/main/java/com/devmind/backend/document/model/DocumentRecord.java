package com.devmind.backend.document.model;

public record DocumentRecord(
    long id,
    long knowledgeBaseId,
    String fileName,
    String fileType,
    String objectKey,
    String parseStatus
) {
}
