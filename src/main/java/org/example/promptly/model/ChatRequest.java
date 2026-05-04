package org.example.promptly.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {

    @NotBlank(message = "Personality is required")
    private String personality;

    @NotBlank(message = "Message is required")
    private String message;

    private String sessionId;
}
