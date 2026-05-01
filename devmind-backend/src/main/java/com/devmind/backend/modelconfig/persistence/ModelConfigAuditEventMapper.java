package com.devmind.backend.modelconfig.persistence;

import com.devmind.backend.modelconfig.model.ModelConfigAuditEventRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface ModelConfigAuditEventMapper {

    @Insert("""
        INSERT INTO model_config_audit_events (
            actor_user_id,
            provider_config_id,
            action_code,
            outcome,
            details_json
        ) VALUES (
            #{actorUserId},
            #{providerConfigId},
            #{actionCode},
            #{outcome},
            #{detailsJson}
        )
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ModelConfigAuditEventRecord record);
}
