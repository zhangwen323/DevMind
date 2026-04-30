package com.devmind.backend.infra.storage;

public final class ObjectStorageKeys {

    private ObjectStorageKeys() {
    }

    public static String documentSource(String environment, long knowledgeBaseId, long documentId, String fileName) {
        return "%s/kb/%d/documents/%d/source/%s".formatted(environment, knowledgeBaseId, documentId, fileName);
    }

    public static String documentDerived(String environment, long knowledgeBaseId, long documentId, String artifactName) {
        return "%s/kb/%d/documents/%d/derived/%s".formatted(environment, knowledgeBaseId, documentId, artifactName);
    }
}
