package com.devmind.backend.sql.web;

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
class SqlAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void shouldReturnReadOnlySqlExplanation() throws Exception {
        mockMvc.perform(post("/api/sql/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "question": "show the most common failure reasons"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.sql").value("SELECT fail_reason, COUNT(*) AS total FROM order_log GROUP BY fail_reason ORDER BY total DESC LIMIT 5"))
            .andExpect(jsonPath("$.data.explanation").value("Returns the top failure reasons in descending order."));
    }

    @Test
    @WithMockUser
    void shouldRejectMutatingSqlRequests() throws Exception {
        mockMvc.perform(post("/api/sql/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "question": "delete all users"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("sql safety policy violation"));
    }
}
