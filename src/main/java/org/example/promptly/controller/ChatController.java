package org.example.promptly.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.promptly.model.ChatRequest;
import org.example.promptly.model.ChatResponse;
import org.example.promptly.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return chatService.chat(request);
    }
}