package com.devmind.backend.rag.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagQueryService {

    public RagAnswer ask(long knowledgeBaseId, String question) {
        return new RagAnswer(
            knowledgeBaseId,
            "Grounded answer for: " + question,
            List.of(new Reference(1001L, "auth-guide.md", true))
        );
    }

    public record RagAnswer(long knowledgeBaseId, String answer, List<Reference> references) {
    }

    public record Reference(long documentId, String documentName, boolean traceable) {
    }
}
