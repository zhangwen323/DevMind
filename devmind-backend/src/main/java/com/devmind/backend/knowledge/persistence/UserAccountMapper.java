package com.devmind.backend.knowledge.persistence;

import com.devmind.backend.knowledge.model.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserAccountMapper {

    @Select("""
        SELECT id, username, role_code, status
        FROM users
        WHERE username = #{username}
        """)
    UserAccount findByUsername(@Param("username") String username);
}
