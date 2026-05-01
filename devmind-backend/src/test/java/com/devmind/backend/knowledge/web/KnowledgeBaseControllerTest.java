package com.devmind.backend.knowledge.web;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class KnowledgeBaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
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
            "INSERT INTO users (id, username, password_hash, role_code, status) VALUES (?, ?, ?, ?, ?)",
            3L, "admin", "hash", "SYSTEM_ADMIN", "ACTIVE"
        );

        jdbcTemplate.update(
            "INSERT INTO knowledge_bases (id, name, description, owner_user_id, visibility, deleted) VALUES (?, ?, ?, ?, ?, ?)",
            10L, "Payments", "Payment docs", 1L, "PRIVATE", false
        );
        jdbcTemplate.update(
            "INSERT INTO knowledge_bases (id, name, description, owner_user_id, visibility, deleted) VALUES (?, ?, ?, ?, ?, ?)",
            11L, "Orders", "Order docs", 2L, "PRIVATE", false
        );

        jdbcTemplate.update(
            "INSERT INTO knowledge_base_permissions (knowledge_base_id, user_id) VALUES (?, ?)",
            10L, 1L
        );
        jdbcTemplate.update(
            "INSERT INTO knowledge_base_permissions (knowledge_base_id, user_id) VALUES (?, ?)",
            11L, 1L
        );

        jdbcTemplate.update(
            "INSERT INTO documents (id, knowledge_base_id, file_name, file_type, object_key, parse_status, uploaded_by) VALUES (?, ?, ?, ?, ?, ?, ?)",
            1001L, 10L, "payments.md", "md", "local/kb/10/documents/1001/source/payments.md", "COMPLETED", 1L
        );
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldReturnOnlyAuthorizedKnowledgeBasesForOrdinaryUser() throws Exception {
        mockMvc.perform(get("/api/knowledge-bases"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.items.length()").value(2))
            .andExpect(jsonPath("$.data.items[0].name").value("Orders"))
            .andExpect(jsonPath("$.data.items[1].name").value("Payments"));
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldCreateKnowledgeBaseForCreator() throws Exception {
        mockMvc.perform(post("/api/knowledge-bases")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Architecture",
                      "description": "Architecture guides"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value("Architecture"))
            .andExpect(jsonPath("$.data.ownerUsername").value("alice"))
            .andExpect(jsonPath("$.data.deleted").value(false));
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldUpdateKnowledgeBaseForOwner() throws Exception {
        mockMvc.perform(put("/api/knowledge-bases/{id}", 10L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Payments Core",
                      "description": "Updated payment docs"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(10))
            .andExpect(jsonPath("$.data.name").value("Payments Core"))
            .andExpect(jsonPath("$.data.description").value("Updated payment docs"));
    }

    @Test
    @WithMockUser(username = "bob", roles = "USER")
    void shouldRejectDeleteByNonOwner() throws Exception {
        mockMvc.perform(delete("/api/knowledge-bases/{id}", 10L))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldApplyNameSearchForKnowledgeBaseList() throws Exception {
        mockMvc.perform(get("/api/knowledge-bases").param("keyword", "pay"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.items.length()").value(1))
            .andExpect(jsonPath("$.data.items[0].name").value("Payments"));
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldReturnKnowledgeBaseDetailWithDocuments() throws Exception {
        mockMvc.perform(get("/api/knowledge-bases/{id}", 10L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(10))
            .andExpect(jsonPath("$.data.documents.length()").value(1))
            .andExpect(jsonPath("$.data.documents[0].fileName").value("payments.md"))
            .andExpect(jsonPath("$.data.documents[0].parseStatus").value("COMPLETED"));
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldHideSoftDeletedKnowledgeBaseFromNormalFlows() throws Exception {
        mockMvc.perform(delete("/api/knowledge-bases/{id}", 10L))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/knowledge-bases"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.items.length()").value(1))
            .andExpect(jsonPath("$.data.items[0].name").value("Orders"));

        mockMvc.perform(get("/api/knowledge-bases/{id}", 10L))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("knowledge base not found"));
    }
}
