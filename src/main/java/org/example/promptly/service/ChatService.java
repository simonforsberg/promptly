package org.example.promptly.service;

import lombok.RequiredArgsConstructor;
import org.example.promptly.memory.ConversationMemory;
import org.example.promptly.model.ChatMessage;
import org.example.promptly.model.ChatRequest;
import org.example.promptly.model.ChatResponse;
import org.example.promptly.personality.PersonalityMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final RestClient restClient;
    private final ConversationMemory conversationMemory;
    private final PersonalityMapper personalityMapper;

    @Value("${ai.model}")
    private String model;

    public ChatResponse chat(ChatRequest request) {
        String sessionId = resolveSessionId(request);

        List<ChatMessage> history = conversationMemory.getHistory(sessionId);

        history.add(new ChatMessage("user", request.getMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", buildMessages(request.getPersonality(), history));

        Map response = restClient.post()
                .uri("/chat/completions")
                .body(body)
                .retrieve()
                .body(Map.class);

        String reply = extractReply(response);

        history.add(new ChatMessage("assistant", reply));
        conversationMemory.save(sessionId, history);

        return new ChatResponse(sessionId, reply);
    }

    private String resolveSessionId(ChatRequest request) {
        return (request.getSessionId() != null && !request.getSessionId().isBlank())
                ? request.getSessionId()
                : UUID.randomUUID().toString();
    }

    private String extractReply(Map response) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("Invalid AI response");
        }
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

        return (String) message.get("content");
    }

    private List<Map<String, String>> buildMessages(String personality, List<ChatMessage> history) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", personalityMapper.getSystemPrompt(personality)));
        for (ChatMessage msg : history) {
            messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
        }
        return messages;
    }
}