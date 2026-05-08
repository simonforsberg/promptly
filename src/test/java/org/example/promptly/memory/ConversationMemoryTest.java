package org.example.promptly.memory;

import org.example.promptly.model.ChatMessage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConversationMemoryTest {

    private final ConversationMemory conversationMemory = new ConversationMemory();

    @Test
    void shouldReturnEmptyHistory_forNewSessionId() {
        assertTrue(conversationMemory.getHistory("new-sessionId").isEmpty());
    }

    @Test
    void shouldReturnEmptyHistory_forNullSessionId() {
        assertTrue(conversationMemory.getHistory(null).isEmpty());
    }

    @Test
    void shouldReturnMessage_afterAppend() {
        String sessionId = "sessionId-1";
        ChatMessage message = new ChatMessage("user", "Hello!");

        conversationMemory.append(sessionId, message);
        List<ChatMessage> history = conversationMemory.getHistory(sessionId);

        assertEquals(1, history.size());
        assertEquals("user", history.getFirst().role());
        assertEquals("Hello!", history.getFirst().content());
    }

    @Test
    void append_shouldThrow_whenNullMessage() {
        String sessionId = "sessionId-1";
        assertThrows(IllegalArgumentException.class, () -> conversationMemory.append(sessionId, null));
    }
}