package org.example.promptly.service;

import lombok.RequiredArgsConstructor;
import org.example.promptly.ai.AiClient;
import org.example.promptly.memory.ConversationMemory;
import org.example.promptly.model.ChatMessage;
import org.example.promptly.model.ChatRequest;
import org.example.promptly.model.ChatResponse;
import org.example.promptly.personality.PersonalityMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final AiClient aiClient;
    private final ConversationMemory conversationMemory;
    private final PersonalityMapper personalityMapper;

    public ChatResponse chat(ChatRequest request) {
        String sessionId = resolveSessionId(request);

        List<ChatMessage> messages = buildMessages(request, sessionId);
        String reply = aiClient.generateReply(messages);

        conversationMemory.append(sessionId, new ChatMessage("user", request.message()));
        conversationMemory.append(sessionId, new ChatMessage("assistant", reply));

        return new ChatResponse(sessionId, reply);
    }

    private String resolveSessionId(ChatRequest request) {
        return (request.sessionId() != null && !request.sessionId().isBlank())
                ? request.sessionId()
                : UUID.randomUUID().toString();
    }

    private List<ChatMessage> buildMessages(ChatRequest request, String sessionId) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", personalityMapper.getSystemPrompt(request.personality())));
        messages.addAll(conversationMemory.getHistory(sessionId));
        messages.add(new ChatMessage("user", request.message()));
        return messages;
    }
}