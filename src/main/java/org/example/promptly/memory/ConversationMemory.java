package org.example.promptly.memory;

import org.example.promptly.model.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConversationMemory {

    private final Map<String, List<ChatMessage>> memory = new ConcurrentHashMap<>();

    public List<ChatMessage> getHistory(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return new ArrayList<>();
        }
        List<ChatMessage> history = memory.get(sessionId);
        if (history == null) {
            return List.of();
        }
        synchronized (history) {
            return List.copyOf(history);
        }
    }

    public void append(String sessionId, ChatMessage message) {
        if (sessionId == null || sessionId.isBlank()) {
            return;
        }
        memory.computeIfAbsent(sessionId, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(message);
    }
}
