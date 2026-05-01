package com.devmind.backend.agenttrace.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AgentTraceControllerTest {

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
            "INSERT INTO users (id, username, password_hash, role_code, status) VALUES (?, ?, ?, ?, ?)",
            9L, "admin", "hash", "SYSTEM_ADMIN", "ACTIVE"
        );

        jdbcTemplate.update(
            "INSERT INTO chat_sessions (id, user_id, knowledge_base_id, title, session_type, context_type, context_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
            101L, 1L, null, "Weekly report", "AGENT", "REPORT", "weekly-1"
        );
        jdbcTemplate.update(
            "INSERT INTO chat_sessions (id, user_id, knowledge_base_id, title, session_type, context_type, context_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
            102L, 2L, null, "Other user task", "AGENT", "TASK", "task-2"
        );

        jdbcTemplate.update(
            """
            INSERT INTO agent_traces
                (id, trace_key, session_id, user_id, agent_name, step_name, tool_name, input_payload, output_payload, latency_ms, status, error_message)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            1001L, "trace-weekly-1", 101L, 1L, "report-agent", "route", null,
            "{\"userInput\":\"summarize weekly delivery work\"}",
            "{\"decision\":\"report-agent\"}",
            5L, "SUCCESS", null
        );
        jdbcTemplate.update(
            """
            INSERT INTO agent_traces
                (id, trace_key, session_id, user_id, agent_name, step_name, tool_name, input_payload, output_payload, latency_ms, status, error_message)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            1002L, "trace-weekly-1", 101L, 1L, "report-agent", "tool-execute", "report-writer",
            "{\"contextId\":\"weekly-1\"}",
            "{\"summary\":\"weekly report generated\"}",
            25L, "SUCCESS", null
        );
        jdbcTemplate.update(
            """
            INSERT INTO agent_traces
                (id, trace_key, session_id, user_id, agent_name, step_name, tool_name, input_payload, output_payload, latency_ms, status, error_message)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            1003L, "trace-task-2", 102L, 2L, "sql-agent", "route", null,
            "{\"userInput\":\"show errors\"}",
            "{\"decision\":\"sql-agent\"}",
            7L, "FAILED", "unsafe query blocked"
        );
    }

    @Test
    @WithMockUser(username = "admin", roles = "SYSTEM_ADMIN")
    void shouldListAndInspectGlobalAgentTracesForSystemAdministrators() throws Exception {
        mockMvc.perform(get("/api/agent-traces")
                .param("status", "SUCCESS")
                .param("agentName", "report-agent")
                .param("keyword", "weekly"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.items.length()").value(1))
            .andExpect(jsonPath("$.data.items[0].traceKey").value("trace-weekly-1"))
            .andExpect(jsonPath("$.data.items[0].stepCount").value(2));

        mockMvc.perform(get("/api/agent-traces/{traceKey}", "trace-weekly-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.traceKey").value("trace-weekly-1"))
            .andExpect(jsonPath("$.data.agentName").value("report-agent"))
            .andExpect(jsonPath("$.data.steps.length()").value(2))
            .andExpect(jsonPath("$.data.steps[1].toolName").value("report-writer"))
            .andExpect(jsonPath("$.data.steps[0].inputPayload").exists());
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldRejectGlobalTraceQueriesForNonAdministrators() throws Exception {
        mockMvc.perform(get("/api/agent-traces"))
            .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/agent-traces/{traceKey}", "trace-weekly-1"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldExposeOnlyRedactedSessionLinkedSummariesToOwningUser() throws Exception {
        mockMvc.perform(get("/api/chat/sessions/{sessionId}/agent-traces", 101L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].traceKey").value("trace-weekly-1"))
            .andExpect(jsonPath("$.data[0].steps[0].summary").value("Step route completed successfully."))
            .andExpect(jsonPath("$.data[0].steps[0].inputPayload").doesNotExist());

        mockMvc.perform(get("/api/chat/sessions/{sessionId}/agent-traces/{traceKey}", 101L, "trace-weekly-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.traceKey").value("trace-weekly-1"))
            .andExpect(jsonPath("$.data.steps.length()").value(2))
            .andExpect(jsonPath("$.data.steps[1].summary").value("Tool report-writer completed successfully."))
            .andExpect(jsonPath("$.data.steps[1].outputPayload").doesNotExist());
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldDenySessionTraceSummaryForDifferentUsersSession() throws Exception {
        mockMvc.perform(get("/api/chat/sessions/{sessionId}/agent-traces", 102L))
            .andExpect(status().isForbidden());
    }
}
