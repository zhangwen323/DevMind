package com.devmind.backend.agent.web;

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
class AgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void shouldCreateBoundedAgentTrace() throws Exception {
        mockMvc.perform(post("/api/agents/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "taskType": "rag",
                      "userInput": "summarize the kb",
                      "toolScope": "rag:read"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.agentName").value("rag-agent"))
            .andExpect(jsonPath("$.data.allowed").value(true))
            .andExpect(jsonPath("$.data.trace.stepName").value("route"));
    }
}
