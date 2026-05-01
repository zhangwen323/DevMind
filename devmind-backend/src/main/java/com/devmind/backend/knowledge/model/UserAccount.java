package com.devmind.backend.knowledge.model;

public record UserAccount(
    long id,
    String username,
    String roleCode,
    String status
) {
}
