package org.example.promptly.ai;

import org.example.promptly.model.ChatMessage;

import java.util.List;

public record AiApiRequest(
        String model,
        List<ChatMessage> messages) {
}