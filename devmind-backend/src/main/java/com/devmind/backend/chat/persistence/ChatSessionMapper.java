package com.devmind.backend.chat.persistence;

import com.devmind.backend.chat.model.ChatSessionRecord;
import com.devmind.backend.chat.model.ChatSessionSummary;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ChatSessionMapper {

    @Insert("""
        INSERT INTO chat_sessions (user_id, knowledge_base_id, title, session_type, context_type, context_id)
        VALUES (#{userId}, #{knowledgeBaseId}, #{title}, #{sessionType}, #{contextType}, #{contextId})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ChatSessionRecord session);

    @Select("""
        SELECT id,
               user_id,
               knowledge_base_id,
               title,
               session_type,
               context_type,
               context_id,
               created_at,
               updated_at
        FROM chat_sessions
        WHERE id = #{sessionId}
          AND user_id = #{userId}
        """)
    ChatSessionRecord findOwnedById(@Param("sessionId") long sessionId, @Param("userId") long userId);

    @Select("""
        <script>
        SELECT id,
               knowledge_base_id,
               title,
               session_type,
               context_type,
               context_id,
               updated_at
        FROM chat_sessions
        WHERE user_id = #{userId}
          <if test="keyword != null and keyword != ''">
            AND LOWER(title) LIKE CONCAT('%', LOWER(#{keyword}), '%')
          </if>
          <if test="sessionType != null and sessionType != ''">
            AND session_type = #{sessionType}
          </if>
        ORDER BY updated_at DESC, id DESC
        LIMIT #{limit} OFFSET #{offset}
        </script>
        """)
    List<ChatSessionSummary> findOwnedPage(
        @Param("userId") long userId,
        @Param("keyword") String keyword,
        @Param("sessionType") String sessionType,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    @Select("""
        <script>
        SELECT COUNT(*)
        FROM chat_sessions
        WHERE user_id = #{userId}
          <if test="keyword != null and keyword != ''">
            AND LOWER(title) LIKE CONCAT('%', LOWER(#{keyword}), '%')
          </if>
          <if test="sessionType != null and sessionType != ''">
            AND session_type = #{sessionType}
          </if>
        </script>
        """)
    long countOwned(
        @Param("userId") long userId,
        @Param("keyword") String keyword,
        @Param("sessionType") String sessionType
    );

    @Update("""
        UPDATE chat_sessions
        SET updated_at = CURRENT_TIMESTAMP,
            title = #{title},
            knowledge_base_id = #{knowledgeBaseId},
            context_type = #{contextType},
            context_id = #{contextId}
        WHERE id = #{id}
        """)
    int touch(ChatSessionRecord session);

    @Update("""
        DELETE FROM chat_sessions
        WHERE id = #{sessionId}
          AND user_id = #{userId}
        """)
    int deleteOwned(@Param("sessionId") long sessionId, @Param("userId") long userId);
}
