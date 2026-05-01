package com.devmind.backend.knowledge.persistence;

import com.devmind.backend.knowledge.model.DocumentSummary;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KnowledgeBaseDocumentMapper {

    @Select("""
        SELECT id, file_name, file_type, parse_status, object_key
        FROM documents
        WHERE knowledge_base_id = #{knowledgeBaseId}
        ORDER BY updated_at DESC, id DESC
        """)
    @ConstructorArgs({
        @Arg(column = "id", javaType = long.class),
        @Arg(column = "file_name", javaType = String.class),
        @Arg(column = "file_type", javaType = String.class),
        @Arg(column = "parse_status", javaType = String.class),
        @Arg(column = "object_key", javaType = String.class)
    })
    List<DocumentSummary> findByKnowledgeBaseId(@Param("knowledgeBaseId") long knowledgeBaseId);
}
