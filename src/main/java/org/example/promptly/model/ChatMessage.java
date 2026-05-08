package org.example.promptly.model;

public record ChatMessage(
        String role,
        String content) {
}