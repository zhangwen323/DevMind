package com.devmind.backend.modelconfig.persistence;

import com.devmind.backend.modelconfig.model.ModelProviderConfigRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ModelProviderConfigMapper {

    @Insert("""
        INSERT INTO model_provider_configs (
            provider_code,
            display_name,
            base_url,
            api_key_env_name,
            chat_model,
            embedding_model,
            timeout_ms,
            temperature,
            notes,
            enabled,
            default_chat,
            default_embedding
        ) VALUES (
            #{providerCode},
            #{displayName},
            #{baseUrl},
            #{apiKeyEnvName},
            #{chatModel},
            #{embeddingModel},
            #{timeoutMs},
            #{temperature},
            #{notes},
            #{enabled},
            #{defaultChat},
            #{defaultEmbedding}
        )
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ModelProviderConfigRecord record);

    @Update("""
        UPDATE model_provider_configs
        SET provider_code = #{providerCode},
            display_name = #{displayName},
            base_url = #{baseUrl},
            api_key_env_name = #{apiKeyEnvName},
            chat_model = #{chatModel},
            embedding_model = #{embeddingModel},
            timeout_ms = #{timeoutMs},
            temperature = #{temperature},
            notes = #{notes}
        WHERE id = #{id}
        """)
    int update(ModelProviderConfigRecord record);

    @Update("""
        UPDATE model_provider_configs
        SET enabled = #{enabled}
        WHERE id = #{id}
        """)
    int updateEnabled(@Param("id") long id, @Param("enabled") boolean enabled);

    @Update("""
        UPDATE model_provider_configs
        SET default_chat = FALSE
        """)
    int clearDefaultChat();

    @Update("""
        UPDATE model_provider_configs
        SET default_chat = TRUE
        WHERE id = #{id}
        """)
    int setDefaultChat(@Param("id") long id);

    @Update("""
        UPDATE model_provider_configs
        SET default_embedding = FALSE
        """)
    int clearDefaultEmbedding();

    @Update("""
        UPDATE model_provider_configs
        SET default_embedding = TRUE
        WHERE id = #{id}
        """)
    int setDefaultEmbedding(@Param("id") long id);

    @Select("""
        SELECT id,
               provider_code,
               display_name,
               base_url,
               api_key_env_name,
               chat_model,
               embedding_model,
               timeout_ms,
               temperature,
               notes,
               enabled,
               default_chat,
               default_embedding,
               created_at,
               updated_at
        FROM model_provider_configs
        ORDER BY updated_at DESC, id DESC
        """)
    List<ModelProviderConfigRecord> findAll();

    @Select("""
        SELECT id,
               provider_code,
               display_name,
               base_url,
               api_key_env_name,
               chat_model,
               embedding_model,
               timeout_ms,
               temperature,
               notes,
               enabled,
               default_chat,
               default_embedding,
               created_at,
               updated_at
        FROM model_provider_configs
        WHERE id = #{id}
        """)
    ModelProviderConfigRecord findById(@Param("id") long id);

    @Select("""
        SELECT COUNT(*)
        FROM model_provider_configs
        WHERE enabled = TRUE
        """)
    long countEnabled();

    @Select("""
        SELECT id,
               provider_code,
               display_name,
               base_url,
               api_key_env_name,
               chat_model,
               embedding_model,
               timeout_ms,
               temperature,
               notes,
               enabled,
               default_chat,
               default_embedding,
               created_at,
               updated_at
        FROM model_provider_configs
        WHERE default_chat = TRUE
        """)
    ModelProviderConfigRecord findDefaultChat();

    @Select("""
        SELECT id,
               provider_code,
               display_name,
               base_url,
               api_key_env_name,
               chat_model,
               embedding_model,
               timeout_ms,
               temperature,
               notes,
               enabled,
               default_chat,
               default_embedding,
               created_at,
               updated_at
        FROM model_provider_configs
        WHERE default_embedding = TRUE
        """)
    ModelProviderConfigRecord findDefaultEmbedding();
}
