package org.example.promptly.service;

import org.example.promptly.ai.AiClient;
import org.example.promptly.memory.ConversationMemory;
import org.example.promptly.model.ChatMessage;
import org.example.promptly.model.ChatRequest;
import org.example.promptly.model.ChatResponse;
import org.example.promptly.personality.PersonalityMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private AiClient aiClient;
    @Mock
    private ConversationMemory conversationMemory;
    @Mock
    private PersonalityMapper personalityMapper;

    @InjectMocks
    private ChatService chatService;

    private ChatRequest defaultRequest() {
        return new ChatRequest("sessionId-1", "assistant", "Hello");
    }

    private void stubHappyPathFor(String sessionId) {
        when(conversationMemory.getHistory(sessionId)).thenReturn(List.of());
        when(personalityMapper.getSystemPrompt("assistant")).thenReturn("You are a helpful assistant.");
        when(aiClient.generateReply(anyList())).thenReturn("Hello, how may I assist you today?");
    }

    @Test
    void shouldReturnReply_whenChatIsCalled() {
        // Arrange
        ChatRequest request = defaultRequest();
        stubHappyPathFor("sessionId-1");

        // Act
        ChatResponse response = chatService.chat(request);

        // Assert
        assertEquals("Hello, how may I assist you today?", response.reply());
        assertEquals("sessionId-1", response.sessionId());
    }

    @Test
    void shouldSaveMessageToConversationMemory_afterChat() {
        // Arrange
        ChatRequest request = defaultRequest();
        stubHappyPathFor("sessionId-1");

        // Act
        chatService.chat(request);

        // Assert
        verify(conversationMemory).append("sessionId-1", new ChatMessage("user", "Hello"));
        verify(conversationMemory).append("sessionId-1", new ChatMessage("assistant", "Hello, how may I assist you today?"));
    }

    @Test
    void shouldReturnExistingSessionId_whenSessionIdProvided() {
        // Arrange
        ChatRequest request = defaultRequest();
        stubHappyPathFor("sessionId-1");

        // Act
        ChatResponse response = chatService.chat(request);

        // Assert
        assertEquals("sessionId-1", response.sessionId());
    }

    @Test
    void shouldReturnNewSessionId_whenNoSessionIdProvided() {
        // Arrange
        ChatRequest request = new ChatRequest(null, "assistant", "Hello");

        when(conversationMemory.getHistory(anyString())).thenReturn(List.of());
        when(personalityMapper.getSystemPrompt("assistant")).thenReturn("You are a helpful assistant.");
        when(aiClient.generateReply(anyList())).thenReturn("Hello, how may I assist you today?");

        // Act
        ChatResponse response = chatService.chat(request);

        // Assert
        assertNotNull(response.sessionId());
    }
}