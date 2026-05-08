package org.example.promptly.controller;

import org.example.promptly.model.ChatResponse;
import org.example.promptly.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @Test
    void shouldReturn200_withReply_whenRequestIsValid() throws Exception {
        ChatResponse response = new ChatResponse("sessionId-1", "Hello, how may I assist you today?");

        when(chatService.chat(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "sessionId": "sessionId-1",
                                    "personality": "assistant",
                                    "message": "Hello"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("sessionId-1"))
                .andExpect(jsonPath("$.reply").value("Hello, how may I assist you today?"));
        verify(chatService).chat(any());
    }

    @Test
    void shouldReturn201_whenPersonalityIsUnknown() throws Exception {
        ChatResponse response = new ChatResponse("sessionId-1", "Hello, how may I assist you today?");

        when(chatService.chat(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "sessionId": "sessionId-1",
                                    "personality": "pirate",
                                    "message": "Hello"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("sessionId-1"))
                .andExpect(jsonPath("$.reply").value("Hello, how may I assist you today?"));
        verify(chatService).chat(any());
    }

    @Test
    void shouldReturn400_whenPersonalityIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "sessionId": "sessionId-1",
                                    "personality": "",
                                    "message": "Hello"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_whenMessageIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "sessionId": "sessionId-1",
                                    "personality": "assistant",
                                    "message": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_whenPersonalityIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "sessionId": "sessionId-1",
                                    "message": "Hello"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_whenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}