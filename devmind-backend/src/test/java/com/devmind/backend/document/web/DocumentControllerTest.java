package com.devmind.backend.document.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void shouldAcceptWhitelistedUploadAndTrackStatus() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "guide.md",
            "text/markdown",
            "# hello".getBytes()
        );

        String response = mockMvc.perform(multipart("/api/kb/42/documents/upload").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.knowledgeBaseId").value(42))
            .andExpect(jsonPath("$.data.parseStatus").value("UPLOADED"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        String documentId = response.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(get("/api/documents/{id}", documentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(Integer.parseInt(documentId)))
            .andExpect(jsonPath("$.data.parseStatus").value("UPLOADED"));
    }

    @Test
    @WithMockUser
    void shouldRejectNonWhitelistedUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "archive.exe",
            "application/octet-stream",
            "bad".getBytes()
        );

        mockMvc.perform(multipart("/api/kb/42/documents/upload").file(file))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("unsupported file type"));
    }
}
