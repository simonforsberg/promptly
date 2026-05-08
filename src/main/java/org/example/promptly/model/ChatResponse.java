package org.example.promptly.model;

public record ChatResponse(
        String sessionId,
        String reply) {
}