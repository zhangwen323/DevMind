package com.devmind.backend.agent.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CodeAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM agent_traces");
        jdbcTemplate.execute("DELETE FROM chat_messages");
        jdbcTemplate.execute("DELETE FROM chat_sessions");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.update(
            "INSERT INTO users (id, username, password_hash, role_code, status) VALUES (?, ?, ?, ?, ?)",
            1L, "alice", "hash", "USER", "ACTIVE"
        );
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldAnalyzeCodeSnippetAndPersistAgentSession() throws Exception {
        mockMvc.perform(post("/api/agents/code-analysis")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "language": "java",
                      "analysisType": "ISSUES",
                      "question": "Focus on null-safety",
                      "codeSnippet": "public String demo(User user) { return user.getName(); }"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.sessionId").exists())
            .andExpect(jsonPath("$.data.agentName").value("code-analysis-agent"))
            .andExpect(jsonPath("$.data.analysis.language").value("java"))
            .andExpect(jsonPath("$.data.analysis.overview").exists())
            .andExpect(jsonPath("$.data.analysis.potentialIssues.length()").value(1))
            .andExpect(jsonPath("$.data.analysis.optimizationSuggestions.length()").value(1))
            .andExpect(jsonPath("$.data.analysis.generatedNotes.length()").value(1));
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldAllowLanguageOmissionAndContinueExistingAgentSession() throws Exception {
        String first = mockMvc.perform(post("/api/agents/code-analysis")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "analysisType": "EXPLAIN",
                      "codeSnippet": "SELECT * FROM orders WHERE status = 'FAILED'"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.analysis.language").value("sql"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        String sessionId = first.replaceAll(".*\"sessionId\":(\\d+).*", "$1");

        mockMvc.perform(post("/api/agents/code-analysis")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "sessionId": %s,
                      "analysisType": "COMMENTS",
                      "codeSnippet": "function sum(a, b) { return a + b }"
                    }
                    """.formatted(sessionId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.sessionId").value(Integer.parseInt(sessionId)));
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldRejectMissingCodeSnippet() throws Exception {
        mockMvc.perform(post("/api/agents/code-analysis")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "language": "java",
                      "analysisType": "ISSUES",
                      "codeSnippet": ""
                    }
                    """))
            .andExpect(status().isBadRequest());
    }
}
