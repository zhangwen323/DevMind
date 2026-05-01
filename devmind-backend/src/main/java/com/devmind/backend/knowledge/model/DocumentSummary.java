package com.devmind.backend.knowledge.model;

public record DocumentSummary(
    long id,
    String fileName,
    String fileType,
    String parseStatus,
    String objectKey
) {
}
