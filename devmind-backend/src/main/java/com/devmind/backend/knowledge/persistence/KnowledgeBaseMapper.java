package com.devmind.backend.knowledge.persistence;

import com.devmind.backend.knowledge.model.KnowledgeBaseRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface KnowledgeBaseMapper {

    @Insert("""
        INSERT INTO knowledge_bases (name, description, owner_user_id, visibility, deleted)
        VALUES (#{name}, #{description}, #{ownerUserId}, #{visibility}, #{deleted})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(KnowledgeBaseRecord record);

    @Update("""
        UPDATE knowledge_bases
        SET name = #{name},
            description = #{description}
        WHERE id = #{id}
        """)
    int update(KnowledgeBaseRecord record);

    @Update("""
        UPDATE knowledge_bases
        SET deleted = TRUE
        WHERE id = #{id}
        """)
    int softDelete(@Param("id") long id);

    @Select("""
        <script>
        SELECT kb.id,
               kb.name,
               kb.description,
               kb.owner_user_id,
               u.username AS owner_username,
               kb.visibility,
               kb.deleted,
               kb.created_at,
               kb.updated_at
        FROM knowledge_bases kb
        JOIN users u ON u.id = kb.owner_user_id
        WHERE kb.id = #{id}
          AND kb.deleted = FALSE
        </script>
        """)
    KnowledgeBaseRecord findActiveById(@Param("id") long id);

    @Select("""
        <script>
        SELECT kb.id,
               kb.name,
               kb.description,
               kb.owner_user_id,
               u.username AS owner_username,
               kb.visibility,
               kb.deleted,
               kb.created_at,
               kb.updated_at
        FROM knowledge_bases kb
        JOIN users u ON u.id = kb.owner_user_id
        WHERE kb.deleted = FALSE
          <if test="keyword != null and keyword != ''">
            AND LOWER(kb.name) LIKE CONCAT('%', LOWER(#{keyword}), '%')
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
        ORDER BY kb.updated_at DESC, kb.id DESC
        LIMIT #{limit} OFFSET #{offset}
        </script>
        """)
    List<KnowledgeBaseRecord> findVisiblePage(
        @Param("userId") long userId,
        @Param("admin") boolean admin,
        @Param("keyword") String keyword,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    @Select("""
        <script>
        SELECT COUNT(*)
        FROM knowledge_bases kb
        WHERE kb.deleted = FALSE
          <if test="keyword != null and keyword != ''">
            AND LOWER(kb.name) LIKE CONCAT('%', LOWER(#{keyword}), '%')
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
        @Param("keyword") String keyword
    );
}
