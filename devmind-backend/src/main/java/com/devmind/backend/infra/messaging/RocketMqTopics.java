package com.devmind.backend.infra.messaging;

public final class RocketMqTopics {

    public static final String DOCUMENT_PARSE = "doc.parse";
    public static final String DOCUMENT_EMBEDDING = "doc.embedding";
    public static final String AGENT_TASK = "agent.task";
    public static final String REPORT_GENERATE = "report.generate";
    public static final String NOTIFICATION = "notification";

    private RocketMqTopics() {
    }
}
