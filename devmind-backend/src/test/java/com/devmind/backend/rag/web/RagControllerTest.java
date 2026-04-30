package com.devmind.backend.rag.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void shouldReturnTraceableCitationForReadyDocument() throws Exception {
        mockMvc.perform(post("/api/rag/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "knowledgeBaseId": 1,
                      "question": "How does login work?"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.knowledgeBaseId").value(1))
            .andExpect(jsonPath("$.data.references[0].documentId").value(1001))
            .andExpect(jsonPath("$.data.references[0].traceable").value(true));
    }
}
