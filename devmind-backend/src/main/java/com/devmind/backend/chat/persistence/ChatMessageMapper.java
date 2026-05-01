package com.devmind.backend.chat.persistence;

import com.devmind.backend.chat.model.ChatMessageRecord;
import com.devmind.backend.chat.model.ChatMessageView;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ChatMessageMapper {

    @Insert("""
        INSERT INTO chat_messages (session_id, role_code, content, agent_name)
        VALUES (#{sessionId}, #{roleCode}, #{content}, #{agentName})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ChatMessageRecord message);

    @Select("""
        SELECT id, role_code, content, agent_name, created_at
        FROM chat_messages
        WHERE session_id = #{sessionId}
        ORDER BY created_at ASC, id ASC
        """)
    @ConstructorArgs({
        @Arg(column = "id", javaType = long.class),
        @Arg(column = "role_code", javaType = String.class),
        @Arg(column = "content", javaType = String.class),
        @Arg(column = "agent_name", javaType = String.class),
        @Arg(column = "created_at", javaType = LocalDateTime.class)
    })
    List<ChatMessageView> findBySessionId(@Param("sessionId") long sessionId);

    @Delete("""
        DELETE FROM chat_messages
        WHERE session_id = #{sessionId}
        """)
    int deleteBySessionId(@Param("sessionId") long sessionId);
}
