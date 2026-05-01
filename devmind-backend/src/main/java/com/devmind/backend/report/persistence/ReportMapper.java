package com.devmind.backend.report.persistence;

import com.devmind.backend.report.model.ReportRecord;
import com.devmind.backend.report.model.ReportSummary;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReportMapper {

    @Insert("""
        INSERT INTO reports (
            title,
            report_type,
            knowledge_base_id,
            session_id,
            trace_key,
            guidance,
            content,
            citations_json,
            created_by
        )
        VALUES (
            #{title},
            #{reportType},
            #{knowledgeBaseId},
            #{sessionId},
            #{traceKey},
            #{guidance},
            #{content},
            #{citationsJson},
            #{createdBy}
        )
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ReportRecord record);

    @Select("""
        <script>
        SELECT r.id,
               r.title,
               r.report_type,
               r.knowledge_base_id,
               kb.name AS knowledgeBaseName,
               u.username AS createdByUsername,
               r.created_at
        FROM reports r
        JOIN knowledge_bases kb ON kb.id = r.knowledge_base_id
        JOIN users u ON u.id = r.created_by
        WHERE kb.deleted = FALSE
          <if test="reportType != null and reportType != ''">
            AND r.report_type = #{reportType}
          </if>
          <choose>
            <when test="admin">
            </when>
            <otherwise>
              AND (
                kb.owner_user_id = #{userId}
                OR EXISTS (
                  SELECT 1
                  FROM knowledge_base_permissions kbp
                  WHERE kbp.knowledge_base_id = kb.id
                    AND kbp.user_id = #{userId}
                )
              )
            </otherwise>
          </choose>
        ORDER BY r.created_at DESC, r.id DESC
        LIMIT #{limit} OFFSET #{offset}
        </script>
        """)
    List<ReportSummary> findVisiblePage(
        @Param("userId") long userId,
        @Param("admin") boolean admin,
        @Param("reportType") String reportType,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    @Select("""
        <script>
        SELECT COUNT(*)
        FROM reports r
        JOIN knowledge_bases kb ON kb.id = r.knowledge_base_id
        WHERE kb.deleted = FALSE
          <if test="reportType != null and reportType != ''">
            AND r.report_type = #{reportType}
          </if>
          <choose>
            <when test="admin">
            </when>
            <otherwise>
              AND (
                kb.owner_user_id = #{userId}
                OR EXISTS (
                  SELECT 1
                  FROM knowledge_base_permissions kbp
                  WHERE kbp.knowledge_base_id = kb.id
                    AND kbp.user_id = #{userId}
                )
              )
            </otherwise>
          </choose>
        </script>
        """)
    long countVisible(
        @Param("userId") long userId,
        @Param("admin") boolean admin,
        @Param("reportType") String reportType
    );

    @Select("""
        <script>
        SELECT r.id,
               r.title,
               r.report_type,
               r.knowledge_base_id,
               kb.name AS knowledge_base_name,
               u.username AS created_by_username,
               r.session_id,
               r.trace_key,
               r.guidance,
               r.content,
               r.citations_json,
               r.created_at,
               r.updated_at
        FROM reports r
        JOIN knowledge_bases kb ON kb.id = r.knowledge_base_id
        JOIN users u ON u.id = r.created_by
        WHERE r.id = #{reportId}
          AND kb.deleted = FALSE
          <choose>
            <when test="admin">
            </when>
            <otherwise>
              AND (
                kb.owner_user_id = #{userId}
                OR EXISTS (
                  SELECT 1
                  FROM knowledge_base_permissions kbp
                  WHERE kbp.knowledge_base_id = kb.id
                    AND kbp.user_id = #{userId}
                )
              )
            </otherwise>
          </choose>
        </script>
        """)
    ReportRow findVisibleById(
        @Param("reportId") long reportId,
        @Param("userId") long userId,
        @Param("admin") boolean admin
    );

    record ReportRow(
        Long id,
        String title,
        String reportType,
        Long knowledgeBaseId,
        String knowledgeBaseName,
        String createdByUsername,
        Long sessionId,
        String traceKey,
        String guidance,
        String content,
        String citationsJson,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
    }
}
