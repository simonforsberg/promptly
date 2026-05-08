package org.example.promptly.personality;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PersonalityMapperTest {

    private final PersonalityMapper personalityMapper = new PersonalityMapper();

    @Test
    void shouldReturnCoderPrompt() {
        String result = personalityMapper.getSystemPrompt("coder");
        assertTrue(result.contains("coder"));
        assertEquals("You are a coder, primarily Java. Always answer with code examples. Keep the code short and concise.", result);
    }

    @Test
    void shouldBeCaseInsensitive() {
        String result = personalityMapper.getSystemPrompt("CODER");
        assertEquals("You are a coder, primarily Java. Always answer with code examples. Keep the code short and concise.", result);
    }

    @Test
    void shouldReturnDefaultPromptForUnknownPersonality() {
        String result = personalityMapper.getSystemPrompt("brick-tamland");
        assertEquals("You are a helpful assistant.", result);
    }

    @Test
    void shouldReturnDefaultPromptForNullPersonality() {
        String result = personalityMapper.getSystemPrompt(null);
        assertEquals("You are a helpful assistant.", result);
    }
}