package com.devmind.backend.report.web;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM report_source_documents");
        jdbcTemplate.execute("DELETE FROM reports");
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
            "INSERT INTO users (id, username, password_hash, role_code, status) VALUES (?, ?, ?, ?, ?)",
            9L, "admin", "hash", "SYSTEM_ADMIN", "ACTIVE"
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
            11L, 1L
        );

        jdbcTemplate.update(
            "INSERT INTO documents (id, knowledge_base_id, file_name, file_type, object_key, parse_status, uploaded_by) VALUES (?, ?, ?, ?, ?, ?, ?)",
            1001L, 10L, "payments.md", "md", "dev/kb/10/documents/1001/source/payments.md", "COMPLETED", 1L
        );
        jdbcTemplate.update(
            "INSERT INTO documents (id, knowledge_base_id, file_name, file_type, object_key, parse_status, uploaded_by) VALUES (?, ?, ?, ?, ?, ?, ?)",
            1002L, 10L, "api.md", "md", "dev/kb/10/documents/1002/source/api.md", "COMPLETED", 1L
        );
        jdbcTemplate.update(
            "INSERT INTO documents (id, knowledge_base_id, file_name, file_type, object_key, parse_status, uploaded_by) VALUES (?, ?, ?, ?, ?, ?, ?)",
            1003L, 10L, "draft.md", "md", "dev/kb/10/documents/1003/source/draft.md", "PARSING", 1L
        );
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldGenerateAndPersistReportFromSelectedDocuments() throws Exception {
        mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "knowledgeBaseId": 10,
                      "documentIds": [1001, 1002],
                      "reportType": "weekly-summary",
                      "guidance": "Focus on payment delivery outcomes"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.reportId").exists())
            .andExpect(jsonPath("$.data.sessionId").exists())
            .andExpect(jsonPath("$.data.agentName").value("report-agent"))
            .andExpect(jsonPath("$.data.report.reportType").value("weekly-summary"))
            .andExpect(jsonPath("$.data.report.documents.length()").value(2))
            .andExpect(jsonPath("$.data.report.citations.length()").value(2))
            .andExpect(jsonPath("$.data.report.traceKey").exists());

        mockMvc.perform(get("/api/reports"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.items.length()").value(1))
            .andExpect(jsonPath("$.data.items[0].knowledgeBaseName").value("Payments"));
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldRejectNonCompletedDocumentFromReportScope() throws Exception {
        mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "knowledgeBaseId": 10,
                      "documentIds": [1003],
                      "reportType": "daily-summary"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("report documents must be completed and scoped to the selected knowledge base"));
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldShowOnlyReportsVisibleThroughKnowledgeBaseAccess() throws Exception {
        jdbcTemplate.update(
            """
            INSERT INTO reports
                (id, title, report_type, knowledge_base_id, session_id, trace_key, guidance, content, citations_json, created_by)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            2001L, "Orders Summary", "project-summary", 11L, null, "trace-orders-1", null, "content", "[]", 2L
        );
        jdbcTemplate.update(
            "INSERT INTO report_source_documents (report_id, document_id) VALUES (?, ?)",
            2001L, 1001L
        );

        mockMvc.perform(get("/api/reports"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.items.length()").value(1))
            .andExpect(jsonPath("$.data.items[0].knowledgeBaseName").value("Orders"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "SYSTEM_ADMIN")
    void shouldAllowAdministratorToReadPersistedReportDetail() throws Exception {
        jdbcTemplate.update(
            """
            INSERT INTO reports
                (id, title, report_type, knowledge_base_id, session_id, trace_key, guidance, content, citations_json, created_by)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            2002L, "Payments Draft", "technical-solution-draft", 10L, 301L, "trace-payments-1", "Keep it concise",
            "# Draft", "[{\"documentId\":1001,\"documentName\":\"payments.md\",\"excerpt\":\"Derived from payments.md.\"}]", 1L
        );
        jdbcTemplate.update(
            "INSERT INTO report_source_documents (report_id, document_id) VALUES (?, ?)",
            2002L, 1001L
        );

        mockMvc.perform(get("/api/reports/{reportId}", 2002L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(2002))
            .andExpect(jsonPath("$.data.reportType").value("technical-solution-draft"))
            .andExpect(jsonPath("$.data.documents[0].fileName").value("payments.md"))
            .andExpect(jsonPath("$.data.citations[0].documentName").value("payments.md"));
    }
}
