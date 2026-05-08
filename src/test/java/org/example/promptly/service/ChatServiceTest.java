package org.example.promptly.service;

import org.example.promptly.ai.AiClient;
import org.example.promptly.memory.ConversationMemory;
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


    @Test
    void shouldReturnReply_whenChatIsCalled() {
        // Arrange
        ChatRequest request = new ChatRequest("sessionId-1", "assistant", "Hello");

        when(conversationMemory.getHistory("sessionId-1")).thenReturn(List.of());
        when(personalityMapper.getSystemPrompt("assistant")).thenReturn("You are a helpful assistant.");
        when(aiClient.generateReply(anyList())).thenReturn("Hello, how may I assist you today?");

        // Act
        ChatResponse response = chatService.chat(request);

        // Assert
        assertEquals("Hello, how may I assist you today?", response.getReply());
        assertEquals("sessionId-1", response.getSessionId());
    }

    @Test
    void shouldReturnExistingSessionId_whenSessionIdProvided() {
        // Arrange
        ChatRequest request = new ChatRequest("sessionId-1", "assistant", "Hello");

        when(conversationMemory.getHistory("sessionId-1")).thenReturn(List.of());
        when(personalityMapper.getSystemPrompt("assistant")).thenReturn("You are a helpful assistant.");
        when(aiClient.generateReply(anyList())).thenReturn("Hello, how may I assist you today?");

        // Act
        ChatResponse response = chatService.chat(request);

        // Assert
        assertEquals("sessionId-1", response.getSessionId());
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
        assertNotNull(response.getSessionId());
    }
}