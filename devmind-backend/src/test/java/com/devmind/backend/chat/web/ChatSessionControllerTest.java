package com.devmind.backend.chat.web;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChatSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM agent_traces");
        jdbcTemplate.execute("DELETE FROM chat_messages");
        jdbcTemplate.execute("DELETE FROM chat_sessions");
        jdbcTemplate.execute("DELETE FROM knowledge_base_permissions");
        jdbcTemplate.execute("DELETE FROM documents");
        jdbcTemplate.execute("DELETE FROM knowledge_bases");
        jdbcTemplate.execute("DELETE FROM users");

        jdbcTemplate.update(
            "INSERT INTO users (id, username, password_hash, role_code, status) VALUES (?, ?, ?, ?, ?)",
            1L, "alice", "hash", "USER", "ACTIVE"
        );
        jdbcTemplate.update(
            "INSERT INTO users (id, username, password_hash, role_code, status) VALUES (?, ?, ?, ?, ?)",
            2L, "bob", "hash", "USER", "ACTIVE"
        );
        jdbcTemplate.update(
            "INSERT INTO knowledge_bases (id, name, description, owner_user_id, visibility, deleted) VALUES (?, ?, ?, ?, ?, ?)",
            10L, "Payments", "Payment docs", 1L, "PRIVATE", false
        );
        jdbcTemplate.update(
            "INSERT INTO knowledge_base_permissions (knowledge_base_id, user_id) VALUES (?, ?)",
            10L, 1L
        );
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldCreateFilterAndDeleteUserOwnedSessions() throws Exception {
        jdbcTemplate.update(
            "INSERT INTO chat_sessions (id, user_id, knowledge_base_id, title, session_type, context_type, context_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
            100L, 1L, 10L, "Payments follow-up", "RAG", null, null
        );
        jdbcTemplate.update(
            "INSERT INTO chat_sessions (id, user_id, knowledge_base_id, title, session_type, context_type, context_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
            101L, 1L, null, "Weekly report task", "AGENT", "REPORT", "weekly-1"
        );
        jdbcTemplate.update(
            "INSERT INTO chat_sessions (id, user_id, knowledge_base_id, title, session_type, context_type, context_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
            102L, 2L, 10L, "Other user session", "RAG", null, null
        );

        mockMvc.perform(get("/api/chat/sessions").param("keyword", "pay").param("sessionType", "RAG"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.items.length()").value(1))
            .andExpect(jsonPath("$.data.items[0].title").value("Payments follow-up"));

        mockMvc.perform(delete("/api/chat/sessions/{sessionId}", 100L))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/chat/sessions").param("sessionType", "RAG"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.items.length()").value(0));
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldPersistAndAppendRagConversationWithinSession() throws Exception {
        String response = mockMvc.perform(post("/api/rag/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "knowledgeBaseId": 10,
                      "question": "How does payment retry work?"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.sessionId").exists())
            .andExpect(jsonPath("$.data.answer").value("Grounded answer for: How does payment retry work?"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        String sessionId = response.replaceAll(".*\"sessionId\":(\\d+).*", "$1");

        mockMvc.perform(post("/api/rag/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "sessionId": %s,
                      "knowledgeBaseId": 10,
                      "question": "What about timeout handling?"
                    }
                    """.formatted(sessionId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.sessionId").value(Integer.parseInt(sessionId)));

        mockMvc.perform(get("/api/chat/sessions/{sessionId}", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.sessionType").value("RAG"))
            .andExpect(jsonPath("$.data.knowledgeBaseId").value(10))
            .andExpect(jsonPath("$.data.messages.length()").value(4));
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldRejectRagSessionWithoutKnowledgeBaseBinding() throws Exception {
        mockMvc.perform(post("/api/rag/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "question": "Missing scope"
                    }
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldPersistAgentConversationWithOptionalContext() throws Exception {
        String response = mockMvc.perform(post("/api/agents/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "taskType": "report",
                      "userInput": "summarize weekly delivery work",
                      "toolScope": "report:write",
                      "contextType": "REPORT",
                      "contextId": "weekly-1"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.sessionId").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String sessionId = response.replaceAll(".*\"sessionId\":(\\d+).*", "$1");

        mockMvc.perform(get("/api/chat/sessions/{sessionId}", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.sessionType").value("AGENT"))
            .andExpect(jsonPath("$.data.contextType").value("REPORT"))
            .andExpect(jsonPath("$.data.contextId").value("weekly-1"))
            .andExpect(jsonPath("$.data.knowledgeBaseId").doesNotExist());
    }
}
