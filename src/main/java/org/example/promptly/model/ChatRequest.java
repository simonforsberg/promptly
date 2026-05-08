package org.example.promptly.model;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        String sessionId,
        @NotBlank(message = "Personality is required") String personality,
        @NotBlank(message = "Message is required") String message
) {
}
