package org.example.promptly.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatResponse {

    private String sessionId;

    private String reply;
}