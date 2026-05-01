package com.devmind.backend.agenttrace.persistence;

import com.devmind.backend.agenttrace.model.AdminTraceListItem;
import com.devmind.backend.agenttrace.model.AgentTraceRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AgentTraceMapper {

    @Insert("""
        INSERT INTO agent_traces (
            trace_key,
            session_id,
            user_id,
            agent_name,
            step_name,
            tool_name,
            input_payload,
            output_payload,
            latency_ms,
            status,
            error_message
        )
        VALUES (
            #{traceKey},
            #{sessionId},
            #{userId},
            #{agentName},
            #{stepName},
            #{toolName},
            #{inputPayload},
            #{outputPayload},
            #{latencyMs},
            #{status},
            #{errorMessage}
        )
        """)
    void insert(AgentTraceRecord record);

    @Select("""
        <script>
        SELECT t.trace_key AS traceKey,
               MIN(t.session_id) AS sessionId,
               MIN(t.user_id) AS userId,
               MIN(u.username) AS username,
               MIN(s.title) AS sessionTitle,
               MIN(t.agent_name) AS agentName,
               CASE
                 WHEN SUM(CASE WHEN t.status = 'FAILED' THEN 1 ELSE 0 END) &gt; 0 THEN 'FAILED'
                 ELSE 'SUCCESS'
               END AS status,
               COUNT(*) AS stepCount,
               COALESCE(SUM(t.latency_ms), 0) AS totalLatencyMs,
               MIN(t.created_at) AS startedAt
        FROM agent_traces t
        LEFT JOIN users u ON u.id = t.user_id
        LEFT JOIN chat_sessions s ON s.id = t.session_id
        WHERE 1 = 1
          <if test="status != null and status != ''">
            AND t.status = #{status}
          </if>
          <if test="agentName != null and agentName != ''">
            AND t.agent_name = #{agentName}
          </if>
          <if test="keyword != null and keyword != ''">
            AND (
                LOWER(t.trace_key) LIKE CONCAT('%', LOWER(#{keyword}), '%')
                OR LOWER(COALESCE(s.title, '')) LIKE CONCAT('%', LOWER(#{keyword}), '%')
                OR LOWER(COALESCE(t.error_message, '')) LIKE CONCAT('%', LOWER(#{keyword}), '%')
                OR LOWER(COALESCE(t.agent_name, '')) LIKE CONCAT('%', LOWER(#{keyword}), '%')
            )
          </if>
        GROUP BY t.trace_key
        ORDER BY startedAt DESC, traceKey DESC
        LIMIT #{limit} OFFSET #{offset}
        </script>
        """)
    List<AdminTraceListItem> findAdminPage(
        @Param("status") String status,
        @Param("agentName") String agentName,
        @Param("keyword") String keyword,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    @Select("""
        <script>
        SELECT COUNT(*) FROM (
            SELECT t.trace_key
            FROM agent_traces t
            LEFT JOIN chat_sessions s ON s.id = t.session_id
            WHERE 1 = 1
              <if test="status != null and status != ''">
                AND t.status = #{status}
              </if>
              <if test="agentName != null and agentName != ''">
                AND t.agent_name = #{agentName}
              </if>
              <if test="keyword != null and keyword != ''">
                AND (
                    LOWER(t.trace_key) LIKE CONCAT('%', LOWER(#{keyword}), '%')
                    OR LOWER(COALESCE(s.title, '')) LIKE CONCAT('%', LOWER(#{keyword}), '%')
                    OR LOWER(COALESCE(t.error_message, '')) LIKE CONCAT('%', LOWER(#{keyword}), '%')
                    OR LOWER(COALESCE(t.agent_name, '')) LIKE CONCAT('%', LOWER(#{keyword}), '%')
                )
              </if>
            GROUP BY t.trace_key
        ) grouped_traces
        </script>
        """)
    long countAdminPage(
        @Param("status") String status,
        @Param("agentName") String agentName,
        @Param("keyword") String keyword
    );

    @Select("""
        SELECT t.trace_key AS traceKey,
               t.session_id AS sessionId,
               t.user_id AS userId,
               u.username AS username,
               s.title AS sessionTitle,
               t.agent_name AS agentName,
               t.step_name AS stepName,
               t.tool_name AS toolName,
               t.input_payload AS inputPayload,
               t.output_payload AS outputPayload,
               t.latency_ms AS latencyMs,
               t.status AS status,
               t.error_message AS errorMessage,
               t.created_at AS createdAt
        FROM agent_traces t
        LEFT JOIN users u ON u.id = t.user_id
        LEFT JOIN chat_sessions s ON s.id = t.session_id
        WHERE t.trace_key = #{traceKey}
        ORDER BY t.created_at ASC, t.id ASC
        """)
    List<AgentTraceRow> findRowsByTraceKey(@Param("traceKey") String traceKey);

    @Select("""
        SELECT t.trace_key AS traceKey,
               t.session_id AS sessionId,
               t.user_id AS userId,
               u.username AS username,
               s.title AS sessionTitle,
               t.agent_name AS agentName,
               t.step_name AS stepName,
               t.tool_name AS toolName,
               t.input_payload AS inputPayload,
               t.output_payload AS outputPayload,
               t.latency_ms AS latencyMs,
               t.status AS status,
               t.error_message AS errorMessage,
               t.created_at AS createdAt
        FROM agent_traces t
        LEFT JOIN users u ON u.id = t.user_id
        LEFT JOIN chat_sessions s ON s.id = t.session_id
        WHERE t.session_id = #{sessionId}
        ORDER BY t.created_at ASC, t.id ASC
        """)
    List<AgentTraceRow> findRowsBySessionId(@Param("sessionId") long sessionId);

    record AgentTraceRow(
        String traceKey,
        Long sessionId,
        Long userId,
        String username,
        String sessionTitle,
        String agentName,
        String stepName,
        String toolName,
        String inputPayload,
        String outputPayload,
        Long latencyMs,
        String status,
        String errorMessage,
        java.time.LocalDateTime createdAt
    ) {
    }
}
