package org.example.promptly.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatRequest {

    private String sessionId;

    @NotBlank(message = "Personality is required")
    private String personality;

    @NotBlank(message = "Message is required")
    private String message;
}
