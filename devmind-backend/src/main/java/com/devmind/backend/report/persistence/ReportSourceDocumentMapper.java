package com.devmind.backend.report.persistence;

import com.devmind.backend.knowledge.model.DocumentSummary;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReportSourceDocumentMapper {

    @Insert("""
        INSERT INTO report_source_documents (report_id, document_id)
        VALUES (#{reportId}, #{documentId})
        """)
    void insert(@Param("reportId") long reportId, @Param("documentId") long documentId);

    @Select("""
        <script>
        SELECT d.id, d.file_name, d.file_type, d.parse_status, d.object_key
        FROM documents d
        WHERE d.knowledge_base_id = #{knowledgeBaseId}
          AND d.parse_status = 'COMPLETED'
          <if test="documentIds != null and documentIds.size() > 0">
            AND d.id IN
            <foreach item="documentId" collection="documentIds" open="(" separator="," close=")">
              #{documentId}
            </foreach>
          </if>
        ORDER BY d.updated_at DESC, d.id DESC
        </script>
        """)
    @ConstructorArgs({
        @Arg(column = "id", javaType = long.class),
        @Arg(column = "file_name", javaType = String.class),
        @Arg(column = "file_type", javaType = String.class),
        @Arg(column = "parse_status", javaType = String.class),
        @Arg(column = "object_key", javaType = String.class)
    })
    List<DocumentSummary> findCompletedByKnowledgeBaseAndIds(
        @Param("knowledgeBaseId") long knowledgeBaseId,
        @Param("documentIds") List<Long> documentIds
    );

    @Select("""
        SELECT d.id, d.file_name, d.file_type, d.parse_status, d.object_key
        FROM report_source_documents rsd
        JOIN documents d ON d.id = rsd.document_id
        WHERE rsd.report_id = #{reportId}
        ORDER BY d.updated_at DESC, d.id DESC
        """)
    @ConstructorArgs({
        @Arg(column = "id", javaType = long.class),
        @Arg(column = "file_name", javaType = String.class),
        @Arg(column = "file_type", javaType = String.class),
        @Arg(column = "parse_status", javaType = String.class),
        @Arg(column = "object_key", javaType = String.class)
    })
    List<DocumentSummary> findByReportId(@Param("reportId") long reportId);
}
