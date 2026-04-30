package com.devmind.backend.infra.redis;

public final class RedisKeys {

    private RedisKeys() {
    }

    public static String loginToken(long userId) {
        return "login:token:%d".formatted(userId);
    }

    public static String userRateLimit(long userId) {
        return "rate_limit:user:%d".formatted(userId);
    }

    public static String chatContext(long sessionId) {
        return "chat:context:%d".formatted(sessionId);
    }

    public static String agentTask(String taskId) {
        return "agent:task:%s".formatted(taskId);
    }

    public static String documentParseStatus(long documentId) {
        return "doc:parse:status:%d".formatted(documentId);
    }
}
