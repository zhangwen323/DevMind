package com.devmind.backend.knowledge.persistence;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface KnowledgeBasePermissionMapper {

    @Insert("""
        INSERT INTO knowledge_base_permissions (knowledge_base_id, user_id)
        VALUES (#{knowledgeBaseId}, #{userId})
        """)
    void insert(@Param("knowledgeBaseId") long knowledgeBaseId, @Param("userId") long userId);

    @Select("""
        SELECT COUNT(*)
        FROM knowledge_base_permissions
        WHERE knowledge_base_id = #{knowledgeBaseId}
          AND user_id = #{userId}
        """)
    long countPermission(@Param("knowledgeBaseId") long knowledgeBaseId, @Param("userId") long userId);
}
