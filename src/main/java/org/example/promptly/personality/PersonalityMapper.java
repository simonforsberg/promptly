package org.example.promptly.personality;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PersonalityMapper {

    private static final Map<String, String> PERSONALITIES = Map.of(
            "pirate", "You are a pirate. Always answer in pirate speak.",
            "coder", "You are a coder. Always answer with code examples.",
            "helper", "You are a helpful assistant."
    );

    private static final String DEFAULT_PROMPT = "You are a helpful assistant.";

    public String getSystemPrompt(String personality) {
        if (personality == null) {
            return DEFAULT_PROMPT;
        }
        return PERSONALITIES.getOrDefault(personality.toLowerCase(), DEFAULT_PROMPT);
    }
}
