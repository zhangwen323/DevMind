package com.devmind.backend.modelconfig.web;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ModelConfigurationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM model_config_audit_events");
        jdbcTemplate.execute("DELETE FROM model_provider_configs");
        jdbcTemplate.execute("DELETE FROM users");

        jdbcTemplate.update(
            "INSERT INTO users (id, username, password_hash, role_code, status) VALUES (?, ?, ?, ?, ?)",
            1L, "alice", "hash", "USER", "ACTIVE"
        );
        jdbcTemplate.update(
            "INSERT INTO users (id, username, password_hash, role_code, status) VALUES (?, ?, ?, ?, ?)",
            9L, "admin", "hash", "SYSTEM_ADMIN", "ACTIVE"
        );
    }

    @Test
    @WithMockUser(username = "admin", roles = "SYSTEM_ADMIN")
    void shouldCreateListAndOverviewProviderConfigurationsForAdministrators() throws Exception {
        String response = mockMvc.perform(post("/api/admin/model-configurations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "providerCode": "qwen",
                      "displayName": "Qwen Primary",
                      "baseUrl": "https://dashscope.aliyuncs.com",
                      "apiKeyEnvName": "QWEN_API_KEY",
                      "chatModel": "qwen-plus",
                      "embeddingModel": "text-embedding-v4",
                      "timeoutMs": 30000,
                      "temperature": 0.2,
                      "notes": "Primary provider",
                      "enabled": true
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.providerCode").value("qwen"))
            .andExpect(jsonPath("$.data.apiKeyEnvName").value("QWEN_API_KEY"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        String configId = response.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(patch("/api/admin/model-configurations/{id}/default-chat", configId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.defaultChat").value(true));

        mockMvc.perform(patch("/api/admin/model-configurations/{id}/default-embedding", configId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.defaultEmbedding").value(true));

        mockMvc.perform(get("/api/admin/model-configurations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].displayName").value("Qwen Primary"));

        mockMvc.perform(get("/api/admin/model-configurations/overview"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.chat.providerCode").value("qwen"))
            .andExpect(jsonPath("$.data.embedding.model").value("text-embedding-v4"))
            .andExpect(jsonPath("$.data.enabledProviderCount").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = "SYSTEM_ADMIN")
    void shouldRejectDisabledProviderAsDefaultAndRejectDisablingCurrentDefault() throws Exception {
        jdbcTemplate.update("""
            INSERT INTO model_provider_configs
                (id, provider_code, display_name, api_key_env_name, chat_model, embedding_model, timeout_ms, temperature, enabled, default_chat, default_embedding)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            2L, "openai", "OpenAI", "OPENAI_API_KEY", "gpt-4.1", "text-embedding-3-small", 30000, 0.20, false, false, false
        );
        jdbcTemplate.update("""
            INSERT INTO model_provider_configs
                (id, provider_code, display_name, api_key_env_name, chat_model, embedding_model, timeout_ms, temperature, enabled, default_chat, default_embedding)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            3L, "qwen", "Qwen", "QWEN_API_KEY", "qwen-plus", "text-embedding-v4", 30000, 0.20, true, true, false
        );

        mockMvc.perform(patch("/api/admin/model-configurations/{id}/default-chat", 2L))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("only enabled providers can be assigned as default"));

        mockMvc.perform(patch("/api/admin/model-configurations/{id}/enabled", 3L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "enabled": false
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("move default assignment before disabling provider"));
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldRejectNonAdministratorsFromManagingModelConfiguration() throws Exception {
        mockMvc.perform(get("/api/admin/model-configurations"))
            .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/admin/model-configurations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "providerCode": "qwen",
                      "displayName": "Qwen Primary",
                      "apiKeyEnvName": "QWEN_API_KEY",
                      "chatModel": "qwen-plus",
                      "embeddingModel": "text-embedding-v4",
                      "timeoutMs": 30000,
                      "temperature": 0.2,
                      "enabled": true
                    }
                    """))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "SYSTEM_ADMIN")
    void shouldUpdateProviderConfigurationAndWriteAuditTrail() throws Exception {
        jdbcTemplate.update("""
            INSERT INTO model_provider_configs
                (id, provider_code, display_name, api_key_env_name, chat_model, embedding_model, timeout_ms, temperature, enabled, default_chat, default_embedding)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            4L, "qwen", "Qwen", "QWEN_API_KEY", "qwen-plus", "text-embedding-v4", 30000, 0.20, true, false, false
        );

        mockMvc.perform(put("/api/admin/model-configurations/{id}", 4L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "providerCode": "qwen",
                      "displayName": "Qwen Updated",
                      "baseUrl": "https://dashscope.aliyuncs.com",
                      "apiKeyEnvName": "QWEN_API_KEY",
                      "chatModel": "qwen-max",
                      "embeddingModel": "text-embedding-v4",
                      "timeoutMs": 45000,
                      "temperature": 0.3,
                      "notes": "Updated",
                      "enabled": true
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.displayName").value("Qwen Updated"))
            .andExpect(jsonPath("$.data.timeoutMs").value(45000));

        Integer auditCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM model_config_audit_events", Integer.class);
        org.assertj.core.api.Assertions.assertThat(auditCount).isNotNull().isGreaterThan(0);
    }
}
